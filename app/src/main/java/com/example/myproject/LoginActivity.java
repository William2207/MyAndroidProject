package com.example.myproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.ActivityLoginBinding;
import com.example.myproject.databinding.ActivitySignupBinding;
import com.example.myproject.models.User;
import com.example.myproject.utils.JwtUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private ApiService apiService;
    private JwtUtils jwtUtils;
    public static User user;
    public static String jwtToken;
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
                apiService = RetrofitClient.getRetrofit().create(ApiService.class);
                apiService.login(username,password).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()){
                            jwtToken = response.body();
                            Log.d("Login", "JWT Token: " + jwtToken);

                            // Lưu JWT token vào SharedPreferences ngay lập tức
                            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("jwt_token", jwtToken);
                            editor.apply();  // hoặc commit() nếu cần đảm bảo lưu ngay lập tức


                            try{
                                String uname = JwtUtils.getUsernameFromToken(jwtToken);
                                //long expiration = JwtUtils.getExpirationFromToken(message);

                                apiService.getUserByUsername("Bearer " + jwtToken,uname).enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        if(response.isSuccessful())
                                        {
                                            user = response.body();
                                            Log.d("GetUser", "User: " + user.toString());
                                            Log.d("GetUser", "User: " + user.getUserId());

                                            // Lưu thông tin user vào SharedPreferences
                                            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt("user_id", user.getUserId());
                                            editor.putString("username", user.getName());
                                            // Lưu các thông tin khác nếu cần
                                            editor.commit();  // Dùng commit() để đảm bảo lưu xong mới chuyển màn hình

                                            // CHỈ chuyển màn hình sau khi đã lưu thông tin user thành công
                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                            finish();
                                        }
                                        else{
                                            Log.e("API Error", "Response code: " + response.code());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                        Log.e("API Error", "Failure: " + t.getMessage());
                                    }
                                });

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            // Hiển thị mã lỗi và body phản hồi để debug
                            String errorBody = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(LoginActivity.this, "Login failed: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                            Log.e("LoginActivity", "Response code: " + response.code() + ", Error: " + errorBody);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Error: " + t.getMessage());
                    }
                });
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