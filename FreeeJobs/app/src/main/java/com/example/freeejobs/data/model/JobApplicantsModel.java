package com.example.freeejobs.data.model;

import java.io.Serializable;
import java.util.Date;

public class JobApplicantsModel implements Serializable {

    private long id;
    private String description;
    private long jobId;
    private UserModel user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

}
