package tronku.dsc.eventmanager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    @BindView(R.id.resend_otp)
    TextView resendOtp;
    @BindView(R.id.changePassword)
    Button changePassword;
    @BindView(R.id.layer) View layer;
    @BindView(R.id.loader)
    ProgressBar loader;
    
    private String otp, password;
    private View view;
    private ConnectivityReceiver receiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);
        view = findViewById(android.R.id.content);

        Snackbar snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
        receiver = new ConnectivityReceiver();
        receiver.setSnackbar(snackbar);
        
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiver.isConnected()) {
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
                else {
                    Toast.makeText(ForgotPasswordActivity.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
                    
            }
        });
    }

    private void change() {
        layer.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
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

        EventoApplication.getInstance().addToRequestQueue(change);
        EventoApplication.getInstance().getRequestQueue().addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONObject>() {

            @Override
            public void onRequestFinished(Request<JSONObject> request) {
                layer.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
