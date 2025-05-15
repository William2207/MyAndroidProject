package com.example.myproject.post;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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

import com.example.myproject.R;
import com.example.myproject.adapters.ImageAdapter;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.ActivityPostBinding;
import com.example.myproject.models.PostCollection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {
    private ActivityPostBinding binding;
    private ImageAdapter imageAdapter;
    // Lưu trữ danh sách URI ảnh đã chọn
    private List<Uri> selectedImageUris = new ArrayList<>();
    private ApiService apiService;
    private PostCollection postCollection;

    // Khai báo launcher ở cấp lớp
    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo RecyclerView với adapter
        imageAdapter = new ImageAdapter(this, selectedImageUris);
        binding.imageAdded.setAdapter(imageAdapter);

        // Khởi tạo launcher cho chọn nhiều ảnh (tối đa 5)
        pickMultipleMedia = registerForActivityResult(
                new ActivityResultContracts.PickMultipleVisualMedia(5), // Số 5 là giới hạn số lượng ảnh
                uris -> {
                    if (!uris.isEmpty()) {
                        // Lưu và hiển thị ảnh đã chọn
                        selectedImageUris.clear();
                        selectedImageUris.addAll(uris);
                        imageAdapter.updateImages(selectedImageUris);
                    } else {
                        // Không có ảnh nào được chọn
                        Toast.makeText(this, "Bạn chưa chọn ảnh nào", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //addimage btn
        binding.imageAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPhotoPicker();
            }
        });

        //close btn
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        //apiService.uploadImages("Bearer " + jwtToken,selectedImageUris);
        //post btn
        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImageUris.isEmpty()) {
                    Toast.makeText(PostActivity.this, "Vui lòng chọn ít nhất một ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadImages(selectedImageUris);
            }
        });

    }
    private void launchPhotoPicker() {
        // Mở Photo Picker để chọn ảnh
        pickMultipleMedia.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
    }

    private void uploadImages(List<Uri> imageUris) {
        // Hiển thị progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userid = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");
        String uname = sharedPreferences.getString("username","");
        String bio = sharedPreferences.getString("bio","");
        String profileImage = sharedPreferences.getString("profile_image","");

        // Chuyển đổi danh sách URI thành mảng MultipartBody.Part
        MultipartBody.Part[] imageParts = new MultipartBody.Part[imageUris.size()];

        try {
            for (int i = 0; i < imageUris.size(); i++) {
                Uri imageUri = imageUris.get(i);

                // Lấy file từ URI
                ContentResolver contentResolver = getContentResolver();
                String fileName = getFileName(contentResolver, imageUri);

                // Tạo RequestBody từ file
                InputStream inputStream = contentResolver.openInputStream(imageUri);
                byte[] byteArray = getBytes(inputStream);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), byteArray);

                // Tạo MultipartBody.Part
                imageParts[i] = MultipartBody.Part.createFormData("files", fileName, requestFile);
            }

            String content = binding.description.getText().toString();
            // Lấy JWT token
            // Gọi API upload ảnh
            Call<List<String>> call = apiService.uploadImages("Bearer " + jwtToken, imageParts);
            call.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    if (response.isSuccessful()) {
                        List<String> imageUrls = response.body();
                        Log.d("GetUser", "User: " + userid);
                        postCollection = new PostCollection(userid,content,imageUrls);
                        apiService.createPost("Bearer "+ jwtToken, postCollection).enqueue(new Callback<PostCollection>() {
                            @Override
                            public void onResponse(Call<PostCollection> call, Response<PostCollection> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(PostActivity.this, "Success ", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                }
                                else {
                                    Log.e("API Error", "Response code: " + response.code());
                                }
                            }
                            @Override
                            public void onFailure(Call<PostCollection> call, Throwable t) {
                                Log.e("API Error", "Failure: " + t.getMessage());
                            }
                        });

                    } else {
                        Toast.makeText(PostActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error with files " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    // Phương thức lấy tên file từ URI
    private String getFileName(ContentResolver contentResolver, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // Phương thức chuyển đổi InputStream thành mảng byte
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


}