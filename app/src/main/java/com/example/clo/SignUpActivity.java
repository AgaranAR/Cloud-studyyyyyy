package com.example.clo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    CheckBox showcheck_btn_signup;
    EditText signUpName,signUpEmail,signupPassword;
    Button createAccountSignUpBtn,alreadyAccountSignUpBtn;
    FirebaseDatabase database;
    DatabaseReference reference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        auth = FirebaseAuth.getInstance();
        signUpName = findViewById(R.id.nameSignUp);
        signUpEmail = findViewById(R.id.emailSignup);
        signupPassword = findViewById(R.id.passwordSignUp);
        createAccountSignUpBtn = findViewById(R.id.createSignUp);
        alreadyAccountSignUpBtn = findViewById(R.id.alreadyBtnSignUp);
        showcheck_btn_signup = findViewById(R.id.checkbox_btn_signup);

        showcheck_btn_signup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }

        });

        createAccountSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String signup_name = signUpName.getText().toString();
                String signup_email = signUpEmail.getText().toString();
                String signup_password = signupPassword.getText().toString();

                if (signup_email.isEmpty()){
                    signUpEmail.setError("Email cannot be empty");
                }
                if (signup_password.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                }
                if(signup_name.isEmpty()){
                    signupPassword.setError("Name cannot be empty");
                }else {

                    auth.createUserWithEmailAndPassword(signup_email, signup_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                HelperClass helperClass = new HelperClass(signup_name, signup_email, signup_password);
                                reference.child(signup_name).setValue(helperClass);
                                Toast.makeText(SignUpActivity.this, "You have signed up successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(SignUpActivity.this, "SignUp failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                               Log.d("TAG", "onComplete: " + task.getException().getMessage());
                            }
                        }
                    });
                }

            }

        });

        alreadyAccountSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}