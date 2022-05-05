package com.example.freeejobs.ui.jobApplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.freeejobs.MySingleton;
import com.example.freeejobs.R;
import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.constant.JobAplicationStatusEnum;
import com.example.freeejobs.constant.JobListingStatusEnum;
import com.example.freeejobs.data.model.JobApplicationModel;
import com.example.freeejobs.data.model.JobListingModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobApplicationActivity extends AppCompatActivity {

    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<JobApplicationModel> jobApplications = new ArrayList<>();

    ApplicationsAdapter applicationAdapter;

    ListView jobListView;
    TextView jobApplicationsTitle;
    int page = 1;
    int authorId = 2;
    int jobAppCount = 0;

    Button btn;

    final String jobListingUrl = Constants.freeeJobsURL + Constants.jobListingAPIPath;
    final String jobAppUrl = Constants.freeeJobsURL + Constants.jobApplicationAPIPath;
    final String iamUrl = Constants.freeeJobsURL + Constants.iamAPIPath;
    final String ratingUrl = Constants.freeeJobsURL + Constants.ratingAPIPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_application);
        System.out.println("JobApplication55");
        nestedScrollView = findViewById(R.id.nestedScrollViewJobApplication);
        recyclerView = findViewById(R.id.recyclerViewJobApplication);
        progressBar = findViewById(R.id.progress_bar_job_application);
        System.out.println("JobApplication58");
        applicationAdapter = new ApplicationsAdapter(JobApplicationActivity.this, jobApplications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(applicationAdapter);

        jobApplicationsTitle = findViewById(R.id.jobApplicationsTitle);
        System.out.println("JobApplication64");
        getJobApplications(page, authorId);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getJobApplications(page, authorId);
                }
            }
        });

    }

    private void getJobApplications(int pageNumber, long userId) {

        String url = jobAppUrl + "/listJobApplicationByApplicantId?applicantId=" + userId;
        System.out.println("JobApplication83 " + url);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jobApplications = response.getJSONArray("data");
                            System.out.println("jobApp response " + response);
                            if (jobApplications.length() > 0) {
                                // implement for loop for getting users list data
                                jobAppCount = jobApplications.length();
                                for (int i = 0; i < jobApplications.length(); i++) {
                                    // create a JSONObject for fetching single listing data
                                    JSONObject applicationDetail = jobApplications.getJSONObject(i);
                                    JobApplicationModel jobApp = new JobApplicationModel();
                                    jobApp.setJobId(Long.parseLong(applicationDetail.getString("jobId")));
                                    jobApp.setApplicantId(Long.parseLong(applicationDetail.getString("applicantId")));
                                    jobApp.setStatus(applicationDetail.getString("status"));
                                    getJobListing(jobApp, userId);


                                }
                            } else {
                                //TODO: set no records found
                                Log.d("testElse ", "Hello World");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("testElse ", "Hello World2");
                        }
                        progressBar.setVisibility(View.GONE);
//                        mainAdapter=new MainAdapter(JobApplication.this, jobListings);
//                        recyclerView.setAdapter(mainAdapter);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("test", error.toString());
                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);

    }

    private void getJobListing(JobApplicationModel jobApp, long userId) {

        String url = jobListingUrl + "/getJobListing?listingId=" + jobApp.getJobId();
        System.out.println("getApplicants: " + url);
        JsonObjectRequest jsonAObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jobInfoObject = response.getJSONObject("data");
                            System.out.println("response:" + response);
                            if (jobInfoObject != null || jobInfoObject.length() > 0) {
                                JSONObject jobDetail = jobInfoObject;
                                JobListingModel job = new JobListingModel();
                                // fetch variables and store it in JobListing object
                                job.setTitle(jobDetail.getString("title"));
                                job.setStatus(jobDetail.getString("status"));
                                job.setAuthorId(jobDetail.getLong("authorId"));
                                jobApp.setJobListing(job);

                                if (job.getStatus().equals(JobListingStatusEnum.JOB_LISTING_STATUS_COMPLETED.getCode())) {
                                    getRatingsStatus(jobApp, userId);
                                } else {
                                    jobApp.getJobListing().setReviewButton(false);
                                    jobApplications.add(jobApp);
                                    jobAppCount = jobAppCount - 1;
                                    if (jobAppCount == 0) {
                                        recyclerView.setAdapter(applicationAdapter);
                                    }
                                }

                            } else {
                                //TODO: set no records found
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        applicationAdapter = new ApplicationsAdapter(JobApplicationActivity.this, jobApplications);
                        recyclerView.setAdapter(applicationAdapter);


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("response:" + error.toString());
                        //Toast.makeText(JobListingDetailsActivity.this, error.toString(),Toast.LENGTH_LONG).show();
                        // TODO: Handle error

                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonAObjectRequest);

    }

    private void getRatingsStatus(JobApplicationModel jobApp, long userId) {
        //if(jobApp.getJobListing().getStatus().equals(JobListingStatusEnum.JOB_LISTING_STATUS_COMPLETED.getCode())) {
        String url = ratingUrl + "/getRatingsByReviewerIdJobId?reviewerId=" + userId + "&jobId=" + jobApp.getJobId();
        System.out.println("JobApplication186 " + url);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray ratings = response.getJSONArray("data");
                            System.out.println("rating response " + response);
                            if (ratings.length() > 0 || jobApp.getStatus().equals(JobAplicationStatusEnum.JOB_Application_STATUS_REJECTED.getCode())) {
                                jobApp.getJobListing().setReviewButton(false);
                            } else {
                                jobApp.getJobListing().setReviewButton(true);
                            }
                            jobApplications.add(jobApp);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("testElse ", "Hello World2");
                        }
                        progressBar.setVisibility(View.GONE);
//                        mainAdapter=new MainAdapter(JobApplication.this, jobListings);
//                        recyclerView.setAdapter(mainAdapter);
                        jobAppCount = jobAppCount - 1;
                        if (jobAppCount == 0) {
                            recyclerView.setAdapter(applicationAdapter);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("test", error.toString());
                        jobAppCount = jobAppCount - 1;
                        if (jobAppCount == 0) {
                            recyclerView.setAdapter(applicationAdapter);
                        }
                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }
}