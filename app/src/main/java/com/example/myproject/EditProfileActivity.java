package com.example.myproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.ActivityEditProfileBinding;
import com.example.myproject.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private LoginActivity loginActivity;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.unameEditText.setText(loginActivity.user.getName());
        binding.bioEditText.setText(loginActivity.user.getBio());
        binding.emailEditText.setText(loginActivity.user.getEmail());

        String imageUrl = loginActivity.user.getImage();
        if(imageUrl == null || imageUrl.isEmpty())
        {
            binding.imageProfile.setImageResource(R.drawable.blankprofile);
        }
        else{
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.blankprofile) // ảnh hiển thị tạm thời khi load
                    .error(R.drawable.blankprofile) // ảnh hiển thị khi có lỗi
                    .into(binding.imageProfile);
        }

        //save click
        binding.save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = binding.unameEditText.getText().toString();
                String bio = binding.bioEditText.getText().toString();
                String email = binding.emailEditText.getText().toString();

                apiService = RetrofitClient.getRetrofit().create(ApiService.class);
                apiService.editUserProfile("Bearer " + LoginActivity.jwtToken,loginActivity.user.getUserId(),username,bio,email).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.isSuccessful()){
                            loginActivity.user = response.body();
                            Log.d("GetUser", "User: " + loginActivity.user.toString());
                            setResult(RESULT_OK); // Thông báo cập nhật thành công
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
            }
        });

        //back click
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}