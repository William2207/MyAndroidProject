package com.example.myproject.post;


import static com.example.myproject.LoginActivity.jwtToken;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.CursorLoader;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.myproject.R;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.ActivityReelsBinding;
import com.example.myproject.models.PostCollection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReelsActivity extends AppCompatActivity {
    private ActivityReelsBinding binding;
    private ExoPlayer player;
    private ApiService apiService;
    private Uri selectedVideoUri;

    // Khai báo ActivityResultLauncher để chọn video
    private final ActivityResultLauncher<String> pickVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    setupPlayer(uri);
                    selectedVideoUri = uri;
                    // Hiển thị container video nếu nó đang ẩn
                    binding.videoPreview.setVisibility(View.VISIBLE);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thiết lập sự kiện cho nút chọn video
        binding.videoAddBtn.setOnClickListener(v -> {
            pickVideoLauncher.launch("video/*");
        });
        //closebtn
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        //postbtn click
        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedVideoUri != null) {
                    String caption = binding.description.getText().toString().trim();
                    uploadReel(selectedVideoUri, caption);
                } else {
                    Toast.makeText(ReelsActivity.this, "Vui lòng chọn video trước", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadReel(Uri videoUri,String caption){
        // Hiển thị progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải lên...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userid = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");

        try{
            // Lấy đường dẫn thực của file video
            String filePath = getRealPathFromURI(videoUri);
            if (filePath == null) {
                Toast.makeText(this, "Không thể xử lý video, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }

            // Tạo file từ đường dẫn
            File videoFile = new File(filePath);

            // Tạo RequestBody từ file
            RequestBody requestFile = RequestBody.create(MediaType.parse("video/*"), videoFile);

            // Tạo MultipartBody.Part từ RequestBody
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", videoFile.getName(), requestFile);

            apiService.uploadReel("Bearer "+jwtToken,filePart).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String videoUrl = response.body();
                        // Sau khi upload thành công, lưu thông tin vào database
                        saveReelToDatabase(videoUrl, caption, progressDialog);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(ReelsActivity.this, "Lỗi khi tải video: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(ReelsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    // Phương thức để lấy đường dẫn thực từ URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void saveReelToDatabase(String videoUrl, String caption, ProgressDialog progressDialog) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        jwtToken = sharedPreferences.getString("jwt_token", "");
        String uname = sharedPreferences.getString("username","");
        String bio = sharedPreferences.getString("bio","");
        String profileImage = sharedPreferences.getString("profile_image","");
        // Tạo đối tượng PostCollection để gửi lên server
        List<String> mediaUrls= new ArrayList<>();
        mediaUrls.add(videoUrl);
        PostCollection reel = new PostCollection(userId,caption,mediaUrls);
        apiService.createReel("Bearer "+jwtToken,reel).enqueue(new Callback<PostCollection>() {
            @Override
            public void onResponse(Call<PostCollection> call, Response<PostCollection> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(ReelsActivity.this, "Success ", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
                else{
                    Log.e("API Error", "Response code: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<PostCollection> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("API Error", "Failure: " + t.getMessage());
            }
        });
    }

    // Thiết lập Media3 Player
    private void setupPlayer(Uri videoUri) {
        // Giải phóng player cũ nếu có
        releasePlayer();

        // Tạo player mới
        player = new ExoPlayer.Builder(this).build();
        binding.videoPreview.setPlayer(player);

        // Tạo MediaItem từ URI video
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);

        // Chuẩn bị và bắt đầu phát khi sẵn sàng
        player.prepare();
        player.setPlayWhenReady(false); // Không tự động phát
    }

    // Giải phóng player khi không cần thiết
    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    // Tạm dừng player khi activity bị tạm dừng
    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    // Giải phóng player khi activity bị hủy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }


}