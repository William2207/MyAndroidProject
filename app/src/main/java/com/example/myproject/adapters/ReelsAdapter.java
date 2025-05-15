package com.example.myproject.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bumptech.glide.Glide;
import com.example.myproject.CommentActivity;
import com.example.myproject.LoginActivity;
import com.example.myproject.R;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.ItemReelsBinding;
import com.example.myproject.dto.CommentDTO;
import com.example.myproject.dto.LikeDTO;
import com.example.myproject.dto.PostDTO;
import com.example.myproject.dto.SaveDTO;
import com.example.myproject.fragments.ReelFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.ReelViewHolder> {
    private Context context;
    private List<PostDTO> reelsList;
    private ExoPlayer currentPlayer;
    private int currentPlayingPosition = -1;
    private ApiService apiService;

    public void setCurrentPlayingPosition(int position) {
        int previousPosition = currentPlayingPosition;
        currentPlayingPosition = position;

        // Thông báo để cập nhật UI của item trước đó và item hiện tại
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        if (currentPlayingPosition != -1) {
            notifyItemChanged(currentPlayingPosition);
        }
    }
    public void releaseCurrentPlayer() {
        if (currentPlayer != null) {
            currentPlayer.release();
            currentPlayer = null;
        }
    }
    public void pausePlayer() {
        if (currentPlayer != null) {
            currentPlayer.setPlayWhenReady(false); // Tạm dừng phát
        }
    }

    public void resumePlayer() {
        if (currentPlayer != null && currentPlayingPosition != -1) {
            currentPlayer.setPlayWhenReady(true); // Tiếp tục phát
        }
    }


    public ReelsAdapter(Context context, List<PostDTO> reelsList) {
        this.context = context;
        this.reelsList = reelsList;
    }

    @NonNull
    @Override
    public ReelsAdapter.ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reels, parent, false);
        return new ReelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelsAdapter.ReelViewHolder holder, int position) {
        PostDTO reel = reelsList.get(position);
        // Thiết lập video player
        if (currentPlayingPosition == position) {
            // Khởi tạo player nếu chưa có
            if (currentPlayer == null) {
                currentPlayer = new ExoPlayer.Builder(context).build();
                MediaItem mediaItem = MediaItem.fromUri(reel.getMediaUrls().get(0));
                currentPlayer.setMediaItem(mediaItem);
                currentPlayer.prepare();
                currentPlayer.setPlayWhenReady(true);
                currentPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
            }
            holder.playerView.setPlayer(currentPlayer);
        } else {
            holder.playerView.setPlayer(null);
        }
        // Bind user data
        holder.usernameTV.setText("@" + reel.getUser().getName());
        holder.descriptionTV.setText(reel.getContent());
        holder.likeCountTV.setText(formatCount(reel.getLikesCount()));

        // Load profile image
        Glide.with(context)
                .load(reel.getUser().getImage())
                .placeholder(R.drawable.blankprofile)
                .into(holder.profileImageView);

        // Setup like button state
        if (reel.isLiked()) {
            holder.likeBtn.setImageResource(R.drawable.ic_liked);
        } else {
            holder.likeBtn.setImageResource(R.drawable.ic_like_white);
        }
        // Setup save button state
        if(reel.isSaved()){
            holder.saveBtn.setImageResource(R.drawable.ic_saved);
        }
        else{
            holder.saveBtn.setImageResource(R.drawable.ic_save_white);
        }

        // Setup follow button
//        if (reel.isFollowing()) {
//            holder.followBtn.setText("Following");
//        } else {
//            holder.followBtn.setText("Follow");
//        }

        // Set click listeners
        setupClickListeners(holder, position);

        // Setup video player when visible
        if (currentPlayingPosition == position) {
            // If this item is currently playing, attach the existing player
            if (currentPlayer != null) {
                holder.playerView.setPlayer(currentPlayer);
            }
        } else {
            // Reset the player view
            holder.playerView.setPlayer(null);
        }
    }

    // Helper method to format count numbers
    private String formatCount(long count) {
        if (count < 1000) return String.valueOf(count);
        else if (count < 1000000) return String.format("%.1fK", count/1000.0);
        else return String.format("%.1fM", count/1000000.0);
    }

    private void setupClickListeners(ReelViewHolder holder, int position) {
        PostDTO reel = reelsList.get(position);
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");
        String uname = sharedPreferences.getString("username","");
        // Like button click
        holder.likeBtn.setOnClickListener(v -> {
            LikeDTO likeDTO = new LikeDTO(userId,reel.getPostId());
            apiService.toggleLike("Bearer "+jwtToken,likeDTO).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    boolean isLiked = response.body();
                    reel.setLiked(isLiked);
                    int newLikesCount = reel.getLikesCount() + (isLiked ? 1 : -1);
                    holder.itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.likeBtn.setImageResource(reel.isLiked()? R.drawable.ic_liked : R.drawable.ic_like_white);
                            holder.likeBtn.setEnabled(true);
                            holder.likeCountTV.setText(String.valueOf(newLikesCount));
                        }
                    });
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    holder.itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            holder.likeBtn.setEnabled(true);
                        }
                    });
                }
            });
        });

        // Comment button click
        holder.commentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", reel.getPostId());

            // Chuyển danh sách comments thành ArrayList (vì ArrayList cũng triển khai Parcelable)
            ArrayList<CommentDTO> commentArrayList = new ArrayList<>(reel.getComments());
            intent.putParcelableArrayListExtra("comments", commentArrayList);
            Log.d("CommentTest", "Serialized post: " + commentArrayList);
            context.startActivity(intent);
        });

        // Share button click
        holder.shareBtn.setOnClickListener(v -> {
            // Implement share functionality
            Toast.makeText(context, "Share " + reel.getUser().getName() + "'s reel", Toast.LENGTH_SHORT).show();
        });

        // Save button click
        holder.saveBtn.setOnClickListener(v -> {
            SaveDTO saveDTO = new SaveDTO(userId,reel.getPostId());
            apiService.toggleSave("Bearer "+ jwtToken,saveDTO).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    boolean isSaved = response.body();
                    reel.setSaved(isSaved);
                    holder.itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.saveBtn.setImageResource(reel.isSaved()? R.drawable.ic_saved : R.drawable.ic_save_white);
                            holder.saveBtn.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    holder.itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            holder.saveBtn.setEnabled(true);
                        }
                    });
                }
            });
            // Update UI
        });

        // Follow button click
