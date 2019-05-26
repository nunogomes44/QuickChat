package com.example.quickchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

public class RegisterPhoneFragment extends Fragment {

    private View registerPhoneFragmentView;
    private EditText inputPhoneNumber,UserPassword, UserPasswordConfirm;
    private RadioButton rb_masculino, rb_feminino;
    private Button createAccountButton;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String mVerificationId;
    private String gender;
    private String password_confirmcellactivity;
    private String mobilePhone;
    private DatabaseReference RootRef;
    private boolean verify;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    public RegisterPhoneFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        registerPhoneFragmentView = inflater.inflate(R.layout.fragment_register_phone, container, false);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        initializeField();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber_aux = inputPhoneNumber.getText().toString();
                String password = UserPassword.getText().toString();
                String password_confirm = UserPasswordConfirm.getText().toString();
                password_confirmcellactivity = password;

                if (TextUtils.isEmpty(phoneNumber_aux)) {
                    Toast.makeText(getContext(), "Phone number is required...", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "Please enter Password...", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(password_confirm)) {
                    Toast.makeText(getContext(), "Please re-enter the Password...", Toast.LENGTH_SHORT).show();
                }
                if (!(rb_masculino.isChecked()) & !(rb_feminino.isChecked())) {
                    Toast.makeText(getContext(), "Select the Gender", Toast.LENGTH_SHORT).show();
                } else {
                    if (verificaPassword(password, password_confirm)) {
                        if(verifyUserInfo()){
                        String phoneNumber = "+258"+phoneNumber_aux;
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait, while we are authenticating your phone");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            getActivity(),               // Activity (for callback binding)
                            callBacks);

                }}
            }
        callBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(getContext(),"Invalid Phone Number, Please enter correct phone number with your country code", Toast.LENGTH_SHORT).show();
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                if(rb_masculino.isChecked()){
                    gender = "Male";
                }
                if(rb_feminino.isChecked()){
                    gender = "Female";
                }

                loadingBar.dismiss();
                Toast.makeText(getContext(),"Code has been sent successfully",Toast.LENGTH_SHORT).show();

                mobilePhone = inputPhoneNumber.getText().toString().trim();
                Intent intent = new Intent(getContext(), ConfirmCellActivity.class);
                intent.putExtra("gender", gender);
                intent.putExtra("password", password_confirmcellactivity);
                intent.putExtra("verificationcode", mVerificationId);
                intent.putExtra("phonenumber", mobilePhone);
                startActivity(intent);
            }
        };
    }
        });
        return registerPhoneFragmentView;
    }

    private boolean verifyUserInfo(){
        final String phonenumber = inputPhoneNumber.getText().toString().trim();
        RootRef.child("Phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(phonenumber)){
                    //Toast.makeText(getContext(), "Users already exist", Toast.LENGTH_SHORT).show();
                }else{
                    verify = true;
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

    public boolean verificaPassword(String password, String password_confirm){
        if(!password.equals(password_confirm)){
            Toast.makeText(getContext(), "Enter with the same Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(getContext(),"Congratulation, you're successfully login", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(getContext(),"Error :"+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initializeField() {
        inputPhoneNumber = (EditText) registerPhoneFragmentView.findViewById(R.id.phone_number_input);
        UserPassword = (EditText) registerPhoneFragmentView.findViewById(R.id.register_password_phone);
        createAccountButton = (Button) registerPhoneFragmentView.findViewById(R.id.register_phone_button);
        UserPasswordConfirm = (EditText) registerPhoneFragmentView.findViewById(R.id.register_password_phone_confirm);
        rb_masculino = (RadioButton) registerPhoneFragmentView.findViewById(R.id.rb_masculino_phone);
        rb_feminino = (RadioButton) registerPhoneFragmentView.findViewById(R.id.rb_feminino_phone);

        loadingBar = new ProgressDialog(getActivity());
    }
}
