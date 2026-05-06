package com.example.notetakingmobileapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notetakingmobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword;
    private Button btnCreateAccount;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnCreateAccount = findViewById(R.id.btn_create_account);

        btnCreateAccount.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(fullName)) {
                etFullName.setError("Full name is required");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                return;
            }

            if (password.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                return;
            }

            // Thực hiện đăng ký
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Đăng ký thành công, lấy UID
                            String userId = task.getResult().getUser().getUid();
                            
                            // Chuẩn bị dữ liệu user (Loại bỏ avatarUrl như yêu cầu)
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("fullName", fullName);
                            userMap.put("email", email);
                            userMap.put("uid", userId);

                            // Lưu vào Firestore
                            mFirestore.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(CreateAccount.this, "Account created successfully! Please login.", Toast.LENGTH_SHORT).show();
                                        // Sau khi tạo thành công, ép người dùng đăng nhập lại để đồng bộ Session
                                        mAuth.signOut(); 
                                        Intent intent = new Intent(CreateAccount.this, Login.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Nếu lưu Firestore thất bại (thường do Rules)
                                        Toast.makeText(CreateAccount.this, "Auth OK, but DB Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            // Nếu đăng ký Auth thất bại (Email đã tồn tại, lỗi mạng...)
                            Toast.makeText(CreateAccount.this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}