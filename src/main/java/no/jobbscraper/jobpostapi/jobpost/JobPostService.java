package no.jobbscraper.jobpostapi.jobpost;

import jakarta.transaction.Transactional;
import no.jobbscraper.jobpostapi.exception.BadSecretKeyException;
import no.jobbscraper.jobpostapi.exception.JobPostNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = Exception.class)
public class JobPostService {

    private final JobPostRepository jobPostRepository;
    private final JobTagRepository jobTagRepository;
    private final JobDefinitionRepository jobDefinitionRepository;
    private final JobPostDtoMapper jobPostDTOMapper;

    @Value("${secret_key}")
    private String secretKey;

    public JobPostService(JobPostRepository jobPostRepository,
                          JobTagRepository jobTagRepository,
                          JobDefinitionRepository jobDefinitionRepository,
                          JobPostDtoMapper jobPostDTOMapper) {
        this.jobPostRepository = jobPostRepository;
        this.jobTagRepository = jobTagRepository;
        this.jobDefinitionRepository = jobDefinitionRepository;
        this.jobPostDTOMapper = jobPostDTOMapper;
    }

    /**
     * Retrieves a page of job posts based on the given criteria.
     *
     * @param jobPostGetRequest The request containing the search criteria.
     * @param page              The page number to retrieve.
     * @param size              The size of each page.
     * @return                  A page of job posts.
     * @see Page
     * @see JobPostDto
     */
    public Page<JobPostDto> getAllJobPosts(JobPostGetRequest jobPostGetRequest, int page, int size) {
        var baseSpecification = buildBaseSpecification();

        var finalSpecification = baseSpecification
                        .and(addQuerySpecifications(baseSpecification, jobPostGetRequest))
                        .and(addPositionSpecification(baseSpecification, jobPostGetRequest))
                        .and(addSectorSpecification(baseSpecification, jobPostGetRequest))
                        .and(addMunicipalitySpecification(baseSpecification, jobPostGetRequest))
                        .and(addDeadlineSpecification(baseSpecification, jobPostGetRequest));

        return jobPostRepository
                .findAll(finalSpecification, PageRequest.of(page, size))
                .map(jobPostDTOMapper);
    }

    /**
     * Retrieves a job post by its ID.
     *
     * @param                           jobPostId The ID of the job post to retrieve.
     * @return                          The {@link JobPostDto}.
     * @throws JobPostNotFoundException if no {@link JobPost} with the given ID is found.
     * @see JobPostDto
     */
    public JobPostDto getJobPostFromId(Long jobPostId) {
        return jobPostRepository.findById(jobPostId)
                .stream()
                .map(jobPostDTOMapper)
                .findFirst()
                .orElseThrow(() -> new JobPostNotFoundException(jobPostId));
    }

    /**
     * Creates {@link JobPost} based on the provided {@link JobPostCreateRequest}.
     *
     * @param createRequest     The request containing the job post data.
     * @param givenSecretKey    Secret key required for creating the job data.
     * @return                  A list of IDs of the created job posts.
     * @see JobPost
     * @see JobPostCreateRequest
     */
    public List<Long> createJobPosts(JobPostCreateRequest createRequest, String givenSecretKey) {
        if (givenSecretKey == null || !givenSecretKey.equalsIgnoreCase(secretKey)){
            throw new BadSecretKeyException();
        }

        return createRequest.jobPosts().stream()
                .filter(jobPostCreateDto -> doesJobPostNotExistByUrl(jobPostCreateDto.url()))
                .map(this::buildJobPostFromDto)
                .map(jobPostRepository::save)
                .map(JobPost::getId)
                .toList();
    }

    /**
     * Builds the base specification for querying job posts. Ensures all
     * {@link JobPost} returned have an url set.
     *
     * @return The base specification.
     * @see JobPost
     */
    private Specification<JobPost> buildBaseSpecification() {
        return JobPostSpecifications.hasUrl();
    }

    /**
     * Adds query specifications to the base specification.
     *
     * @param specification     The base specification to add to.
     * @param jobPostGetRequest The request containing the query.
     * @return                  The updated specification.
     * @see JobPost
     */
    private Specification<JobPost> addQuerySpecifications(Specification<JobPost> specification, JobPostGetRequest jobPostGetRequest) {
        if (jobPostGetRequest.query() != null) {
            String query = jobPostGetRequest.query();
            return JobPostSpecifications.hasDescription(query);
        }
        return specification;
    }

