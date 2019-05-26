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

public class ForgotActivity extends AppCompatActivity {

    private EditText mobilePhone;
    private ProgressDialog loadingBar;
    private String mVerificationId;
    private DatabaseReference RootRef;
    private boolean verify = false;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Button next;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);

        mobilePhone = (EditText) findViewById(R.id.field_number_recover);
        next = (Button) findViewById(R.id.button_next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mobilePhone.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(ForgotActivity.this, "Please enter your phone number...", Toast.LENGTH_SHORT).show();
                } else {
                    if(verifyUserInfo()){
                        phone = "+258"+mobilePhone.getText().toString().trim();
                        loadingBar.setTitle("Sign In");
                        loadingBar.setMessage("Please wait...");
                        loadingBar.setCanceledOnTouchOutside(true);
                        loadingBar.show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phone,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                ForgotActivity.this,               // Activity (for callback binding)
                                callBacks);
                    }
            }
        }
    });

        callBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(ForgotActivity.this,"Invalid Phone Number, Please enter correct phone number with your country code", Toast.LENGTH_SHORT).show();
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();
                Toast.makeText(ForgotActivity.this,"Code has been sent successfully",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ForgotActivity.this, EnterConfirmationCodeActivity.class);
                intent.putExtra("verificationcode", mVerificationId);
                intent.putExtra("mobilephone", phone);
                startActivity(intent);
            }
        };
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public void abrirLogin(View view){
        Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
                .makeCustomAnimation(getApplicationContext(), R.anim.zoom_in, R.anim.zoom_out);
        ActivityCompat.startActivity(ForgotActivity.this, intent,  activityOptionsCompat.toBundle());
        //startActivity(intent);
    }

    private boolean verifyUserInfo(){
        final String phonenumber = mobilePhone.getText().toString();
        RootRef.child("Phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(phonenumber).exists()){
                    verify = true;
                }else{
                    Toast.makeText(ForgotActivity.this, "User doesn't exist, Please create a new account", Toast.LENGTH_SHORT).show();
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(ForgotActivity.this,"Congratulation, you're successfully login", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(ForgotActivity.this,"Error :"+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
