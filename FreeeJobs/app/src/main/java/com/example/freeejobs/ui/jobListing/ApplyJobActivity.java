package com.example.freeejobs.ui.jobListing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.freeejobs.MySingleton;
import com.example.freeejobs.R;
import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.constant.JobListingStatusEnum;
import com.example.freeejobs.data.model.JobListingModel;
import com.example.freeejobs.ui.common.ErrorDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplyJobActivity extends AppCompatActivity {

    FloatingActionButton backButton;
    Button applyJobApplyButton;
    EditText applyJobDesc;
    TextView jobName;
    TextView clientName;

    final String jobAppUrl = Constants.freeeJobsURL+Constants.jobApplicationAPIPath;
    private Activity activity;

    Logger log = LoggerFactory.getLogger(ApplyJobActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);
        backButton = findViewById(R.id.ApplyJobBackButton);
        applyJobApplyButton = findViewById(R.id.ApplyJobApplyButton);
        applyJobDesc = findViewById(R.id.ApplyJobDescriptionValueTextView);
        jobName = findViewById(R.id.JobNameValueTextView);
        clientName = findViewById(R.id.ClientNameValueTextView);
        Bundle extras = getIntent().getExtras();
        jobName.setText(extras.getString("jobName"));
        clientName.setText(extras.getString("clientName"));


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        applyJobApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (extras != null) {
                    long jobListingId = extras.getLong("jobListingId");
                    long applicantId = extras.getLong("applicantId");
                    applyJobs(jobListingId,applicantId,applyJobDesc.getText().toString());
                }

            }
        });

    }

    private void applyJobs(long jobId, long applicantId, String desc) {
        String url =jobAppUrl+"/applyJob";
        Log.d("test",desc);
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("jobId", jobId);
            jsonObj.put("applicantId", applicantId);
            jsonObj.put("description",desc);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject applyJobObject = response.getJSONObject("data");
                        System.out.println("responseApplyJob:"+response);
                        JSONObject status = response.getJSONObject("status");
                        if(Integer.toString((Integer)status.get("statusCode")).equalsIgnoreCase(Constants.API_SUCCESS_CODE)){
                            //
                        }else{
                            Toast.makeText(getApplicationContext(), "Failed to Apply For Job", Toast.LENGTH_LONG).show();
                            errorDialog(": Status Code is "+status.get("statusCode")+" | "+status);
                        }


                    } catch (JSONException e) {
                        errorDialog(": Exception - "+e.toString());
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("response:"+error.toString());
                    // TODO: Handle error
                    errorDialog(": Response Error - "+error.toString());

                }
            });

            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            errorDialog(": Exception - "+e.toString());
        }



    }
    private void errorDialog(String error){
        log.error(this.getClass().getSimpleName()+ error);
        FragmentManager fm = getSupportFragmentManager();
        ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_RESPONSE_MSG);
        alertDialog.show(fm, "error_alert");
    }
}