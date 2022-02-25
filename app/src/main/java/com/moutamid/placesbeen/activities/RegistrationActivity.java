package com.moutamid.placesbeen.activities;

import static com.moutamid.placesbeen.utils.Utils.toast;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.moutamid.placesbeen.activities.main.MainActivity;
import com.moutamid.placesbeen.databinding.ActivityRegistrationBinding;
import com.moutamid.placesbeen.utils.Constants;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding b;

    private static final String TAG = "RegistrationActivity";
    private Context context = RegistrationActivity.this;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        b = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        b.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkEditText())
                    return;

                String emailStr = b.emailEditText.getText().toString();
                String passwordStr = b.passwordEditText.getText().toString();

                loginUser(emailStr, passwordStr);

            }
        });

        b.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkEditText())
                    return;

                String emailStr = b.emailEditText.getText().toString();
                String passwordStr = b.passwordEditText.getText().toString();

                registerUser(emailStr, passwordStr);
            }
        });

    }

    private void registerUser(String emailStr, String passwordStr) {
        progressDialog.show();

        Constants.auth().createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if (task.isSuccessful()) {

//                    toast("Success");
                    Stash.put(Constants.IS_LOGGED_IN, true);

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);

                } else {

                    toast(task.getException().getMessage());
                    Log.d(TAG, "onComplete: error " + task.getException().getMessage());

                }
            }
        });
    }

    private void loginUser(String emailStr, String passwordStr) {
        progressDialog.show();

        Constants.auth().signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if (task.isSuccessful()) {

//                    toast("Success");
                    Stash.put(Constants.IS_LOGGED_IN, true);

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);

                } else {

                    toast(task.getException().getMessage());
                    Log.d(TAG, "onComplete: error " + task.getException().getMessage());

                }
            }
        });

    }


    private boolean checkEditText() {
        if (b.emailEditText.getText().toString().isEmpty()) {
            toast("Please enter email!");
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(b.emailEditText.getText().toString()).matches()) {
            toast("Email is invalid!");
            return true;
        }

        if (b.passwordEditText.getText().toString().isEmpty()) {
            toast("Please enter password!");
            return true;
        }

        return false;
    }

}