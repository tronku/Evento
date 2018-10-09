package com.example.tronku.eventmanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

    private String email, password, name, mobile;

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
                    Toast.makeText(SignUpActivity.this, "Sending", Toast.LENGTH_SHORT).show();
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
                    new JsonAsyncTask().execute("http://eventoapi.dscjss.in/api/v1/signup");
                }
            }
        });
    }

    public class JsonAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("response","WORKING");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("response","NOT WORKING");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json; charset=UTF-8");
                    return headers;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
            queue.add(request);
            queue.start();
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("Done"))
                startActivity(new Intent(SignUpActivity.this, OTPActivity.class));
        }
    }
}
