package com.example.freeejobs.ui.jobListing;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freeejobs.R;
import com.example.freeejobs.data.model.JobApplicantsModel;
import com.example.freeejobs.data.model.JobListingModel;
import com.example.freeejobs.ui.common.CommonUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class ApplicantsAdapter extends RecyclerView.Adapter<ApplicantsAdapter.ViewHolder> {
    //initialise variable
    private ArrayList<JobApplicantsModel> jobApplicants;
    private Activity activity;
    private long userId;
     public ApplicantsAdapter(Activity activity, ArrayList<JobApplicantsModel> jobApplicants){
         this.activity =activity;
         this.jobApplicants = jobApplicants;
     }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater
                 .from(parent.getContext())
                 .inflate(R.layout.list_job_applicants_row_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         JobApplicantsModel jobApplicant = jobApplicants.get(position);
         String applicant_Name = CommonUtils.removeSpecialCharacters(jobApplicant.getUser().getFirstName() + " " + jobApplicant.getUser().getLastName());
         System.out.println("applicant Name : " + applicant_Name);
         holder.applicantName.setText(applicant_Name);
         userId = 1;

    }

    @Override
    public int getItemCount() {
        return jobApplicants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //initialize variables
        TextView applicantName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            applicantName = itemView.findViewById(R.id.textViewApplicantName);

            applicantName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("ApplicantsAdapter:" + ""+getAdapterPosition());
                    JobApplicantsModel applicant = jobApplicants.get(getAdapterPosition());
                    Intent intent = new Intent(activity, ApplicantDetailsActivity.class);
//                    //Log.d("select Job  ", "" + listing.getId());
////                    intent.putExtra("jobListingId", applicant.getJobId());
////                    intent.putExtra("userId", applicant.getId());
////                    intent.putExtra("description", applicant.getDescription());
                    intent.putExtra("applicantInfo", applicant);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
