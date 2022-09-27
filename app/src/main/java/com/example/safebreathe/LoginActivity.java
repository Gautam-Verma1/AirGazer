package com.example.safebreathe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editLoginEmail, editLoginPassword;
    private TextView forgotPassword, newAccount;
    private LinearLayout googleSignIn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private static final String TAG = "LoginActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActivityCompat.requestPermissions(LoginActivity.this, new String[] {
                Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        googleSignIn = findViewById(R.id.sign_in_with_google);
        newAccount = findViewById(R.id.create_new_account);
        forgotPassword = findViewById(R.id.forgot_password);
        editLoginEmail = findViewById(R.id.email);
        editLoginPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.loading);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginEmail = editLoginEmail.getText().toString();
                String loginPassword = editLoginPassword.getText().toString();

                if (TextUtils.isEmpty(loginEmail)) {
                    Toast.makeText(LoginActivity.this, "Please Enter Your Email", Toast.LENGTH_LONG).show();
                    editLoginEmail.setError("Email required");
                    editLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "Please Enter Valid Email", Toast.LENGTH_LONG).show();
                    editLoginEmail.setError("Invalid Email Address");
                    editLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(loginPassword)) {
                    Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
                    editLoginPassword.setError("Password required");
                    editLoginPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(loginEmail, loginPassword);
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating alert Dialog with one Button
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);

                //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Reset Password");

                // Setting Dialog Message
                alertDialog.setMessage("Enter Email");
                final EditText input = new EditText(LoginActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                //alertDialog.setView(input);

                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.key);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("CONTINUE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String email = input.getText().toString();
                                if (TextUtils.isEmpty(email)) {
                                    Toast.makeText(LoginActivity.this, "Please Enter Your Email", Toast.LENGTH_LONG).show();
                                    input.setError("Email required");
                                    input.requestFocus();
                                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    Toast.makeText(LoginActivity.this, "Please Enter Valid Email", Toast.LENGTH_LONG).show();
                                    input.setError("Invalid Email Address");
                                    input.requestFocus();
                                }
                                else {
                                    progressBar.setVisibility(View.VISIBLE);
                                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Password Reset Link sent to your email", Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            } else {
                                                try {
                                                    throw task.getException();
                                                } catch(Exception e) {
                                                    Log.e(TAG, e.getMessage());
                                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        });


                // Showing Alert Message
                alertDialog.show();
            }
        });

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = gsc.getSignInIntent();
                startActivityForResult(signInIntent, 1000);
            }
        });


        editLoginPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (editLoginPassword.getRight() - editLoginPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(editLoginPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                            editLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        else {
                            editLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void loginUser(String loginEmail, String loginPassword) {
        firebaseAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    if(firebaseUser.isEmailVerified()) {
                        startActivity(new Intent(LoginActivity.this, AirQualityData.class));
                        finish();
                    } else {
                        firebaseUser.sendEmailVerification();
                        firebaseAuth.signOut();
                        ShowAlertDialogBox();
                    }
                } else {
                    try {
                        task.getException();
                    } catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void ShowAlertDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email.\nEmail verification required.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, AirQualityData.class));
            finish();
        }
    }
}