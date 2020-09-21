package com.teammandroid.chatnow.activities.firebase;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.teammandroid.chatnow.R;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener{


    TextInputEditText et_password_reset_email;
    private Button btn_resetPassword, btnBack;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        bindView();


        auth = FirebaseAuth.getInstance();

        btnListener();



    }

    private void btnListener() {
        btn_resetPassword.setOnClickListener(this);
    }

    private void bindView() {
        et_password_reset_email = (TextInputEditText) findViewById(R.id.et_password_reset_email);

        btn_resetPassword = (Button) findViewById(R.id.btn_resetPassword);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_resetPassword:

                String email = et_password_reset_email.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    resetPassword(email);
                }

                break;

            default:
                break;
        }
    }

    private void resetPassword(String email) {

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }

                        //progressBar.setVisibility(View.GONE);
                    }
                });
    }
}