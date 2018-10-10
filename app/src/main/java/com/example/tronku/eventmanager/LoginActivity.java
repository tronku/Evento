package com.example.tronku.eventmanager;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

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

    private void loginUser(String email, String password) {
        Intent main = new Intent(this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
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
