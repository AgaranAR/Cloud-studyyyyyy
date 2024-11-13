package com.example.clo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {



    private FirebaseAuth auth;

    CheckBox showcheck_btn_login,remember;
    EditText login_email, login_password;
    Button loginButton, signupRedirectBtn;

    TextView forgot;

    public static final String SHARED_PREFS = "sharedPrefs";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_xml);


        auth = FirebaseAuth.getInstance();

        checkBoxRemember();

        login_email = findViewById(R.id.emailBoxlogin);
        login_password = findViewById(R.id.passwordBoxlogin);
        loginButton = findViewById(R.id.loginbtn);
        signupRedirectBtn = findViewById(R.id.createBtnlogin);
        remember = findViewById(R.id.checkBoxRememberMe);
        showcheck_btn_login = findViewById(R.id.checkbox_btn_login);
        forgot = findViewById(R.id.forgotPassword);





        showcheck_btn_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }

        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginEmail = login_email.getText().toString();
                String loginPassword = login_password.getText().toString();

                if (!loginEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()){
                    if (!loginPassword.isEmpty()){
                        auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        editor.putString("name","true");
                                        editor.apply();

                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        login_password.setError("Password cannot be empty");
                    }
                }else if(loginEmail.isEmpty()){
                    login_email.setError("Email cannot be empty");
                }else{
                    login_email.setError("Please enter valid email");
                }
            }
        });

        signupRedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot,null);
                EditText emailForgot = dialogView.findViewById(R.id.emailForgotBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String user_forgot_email = emailForgot.getText().toString();

                        if (TextUtils.isEmpty(user_forgot_email) && !Patterns.EMAIL_ADDRESS.matcher(user_forgot_email).matches()){
                            Toast.makeText(LoginActivity.this, "Enter you registered mail address", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        auth.sendPasswordResetEmail(user_forgot_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Please check your mail", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }else{
                                    Toast.makeText(LoginActivity.this, "Unable to send, reset failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if(dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

    }
    private void checkBoxRemember() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String check = sharedPreferences.getString("name","");
        if (check.equals("true")){
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }

}