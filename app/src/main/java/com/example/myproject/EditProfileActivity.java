package com.example.myproject;

import static com.example.myproject.LoginActivity.jwtToken;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.ActivityEditProfileBinding;
import com.example.myproject.models.User;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private LoginActivity loginActivity;
    private ApiService apiService;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Uri selectedImageUri;
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
                apiService.editUserProfile("Bearer " + jwtToken,loginActivity.user.getUserId(),username,bio,email).enqueue(new Callback<User>() {
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

        // Khởi tạo Photo Picker để chọn một ảnh
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                // Hiển thị ảnh trong ImageView
                binding.imageProfile.setImageURI(uri);
                // Tự động upload ảnh lên server
                uploadProfileImage(uri);
            } else {
                Toast.makeText(this, "Bạn chưa chọn ảnh nào", Toast.LENGTH_SHORT).show();
            }
        });

        //edit profile image btn
        binding.changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMedia.launch(
                        new PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                .build()
                );
            }
        });
    }
    private void uploadProfileImage(Uri imageUri) {
        try {
            // Chuyển Uri thành File
            String filePath = getRealPathFromUri(imageUri);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            // Lấy username và token (thay bằng giá trị thực tế)
            String username = loginActivity.user.getName();


            // Gọi API upload
            apiService = RetrofitClient.getRetrofit().create(ApiService.class);
            apiService.uploadProfileImage("Bearer "+ jwtToken,filePart,username).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(EditProfileActivity.this, "Ảnh đại diện đã được cập nhật", Toast.LENGTH_SHORT).show();
                        String imageUrl = response.body();
                        // binding.profileImage.setImageURI(Uri.parse(imageUrl));
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Upload thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Lỗi khi upload: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Utility để lấy đường dẫn file từ Uri
    private String getRealPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }
}