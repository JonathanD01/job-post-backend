package no.jobbscraper.jobpostapi.jobpost;

import jakarta.validation.Valid;
import no.jobbscraper.jobpostapi.response.Response;
import no.jobbscraper.jobpostapi.response.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/jobposts")
public class JobPostController {

    private final JobPostService jobPostService;
    private final ResponseUtil responseUtil;

    public JobPostController(JobPostService jobPostService, ResponseUtil responseUtil) {
        this.jobPostService = jobPostService;
        this.responseUtil = responseUtil;
    }

    /**
     * Retrieves a paginated list of job posts based on the provided criteria.
     *
     * @param jobPostGetRequest The criteria for filtering job posts.
     * @param pageable          Pageable object
     * @return                  A ResponseEntity containing a Response object with the paginated list of job posts.
     */
    @GetMapping
    public ResponseEntity<Response<Page<JobPostDto>>> getAllJobPosts(
            @ModelAttribute JobPostGetRequest jobPostGetRequest,
            Pageable pageable
    ) {
        Page<JobPostDto> jobPosts = jobPostService.getAllJobPosts(jobPostGetRequest, pageable);

        if (jobPosts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(responseUtil.buildSuccessResponse(jobPosts));
    }

    /**
     * Retrieves a single job post by its ID.
     *
     * @param jobPostId The ID of the job post to retrieve.
     * @return          A ResponseEntity containing a Response object with the requested job post.
     */
    @GetMapping("{jobPostId}")
    public ResponseEntity<Response<JobPostDto>> getJobPostFromId(
            @PathVariable("jobPostId") Long jobPostId) {
        JobPostDto jobPost = jobPostService.getJobPostFromId(jobPostId);

        return ResponseEntity.ok(responseUtil.buildSuccessResponse(jobPost));
    }

    /**
     * Creates new job posts based on the provided request.
     *
     * @param createRequest The request containing the details of the job posts to create.
     * @return              A ResponseEntity containing a Response object with the IDs of the created job posts.
     */
    @PostMapping
    public ResponseEntity<Response<List<Long>>> createJobPosts(
            @Valid @RequestBody JobPostCreateRequest createRequest,
            @RequestParam("secretkey") String secretKey
    ) {
        List<Long> jobPostIds = jobPostService.createJobPosts(createRequest, secretKey);

        return ResponseEntity.ok(responseUtil.buildSuccessResponse(jobPostIds));
    }
}