    /**
     * Adds position specification to the base specification.
     *
     * @param specification     The base specification to add to.
     * @param jobPostGetRequest The request containing the position.
     * @return                  The updated specification.
     * @see JobPost
     */
    private Specification<JobPost> addPositionSpecification(Specification<JobPost> specification, JobPostGetRequest jobPostGetRequest) {
        if (jobPostGetRequest.position() != null) {
            return JobPostSpecifications.hasPosition(jobPostGetRequest.position());
        }
        return specification;
    }

    /**
     * Adds sector specification to the base specification.
     *
     * @param specification     The base specification to add to.
     * @param jobPostGetRequest The request containing the sector.
     * @return                  The updated specification.
     * @see JobPost
     */
    private Specification<JobPost> addSectorSpecification(Specification<JobPost> specification, JobPostGetRequest jobPostGetRequest) {
        if (jobPostGetRequest.sector() != null) {
            return JobPostSpecifications.hasSector(jobPostGetRequest.sector());
        }
        return specification;
    }

    /**
     * Adds municipality specification to the base specification.
     *
     * @param specification     The base specification to add to.
     * @param jobPostGetRequest The request containing the municipality.
     * @return                  The updated specification.
     * @see JobPost
     */
    private Specification<JobPost> addMunicipalitySpecification(Specification<JobPost> specification, JobPostGetRequest jobPostGetRequest) {
        if (jobPostGetRequest.municipality() != null) {
            return JobPostSpecifications.isInMunicipality(jobPostGetRequest.municipality());
        }
        return specification;
    }

    /**
     * Adds deadline specification to the base specification.
     *
     * @param specification     The base specification to add to.
     * @param jobPostGetRequest The request containing the deadline.
     * @return                  The updated specification.
     * @see JobPost
     */
    private Specification<JobPost> addDeadlineSpecification(Specification<JobPost> specification, JobPostGetRequest jobPostGetRequest) {
        if (jobPostGetRequest.deadline() != null) {
            return JobPostSpecifications.hasDeadline(jobPostGetRequest.deadline());
        } else {
            return JobPostSpecifications.hasDeadlineNotPassed();
        }
    }

    /**
     * Builds a new {@link JobPost} entity based on the provided {@link JobPostCreateDto}.
     *
     * @param jobPostCreateDTO  The DTO containing the details of the job post to create.
     * @return                  The constructed JobPost entity.
     * @see JobPost
     * @see JobPostCreateDto
     */
    private JobPost buildJobPostFromDto(JobPostCreateDto jobPostCreateDTO) {
        JobPost.Builder jobPostBuilder = new JobPost.Builder(jobPostCreateDTO.url(), jobPostCreateDTO.imageUrl(), jobPostCreateDTO.title())
                .setCompanyImageUrl(jobPostCreateDTO.companyImageUrl())
                .setCompanyName(jobPostCreateDTO.companyName())
                .setDescription(jobPostCreateDTO.description())
                .setDeadlineValid(jobPostCreateDTO.deadlineValid())
                .setDeadline(jobPostCreateDTO.deadline());

        // Save job tags
        if (jobPostCreateDTO.jobTags() != null) {
            Set<JobTag> jobTagSet = jobPostCreateDTO.jobTags().stream()
                    .map(jobTag -> {
                        String tag = jobTag.getTag();
                        return jobTagRepository.findByTagLikeIgnoreCase(tag)
                                .orElseGet(() -> new JobTag(tag));
                    })
                    .collect(Collectors.toSet());
            jobPostBuilder.setTags(jobTagSet);
        }

        // Save job descriptions
        if (jobPostCreateDTO.jobDefinitions() != null) {
            Set<JobDefinition> jobDefinitions = jobPostCreateDTO.jobDefinitions().stream()
                    .map(jobDefinition -> {
                        String key = jobDefinition.getKey();
                        String value = jobDefinition.getValue();
                        return jobDefinitionRepository.findByKeyLikeIgnoreCaseAndValueLikeIgnoreCase(key, value)
                                .orElseGet(() -> new JobDefinition(key, value));
                    })
                    .collect(Collectors.toSet());
            jobPostBuilder.setJobDefinitions(jobDefinitions);
        }

        return jobPostBuilder.build();
    }

    /**
     * Checks if a job post with the given URL already exists.
     *
     * @param url   The URL to check.
     * @return      True if a job post with the URL does not exist, false otherwise.
     */
    private boolean doesJobPostNotExistByUrl(String url) {
        return !jobPostRepository.existsByUrl(url);
    }
}
