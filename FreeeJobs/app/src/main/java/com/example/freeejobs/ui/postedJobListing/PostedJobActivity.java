package com.example.freeejobs.ui.postedJobListing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.freeejobs.MySingleton;
import com.example.freeejobs.R;
import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.constant.JobAplicationStatusEnum;
import com.example.freeejobs.constant.JobListingStatusEnum;
import com.example.freeejobs.data.model.JobApplicationModel;
import com.example.freeejobs.data.model.JobListingModel;
import com.example.freeejobs.ui.common.CommonUtils;
import com.example.freeejobs.ui.common.ErrorDialogFragment;
import com.example.freeejobs.ui.jobListing.JobListing;
import com.example.freeejobs.ui.jobListing.MainAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PostedJobActivity extends AppCompatActivity {

    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
//    ArrayList<JobApplicationModel> jobApplications = new ArrayList<>();
    ArrayList<JobListingModel> postedJobListings = new ArrayList<>();

    PostedJobAdapter postedJobAdapter;

    ListView jobListView;
    TextView postedJobListingBrowserTitle;
    int page = 1;
    int authorId = 1;
    Logger log = LoggerFactory.getLogger(JobListing.class);
    Button btn;

    int postedJobCount = 0;

    final String jobListingUrl = Constants.freeeJobsURL+Constants.jobListingAPIPath;
    final String jobAppUrl = Constants.freeeJobsURL+Constants.jobApplicationAPIPath;
    final String iamUrl = Constants.freeeJobsURL+Constants.iamAPIPath;
    final String ratingUrl = Constants.freeeJobsURL+Constants.ratingAPIPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);
        System.out.println("JobApplication55");
        nestedScrollView = findViewById(R.id.nestedScrollViewPostedJobListing);
        recyclerView = findViewById(R.id.recyclerViewPostedJobListing);
        progressBar = findViewById(R.id.progress_bar_posted_job_listing);
        System.out.println("JobApplication58");
        postedJobAdapter = new PostedJobAdapter(PostedJobActivity.this, postedJobListings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postedJobListingBrowserTitle = findViewById(R.id.myJobsTitle);
        System.out.println("JobApplication64");
        //get current userId
        SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String sharedPrefId = pref.getString("id", null);
        if(sharedPrefId!=null){
            authorId=Integer.parseInt(sharedPrefId);
        }

        Context getPostedJob = this;
        getPostedJob(authorId);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY== v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getPostedJob(authorId);
                }
            }
        });

    }

    private void getPostedJob(int authorId) {

        String url =jobListingUrl+"/listJobListingByAuthorId?authorId="+authorId;
        List<String> errors = new ArrayList<String>();
        Log.d("test", "getPostedJoblistings");
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("test", "onResponse");
                        try {
                            Log.d("test", "onResponse2");
                            Log.d("test", response.toString());
                            JSONArray jobList = response.getJSONArray("data");
                            Log.d("test", "" + jobList.length());
                            JSONObject status = response.getJSONObject("status");
                            if(Integer.toString((Integer)status.get("statusCode")).equalsIgnoreCase(Constants.API_SUCCESS_CODE)){
                                if(jobList.length()>0){
                                    // implement for loop for getting users list data
                                    postedJobCount = jobList.length();
                                    for (int i = 0; i < jobList.length(); i++) {
                                        // create a JSONObject for fetching single listing data
                                        JSONObject listingDetail = jobList.getJSONObject(i);
                                        JobListingModel listing = new JobListingModel();
                                        // fetch variables and store it in JobListing object
                                        if(CommonUtils.isNumber(listingDetail.getString("authorId"))){
                                            listing.setAuthorId(Integer.parseInt(listingDetail.getString("authorId")));
                                        }else{
                                            errors.add("Invalid Author Id");
                                        }
                                        if(CommonUtils.isNumber(listingDetail.getString("id"))){
                                            listing.setId(Integer.parseInt(listingDetail.getString("id")));
                                        }else{
                                            errors.add("Invalid Id");
                                        }

                                        listing.setTitle(CommonUtils.removeSpecialCharacters(listingDetail.getString("title")));
                                        listing.setDetails(CommonUtils.removeSpecialCharacters(listingDetail.getString("details")));
                                        if(CommonUtils.isNumber(listingDetail.getString("rate"))){
                                            listing.setRate(listingDetail.getString("rate"));
                                        }else{
                                            errors.add("Invalid rate");
                                        }
                                        if(CommonUtils.isRateType(listingDetail.getString("rateType"))){
                                            listing.setRateType(listingDetail.getString("rateType"));
                                        }else{
                                            errors.add("Invalid rate type");
                                        }
                                        //listing.setDateCreated(new SimpleDateFormat("MM-dd-yyyy").parse(listingDetail.getString("dateCreated")));
                                        if(!JobListingStatusEnum.Constants.JOB_LISTING_STATUS_LIST.contains(listingDetail.getString("status"))) {
                                            errors.add("Invalid status value");
                                        }else{
                                            listing.setStatus(listingDetail.getString("status"));
                                        }

                                        if(errors.isEmpty()) {
                                            Log.d("test147 ", listing.getTitle());
                                            //postedJobListings.add(listing);
                                            System.out.println("----- Posted job " + listing.getTitle());
                                            if (listing.getStatus().equals(JobListingStatusEnum.JOB_LISTING_STATUS_COMPLETED.getCode())) {
                                                System.out.println("----- status = C");
                                                getRatingsStatus(listing, authorId);

                                            } else {
                                                System.out.println("----- else");
                                                listing.setReviewButton(false);
                                                postedJobListings.add(listing);
                                                postedJobCount = postedJobCount - 1;
                                                if (postedJobCount == 0) {
                                                    String strMyJobTitle = postedJobListingBrowserTitle.getText().toString();
                                                    postedJobListingBrowserTitle.setText(strMyJobTitle + " ("+postedJobListings.size()+")");
                                                    recyclerView.setAdapter(postedJobAdapter);
                                                }
                                            }
                                            System.out.println("----- After rating -- Size: " + postedJobListings.size());
                                        }else{
                                            String listOfErrors = TextUtils.join(",", errors);
                                            log.error(this.getClass().getSimpleName()+ ": " +listOfErrors);
                                            FragmentManager fm = getSupportFragmentManager();
                                            ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_IN_FIELD_MSG);
                                            alertDialog.show(fm, "error_alert");
                                        }


                                    }
                                }else{
                                    //TODO: set no records found
                                    //Log.d("testElse ", "Hello World");
                                    log.info(this.getClass().getSimpleName()+ ": No Records Found.");
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed to Get Posted Job", Toast.LENGTH_LONG).show();
                                errorDialog(": Status Code is "+status.get("statusCode")+" | "+status);
                            }


                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.d("testElse ", "Hello World2");
                            errorDialog(": Exception - "+e.toString());
                        }
                        progressBar.setVisibility(View.GONE);
                        postedJobAdapter=new PostedJobAdapter(PostedJobActivity.this, postedJobListings);
                        //recyclerView.setAdapter(postedJobAdapter);


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        //Log.d("test", error.toString());
                        errorDialog(": Response Error - "+error.toString());
                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void errorDialog(String error){
        log.error(this.getClass().getSimpleName()+ error);
        FragmentManager fm = getSupportFragmentManager();
        ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_RESPONSE_MSG);
        alertDialog.show(fm, "error_alert");
    }

    private void getRatingsStatus(JobListingModel job, long userId){

            String url = ratingUrl+"/getRatingsByReviewerIdJobId?reviewerId=" + userId +"&jobId=" + job.getId();
            System.out.println("getRatingsStatus ---" + url);
            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray ratings = response.getJSONArray("data");
                                System.out.println("------rating response " + response);
                                if(ratings.length()>0){
                                    job.setReviewButton(false);
                                }else{
                                    job.setReviewButton(true);
                                }
                                postedJobListings.add(job);
                                System.out.println("panlinggggg--- " + postedJobListings.size());

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("testElse ", "Hello World2");
                            }
                            progressBar.setVisibility(View.GONE);
//                        mainAdapter=new MainAdapter(JobApplication.this, jobListings);
//                        recyclerView.setAdapter(mainAdapter);
                            postedJobCount = postedJobCount - 1;
                            if (postedJobCount == 0) {
                                String strMyJobTitle = postedJobListingBrowserTitle.getText().toString();
                                postedJobListingBrowserTitle.setText(strMyJobTitle + " ("+postedJobListings.size()+")");
                                recyclerView.setAdapter(postedJobAdapter);
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Log.d("test", error.toString());
                            postedJobCount = postedJobCount - 1;
                            if (postedJobCount == 0) {
                                String strMyJobTitle = postedJobListingBrowserTitle.getText().toString();
                                postedJobListingBrowserTitle.setText(strMyJobTitle + " ("+postedJobListings.size()+")");
                                recyclerView.setAdapter(postedJobAdapter);
                            }
                        }
                    });

            // Add a request (in this example, called stringRequest) to your RequestQueue.
            MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }


}