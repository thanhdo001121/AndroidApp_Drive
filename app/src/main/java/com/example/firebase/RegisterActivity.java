package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase.mainpage.MainpageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText emailId, password,conformPassword, Phone;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");

    String User_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.edtUsername);
        password = findViewById(R.id.edtPassword);
        Phone = findViewById(R.id.edtPhone);
        conformPassword = findViewById(R.id.et_confirm_password);
        btnSignUp = findViewById(R.id.btnLogin);
        tvSignIn = findViewById(R.id.login);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                String phone = Phone.getText().toString();
                String cfPassword =  conformPassword.getText().toString();


                if (email.isEmpty()) {
                    emailId.setError("Please enter email ");
                    emailId.requestFocus();
                }
                else if (pwd.isEmpty()) {
                    password.setError("Please enter password ");
                    password.requestFocus();
                }
                else if (cfPassword.isEmpty()){
                    conformPassword.setError("Please conform your password");
                }
                else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Fiels Are Empty !", Toast.LENGTH_SHORT).show();
                }
                else if (!cfPassword.equals(pwd)){
                    conformPassword.setError("Not match your password");
                }
                else if (!(email.isEmpty() && pwd.isEmpty()) && cfPassword.equals(pwd)) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Unsuccessful "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                RegisterUser();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Error Occurd!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    public void RegisterUser(){
        String email = emailId.getText().toString();
        String pwd = password.getText().toString();
        String phone = Phone.getText().toString();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String currentUserId = mFirebaseUser.getUid();
        HashMap<String, String> user = new HashMap<String, String>();
        user.put("Email",email);
        user.put("Phone",phone);


        root.child(currentUserId).child("Info").setValue(user);
        Intent inToHome = new Intent(RegisterActivity.this, MainpageActivity.class);
//        inToHome.putExtra("PhoneNum",phone);
        startActivity(inToHome);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}