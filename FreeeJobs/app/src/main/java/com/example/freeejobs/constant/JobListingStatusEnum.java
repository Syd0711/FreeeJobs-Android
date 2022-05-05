package com.example.freeejobs.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum JobListingStatusEnum {
    JOB_LISTING_STATUS_OPEN_FOR_APPLICATION("OFA", "Open for Application"),
    JOB_LISTING_STATUS_PENDING_COMPLETION("PC", "Pending Completion"),
    JOB_LISTING_STATUS_COMPLETED("C", "Completed"),
    JOB_LISTING_STATUS_REMOVED("R", "Removed");

    public static class Constants{
        private Constants() {
        }

        public static final List<String> JOB_LISTING_STATUS_LIST = Collections.unmodifiableList(Arrays.asList(
                JOB_LISTING_STATUS_OPEN_FOR_APPLICATION.getCode(), JOB_LISTING_STATUS_PENDING_COMPLETION.getCode(),
                JOB_LISTING_STATUS_COMPLETED.getCode(), JOB_LISTING_STATUS_REMOVED.getCode()));
    }

    private String code;
    private String description;

    JobListingStatusEnum(final String code, final String description){
        this.code = code;
        this.description = description;

    }

    private static final Map<String, JobListingStatusEnum> lookup =new HashMap<>();
    static{
        for(final JobListingStatusEnum JobListingStatusEnum: com.example.freeejobs.constant.JobListingStatusEnum.values()){
            lookup.put(JobListingStatusEnum.getCode(), JobListingStatusEnum);
        }
    }

    public static JobListingStatusEnum getEnumByCode(final String code){
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
