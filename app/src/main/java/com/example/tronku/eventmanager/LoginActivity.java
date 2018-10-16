package com.example.tronku.eventmanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.emailid_mobno)EditText emailId_mobnoEdit;
    @BindView(R.id.password)EditText passwordEdit;
    @BindView(R.id.login)Button loginButton;
    @BindView(R.id.signup)Button signupButton;
    @BindView(R.id.layer)View layer;
    @BindView(R.id.loader)ProgressBar loader;

    private String email_mobno, password;
    private View view;
    private String loginApi = "http://13.126.64.67/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        view = findViewById(android.R.id.content);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    private void loginUser(String email_mobno, String password) {
        RequestQueue login;
        JSONObject credentials = new JSONObject();
        try{
            credentials.put("email", email_mobno);
            credentials.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginreq = new JsonObjectRequest(Request.Method.POST, loginApi, credentials,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("token")){
                            try {
                                String token = response.getString("token");
                                Log.d("token", token);
                                emailId_mobnoEdit.setText("");
                                passwordEdit.setText("");
                                emailId_mobnoEdit.setEnabled(true);
                                passwordEdit.setEnabled(true);
                                layer.setVisibility(View.INVISIBLE);
                                loader.setVisibility(View.INVISIBLE);

                                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                main.putExtra("token",token);
                                startActivity(main);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode==404){
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
        try {
            if(!isConnected()){
                Snackbar snackbar = Snackbar.make(view, "Enter Password!", Snackbar.LENGTH_SHORT);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                snackbar.show();
                loginButton.setEnabled(false);
            }
            else
                loginButton.setEnabled(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
    }
}
