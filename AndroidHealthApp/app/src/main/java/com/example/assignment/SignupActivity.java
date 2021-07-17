package com.example.assignment;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private boolean isPwdShow = false;
    private boolean isPwdShow2 = false;
    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.registerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        binding.registerPassword.setTypeface(Typeface.DEFAULT);
        isPwdShow = true;
        binding.showBtn.setText("Show");
        binding.registerPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        binding.registerPassword2.setTypeface(Typeface.DEFAULT);
        isPwdShow2 = true;
        binding.showBtn2.setText("Show");

        authentication = FirebaseAuth.getInstance();

        binding.abandonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });

        binding.showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPwdShow) {
                    binding.registerPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.registerPassword.setTypeface(Typeface.DEFAULT);
                    isPwdShow = false;
                    binding.showBtn.setText("Hide");
                } else {
                    binding.registerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.registerPassword.setTypeface(Typeface.DEFAULT);
                    isPwdShow = true;
                    binding.showBtn.setText("Show");
                }
                Editable etable = binding.registerPassword.getText();
                Selection.setSelection(etable, etable.length());
            }
        });

        binding.showBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPwdShow2) {
                    binding.registerPassword2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.registerPassword2.setTypeface(Typeface.DEFAULT);
                    isPwdShow2 = false;
                    binding.showBtn2.setText("Hide");
                } else {
                    binding.registerPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.registerPassword2.setTypeface(Typeface.DEFAULT);
                    isPwdShow2 = true;
                    binding.showBtn2.setText("Show");
                }
                Editable etable = binding.registerPassword2.getText();
                Selection.setSelection(etable, etable.length());
            }
        });

        binding.registerEmailAddress.addTextChangedListener(new TextWatcher() {
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
                binding.errorPassword2.setText("");
            }
        });
        binding.registerPassword.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.errorEmail.setText("");
                binding.errorPassword.setText("");
                binding.errorPassword2.setText("");
            }
        });
        binding.registerPassword2.addTextChangedListener(new TextWatcher() {
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
                binding.errorPassword2.setText("");
            }
        });

        binding.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPass = checkEnter();
                // Sign up information is passed to firebase
                if (isPass){
                    authentication.createUserWithEmailAndPassword(binding.registerEmailAddress.getText().toString(),
                            binding.registerPassword.getText().toString()).addOnCompleteListener(SignupActivity.this,
                            task -> {
                                if (task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, SigninActivity.class));}
                                else {
                                    Toast.makeText(SignupActivity.this, "Fail to sign up", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private boolean checkEnter() {
        String email = binding.registerEmailAddress.getText().toString();
        String password = binding.registerPassword.getText().toString();
        String password2 = binding.registerPassword2.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.errorEmail.setText("Email cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            binding.errorPassword.setText("Password cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(password2)) {
            binding.errorPassword2.setText("Password cannot be empty");
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
        if (password2.length() < 6 || password2.length() > 20) {
            binding.errorPassword2.setText("Password length should be 6-20 characters");
            return false;
        }
        if (!password.equals(password2)){
            binding.errorPassword2.setText("Inconsistent password input");
            return false;
        }
        return true;

    }
}
