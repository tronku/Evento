package tronku.dsc.eventmanager;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import tronku.dsc.eventmanager.POJO.API;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmailSendActivity extends AppCompatActivity {

    @BindView(R.id.sendEmail)
    Button sendEmail;
    @BindView(R.id.emailId)
    EditText emailIdEditText;

    private View view;
    private ConnectivityReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_send);

        ButterKnife.bind(this);
        view = findViewById(android.R.id.content);
        receiver = new ConnectivityReceiver(view);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiver.isConnected()) {
                    String email = emailIdEditText.getText().toString();
                    if(email.length()==0) {
                        Snackbar snackbar = Snackbar.make(view, "Enter email-id!", Snackbar.LENGTH_SHORT);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        snackbar.show();
                    }
                    else
                        sendData(email);
                }
                else
                    Toast.makeText(EmailSendActivity.this, "No internet!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendData(final String email) {
        JSONObject params = new JSONObject();
        try{
            params.put("email", email);
            params.put("reset", true);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest otpGen = new JsonObjectRequest(Request.Method.POST, API.RESEND_API, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent forgot = new Intent(EmailSendActivity.this, ForgotPasswordActivity.class);
                        forgot.putExtra("email", email);
                        startActivity(forgot);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue queue = Volley.newRequestQueue(EmailSendActivity.this);
        queue.add(otpGen);
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
