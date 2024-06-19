package no.jobbscraper.jobpostapi.jobpost;

import jakarta.transaction.Transactional;
import no.jobbscraper.jobpostapi.exception.BadSecretKeyException;
import no.jobbscraper.jobpostapi.exception.JobPostNotFoundException;
import no.jobbscraper.jobpostapi.jobdefinition.JobDefinition;
import no.jobbscraper.jobpostapi.jobdefinition.JobDefinitionRepository;
import no.jobbscraper.jobpostapi.jobtag.JobTag;
import no.jobbscraper.jobpostapi.jobtag.JobTagRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    @Transactional
    public Page<JobPostDto> getAllJobPosts(JobPostGetRequest jobPostGetRequest, int page, int size) {
        var finalSpecification = hasUrlSpecification(
                addDescriptionSpecification(jobPostGetRequest)
                        .and(addPositionSpecification(jobPostGetRequest))
                        .and(addSectorSpecification(jobPostGetRequest))
                        .and(addMunicipalitySpecification(jobPostGetRequest))
                        .and(addDeadlineSpecification(jobPostGetRequest))
        );
        // Fixes HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
        List<JobPost> jobPosts = jobPostRepository.findAll(finalSpecification);

        PagedListHolder<JobPost> pagedListHolder = new PagedListHolder<>(jobPosts);
        pagedListHolder.setPage(page);
        pagedListHolder.setPageSize(size);

        var totalPages = pagedListHolder.getPageCount();

        // It seems like PagedListHolder always returns the last page no matter what
        if (page > totalPages) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
        }
        List<JobPostDto> dtoList = pagedListHolder.getPageList().stream()
                .map(jobPostDTOMapper)
                .toList();

        return new PageImpl<>(dtoList, PageRequest.of(page, size), jobPosts.size());
    }

    /**
     * Retrieves a job post by its ID.
     *
     * @param                           jobPostId The ID of the job post to retrieve.
     * @return                          The {@link JobPostDto}.
     * @throws JobPostNotFoundException if no {@link JobPost} with the given ID is found.
     * @see JobPostDto
     */
    @Transactional
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
    @Transactional
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
     * Ensures all {@link JobPost} returned have an url set.
     *
     * @return The base specification.
     * @see JobPost
     */
    private Specification<JobPost> hasUrlSpecification(Specification<JobPost> specification) {
        return JobPostSpecifications.hasUrl(specification);
    }

    /**
     * Adds description specifications to the base specification.
     *
     * @param jobPostGetRequest The request containing the query.
     * @return                  The updated specification.
     * @see JobPost
     */
    private Specification<JobPost> addDescriptionSpecification(JobPostGetRequest jobPostGetRequest) {
        return JobPostSpecifications.hasDescription(jobPostGetRequest.query());
    }

    /**
     * Adds position specification to the base specification.
     *
     * @param jobPostGetRequest The request containing the position.
     * @return                  The updated specification.
     * @see JobPost
     */
    private Specification<JobPost> addPositionSpecification(JobPostGetRequest jobPostGetRequest) {
        return JobPostSpecifications.hasPosition(jobPostGetRequest.position());
    }

    /**
     * Adds sector specification to the base specification.
     *
     * @param jobPostGetRequest The request containing the sector.
     * @return                  The updated specification.
     * @see JobPost
     */
    private Specification<JobPost> addSectorSpecification(JobPostGetRequest jobPostGetRequest) {
        return JobPostSpecifications.hasSector(jobPostGetRequest.sector());
    }

    /**
     * Adds municipality specification to the base specification.
     *
     * @param jobPostGetRequest The request containing the municipality.
     * @return                  The updated specification.
     * @see JobPost
     */
    private Specification<JobPost> addMunicipalitySpecification(JobPostGetRequest jobPostGetRequest) {
        return JobPostSpecifications.isInMunicipality(jobPostGetRequest.municipality());
    }

    /**
     * Adds deadline specification to the base specification.
     *
     * @param jobPostGetRequest The request containing the deadline.
     * @return                  The updated specification.
     * @see JobPost
     */
    private Specification<JobPost> addDeadlineSpecification(JobPostGetRequest jobPostGetRequest) {
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
                .setDeadline(jobPostCreateDTO.deadline());

        if (jobPostCreateDTO.jobTags() != null) {
            Set<JobTag> jobTagSet = jobPostCreateDTO.jobTags().stream()
                    .map(this::getOrCreateJobTag)
                    .collect(Collectors.toSet());
            jobPostBuilder.setTags(jobTagSet);
        }

        if (jobPostCreateDTO.jobDefinitions() != null) {
            Set<JobDefinition> jobDefinitions = jobPostCreateDTO.jobDefinitions().stream()
                    .map(this::getOrCreateJobDefinition)
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

    /**
     * Retrieves an existing JobTag from the repository by its tag.
     * If the JobTag does not exist, a new JobTag with the specified tag is created and returned.
     *
     * @param jobTag    the JobTag to retrieve or create
     * @return          the existing or newly created JobTag
     */
    private JobTag getOrCreateJobTag(JobTag jobTag) {
        String tag = jobTag.getTag();

        Optional<JobTag> optionalJobTag = jobTagRepository.findByTag(tag);
        return optionalJobTag.orElseGet(() -> new JobTag(tag));
    }

    /**
     * Retrieves an existing JobDefinition from the repository by its key and value.
     * If the JobDefinition does not exist, the provided JobDefinition object is returned.
     *
     * @param jobDefinition     the JobDefinition to retrieve or create
     * @return                  the existing JobDefinition if found, otherwise the provided JobDefinition
     */
    private JobDefinition getOrCreateJobDefinition(JobDefinition jobDefinition) {
        String key = jobDefinition.getKey();
        String value = jobDefinition.getValue();

        Optional<JobDefinition> jobDefinitionOptional = jobDefinitionRepository.findByKeyAndValue(key, value);
        return jobDefinitionOptional.orElseGet(() -> new JobDefinition(key, value));
    }
}
