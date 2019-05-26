package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccountLink,ForgetPasswordLink;
    private boolean verify;

    private ProgressDialog loadingBar;
    private String mVerificationId;
    private DatabaseReference RootRef;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        initialize();

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
            }
        });

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber_check = UserEmail.getText().toString().trim();
                if(TextUtils.isEmpty(phoneNumber_check)){
                    Toast.makeText(LoginActivity.this,"Please enter your phone number or Email...", Toast.LENGTH_SHORT).show();
                }else{
                if(verificaUsername()){
                    if(verifyUserInfo()){
                    String phoneNumber = "+258"+UserEmail.getText().toString().trim();
                    telefone = phoneNumber;
                    String password = UserPassword.getText().toString();

                    if(TextUtils.isEmpty(password)){
                        Toast.makeText(LoginActivity.this,"Please enter Password...", Toast.LENGTH_SHORT).show();
                    }else {
                        loadingBar.setTitle("Sign In");
                        loadingBar.setMessage("Please wait...");
                        loadingBar.setCanceledOnTouchOutside(true);
                        loadingBar.show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                LoginActivity.this,               // Activity (for callback binding)
                                callBacks);
                    }}
                }
                else{
                AllowUserToLogin();
            }}}
        });

        callBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this,"Invalid Phone Number, Please enter correct phone number", Toast.LENGTH_SHORT).show();
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later

                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, "Code has been sent successfully",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, ConfirmCellLoginActivity.class);
                intent.putExtra("mobilephone", telefone);
                intent.putExtra("verificationcode", mVerificationId);
                startActivity(intent);
            }
        };
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public void forgot(View view){
        Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
                .makeCustomAnimation(getApplicationContext(), R.anim.zoom_in, R.anim.zoom_out);
        ActivityCompat.startActivity(LoginActivity.this, intent,  activityOptionsCompat.toBundle());
    }

    private boolean verifyUserInfo(){
        final String phonenumber = UserEmail.getText().toString();
        RootRef.child("Phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(phonenumber).exists()){
                    String currentUserID = dataSnapshot.child(phonenumber).child("uid").getValue().toString();
                    RootRef.child("Users").child(currentUserID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child("password").exists() && dataSnapshot.child("phonenumber").exists()){

                                        String password = dataSnapshot.child("password").getValue().toString();

                                        if(password.equals(UserPassword.getText().toString())){
                                            verify = true;
                                        }else{
                                            //Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }else{
                    Toast.makeText(LoginActivity.this, "User doesn't exist, Please create a new account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(verify){
            return true;
        }
        return false;
    }

    private boolean verificaUsername(){
        try{
            Integer number = Integer.valueOf(UserEmail.getText().toString().trim());
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    private void AllowUserToLogin() {

        String email = UserEmail.getText().toString().trim();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter Password...", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this,"Logged in Successful ...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }else{
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this,"Error : "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
    }

    private void initialize() {
        LoginButton = (Button) findViewById(R.id.login_button);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        NeedNewAccountLink = (TextView) findViewById(R.id.need_new_account_link);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,"Congratulation, you're successfully login", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(LoginActivity.this,"Error :"+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}