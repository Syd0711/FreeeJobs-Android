package com.example.freeejobs.ui.login;

import android.app.Activity;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.freeejobs.MySingleton;
import com.example.freeejobs.R;
import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.data.Result;
import com.example.freeejobs.data.model.LoggedInUser;
import com.example.freeejobs.ui.common.ErrorDialogFragment;
import com.example.freeejobs.ui.jobListing.JobListing;
import com.example.freeejobs.ui.landingPage.LandingPage;
import com.example.freeejobs.ui.login.LoginViewModel;
import com.example.freeejobs.ui.login.LoginViewModelFactory;
import com.example.freeejobs.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    final String iamUrl = Constants.freeeJobsURL+Constants.iamAPIPath;

    final String PREFS_NAME = Constants.PREFS_NAME;
    final String API_SUCCESS_CODE = Constants.API_SUCCESS_CODE;
    Logger log = LoggerFactory.getLogger(JobListing.class);

    ProgressBar loadingProgressBar=null;

    private LoggedInUser user = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.loginBasic;
        final Button loginLinkedinButton = binding.loginLinkedin;
        final Button changePasswordButton = binding.requestPwChange;
        this.loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                boolean loginPassed = getLogin(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                System.out.println("is user null"+loginPassed);


            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.changePassword();
            }
        });
    }

    private void openLandingPage() {
        Intent intent = new Intent(this, LandingPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private boolean getLogin(String username, String password) {
        String userId="";
        String url =iamUrl+"/login";
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("email", username);
            jsonObj.put("password", encryptData(password));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject setUserObject = response.getJSONObject("data");
                        JSONObject status = response.getJSONObject("status");
                        System.out.println("responseLogin:"+response);

                        if(Integer.toString((Integer)status.get("statusCode")).equalsIgnoreCase(API_SUCCESS_CODE)){

                            LoggedInUser userDetails =
                                    new LoggedInUser(
                                            setUserObject.getString("userId"),
                                            username);
                            saveSharedPref(userDetails.getUserId());
                            openLandingPage();
                        }else{
                            errorToast();
                            throw new Exception();
                        }


                    } catch (JSONException e) {
                        errorDialog(": Exception - "+e.toString());
                    } catch (Exception e){
                        errorDialog(": Exception - "+e.toString());
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("response:"+error.toString());
                    // TODO: Handle error
                    errorDialog(": Exception - "+error.toString());

                }
            });

            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            errorDialog(": Exception - "+e.toString());
        }
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        return settings.contains("id");

    }

    public static String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjYMZlmADvIdv/w4H+IATco8oLVLHXcjAqa7WRgJr3GdWcG10sU/gWpEnM0aEMkNa8/2yXjeuV7PcC935+8p4JnIeFiAxo6CdL+vEN02805E1QB7hP+xzoJk4NfxgR6Re4iw42v4sZOrTE3Ky9/lHNGYIGqZeLf82rEqQOkrfsZ+MZufDaOfJ1MqwwQN1x33xY4g7N4OG75CW1NfsoP7/tM1WbRy9FKe37q4m1QzF6FPGp5NAshlMyVkFSTWRLR7r+qlmnGS394C3iFKbyLUrfwr3WKpfwBcnoNil+6tfITcmF6b2WdD/Ia0zT2AvuwQFtVogGZJWF+MQ7tA0PZmbsQIDAQAB";

    private String encryptData(String txt)
    {
        String encoded = "";
        byte[] encrypted = null;
        try {
            byte[] publicBytes = Base64.decode(PUBLIC_KEY, Base64.NO_WRAP);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); //or try with "RSA"
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encrypted = cipher.doFinal(txt.getBytes("UTF-8"));
            encoded = Base64.encodeToString(encrypted, Base64.NO_WRAP);
            System.out.println("encoded:"+encoded);
        }
        catch (Exception e) {
            errorDialog(": Exception - "+e.toString());
        }
        return encoded;
    }

    private void saveSharedPref(String userId){
        SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("id",userId);

        editor.commit();
    }

    private void errorToast(){
        this.loadingProgressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
    }
    private void errorDialog(String error){
        log.error(this.getClass().getSimpleName()+ error);
        FragmentManager fm = getSupportFragmentManager();
        ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_RESPONSE_MSG);
        alertDialog.show(fm, "error_alert");
    }
}