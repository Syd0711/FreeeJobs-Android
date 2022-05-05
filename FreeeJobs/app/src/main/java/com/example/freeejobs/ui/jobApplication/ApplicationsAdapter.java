package com.example.freeejobs.ui.jobApplication;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freeejobs.R;
import com.example.freeejobs.constant.JobAplicationStatusEnum;
import com.example.freeejobs.constant.JobListingStatusEnum;
import com.example.freeejobs.data.model.JobApplicationModel;
import com.example.freeejobs.ui.jobListing.ApplicantDetailsActivity;
import com.example.freeejobs.ui.jobListing.JobListingDetailsActivity;

import java.util.ArrayList;

public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ViewHolder> {
    //initialise variable
    private ArrayList<JobApplicationModel> jobApplication;
    private Activity activity;
    private long userId;
     public ApplicationsAdapter(Activity activity, ArrayList<JobApplicationModel> jobApplication){
         this.activity =activity;
         this.jobApplication = jobApplication;
     }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater
                 .from(parent.getContext())
                 .inflate(R.layout.list_job_application_row_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobApplicationModel jobApp = jobApplication.get(position);

         holder.jobTitle.setText(jobApp.getJobListing().getTitle());
         String jobAppStatus = "";
         System.out.println("status jobApp " + jobApp.getStatus());
         if(jobApp.getStatus().equals(JobAplicationStatusEnum.JOB_Application_STATUS_ACCEPTED.getCode())) {
             jobAppStatus = JobListingStatusEnum.getEnumByCode(jobApp.getJobListing().getStatus()).getDescription();
             if(jobApp.getJobListing().getStatus().equals(JobListingStatusEnum.JOB_LISTING_STATUS_PENDING_COMPLETION.getCode())) {
                 jobAppStatus = JobAplicationStatusEnum.JOB_Application_STATUS_IN_PROGRESS.getDescription();
             }
         }
         else {
             jobAppStatus = JobAplicationStatusEnum.getEnumByCode(jobApp.getStatus()).getDescription();
         }

         if(!jobApp.getJobListing().getReviewButton()) {
             holder.reviewButton.setVisibility(View.GONE);
         }
        holder.status.setText(jobAppStatus);

         userId = 1;

    }

    @Override
    public int getItemCount() {
        return jobApplication.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //initialize variables
        TextView jobTitle;
        TextView status;
        TextView reviewButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.textAppJobTitle);
            status = itemView.findViewById(R.id.textAppStatus);
            reviewButton = itemView.findViewById(R.id.textReviewButton);


            jobTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("ApplicationsAdapter:" + ""+getAdapterPosition());
                    JobApplicationModel job_Application = jobApplication.get(getAdapterPosition());
                    if(job_Application.getStatus().equals(JobAplicationStatusEnum.JOB_Application_STATUS_ACCEPTED.getCode()) ||
                            job_Application.getStatus().equals(JobAplicationStatusEnum.JOB_Application_STATUS_PENDING_ACCEPTANCE.getCode())) {
                        Intent intent = new Intent(activity, JobListingDetailsActivity.class);
//                    //Log.d("select Job  ", "" + listing.getId());
                        intent.putExtra("jobListingId", job_Application.getJobId());
////                    intent.putExtra("userId", applicant.getId());
////                    intent.putExtra("description", applicant.getDescription());
//                    intent.putExtra("jobApplication", job_Application);
                        activity.startActivity(intent);
                    }

                }
            });

            reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("ApplicationsAdapter:" + ""+getAdapterPosition());
                    JobApplicationModel job_Application = jobApplication.get(getAdapterPosition());
                    Intent intent = new Intent(activity, RatingsActivity.class);
//                    //Log.d("select Job  ", "" + listing.getId());
                    intent.putExtra("jobListingId", job_Application.getJobId());
                    intent.putExtra("targetId", job_Application.getJobListing().getAuthorId());
                    activity.startActivity(intent);
                }
            });
        }
    }
}
