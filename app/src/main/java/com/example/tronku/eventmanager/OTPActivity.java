package com.example.tronku.eventmanager;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chaos.view.PinView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OTPActivity extends AppCompatActivity {

    @BindView(R.id.validate)Button validate;
    @BindView(R.id.otpView)PinView otpEditView;

    private String otp;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        view = findViewById(android.R.id.content);
        ButterKnife.bind(this);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }

    private void validate(String otp) {
        //validation
        if(otp.equals("111111")){
            Snackbar snackbar = Snackbar.make(view, "Verification Successful!", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
            snackbar.show();
        }
        else{
            Snackbar snackbar = Snackbar.make(view, "Wrong OTP!", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            snackbar.show();
        }
    }
}
