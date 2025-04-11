package com.example.n7_countdown.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.R;
import com.example.n7_countdown.storage.UserDatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etGroupName;
    private Button btnRegister;
    private TextView tvLogin;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etGroupName = findViewById(R.id.etGroupName);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        dbHelper = new UserDatabaseHelper(this);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String groupName = etGroupName.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || groupName.isEmpty()) {
                Toast.makeText(RegisterActivity.this, R.string.register_empty_error, Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.addUser(email, password, groupName)) {
                Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, R.string.register_email_exists, Toast.LENGTH_SHORT).show();
            }
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }
}