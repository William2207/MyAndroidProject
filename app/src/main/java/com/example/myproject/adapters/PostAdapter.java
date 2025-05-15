package com.example.myproject.adapters;

import static com.example.myproject.LoginActivity.jwtToken;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.CommentActivity;
import com.example.myproject.LoginActivity;
import com.example.myproject.R;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.dto.CommentDTO;
import com.example.myproject.dto.LikeDTO;
import com.example.myproject.dto.PostDTO;
import com.example.myproject.dto.SaveDTO;
import com.example.myproject.models.PostCollection;
import com.example.myproject.models.ProfileViewModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<PostDTO> posts;

    private ApiService apiService;


    public PostAdapter(Context context, List<PostDTO> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostDTO post = posts.get(position);
        holder.contentTextView.setText(post.getContent());
        holder.usernameTextView.setText(post.getUser().getName());
        holder.subusernameTextView.setText(post.getUser().getName());
        holder.postDate.setText(post.getCreatedAt());
        holder.likesCount.setText(String.valueOf(post.getLikesCount()));
        holder.like.setImageResource(post.isLiked() ? R.drawable.ic_liked : R.drawable.ic_like);
        holder.save.setImageResource(post.isSaved()? R.drawable.ic_saved : R.drawable.ic_save);
        Glide.with(holder.itemView.getContext())
                .load(post.getUser().getImage())
                .placeholder(R.drawable.blankprofile)
                .into(holder.profileImage);
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        jwtToken = sharedPreferences.getString("jwt_token", "");
        // save click event
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveDTO saveDTO = new SaveDTO(userId,post.getPostId());
                apiService.toggleSave("Bearer "+ jwtToken,saveDTO).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        boolean isSaved = response.body();
                        post.setSaved(isSaved);
                        holder.itemView.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.save.setImageResource(post.isSaved()? R.drawable.ic_saved : R.drawable.ic_save);
                                holder.save.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        holder.itemView.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                holder.save.setEnabled(true);
                            }
                        });
                    }
                });
            }
        });

        // Tạo biến likeAction để tái sử dụng cho nút like và double click
        final Runnable likeAction = new Runnable() {
            @Override
            public void run() {
                // Tránh nhiều lần nhấn liên tiếp
                holder.like.setEnabled(false);

                LikeDTO likeDTO = new LikeDTO(userId, post.getPostId());
                apiService.toggleLike("Bearer " + jwtToken, likeDTO).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        boolean isLiked = response.body();
                        post.setLiked(isLiked);
                        int newLikesCount = post.getLikesCount() + (isLiked ? 1 : -1);
                        post.setLikesCount(newLikesCount);

                        // Cập nhật UI trên main thread
                        holder.itemView.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.like.setImageResource(isLiked ? R.drawable.ic_liked : R.drawable.ic_like);
                                holder.likesCount.setText(String.valueOf(newLikesCount));
                                holder.like.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        holder.itemView.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                holder.like.setEnabled(true);
                            }
                        });
                    }
                });
            }
        };
        //Comment click
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());

                // Chuyển danh sách comments thành ArrayList (vì ArrayList cũng triển khai Parcelable)
                ArrayList<CommentDTO> commentArrayList = new ArrayList<>(post.getComments());
                intent.putParcelableArrayListExtra("comments", commentArrayList);
                //Log.d("CommentTest", "Serialized post: " + commentArrayList);
                context.startActivity(intent);
            }
        });


        // Thiết lập sự kiện click cho nút like
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeAction.run();
            }
        });

        // Thiết lập RecyclerView con cho ảnh
        PostImageAdapter imageAdapter = new PostImageAdapter(context, post.getMediaUrls());
        holder.imageRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.imageRecyclerView.setAdapter(imageAdapter);

        // Thiết lập GestureDetector để phát hiện double-click cho RecyclerView chứa ảnh
        final GestureDetector gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        // Nếu chưa like hoặc muốn toggle trạng thái like
                        likeAction.run();

                        // Hiệu ứng trái tim (tùy chọn)
                        showHeartAnimation(holder.imageRecyclerView, e.getX(), e.getY());
                        return true;
                    }
                });

        // Thêm touch listener cho RecyclerView để phát hiện double-click
        holder.imageRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Cho phép scroll bình thường
                holder.imageRecyclerView.onTouchEvent(event);
                // Xử lý gesture để phát hiện double-click
                return gestureDetector.onTouchEvent(event);
            }
        });

        // Xử lý counter
        TextView imageCounter = holder.imageCounter; //thêm imageCounter vào ViewHolder
        int totalImages = post.getMediaUrls().size();

        // Chỉ hiển thị counter nếu có nhiều hơn 1 ảnh
        if (totalImages > 1) {
            imageCounter.setVisibility(View.VISIBLE);
            imageCounter.setText("1/" + totalImages);
        } else {
            imageCounter.setVisibility(View.GONE);
        }

        // Xóa các SnapHelper cũ để tránh lỗi
        RecyclerView.OnFlingListener onFlingListener = holder.imageRecyclerView.getOnFlingListener();
        if (onFlingListener != null) {
            holder.imageRecyclerView.setOnFlingListener(null);
        }

        // Thêm PagerSnapHelper để đảm bảo mỗi ảnh chiếm full và vuốt mượt
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(holder.imageRecyclerView);

        // Xóa các listener cũ trước khi thêm mới
        holder.imageRecyclerView.clearOnScrollListeners();

        // Thêm listener để cập nhật counter khi scroll
        holder.imageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    }

    // Thêm phương thức này vào class của bạn để hiển thị hiệu ứng trái tim khi double-click
    private void showHeartAnimation(View view, float x, float y) {
        // Tạo một ImageView cho trái tim
        ImageView heartView = new ImageView(context);
        heartView.setImageResource(R.drawable.ic_liked);

        // Thiết lập LayoutParams cho heartView
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpToPx(72), // Kích thước trái tim (72dp)
                dpToPx(72)
        );

        // Tìm vị trí chính xác trong màn hình
        int[] viewLocation = new int[2];
        view.getLocationInWindow(viewLocation);

        // Tìm activity hiện tại để lấy content view (root view của activity)
        Activity activity = null;
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                activity = (Activity) context;
                break;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (activity != null) {
            // Sử dụng content view của activity làm parent cho animation
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            FrameLayout content = decorView.findViewById(android.R.id.content);

            // Tính toán vị trí chính xác để đặt trái tim
            params.leftMargin = (int) (viewLocation[0] + x - dpToPx(36));
            params.topMargin = (int) (viewLocation[1] + y - dpToPx(36));

            // Thêm trái tim vào content view
            content.addView(heartView, params);

            // Hiệu ứng cho trái tim
            heartView.setScaleX(0);
            heartView.setScaleY(0);
            heartView.setAlpha(0.9f);

            heartView.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .alpha(0)
                    .setDuration(800)
                    .setInterpolator(new OvershootInterpolator())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // Xóa heartView sau khi hiệu ứng kết thúc
                            content.removeView(heartView);
                        }
                    })
                    .start();
        }
    }

    // Phương thức chuyển đổi dp sang pixel
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView contentTextView;
        RecyclerView imageRecyclerView;
        TextView subusernameTextView;
        CircleImageView profileImage;
        TextView imageCounter;
        TextView likesCount;
        TextView postDate;
        ImageView like;
        ImageView save;
        ImageView comment;
        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.uname_post);
            subusernameTextView = itemView.findViewById(R.id.post_author);
            contentTextView = itemView.findViewById(R.id.post_des);
            imageRecyclerView = itemView.findViewById(R.id.post_images);
            profileImage = itemView.findViewById(R.id.profile_post_image);
            imageCounter = itemView.findViewById(R.id.image_counter);
            likesCount = itemView.findViewById(R.id.likes_count);
            postDate = itemView.findViewById(R.id.post_date);
            like = itemView.findViewById(R.id.like);
            save = itemView.findViewById(R.id.save_post);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}