package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myproject.databinding.ActivityLoginBinding;
import com.example.myproject.databinding.ActivitySignupBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo View Binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        // Thiết lập WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // loggin btn
        binding.logInBtn.setOnClickListener(view->{
            // Lấy text từ các trường TextInputEditText
            String username = binding.username.getEditText().getText().toString();
            String password = binding.password.getEditText().getText().toString();

            if (username.isEmpty()  || password.isEmpty()) {
                // Hiển thị thông báo Toast nếu có trường nào bị bỏ trống
                Toast.makeText(LoginActivity.this, "Please fill all the information", Toast.LENGTH_SHORT).show();
            } else {
                // Bạn có thể thêm logic đăng ký tại đây
                Toast.makeText(LoginActivity.this, "All fields are filled!", Toast.LENGTH_SHORT).show();
            }
        });
        // Create new account btn
        binding.signUpBtn.setOnClickListener(view->{
            startActivity(new Intent(LoginActivity.this,SignupActivity.class));
        });

        //forgot password
        binding.fpassword.setOnClickListener(view->{
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }
}