//        holder.followBtn.setOnClickListener(v -> {
//            boolean newFollowState = !reel.isFollowing();
//            reel.setFollowing(newFollowState);
//
//            // Update UI
//            if (newFollowState) {
//                holder.followBtn.setText("Following");
//            } else {
//                holder.followBtn.setText("Follow");
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return reelsList.size();
    }

    @Override
    public void onViewRecycled(@NonNull ReelViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.playerView.getPlayer() != null) {
            holder.playerView.setPlayer(null);
        }
    }

    // Play video for the current visible item
    public void playVideoAtPosition(int position) {
        if (position == currentPlayingPosition) {
            return; // Already playing this video
        }

        // Release previous player if exists
        if (currentPlayer != null) {
            currentPlayer.release();
            currentPlayer = null;
        }

        // Create a new player for the current position
        currentPlayingPosition = position;

        if (position >= 0 && position < reelsList.size()) {
            PostDTO reel = reelsList.get(position);
            // Lấy URL đầu tiên từ danh sách
            String mediaUrl = reel.getMediaUrls().get(0);
            // Create and prepare the player
            currentPlayer = new ExoPlayer.Builder(context).build();
            MediaItem mediaItem = MediaItem.fromUri(mediaUrl);
            currentPlayer.setMediaItem(mediaItem);

            // Loop playback
            currentPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);

            // Prepare and start playback
            currentPlayer.prepare();
            currentPlayer.setPlayWhenReady(true);

            // Notify to update UI
            notifyDataSetChanged();
        }
    }

    // Release all resources
    public void releaseAllPlayers() {
        if (currentPlayer != null) {
            currentPlayer.release();
            currentPlayer = null;
        }
        currentPlayingPosition = -1;
    }

    public class ReelViewHolder extends RecyclerView.ViewHolder {
        PlayerView playerView;
        CircleImageView profileImageView;
        TextView usernameTV, descriptionTV, likeCountTV, commentCountTV;
        ImageView likeBtn, commentBtn, shareBtn, saveBtn;
        Button followBtn;

        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.player_view);
            profileImageView = itemView.findViewById(R.id.user_profile_pic);
            usernameTV = itemView.findViewById(R.id.username);
            descriptionTV = itemView.findViewById(R.id.description);
            likeCountTV = itemView.findViewById(R.id.like_count);
            likeBtn = itemView.findViewById(R.id.btn_like);
            commentBtn = itemView.findViewById(R.id.btn_comment);
            shareBtn = itemView.findViewById(R.id.btn_share);
            saveBtn = itemView.findViewById(R.id.btn_save);
            followBtn = itemView.findViewById(R.id.btn_follow);
        }
    }
}
