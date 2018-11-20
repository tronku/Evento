package tronku.dsc.eventmanager;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import tronku.dsc.eventmanager.POJO.API;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.otpView)
    PinView otpView;
    @BindView(R.id.password)
    EditText passwordEditText;
    @BindView(R.id.resend)
    Button resendOtp;
    @BindView(R.id.changePassword)
    Button changePassword;
    
    private String otp, password;
    private View view;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);
        view = findViewById(android.R.id.content);
        
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = otpView.getText().toString();
                password = passwordEditText.getText().toString();

                if(otp.length()==0 && password.length()==0){
                    Snackbar snackbar = Snackbar.make(view, "Enter details!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
                else if(otp.length()==0) {
                    Snackbar snackbar = Snackbar.make(view, "Enter OTP!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
                else if(password.length()==0) {
                    Snackbar snackbar = Snackbar.make(view, "Enter password!", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    snackbar.show();
                }
                else
                    change();
                    
            }
        });
    }

    private void change() {
        JSONObject credentials = new JSONObject();
        try{
            credentials.put("email", getIntent().getStringExtra("email"));
            credentials.put("otp", otp);
            credentials.put("password", password);

        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest change = new JsonObjectRequest(Request.Method.POST, API.FORGOT_PASSWORD_API, credentials,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ForgotPasswordActivity.this, "Password changed!", Toast.LENGTH_SHORT).show();
                        Intent login = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode==400){
                    String json = new String(error.networkResponse.data);
                    try {
                        JSONObject errorJson = new JSONObject(json);
                        if(errorJson.has("error")){
                            String errorMsg = errorJson.getString("error");
                            Snackbar snackbar = Snackbar.make(view, errorMsg, Snackbar.LENGTH_INDEFINITE);
                            View snackbarView = snackbar.getView();
                            snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            snackbar.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
        queue.add(change);
    }
}
