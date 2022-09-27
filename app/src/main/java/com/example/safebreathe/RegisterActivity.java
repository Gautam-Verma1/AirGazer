package com.example.safebreathe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText editUsername, editEmail, editPhone, editPassword, editConfirmPassword;
    ProgressBar progressBar;
    Button register;
    LinearLayout signUPGmail;
    private static String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editUsername = findViewById(R.id.username);
        editEmail = findViewById(R.id.reemail);
        editPhone = findViewById(R.id.phone);
        editPassword = findViewById(R.id.register_password);
        editConfirmPassword = findViewById(R.id.repassword);
        progressBar = findViewById(R.id.progress_bar);
        register = findViewById(R.id.register_button);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editUsername.getText().toString();
                String email = editEmail.getText().toString();
                String phone = editPhone.getText().toString();
                String password = editPassword.getText().toString();
                String confirmPassword = editConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Name", Toast.LENGTH_LONG).show();
                    editUsername.setError("Full Name required");
                    editUsername.requestFocus();
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Email", Toast.LENGTH_LONG).show();
                    editEmail.setError("Email required");
                    editEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Valid Email", Toast.LENGTH_LONG).show();
                    editEmail.setError("Invalid Email Address");
                    editEmail.requestFocus();
                } else if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Phone Number", Toast.LENGTH_LONG).show();
                    editPhone.setError("Phone Number required");
                    editPhone.requestFocus();
                } else if (!phone.matches("[6-9][0-9]{9}")) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Valid Phone Number", Toast.LENGTH_LONG).show();
                    editPhone.setError("Invalid Phone Number");
                    editPhone.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
                    editPassword.setError("Password required");
                    editPassword.requestFocus();
                } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")) {
                    Toast.makeText(RegisterActivity.this, "Password too weak", Toast.LENGTH_LONG).show();
                    editPassword.setError("1. Password must contain at least one digit [0-9].\n" +
                            "2. Password must contain at least one lowercase character [a-z].\n" +
                            "3. Password must contain at least one uppercase character [A-Z].\n" +
                            "4. Password must contain at least one special character !,@,#,&,(,).\n" +
                            "5. Password length must be between 8 and 20 characters.");
                    editPassword.requestFocus();
                } else if (TextUtils.isEmpty(confirmPassword) || !confirmPassword.equals(password)) {
                    Toast.makeText(RegisterActivity.this, "Password did not match", Toast.LENGTH_LONG).show();
                    editConfirmPassword.setError("Password did not match");
                    editConfirmPassword.requestFocus();
                    editPassword.clearComposingText();
                    editConfirmPassword.clearComposingText();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    RegisterUser(username, email, phone, password);
                }
            }
        });
    }

    private void RegisterUser(String username, String email, String phone, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            UserDetails userDetails = new UserDetails(username, phone);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");

//                            assert firebaseUser != null;
                            databaseReference.child(firebaseUser.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "User Registered Successfully. Please Verify your mail.", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "User Registration Unsuccessful. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


}