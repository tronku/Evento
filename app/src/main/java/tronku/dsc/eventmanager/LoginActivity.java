package tronku.dsc.eventmanager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import tronku.dsc.eventmanager.POJO.API;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.emailid_mobno)EditText emailId_mobnoEdit;
    @BindView(R.id.password)EditText passwordEdit;
    @BindView(R.id.login)Button loginButton;
    @BindView(R.id.signup)Button signupButton;
    @BindView(R.id.layer)View layer;
    @BindView(R.id.loader)ProgressBar loader;
    @BindView(R.id.forgotPassword) TextView forgotPassword;
    private String email_mobno, password, fcm_token;
    private View view;
    private SharedPreferences pref;
    private static final int REQUEST_CODE = 101;
    private ConnectivityReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        view = findViewById(android.R.id.content);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Snackbar snackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
        receiver = new ConnectivityReceiver();
        receiver.setSnackbar(snackbar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (receiver.isConnected()) {
                    email_mobno = emailId_mobnoEdit.getText().toString();
                    password = passwordEdit.getText().toString();

                    if(password.length()==0 && email_mobno.length()==0){
                        Snackbar snackbar = Snackbar.make(view, "Enter details!", Snackbar.LENGTH_SHORT);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        snackbar.show();
                    }
                    else if(email_mobno.length()==0){
                        Snackbar snackbar = Snackbar.make(view, "Enter Email-id or Mobile no!", Snackbar.LENGTH_SHORT);
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
                    else {
                        emailId_mobnoEdit.setText("");
                        passwordEdit.setText("");
                        emailId_mobnoEdit.setEnabled(false);
                        passwordEdit.setEnabled(false);
                        layer.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.VISIBLE);
                        loginUser(email_mobno, password);
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!pref.contains("fcm_token")){
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if(!task.isSuccessful()){
                        Log.w("FCM TOKEN", "not generated");
                    }
                    else{
                        fcm_token = task.getResult().getToken();
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("fcm_token", fcm_token);
                        editor.apply();
                    }
                }
            });
        }

        else
            fcm_token = pref.getString("fcm_token", "0");

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, EmailSendActivity.class));
            }
        });

        askPermission();
        userLoggedIn();
    }

    private void askPermission() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CODE);
        }
    }

    private void loginUser(final String email_mobno, String password) {
        RequestQueue login;
        JSONObject credentials = new JSONObject();
        try{
            credentials.put("email", email_mobno);
            credentials.put("password", password);
            credentials.put("fcm_token", fcm_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginreq = new JsonObjectRequest(Request.Method.POST, API.LOGIN_API, credentials,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("token")){
                            try {
                                String token = response.getString("token");
                                String verified = "true";
                                if(response.has("verified"))
                                    verified = response.getString("verified");
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("token", token);
                                editor.putString("email", response.getString("email"));
                                editor.putString("name", response.getString("name"));
                                editor.apply();

                                Log.d("token", token);
                                emailId_mobnoEdit.setText("");
                                passwordEdit.setText("");
                                emailId_mobnoEdit.setEnabled(true);
                                passwordEdit.setEnabled(true);
                                layer.setVisibility(View.INVISIBLE);
                                loader.setVisibility(View.INVISIBLE);

                                if(verified.equals("false")) {
                                    Toast.makeText(LoginActivity.this, "You must verify yourself!", Toast.LENGTH_SHORT).show();
                                    Intent otp = new Intent(LoginActivity.this, OTPActivity.class);
                                    otp.putExtra("email", email_mobno);
                                    startActivity(otp);
                                }
                                else {
                                    Intent main = new Intent(LoginActivity.this, MainActivity.class);
                                    main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    main.putExtra("token",token);
                                    startActivity(main);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse!=null && error.networkResponse.statusCode==404){
                    String json = new String(error.networkResponse.data);
                    try {
                        JSONObject jsonError = new JSONObject(json);

                        if(jsonError.has("error")){
                            String errorString = jsonError.get("error").toString();

                            final Dialog dialog = new Dialog(LoginActivity.this);
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
                                    emailId_mobnoEdit.setText("");
                                    passwordEdit.setText("");
                                    emailId_mobnoEdit.setEnabled(true);
                                    passwordEdit.setEnabled(true);
                                    layer.setVisibility(View.INVISIBLE);
                                    loader.setVisibility(View.INVISIBLE);
                                }
                            });

                            TextView errorView = dialog.findViewById(R.id.errorText);
                            errorView.setText(errorString);
                            dialog.show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        login = Volley.newRequestQueue(LoginActivity.this);
        login.add(loginreq);
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

    public void userLoggedIn(){
        if(pref.contains("token")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LoginActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

}
