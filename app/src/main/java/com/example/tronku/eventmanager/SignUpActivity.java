package com.example.tronku.eventmanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tronku.eventmanager.POJO.API;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.emailid)EditText emailIdEdit;
    @BindView(R.id.password)EditText passwordEdit;
    @BindView(R.id.name)EditText nameEdit;
    @BindView(R.id.mobileNo)EditText mobileNoEdit;
    @BindView(R.id.signupButton)Button signupButton;
    @BindView(R.id.loader)ProgressBar loader;
    @BindView(R.id.layer)View layer;

    private String email, password, name, mobile, fcm_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailIdEdit.getText().toString();
                mobile = mobileNoEdit.getText().toString();
                password = passwordEdit.getText().toString();
                name = nameEdit.getText().toString();

                if(password.length()==0 && email.length()==0 && name.length()==0 && mobile.length()==0){
                    Snackbar snackbar = Snackbar.make(view, "Enter details!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
                else if(email.length()==0){
                    Snackbar snackbar = Snackbar.make(view, "Enter Email-id!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Snackbar snackbar = Snackbar.make(view, "Enter correct email-id!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
                else if(password.length()==0){
                    Snackbar snackbar = Snackbar.make(view, "Enter Password!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
                else if(name.length()==0){
                    Snackbar snackbar = Snackbar.make(view, "Enter Name!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
                else if(mobile.length()==0){
                    Snackbar snackbar = Snackbar.make(view, "Enter Mobile number!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
                else if(!Patterns.PHONE.matcher(mobile).matches()){
                    Snackbar snackbar = Snackbar.make(view, "Enter valid mobile number!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
                else {
                    emailIdEdit.setText("");
                    passwordEdit.setText("");
                    nameEdit.setText("");
                    mobileNoEdit.setText("");
                    nameEdit.setEnabled(false);
                    emailIdEdit.setEnabled(false);
                    passwordEdit.setEnabled(false);
                    mobileNoEdit.setEnabled(false);
                    layer.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.VISIBLE);
                    signUpData(name, email, mobile, password);
                }
            }
        });

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!pref.contains("fcm_token")){
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if(!task.isSuccessful()){
                        Log.w("FCM TOKEN", "not generated");
                    }
                    else{
                        fcm_token = task.getResult().getToken();
                    }
                }
            });
        }

        else
            fcm_token = pref.getString("fcm_token", "0");
    }

    public void signUpData(final String name, final String email, final String mobile, final String password){

        JSONObject object = new JSONObject();
        try {
            object.put("name", name);
            object.put("email", email);
            object.put("phone", mobile);
            object.put("password", password);
            object.put("fcm_token", fcm_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, API.SIGNUP_API, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        nameEdit.setEnabled(true);
                        emailIdEdit.setEnabled(true);
                        passwordEdit.setEnabled(true);
                        mobileNoEdit.setEnabled(true);
                        Log.d("response",response.toString());
                        layer.setVisibility(View.INVISIBLE);
                        loader.setVisibility(View.INVISIBLE);

                        Intent otp = new Intent(SignUpActivity.this, OTPActivity.class);
                        otp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        otp.putExtra("email", email);
                        startActivity(otp);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("error",volleyError.toString());
                if(volleyError.networkResponse.statusCode==400){
                    String json = new String(volleyError.networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(json);
                        String error="";
                        if(response.has("email")){
                            JSONArray emailArray = response.getJSONArray("email");
                            String emailError = emailArray.get(0).toString().replaceAll("[\"]","");
                            error = error + emailError;
                        }
                        if(response.has("phone")){
                            JSONArray phoneArray = response.getJSONArray("phone");
                            String phoneError = phoneArray.get(0).toString().replaceAll("[\"]","");
                            error = error + "\n" + phoneError;
                        }
                        if(response.has("password")){
                            JSONArray passwordArray = response.getJSONArray("password");
                            String passwordError = passwordArray.get(0).toString().replaceAll("[\"]","");
                            error = error + "\n" + passwordError;
                        }

                        final Dialog dialog = new Dialog(SignUpActivity.this);
                        dialog.setContentView(R.layout.dialog_layout);
                        ImageView close = dialog.findViewById(R.id.close);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                nameEdit.setEnabled(true);
                                emailIdEdit.setEnabled(true);
                                passwordEdit.setEnabled(true);
                                mobileNoEdit.setEnabled(true);
                                layer.setVisibility(View.INVISIBLE);
                                loader.setVisibility(View.INVISIBLE);
                            }
                        });
                        TextView errorView = dialog.findViewById(R.id.errorText);
                        errorView.setText(error);
                        dialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        objectRequest.setTag("auth");
        RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
        queue.add(objectRequest);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
