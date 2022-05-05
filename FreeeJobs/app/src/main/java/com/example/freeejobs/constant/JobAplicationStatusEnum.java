package com.example.freeejobs.constant;

import java.util.HashMap;
import java.util.Map;

public enum JobAplicationStatusEnum {
    JOB_Application_STATUS_PENDING_ACCEPTANCE("PA", "Pending Acceptance"),
    JOB_Application_STATUS_ACCEPTED("A", "Accepted"),
    JOB_Application_STATUS_CLOSED("C", "Closed"),
    JOB_Application_STATUS_REJECTED("R", "Rejected"),
    JOB_Application_STATUS_IN_PROGRESS("IP", "In Progress");

    private String code;
    private String description;

    JobAplicationStatusEnum(final String code, final String description){
        this.code = code;
        this.description = description;

    }

    private static final Map<String, JobAplicationStatusEnum> lookup =new HashMap<>();
    static{
        for(final JobAplicationStatusEnum JobListingStatusEnum: JobAplicationStatusEnum.values()){
            lookup.put(JobListingStatusEnum.getCode(), JobListingStatusEnum);
        }
    }

    public static JobAplicationStatusEnum getEnumByCode(final String code){
        return lookup.get(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
