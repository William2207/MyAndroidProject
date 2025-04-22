package com.example.myproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.ActivitySignupBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo View Binding
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Thiết lập layout từ binding

        EdgeToEdge.enable(this);

        // Thiết lập WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // doi mau chu
        String text = "<font color='#FF000000'>Have an account?</font> <font color='#1E88E5'>Log in</font>";
        binding.login.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));


        // Thiết lập sự kiện cho button khi nhấn
        binding.signUpBtn.setOnClickListener(view -> {
            // Lấy text từ các trường TextInputEditText
            String username = binding.username.getEditText().getText().toString();
            String email = binding.email.getEditText().getText().toString();
            String password = binding.password.getEditText().getText().toString();

            // Kiểm tra nếu bất kỳ trường nào bị bỏ trống
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                // Hiển thị thông báo Toast nếu có trường nào bị bỏ trống
                Toast.makeText(SignupActivity.this, "Please fill all the information", Toast.LENGTH_SHORT).show();
            } else {
                    apiService = RetrofitClient.getRetrofit().create(ApiService.class);
                    apiService.registerUser(username,password,email).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful()){
                                String message = response.body();
                                Toast.makeText(SignupActivity.this, message != null ? message : "Registration successful", Toast.LENGTH_SHORT).show();
                                binding.username.getEditText().setText("");
                                binding.email.getEditText().setText("");
                                binding.password.getEditText().setText("");
                                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                                finish();
                            }
                            else {
                                Toast.makeText(SignupActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("logg",t.getMessage());
                        }
                    });
            }

        });
        // loggin
        binding.login.setOnClickListener(view->{
            startActivity(new Intent(SignupActivity.this,LoginActivity.class));
        });
    }
}