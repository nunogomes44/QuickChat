package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class ConfirmCellActivity extends AppCompatActivity {

    private EditText InputVerificationCode;
    private Button VerifyButton;
    private ProgressDialog loadingBar;
    private String mVerificationId, password, gender, mobilePhone;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private TextView mobileNumber, changephoneNumber, resend;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_cell);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        mobileNumber = (TextView) findViewById(R.id.mobilephone);

        Bundle extra = getIntent().getExtras();
        mVerificationId = extra.getString("verificationcode");
        gender = extra.getString("gender");
        password = extra.getString("password");
        mobilePhone = extra.getString("phonenumber");

        mobileNumber.setText("+258"+mobilePhone);
        InputVerificationCode = (EditText) findViewById(R.id.verification_code_input);
        VerifyButton = (Button) findViewById(R.id.confirm_button_pass);
        loadingBar = new ProgressDialog(this);

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = InputVerificationCode.getText().toString();

                if(TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(ConfirmCellActivity.this,"Please write the code that has been sent do you",Toast.LENGTH_SHORT).show();
                }else{
                    loadingBar.setTitle("Code Verification");
                    loadingBar.setMessage("Please wait, while we are authenticating your code");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            RootRef.child("Users").child(currentUserId).child("gender").setValue(gender);
                            RootRef.child("Users").child(currentUserId).child("password").setValue(password);
                            RootRef.child("Users").child(currentUserId).child("phonenumber").setValue(mobilePhone);
                            RootRef.child("Phone").child(mobilePhone).child("phonenumber").setValue(mobilePhone);
                            RootRef.child("Phone").child(mobilePhone).child("uid").setValue(currentUserId);
                            Toast.makeText(ConfirmCellActivity.this,"Congratulation, you're successfully login", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(ConfirmCellActivity.this,"Error :"+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(ConfirmCellActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }
}
