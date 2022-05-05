package com.example.freeejobs.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.freeejobs.MySingleton;
import com.example.freeejobs.R;
import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.data.model.JobApplicantsModel;
import com.example.freeejobs.data.model.UserModel;
import com.example.freeejobs.ui.common.CommonUtils;
import com.example.freeejobs.ui.common.ErrorDialogFragment;
import com.example.freeejobs.ui.jobListing.ApplicantDetailsActivity;
import com.example.freeejobs.ui.jobListing.ApplicantsAdapter;
import com.example.freeejobs.ui.jobListing.JobApplicantsActivity;
import com.example.freeejobs.ui.jobListing.JobListingDetailsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileActivity extends AppCompatActivity {

    TextView fullName;
    TextView professionalTitle;
    TextView aboutMe;
    TextView skills;
    TextView contactNo;
    TextView gender;
    TextView dob;

    private Integer userId;
    final String iamUrl = Constants.freeeJobsURL+Constants.iamAPIPath;

    Logger log = LoggerFactory.getLogger(ApplicantDetailsActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullName = findViewById(R.id.FullNameTextView);
        professionalTitle = findViewById(R.id.ProfessionalTitleTextView);
        gender = findViewById(R.id.GenderTextView);
        dob = findViewById(R.id.DateOfBirthTextView);
        contactNo = findViewById(R.id.ContactNoTextView);
        aboutMe = findViewById(R.id.AboutMeTextView);
        skills = findViewById(R.id.SkillsTextView);
        //get current userId
        SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String sharedPrefId = pref.getString("id", null);
        if(sharedPrefId!=null){
            userId=Integer.parseInt(sharedPrefId);
        }

        getUserProfile(userId);
    }

   private void getUserProfile(Integer userId) {
        String url = iamUrl + "/userProfile?userId=" + userId;

        JsonObjectRequest jsonAObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject userInfoObject = response.getJSONObject("data");
                            System.out.println("response:"+response);
                            List<String> errors = new ArrayList<String>();
                            JSONObject status = response.getJSONObject("status");
                            if(Integer.toString((Integer)status.get("statusCode")).equalsIgnoreCase(Constants.API_SUCCESS_CODE)){
                                if(userInfoObject != null || userInfoObject.length() > 0) {
                                    JSONObject userDetail = userInfoObject;
                                    UserModel user = new UserModel();

                                    user.setFirstName(CommonUtils.removeSpecialCharacters(userDetail.getString("firstName")));
                                    user.setLastName(CommonUtils.removeSpecialCharacters(userDetail.getString("lastName")));

                                    user.setAboutMe(CommonUtils.removeSpecialCharacters(userDetail.getString("aboutMe")));
                                    if(CommonUtils.isContactNo(userDetail.getString("contactNo"))) {
                                        user.setContactNo(userDetail.getString("contactNo"));
                                    } else {
                                        errors.add("Invalid contact number");
                                    }

                                    Timestamp stamp = new Timestamp(Long.parseLong(String.valueOf(userDetail.getString("dob"))));
                                    Date date = new Date(stamp.getTime());
                                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    String strDate = dateFormat.format(date);
                                    user.setDOB(strDate);
                                    if(CommonUtils.isGender(userDetail.getString("gender"))) {
                                        user.setGender(userDetail.getString("gender"));
                                    } else {
                                        errors.add("Invalid gender");
                                    }

                                    user.setSkills(CommonUtils.removeSpecialCharacters(userDetail.getString("skills")));
                                    user.setProfessionalTitle(CommonUtils.removeSpecialCharacters(userDetail.getString("professionalTitle")));
                                    //user.setLinkedInAcct(CommonUtils.removeSpecialCharacters(userDetail.getString("linkedInAcct")));

                                    if(errors.isEmpty()) {
                                        fullName.setText(user.getFirstName() + " " + user.getLastName());
                                        professionalTitle.setText(user.getProfessionalTitle());
                                        aboutMe.setText(user.getAboutMe());
                                        skills.setText(user.getSkills());
                                        contactNo.setText(user.getContactNo());
                                        gender.setText(user.getGender());
                                        dob.setText(user.getDOB());

                                    } else {
                                        String listOfErrors = TextUtils.join(",", errors);
                                        errorDialog(": " +listOfErrors);
                                    }

                                } else {
                                    errorDialog(": Record Not Found.");
                                }
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
                        //Toast.makeText(JobListingDetailsActivity.this, error.toString(),Toast.LENGTH_LONG).show();
                        // TODO: Handle error
                        errorDialog(": Response Error - "+error.toString());
                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonAObjectRequest);
    }
    private void errorDialog(String error){
        log.error(this.getClass().getSimpleName()+ error);
        FragmentManager fm = getSupportFragmentManager();
        ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_RESPONSE_MSG);
        alertDialog.show(fm, "error_alert");
    }
}