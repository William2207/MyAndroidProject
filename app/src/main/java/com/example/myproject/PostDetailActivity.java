package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.adapters.PostImageAdapter;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.ActivityPostDetailBinding;
import com.example.myproject.dto.CommentDTO;
import com.example.myproject.dto.LikeDTO;
import com.example.myproject.dto.PostDTO;
import com.example.myproject.dto.SaveDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {
    private ActivityPostDetailBinding binding;
    private PostDTO currentPost;
    private PostImageAdapter postImagesAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar); // Giả sử ID toolbar trong binding là 'toolbar'
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Post Detail"); // Hoặc bạn có thể để trống hoặc tên người đăng
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // --- Nhận dữ liệu Post từ Intent ---
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("POST_DATA")) {
            currentPost = intent.getParcelableExtra("POST_DATA");
        }

        if (currentPost != null) {
            populatePostDetails();
        } else {
            Toast.makeText(this, "Không thể tải chi tiết bài đăng.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có dữ liệu
        }

    }
    private void populatePostDetails(){
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        if (currentPost == null) return;
        if (currentPost.getUser().getImage() != null && !currentPost.getUser().getImage().isEmpty()) {
            Glide.with(this)
                    .load(currentPost.getUser().getImage())
                    .placeholder(R.drawable.blankprofile) // THAY THẾ bằng placeholder của bạn
                    .error(R.drawable.blankprofile)       // THAY THẾ bằng error image của bạn
                    .into(binding.profilePostImage); // Truy cập qua binding
        } else {
            binding.profilePostImage.setImageResource(R.drawable.blankprofile); // Ảnh mặc định
        }
        binding.unamePost.setText(currentPost.getUser().getName());
        binding.postAuthor.setText(currentPost.getUser().getName());
        if (currentPost.getContent() != null && !currentPost.getContent().isEmpty()) {
            binding.postDes.setText(currentPost.getContent());
            binding.postDes.setVisibility(View.VISIBLE);
        } else {
            binding.postDes.setVisibility(View.GONE);
        }
        binding.postDate.setText(currentPost.getCreatedAt());
        binding.likesCount.setText(String.valueOf(currentPost.getLikesCount()));
        binding.like.setImageResource(currentPost.isLiked() ? R.drawable.ic_liked : R.drawable.ic_like);
        binding.savePost.setImageResource(currentPost.isSaved()? R.drawable.ic_saved : R.drawable.ic_save);
        // Thiết lập RecyclerView con cho ảnh
        PostImageAdapter imageAdapter = new PostImageAdapter(this, currentPost.getMediaUrls());
        binding.postImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.postImages.setAdapter(imageAdapter);
        // Xử lý counter
        TextView imageCounter = binding.imageCounter; //thêm imageCounter vào ViewHolder
        int totalImages = currentPost.getMediaUrls().size();
        // Chỉ hiển thị counter nếu có nhiều hơn 1 ảnh
        if (totalImages > 1) {
            imageCounter.setVisibility(View.VISIBLE);
            imageCounter.setText("1/" + totalImages);
        } else {
            imageCounter.setVisibility(View.GONE);
        }

        // Xóa các SnapHelper cũ để tránh lỗi
        RecyclerView.OnFlingListener onFlingListener = binding.postImages.getOnFlingListener();
        if (onFlingListener != null) {
            binding.postImages.setOnFlingListener(null);
        }

        // Thêm PagerSnapHelper để đảm bảo mỗi ảnh chiếm full và vuốt mượt
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.postImages);

        binding.postImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Lấy vị trí hiện tại khi dừng scroll
                    int currentPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstVisibleItemPosition();

                    if (currentPosition != RecyclerView.NO_POSITION) {
                        // Cập nhật text của counter
                        imageCounter.setText((currentPosition + 1) + "/" + totalImages);
                    }
                }
            }
        });
        //Comment click
        binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailActivity.this, CommentActivity.class);
                intent.putExtra("postId", currentPost.getPostId());

                // Chuyển danh sách comments thành ArrayList (vì ArrayList cũng triển khai Parcelable)
                ArrayList<CommentDTO> commentArrayList = new ArrayList<>(currentPost.getComments());
                intent.putParcelableArrayListExtra("comments", commentArrayList);
                //Log.d("CommentTest", "Serialized post: " + commentArrayList);
                PostDetailActivity.this.startActivity(intent);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");
        //like btn click
        binding.like.setOnClickListener(v -> {
            LikeDTO likeDTO = new LikeDTO(userId,currentPost.getPostId());
            apiService.toggleLike("Bearer "+jwtToken,likeDTO).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    boolean isLiked = response.body();
                    currentPost.setLiked(isLiked);
                    int newLikesCount = currentPost.getLikesCount() + (isLiked ? 1 : -1);

                    // Cập nhật giao diện trên luồng chính
                    binding.like.post(() -> {
                        binding.like.setImageResource(currentPost.isLiked() ? R.drawable.ic_liked : R.drawable.ic_like);
                        binding.like.setEnabled(true);
                        binding.likesCount.setText(String.valueOf(newLikesCount));
                    });
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    // Hiển thị lỗi trên luồng chính
                    binding.like.post(() -> {
                        Toast.makeText(PostDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.like.setEnabled(true);
                    });
                }
            });
        });

        // Save button click
        binding.savePost.setOnClickListener(v -> {
            SaveDTO saveDTO = new SaveDTO(userId, currentPost.getPostId());
            apiService.toggleSave("Bearer " + jwtToken, saveDTO).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    boolean isSaved = response.body();
                    currentPost.setSaved(isSaved);
                    // Cập nhật giao diện trên luồng chính
                    binding.like.post(() -> {
                        binding.savePost.setImageResource(currentPost.isSaved() ? R.drawable.ic_saved : R.drawable.ic_save);
                        binding.savePost.setEnabled(true);
                    });
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    // Hiển thị lỗi trên luồng chính
                    binding.savePost.post(() -> {
                        Toast.makeText(PostDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.like.setEnabled(true);
                    });
                }
            });
        });
    }

}