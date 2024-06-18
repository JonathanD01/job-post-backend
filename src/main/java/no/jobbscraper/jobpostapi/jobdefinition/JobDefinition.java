package no.jobbscraper.jobpostapi.jobdefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import no.jobbscraper.jobpostapi.jobpost.JobPost;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "job_definitions",
        uniqueConstraints={@UniqueConstraint(columnNames={"key", "value"})}
)
public class JobDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "jobDefinitions")
    private Set<JobPost> jobPosts = new HashSet<>();

    private String key;

    private String value;

    public JobDefinition(){}

    public JobDefinition(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public JobDefinition(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<JobPost> getJobPosts() {
        return jobPosts;
    }

    public void setJobPosts(Set<JobPost> jobPosts) {
        this.jobPosts = jobPosts;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("JobDefinition{id=%s, key=%s, value=%s}", id, key, value);
    }
}
