package com.example.freeejobs.ui.postedJobListing;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
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
import com.example.freeejobs.data.model.JobListingModel;
import com.example.freeejobs.ui.jobApplication.RatingsActivity;
import com.example.freeejobs.ui.jobListing.JobListingDetailsActivity;

import java.util.ArrayList;

public class PostedJobAdapter extends RecyclerView.Adapter<PostedJobAdapter.ViewHolder> {
    //initialise variable
    private ArrayList<JobListingModel>  postedJobListings;
    private Activity activity;
    private long userId;
    public PostedJobAdapter(Activity activity, ArrayList<JobListingModel> postedJobListings){
        this.activity =activity;
        this.postedJobListings=postedJobListings;
        System.out.println("PostedJobAdapter:");
    }

    @NonNull
    @Override
    public PostedJobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.posted_job_row_main, parent, false);
        return new PostedJobAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostedJobAdapter.ViewHolder holder, int position) {
        JobListingModel postedJob = postedJobListings.get(position);

        holder.jobTitle.setText(postedJob.getTitle());
        String postedJobsStatus = "";
        System.out.println("status jobApp " + postedJob.getStatus());
        postedJobsStatus = JobListingStatusEnum.getEnumByCode(postedJob.getStatus()).getDescription();

        if(!postedJob.getReviewButton()) {
            holder.reviewButton.setVisibility(View.GONE);
        }

        holder.status.setText(postedJobsStatus);

        userId = 1;

    }

    @Override
    public int getItemCount() {
        return postedJobListings.size();
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
                    JobListingModel jobListing = postedJobListings.get(getAdapterPosition());
                    if(!jobListing.getStatus().equals(JobListingStatusEnum.JOB_LISTING_STATUS_REMOVED.getCode())) {
                        Intent intent = new Intent(activity, JobListingDetailsActivity.class);
//                    //Log.d("select Job  ", "" + listing.getId());
                        intent.putExtra("jobListingId", jobListing.getId());
////                    intent.putExtra("userId", applicant.getId());
////                    intent.putExtra("description", applicant.getDescription());
//                    intent.putExtra("jobListing", job_Application);
                        activity.startActivity(intent);
                    }

                }
            });

            /*reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("ApplicationsAdapter:" + ""+getAdapterPosition());
                    JobListingModel jobListing = postedJobListings.get(getAdapterPosition());
                    Intent intent = new Intent(activity, RatingsActivity.class);
//                    //Log.d("select Job  ", "" + listing.getId());
                    intent.putExtra("jobListingId", jobListing.getId());
                    intent.putExtra("targetId", jobListing.getAuthorId());
                    activity.startActivity(intent);
                }
            });*/
        }
    }
}
