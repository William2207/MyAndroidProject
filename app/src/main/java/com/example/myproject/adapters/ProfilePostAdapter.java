package com.example.myproject.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.dto.PostDTO;

import java.util.List;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.PostProfileViewHolder> {
    private List<PostDTO> postList;
    private OnPostClickListener onPostClickListener; // Interface để xử lý click
    public ProfilePostAdapter(List<PostDTO> postList){
        this.postList = postList;
    }

    // Interface cho sự kiện click
    public interface OnPostClickListener {
        void onPostClick(PostDTO post);
    }

    public void setOnPostClickListener(OnPostClickListener listener) {
        this.onPostClickListener = listener;
    }

    @NonNull
    @Override
    public ProfilePostAdapter.PostProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_item, parent, false);
        return new PostProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilePostAdapter.PostProfileViewHolder holder, int position) {
        PostDTO post = postList.get(position);
        if (post == null) { // Thêm kiểm tra null cho post nếu có khả năng postList chứa null
            Log.e("ProfilePostAdapter", "PostDTO at position " + position + " is null.");
            // Có thể ẩn view hoặc hiển thị trạng thái lỗi
            holder.itemView.setVisibility(View.GONE); // Ví dụ: ẩn item nếu post là null
            return;
        }
        holder.itemView.setVisibility(View.VISIBLE);

        // Kiểm tra mediaUrls
        List<String> mediaUrls = post.getMediaUrls();
        if (mediaUrls != null && !mediaUrls.isEmpty() && mediaUrls.get(0) != null) {
            // Tải ảnh/video đầu tiên
            Glide.with(holder.itemView.getContext())
                    .load(mediaUrls.get(0))
                    .placeholder(R.drawable.blankprofile) // Ảnh mặc định khi đang tải
                    .error(R.drawable.blankprofile) // Ảnh mặc định nếu lỗi
                    .into(holder.postImage);
        } else {
            // Hiển thị ảnh mặc định nếu mediaUrls không hợp lệ
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.blankprofile)
                    .into(holder.postImage);
            Log.w("ProfilePostAdapter", "Invalid mediaUrls for post ID: " + post.getPostId());
        }

        // Hiển thị biểu tượng nếu là bài đăng nhiều ảnh hoặc video
        holder.multipleImagesIcon.setVisibility(post.getMediaUrls().size()>1 ? View.VISIBLE : View.GONE);
        holder.videoIcon.setVisibility(post.getMediaUrls().get(0).contains("video") ? View.VISIBLE : View.GONE);

        //item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPostClickListener != null) {
                    onPostClickListener.onPostClick(post); // Truyền post hiện tại
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage, multipleImagesIcon,videoIcon;

        PostProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
            multipleImagesIcon = itemView.findViewById(R.id.multiple_images_icon);
            videoIcon = itemView.findViewById(R.id.video_icon);
        }
    }
}
