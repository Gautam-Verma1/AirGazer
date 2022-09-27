package com.example.safebreathe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText editOldPassword, editNewPassword, editReenterPassword;
    private Button verifyButton, changeButton;
    private FirebaseAuth firebaseAuth;
    String oldPassword, newPassword, reenterPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editOldPassword = findViewById(R.id.edit_current_password_cp);
        editNewPassword = findViewById(R.id.new_password_edit_cp);
        editReenterPassword = findViewById(R.id.new_reenter_edit);
        verifyButton = findViewById(R.id.button_verify_cp);
        changeButton = findViewById(R.id.change_button_cp);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        editNewPassword.setEnabled(false);
        editReenterPassword.setEnabled(false);
        changeButton.setEnabled(false);

        if(firebaseUser == null) {
            Toast.makeText(ChangePasswordActivity.this, "Somethig went wrong! Cannot fetch user details.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(ChangePasswordActivity.this, AccountActivity.class));
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldPassword = editOldPassword.getText().toString();

                if (TextUtils.isEmpty(oldPassword)) {
                    editOldPassword.setError("Field cannot be empty");
                    editOldPassword.requestFocus();
                } else {
                    AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), oldPassword);
                    firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                editOldPassword.setEnabled(false);
                                editNewPassword.setEnabled(true);
                                editReenterPassword.setEnabled(true);

                                verifyButton.setEnabled(false);
                                changeButton.setEnabled(true);

                                changeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePassword(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });
                }
            }
        });
    }

    private void changePassword(FirebaseUser firebaseUser) {
        newPassword = editNewPassword.getText().toString();
        reenterPassword = editReenterPassword.getText().toString();

        if(TextUtils.isEmpty(newPassword)) {
            editNewPassword.setError("Field cannot be empty");
            editNewPassword.requestFocus();
        } else if (TextUtils.isEmpty(reenterPassword)) {
            editReenterPassword.setError("Field cannot be empty");
            editReenterPassword.requestFocus();
        } else if(!newPassword.equals(reenterPassword)) {
            editReenterPassword.setError("Password did not match");
            editReenterPassword.requestFocus();
        } else if(!newPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")) {
            editReenterPassword.setError("\"1. Password must contain at least one digit [0-9].\\n\" +\n" +
                    "                            \"2. Password must contain at least one lowercase character [a-z].\\n\" +\n" +
                    "                            \"3. Password must contain at least one uppercase character [A-Z].\\n\" +\n" +
                    "                            \"4. Password must contain at least one special character !,@,#,&,(,).\\n\" +\n" +
                    "                            \"5. Password length must be between 8 and 20 characters.\"");
            editReenterPassword.requestFocus();
        } else {
            firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "Password updated", Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }
}