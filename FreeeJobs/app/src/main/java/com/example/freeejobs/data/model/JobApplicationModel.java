package com.example.freeejobs.data.model;

import java.io.Serializable;

public class JobApplicationModel implements Serializable {

    private long id;
    private long jobId;
    private long applicantId;
    private String status;
    private JobListingModel jobListing;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getJobId() {
        return jobId;
    }

    public long getApplicantId() {
        return applicantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setApplicantId(long applicantId) {
        this.applicantId = applicantId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public JobListingModel getJobListing() {
        return jobListing;
    }

    public void setJobListing(JobListingModel jobListing) {
        this.jobListing = jobListing;
    }

}
