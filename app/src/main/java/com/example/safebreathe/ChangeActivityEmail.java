package com.example.safebreathe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangeActivityEmail extends AppCompatActivity {
    private static final String TAG = "Change Email";
    public Dialog dialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView textView;
    private String currentEmail, newEmail, currentPassword;
    private EditText password, editNewEmail, oldEmail;
    private Button changeButton, verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);


        editNewEmail = findViewById(R.id.new_email_edit);
        password = findViewById(R.id.edit_current_password);
        changeButton = findViewById(R.id.change_button);
        verifyButton = findViewById(R.id.button_verify);
        oldEmail = findViewById(R.id.edit_current_mail);
        changeButton.setEnabled(false);
        editNewEmail.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        currentEmail = firebaseUser.getEmail();
//        TextView textOldEmail = findViewById(R.id.text_current_email);
        oldEmail.setText(currentEmail);
        oldEmail.setEnabled(false);

        if (firebaseUser.equals("")) {
//            Toast.makeText(AccountActivity.class, "Something went wrong! User details not availabe.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Error");
        } else {
            reAuthenticate(firebaseUser);
        }

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.update_email_layout);
        verifyButton = findViewById(R.id.button_verify);

    }



        private void reAuthenticate(FirebaseUser firebaseUser) {
            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentPassword = password.getText().toString();

                    if (TextUtils.isEmpty(currentEmail)) {
                        password.setError("Field cannot be empty");
                        password.requestFocus();
                    } else {
                        AuthCredential authCredential = EmailAuthProvider.getCredential(currentEmail, currentPassword);
                        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    editNewEmail.setEnabled(true);
                                    password.setEnabled(false);
                                    verifyButton.setEnabled(false);
                                    changeButton.setEnabled(true);

                                    changeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            newEmail = editNewEmail.getText().toString();

                                            if (TextUtils.isEmpty(newEmail)) {
                                                editNewEmail.setError("Field cannot be empty");
                                                editNewEmail.requestFocus();
                                            } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                                                editNewEmail.setError("Invalid email address");
                                                editNewEmail.requestFocus();
                                            } else if (newEmail.equals(currentEmail)) {
                                                editNewEmail.setError("Please enter new email");
                                                editNewEmail.requestFocus();
                                            } else {
                                                updateEmail(firebaseUser);
                                            }
                                        }
                                    });
                                } else {
                                    try {
                                        throw Objects.requireNonNull(task.getException());
                                    } catch (Exception e) {
                                        Log.d(TAG, e.getMessage());
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }

        private void updateEmail(FirebaseUser firebaseUser) {
            firebaseUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()) {
                        firebaseAuth.signOut();
                        firebaseUser.sendEmailVerification();
                        startActivity(new Intent(ChangeActivityEmail.this, LoginActivity.class));
                        finish();
                        Toast.makeText(ChangeActivityEmail.this, "Email changes successfully. Please verify email and continue.", Toast.LENGTH_LONG).show();

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                }
            });
        }

}