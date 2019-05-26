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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterEmailFragment extends Fragment {

    private View registerEmailFragmentView;
    private EditText UserEmail,UserPassword, UserPasswordConfirm;
    private RadioButton rb_masculino, rb_feminino;
    private Button createAccountButton;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private ProgressDialog loadingBar;

    public RegisterEmailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        registerEmailFragmentView = inflater.inflate(R.layout.fragment_register_email, container, false);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        initializeField();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

        return registerEmailFragmentView;
    }

    public boolean verificaPassword(String password, String password_confirm){
        if(!password.equals(password_confirm)){
            Toast.makeText(getContext(), "Enter with the same Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createNewAccount() {
        final String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String password_confirm = UserPasswordConfirm.getText().toString();
        final String rb_actual_masculino = "Male";
        final String rb_actual_feminino = "Female";

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getContext(),"Please enter Email...", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(getContext(),"Please enter Password...", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password_confirm)){
            Toast.makeText(getContext(), "Please re-enter the Password...", Toast.LENGTH_SHORT).show();
        }
        if(!(rb_masculino.isChecked()) & !(rb_feminino.isChecked())){
            Toast.makeText(getContext(), "Select the Gender", Toast.LENGTH_SHORT).show();
        }
        else{
            if(verificaPassword(password,password_confirm)){
                if(rb_masculino.isChecked()){
                    loadingBar.setTitle("Creating New Account");
                    loadingBar.setMessage("Please wait, while we are creating new account for you...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        String currentUserId = mAuth.getCurrentUser().getUid();
                                        RootRef.child("Users").child(currentUserId).child("gender").setValue(rb_actual_masculino);
                                        RootRef.child("Users").child(currentUserId).child("email").setValue(email);

                                        SendUserToMainActivity();
                                        Toast.makeText(getContext(),"Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }else{
                                        String message = task.getException().toString();
                                        Toast.makeText(getContext(),"Error : "+message, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
                else{
                    loadingBar.setTitle("Creating New Account");
                    loadingBar.setMessage("Please wait, while we are creating new account for you...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        String currentUserId = mAuth.getCurrentUser().getUid();
                                        RootRef.child("Users").child(currentUserId).child("gender").setValue(rb_actual_feminino);
                                        RootRef.child("Users").child(currentUserId).child("email").setValue(email);

                                        SendUserToMainActivity();
                                        Toast.makeText(getContext(),"Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }else{
                                        String message = task.getException().toString();
                                        Toast.makeText(getContext(),"Error : "+message, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }}
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(getContext(),MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    private void initializeField() {
        UserEmail = (EditText) registerEmailFragmentView.findViewById(R.id.login_password);
        UserPassword = (EditText) registerEmailFragmentView.findViewById(R.id.login_email);
        createAccountButton = (Button) registerEmailFragmentView.findViewById(R.id.login_button);
        UserPasswordConfirm = (EditText) registerEmailFragmentView.findViewById(R.id.register_password_confirm);
        rb_masculino = (RadioButton) registerEmailFragmentView.findViewById(R.id.rb_masculino);
        rb_feminino = (RadioButton) registerEmailFragmentView.findViewById(R.id.rb_feminino);

        loadingBar = new ProgressDialog(getActivity());

    }
}
