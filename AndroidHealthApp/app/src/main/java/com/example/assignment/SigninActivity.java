package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.assignment.databinding.ActivitySigninBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SigninActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;
    private boolean isPwdShow = false;
    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.enterPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        binding.enterPassword.setTypeface(Typeface.DEFAULT);
        isPwdShow = true;
        binding.showBtn.setText("Show");
        authentication = FirebaseAuth.getInstance();

        // sign up
        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this,
                        SignupActivity.class);
                startActivity(intent);
            }
        });

        // enter email
        binding.enterEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.errorEmail.setText("");
                binding.errorPassword.setText("");
            }
        });

        // enter password
        binding.enterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.errorEmail.setText("");
                binding.errorPassword.setText("");
            }
        });

        // show or hide password
        binding.showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPwdShow) {
                    binding.enterPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.enterPassword.setTypeface(Typeface.DEFAULT);
                    isPwdShow = false;
                    binding.showBtn.setText("Hide");
                } else {
                    binding.enterPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.enterPassword.setTypeface(Typeface.DEFAULT);
                    isPwdShow = true;
                    binding.showBtn.setText("Show");
                }
                Editable etable = binding.enterPassword.getText();
                Selection.setSelection(etable, etable.length());
            }
        });

        // sign in
        binding.signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPass = checkEnter();
                // Compare sign in information with firebase
                if (isPass){
                    authentication.signInWithEmailAndPassword(binding.enterEmail.getText().toString(),
                            binding.enterPassword.getText().toString()).addOnCompleteListener(
                                    SigninActivity.this, task -> {
                                        // check sign in status
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(SigninActivity.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SigninActivity.this, HomeActivity.class));
                                        }
                                        else {
                                            Toast.makeText(SigninActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                                        }
                            });
                }
            }
        });
    }

    // check input
    private boolean checkEnter() {
        String email = binding.enterEmail.getText().toString();
        String password = binding.enterPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.errorEmail.setText("Email cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            binding.errorPassword.setText("Password cannot be empty");
            return false;
        }
        if (!EmailValidation.isEmail(email)) {
            binding.errorEmail.setText("Email is invalid");
            return false;
        }
        if (password.length() < 6 || password.length() > 20) {
            binding.errorPassword.setText("Password length should be 6-20 characters");
            return false;
        }
        return true;
    }
}

