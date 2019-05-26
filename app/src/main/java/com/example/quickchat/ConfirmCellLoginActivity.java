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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmCellLoginActivity extends AppCompatActivity {

    private EditText InputVerificationCode;
    private Button VerifyButton;
    private ProgressDialog loadingBar;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private String mobile;
    private TextView mobilePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_cell_login);

        mAuth = FirebaseAuth.getInstance();
        mobilePhone = (TextView) findViewById(R.id.mobilephone);

        Bundle extra = getIntent().getExtras();
        mVerificationId = extra.getString("verificationcode");
        mobile = extra.getString("mobilephone").toLowerCase();

        mobilePhone.setText(mobile);

        InputVerificationCode = (EditText) findViewById(R.id.verification_code_input_login);
        VerifyButton = (Button) findViewById(R.id.confirm_button_phone_login);
        loadingBar = new ProgressDialog(this);

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = InputVerificationCode.getText().toString();

                if(TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(ConfirmCellLoginActivity.this,"Please write the code that has been sent do you",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ConfirmCellLoginActivity.this,"Congratulation, you're successfully login", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(ConfirmCellLoginActivity.this,"Error :"+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(ConfirmCellLoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }
}
