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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterConfirmationCodeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText InputVerificationCode;
    private Button VerifyButton, confirmpassowrd;
    private ProgressDialog loadingBar;
    private String mVerificationId, mobilePhone;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserId;
    private TextView mtextView;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_confirmation_code);

        toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Enter Confirmation Code");
        setSupportActionBar(toolbar);
        mtextView = (TextView) findViewById(R.id.textView5);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        Bundle extra = getIntent().getExtras();
        mVerificationId = extra.getString("verificationcode");
        mobilePhone = extra.getString("mobilephone");

        InputVerificationCode = (EditText) findViewById(R.id.verification_code_input_pass);
        VerifyButton = (Button) findViewById(R.id.confirm_button_pass);
        confirmpassowrd = (Button) findViewById(R.id.confirm_password);
        loadingBar = new ProgressDialog(this);

        confirmpassowrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = InputVerificationCode.getText().toString();

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(EnterConfirmationCodeActivity.this, "Please enter Password...", Toast.LENGTH_SHORT).show();
                } else {
                        RootRef.child("Users").child(currentUserId).child("password").setValue(password);
                        goToMainActivity();
                        Toast.makeText(EnterConfirmationCodeActivity.this, "Password changed Sucessfully", Toast.LENGTH_SHORT).show();
                    }
                }});

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = InputVerificationCode.getText().toString();

                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(EnterConfirmationCodeActivity.this, "Please write the code that has been sent do you", Toast.LENGTH_SHORT).show();
                } else {
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

    private void goToMainActivity() {
        Intent intent = new Intent(EnterConfirmationCodeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            currentUserId = mAuth.getCurrentUser().getUid();
                            RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild("password")){
                                        ChangePassWord();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(EnterConfirmationCodeActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void ChangePassWord() {
        toolbar.setTitle("Enter your password");
        VerifyButton.setVisibility(View.GONE);
        mtextView.setVisibility(View.GONE);
        confirmpassowrd.setVisibility(View.VISIBLE);
        //InputVerificationCode.setHint("Enter your Password");
        //InputVerificationCode.setHint("Enter your Password");
        InputVerificationCode.setText("");
    }
}
