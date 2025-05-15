package com.example.myproject;

import android.os.Build;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.adapters.CommentAdapter;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.ActivityCommentBinding;
import com.example.myproject.dto.CommentDTO;
import com.example.myproject.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity implements CommentAdapter.CommentClickListener{
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<CommentDTO> commentList;
    private CommentDTO replyingTo = null; // Để theo dõi comment đang được trả lời
    private EditText editTextComment;
    private TextView btnSendComment;
    private ActivityCommentBinding binding;
    private int postId;
    private ApiService apiService;
    private CommentDTO replyToComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");
        String uname = sharedPreferences.getString("username","");
        String bio = sharedPreferences.getString("bio","");
        String profileImage = sharedPreferences.getString("profile_image","");
        String email = sharedPreferences.getString("email","");
        //Avt user
        Glide.with(CommentActivity.this)
                .load(profileImage)
                .placeholder(R.drawable.blankprofile)
                .into(binding.imageProfileComment);

        // Lấy postId và comments từ Intent
        postId = getIntent().getIntExtra("postId", -1);
        //Log.d("CommentTest", "Serialized post: " + postId);
        if (postId == -1) {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Cho Android API 33+ (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ArrayList<CommentDTO> receivedComments = getIntent().getParcelableArrayListExtra("comments", CommentDTO.class);
            if (receivedComments != null) {
                commentList = receivedComments;
                //Log.d("CommentTest", "Serialized post: " + commentList);
            } else {
                commentList = new ArrayList<>();
            }
        } else {
            // Cho Android API 32 và thấp hơn
            ArrayList<CommentDTO> receivedComments = getIntent().getParcelableArrayListExtra("comments");
            if (receivedComments != null) {
                commentList = receivedComments;
                //Log.d("CommentTest", "Serialized post: " + commentList);
            } else {
                commentList = new ArrayList<>();
            }
        }
        // Ánh xạ view
        recyclerViewComments = binding.recyclerViewComment;
        editTextComment = binding.addComment;
        btnSendComment = binding.postt;
        commentAdapter = new CommentAdapter(this,commentList,this);
        recyclerViewComments.setAdapter(commentAdapter);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));

        // Lấy userId từ SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
//        int userId = sharedPreferences.getInt("user_id", -1);
//        jwtToken = sharedPreferences.getString("jwt_token", "");

        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        loadComments();
        binding.postt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = binding.addComment.getText().toString().trim();
                if (content.isEmpty()) {
                    Toast.makeText(CommentActivity.this, "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
                    return;
                }
                User user = new User(userId,uname,bio,profileImage,email);
                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setContent(content);
                commentDTO.setUser(user);

                if (replyToComment != null) {
                    if(replyToComment.getParentId()!=null)
                    {
                        commentDTO.setParentId(replyToComment.getParentId());
                    }
                    else{
                        commentDTO.setParentId(replyToComment.getCommentId());
                    }
                } else {
                    commentDTO.setParentId(null); // Comment mới, không có parent
                }
                //Log.d("CommentTest", "Serialized post: " + replyToComment.getCommentId());
                apiService.createComment("Bearer "+jwtToken,userId,postId,commentDTO).enqueue(new Callback<CommentDTO>() {
                    @Override
                    public void onResponse(Call<CommentDTO> call, Response<CommentDTO> response) {
                        if(response.isSuccessful()){
                            binding.addComment.setText(""); // Xóa nội dung sau khi gửi
                            replyToComment = null;
                            loadComments(); // Làm mới danh sách bình luận
                        }
                        else{
                            Toast.makeText(CommentActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CommentDTO> call, Throwable t) {
                        Toast.makeText(CommentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void loadComments() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");
        apiService.getComments("Bearer " + jwtToken, postId)
                .enqueue(new Callback<List<CommentDTO>>() {
                    @Override
                    public void onResponse(Call<List<CommentDTO>> call, Response<List<CommentDTO>> response) {
                        if (response.isSuccessful()) {
                            commentList.clear();
                            commentList.addAll(response.body());
                            commentAdapter.updateComments(commentList);
                        } else {
                            Toast.makeText(CommentActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CommentDTO>> call, Throwable t) {
                        Toast.makeText(CommentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onReplyClick(CommentDTO comment) {
        replyToComment = comment;
        //Log.d("CommentTest", "Serialized post: " + replyToComment.getCommentId());
        String userReplyName = comment.getUser() != null ? comment.getUser().getName() : "Unknown";
        Toast.makeText(this, "Reply to: " + userReplyName, Toast.LENGTH_SHORT).show();
        binding.addComment.setText("@" + userReplyName + " ");
    }
}