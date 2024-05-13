package no.jobbscraper.jobpostapi.jobpost;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "jobposts")
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jobpost_id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    private String url;

    private String companyName;

    private String companyImageUrl;

    private String imageUrl;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate deadline;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "j_jobpost_tags",
            joinColumns = { @JoinColumn(name = "jobpost_id") },
            inverseJoinColumns = { @JoinColumn(name = "jobtag_id") })
    private Set<JobTag> jobTags = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "j_jobpost_descriptions",
            joinColumns = { @JoinColumn(name = "jobpost_id") },
            inverseJoinColumns = { @JoinColumn(name = "jobdescription_id") })
    private Set<JobDefinition> jobDefinitions = new HashSet<>();

    public Set<JobTag> getJobTags() {
        return jobTags;
    }

    public JobPost() {}

    public JobPost(String url,
                   String companyName,
                   String companyImageUrl,
                   String imageUrl,
                   String title,
                   String description,
                   LocalDate deadline,
                   Set<JobTag> jobTags,
                   Set<JobDefinition> jobDefinitions) {
        this.url = url;
        this.companyName = companyName;
        this.companyImageUrl = companyImageUrl;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.jobTags = jobTags;
        this.jobDefinitions = jobDefinitions;
    }

    public JobPost(Long id, Date createdAt, String url, String companyName, String companyImageUrl, String imageUrl, String title, String description, LocalDate deadline, Set<JobTag> jobTags, Set<JobDefinition> jobDefinitions) {
        this.id = id;
        this.createdAt = createdAt;
        this.url = url;
        this.companyName = companyName;
        this.companyImageUrl = companyImageUrl;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.jobTags = jobTags;
        this.jobDefinitions = jobDefinitions;
    }

    public Long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyImageUrl() {
        return companyImageUrl;
    }

    public void setCompanyImageUrl(String companyImageUrl) {
        this.companyImageUrl = companyImageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Set<JobTag> getTags() {
        return jobTags;
    }

    public void setTags(Set<JobTag> jobTags) {
        this.jobTags = jobTags;
    }

    public Set<JobDefinition> getJobDefinitions() {
        return jobDefinitions;
    }

    public void setJobDefinitions(Set<JobDefinition> jobDefinitions) {
        this.jobDefinitions = jobDefinitions;
    }

    static final class Builder {
        private final String url;
        private final String imageUrl;
        private final String title;
        private String companyName;
        private String companyImageUrl;
        private String description;
        private LocalDate deadline;
        private Set<JobTag> jobTags;
        private Set<JobDefinition> jobDefinitions;

        public Builder(String url, String imageUrl, String title) {
            this.url = url;
            this.imageUrl = imageUrl;
            this.title = title;
            this.jobTags = Collections.emptySet();
            this.jobDefinitions = Collections.emptySet();
        }

        public Builder setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder setCompanyImageUrl(String companyImageUrl) {
            this.companyImageUrl = companyImageUrl;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setDeadline(LocalDate deadline) {
            this.deadline = deadline;
            return this;
        }

        public Builder setTags(Set<JobTag> jobTags) {
            this.jobTags = jobTags;
            return this;
        }

        public Builder setJobDefinitions(Set<JobDefinition> jobDefinitions) {
            this.jobDefinitions = jobDefinitions;
            return this;
        }

        public JobPost build() {
            return new JobPost(url, companyName, companyImageUrl, imageUrl,
                    title, description, deadline, jobTags,
                    jobDefinitions);
        }
    }

}
