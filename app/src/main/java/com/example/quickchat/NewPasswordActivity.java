package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewPasswordActivity extends AppCompatActivity {

    private EditText passwords, password_confirmation;
    private Button confirm;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        passwords = (EditText) findViewById(R.id.verification_password);
        password_confirmation = (EditText) findViewById(R.id.verification_password_confirm);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        confirm = (Button) findViewById(R.id.confirm_button_pass);
        currentUserId = mAuth.getCurrentUser().getUid();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwords.getText().toString();
                String password_confirm = password_confirmation.getText().toString();

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(NewPasswordActivity.this, "Please enter Password...", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password_confirm)) {
                    Toast.makeText(NewPasswordActivity.this, "Please re-enter the Password...", Toast.LENGTH_SHORT).show();
                } else {
                    if (verificaPassword(password, password_confirm)) {
                        String final_password = password;
                        RootRef.child("Users").child(currentUserId).child("password").setValue(final_password);
                                            goToMainActivity();
                                            Toast.makeText(NewPasswordActivity.this, "Password changed Sucessfully", Toast.LENGTH_SHORT).show();
                                        }
                    }
                }

    });}

    private void goToMainActivity() {
        Intent intent = new Intent (NewPasswordActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean verificaPassword(String password, String password_confirm){
        if(!password.equals(password_confirm)){
            Toast.makeText(NewPasswordActivity.this, "Enter with the same Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
