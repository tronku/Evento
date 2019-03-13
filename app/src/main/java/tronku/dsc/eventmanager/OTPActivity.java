package tronku.dsc.eventmanager;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class OTPActivity extends AppCompatActivity {

    @BindView(R.id.validate)Button validate;
    @BindView(R.id.otpView)PinView otpEditView;
    @BindView(R.id.resend) TextView resendButton;
    @BindView(R.id.layer) View layer;
    @BindView(R.id.loader)
    ProgressBar loader;

    private String otp;
    private View view;
    private ConnectivityReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        view = findViewById(android.R.id.content);
        ButterKnife.bind(this);

        Snackbar snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
        receiver = new ConnectivityReceiver();
        receiver.setSnackbar(snackbar);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (receiver.isConnected()) {
                    otp = otpEditView.getText().toString();
                    if(otp.length()==0){
                        Snackbar snackbar = Snackbar.make(view, "Enter OTP!", Snackbar.LENGTH_SHORT);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        snackbar.show();
                    }
                    else
                        validate(otp);
                }
                else
                    Toast.makeText(OTPActivity.this, "No internet!", Toast.LENGTH_SHORT).show();
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (receiver.isConnected())
                    resend();
                else
                    Toast.makeText(OTPActivity.this, "No internet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resend() {
        RequestQueue queue;
        JSONObject otpResend = new JSONObject();
        try{
            otpResend.put("email", getIntent().getStringExtra("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API.RESEND_API, otpResend,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //setting timer
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue = Volley.newRequestQueue(OTPActivity.this);
        queue.add(request);
    }

    private void validate(String otp) {
        layer.setVisibility(View.VISIBLE);
        loader.setVisibility(View.INVISIBLE);

        //validation
        JSONObject otpValid = new JSONObject();
        try{
            otpValid.put("otp", otp);
            otpValid.put("email", getIntent().getStringExtra("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API.OTP_API, otpValid,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            String email = response.getString("email");
                            String name = response.getString("name");
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("token", token);
                            editor.putString("email", email);
                            editor.putString("name", name);
                            editor.apply();
                            startActivity(new Intent(OTPActivity.this, MainActivity.class));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        EventoApplication.getInstance().addToRequestQueue(request);
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
