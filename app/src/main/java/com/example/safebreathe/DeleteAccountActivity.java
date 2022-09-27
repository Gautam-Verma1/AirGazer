package com.example.safebreathe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class DeleteAccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String password;
    private Button deleteButton, verifyButton;
    private EditText editPassword;
    private static final String TAG = "DeleteAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        editPassword = findViewById(R.id.edit_password_delete);
        verifyButton = findViewById(R.id.button_verify_delete);
        deleteButton = findViewById(R.id.button_delete);

        deleteButton.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser == null) {
            Toast.makeText(DeleteAccountActivity.this, "Something went wrong! Cannot load user data", Toast.LENGTH_LONG).show();
            startActivity(new Intent(DeleteAccountActivity.this, AccountActivity.class));
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }


    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = editPassword.getText().toString();

                if (TextUtils.isEmpty(password)) {
                    editPassword.setError("Field cannot be empty");
                    editPassword.requestFocus();
                } else {
                    AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), password);
                    firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                editPassword.setEnabled(false);
                                verifyButton.setEnabled(false);
                                deleteButton.setEnabled(true);

                                deleteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialogueBox();
                                    }
                                });
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });
                }
            }
        });
    }

    private void alertDialogueBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAccountActivity.this);
        builder.setMessage("Delete Account Permanently? This action is irreversible.");
        builder.setTitle("Delete User ?");
        builder.setCancelable(false);

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(firebaseUser);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(DeleteAccountActivity.this, AccountActivity.class));
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });

        alertDialog.show();
    }

    private void deleteUser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    deleteUserData();
                    firebaseAuth.signOut();
                    Toast.makeText(DeleteAccountActivity.this, "User deleted successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(DeleteAccountActivity.this, LoginActivity.class));
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void deleteUserData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "USer Data Deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }
}