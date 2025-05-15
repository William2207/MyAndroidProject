package com.example.myproject.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private Context context;
    private List<User> userList;
    private ApiService apiService;
    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }
    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.username.setText(user.getName());
        Glide.with(holder.itemView.getContext())
                .load(user.getImage())
                .placeholder(R.drawable.blankprofile)
                .into(holder.profileImage);
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");
        String currentUsername = sharedPreferences.getString("username","");
        //check follow status
        apiService.isFollowing("Bearer "+jwtToken,user.getUserId(), currentUsername).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFollowing = response.body();
                    holder.followButton.setText(isFollowing ? "Following" : "Follow");
                    holder.followButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(context, "Error checking follow status", Toast.LENGTH_SHORT).show();
            }
        });


        holder.followButton.setOnClickListener(v -> {
            if (holder.followButton.getText().equals("Following")) {
                // Unfollow
                apiService.unfollowUser("Bearer "+jwtToken,user.getUserId(), currentUsername).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            holder.followButton.setText("Follow");
                            Toast.makeText(context, "Unfollowed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to unfollow: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Failed to unfollow: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Follow
                apiService.followUser("Bearer "+jwtToken,user.getUserId(), currentUsername).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            holder.followButton.setText("Following");
                            Toast.makeText(context, "Followed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to follow: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Failed to follow: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView username;
        Button followButton;

        UserViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            followButton = itemView.findViewById(R.id.btn_follow);
        }
    }
}
