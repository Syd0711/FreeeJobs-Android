package com.example.freeejobs.ui.jobListing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.freeejobs.MySingleton;
import com.example.freeejobs.R;
import com.example.freeejobs.constant.JobListingStatusEnum;
import com.example.freeejobs.data.model.JobListingModel;
import com.example.freeejobs.databinding.ActivityJobListingBinding;
import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.ui.common.CommonUtils;
import com.example.freeejobs.ui.common.ErrorDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JobListing extends AppCompatActivity {

    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<JobListingModel> jobListings = new ArrayList<>();

    MainAdapter mainAdapter;

    SearchView jobSearchView;
    ListView jobListView;
    TextView jobListingBrowserTitle;
    int page = 1;

    Button btn;

    final String jobListingUrl = Constants.freeeJobsURL+Constants.jobListingAPIPath;

    Logger log = LoggerFactory.getLogger(JobListing.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_listing);
        Log.d("test","JobListing");
        nestedScrollView = findViewById(R.id.nestedScrollViewJobListing);
        recyclerView = findViewById(R.id.recyclerViewJobListing);
        progressBar = findViewById(R.id.progress_bar_job_listing);

        mainAdapter = new MainAdapter(JobListing.this, jobListings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mainAdapter);


        jobSearchView = findViewById(R.id.searchJobView);
        //jobListView = findViewById(R.id.);

        jobListingBrowserTitle = findViewById(R.id.jobListingBrowseTitle);

        getJobListings(page,"");

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY== v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getJobListings(page,"");
                    if(jobListings.size()==0){
                        Toast.makeText(JobListing.this, "No Jobs found",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        jobSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                jobListings.clear();
                getJobListings(page, s);
                if(jobListings.size()==0){
                    Toast.makeText(JobListing.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(TextUtils.isEmpty(s)){
                    jobListings.clear();
                    getJobListings(page, s);
                    // Do your stuff here.
                }
                return false;
            }
        });


        //ArrayList<JobListingModel> jobs = getJobListings();
        //test.setText("Response: " + jobs.toString());
        //System.out.println(jobs.size());




    }

    private void getJobListings(int pageNumber, String searchValue) {

        String url =jobListingUrl+"/listAllOpenActiveJobListing?pageNumber="+pageNumber+"&searchValue="+searchValue;
        List<String> errors = new ArrayList<String>();
        Log.d("test", "getJoblistings");
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
                                            jobListings.add(listing);
                                        }else{
                                            String listOfErrors = TextUtils.join(",", errors);
                                            errorDialog(": " +listOfErrors);
                                        }


                                    }
                                }else{
                                    //TODO: set no records found
                                    //Log.d("testElse ", "Hello World");
                                    log.info(this.getClass().getSimpleName()+ ": No Records Found.");
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed to get Job Listings!", Toast.LENGTH_LONG).show();
                                errorDialog(": Status Code is "+status.get("statusCode")+" | "+status);
                            }


                        } catch (JSONException e) {
                            errorDialog(": Exception - "+e.toString());
                        }
                        progressBar.setVisibility(View.GONE);
                        mainAdapter=new MainAdapter(JobListing.this, jobListings);
                        recyclerView.setAdapter(mainAdapter);
                        jobListingBrowserTitle.setText("Find Jobs! ("+jobListings.size()+")");

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
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
}