package com.moutamid.placesbeen.activities.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.moutamid.placesbeen.R;
import com.moutamid.placesbeen.utils.Constants;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        findViewById(R.id.backbtn_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.send_btn_activity_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.email_edittext_forgot_password);

                String email = editText.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    editText.setError("Email is empty!");
                    editText.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //if Email Address is Invalid..
                    editText.setError("Email is invalid!");
                    editText.requestFocus();
                    return;
                }

                Constants.auth().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        editText.setText("");
                        Toast.makeText(ForgotPasswordActivity.this, "Email is sent. Please check your inbox!", Toast.LENGTH_LONG).show();
                        findViewById(R.id.send_btn_activity_forgot_password).setEnabled(false);

                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}