package com.example.myproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.dto.CommentDTO;
import com.example.myproject.utils.TimeUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentDTO> commentList;
    private Context context;
    private CommentClickListener listener;

    public interface CommentClickListener {
        void onReplyClick(CommentDTO comment);
    }

    public CommentAdapter(Context context, List<CommentDTO> commentList, CommentClickListener listener) {
        this.context = context;
        this.commentList = commentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentDTO comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public void updateComments(List<CommentDTO> newComments) {
        this.commentList = newComments;
        notifyDataSetChanged();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        TextView tvUsername;
        TextView tvContent;
        TextView tvTime;
        TextView tvReply;
        RecyclerView recyclerViewReplies;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvReply = itemView.findViewById(R.id.tv_reply);
            recyclerViewReplies = itemView.findViewById(R.id.recycler_view_replies);
        }

        public void bind(CommentDTO comment) {
            // Hiển thị thông tin comment
            tvUsername.setText(comment.getUser().getName());
            tvContent.setText(comment.getContent());
            tvTime.setText(TimeUtils.getTimeAgo(comment.getCreatedAt()));

            // Tải avatar
            if (comment.getUser().getImage() != null) {
                Glide.with(context)
                        .load(comment.getUser().getImage())
                        .placeholder(R.drawable.blankprofile)
                        .into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.blankprofile);
            }

            // Xử lý sự kiện click Reply
            tvReply.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReplyClick(comment);
                }
            });

            // Nếu có replies thì hiển thị
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                recyclerViewReplies.setVisibility(View.VISIBLE);
                setupRepliesRecyclerView(comment.getReplies());
            } else {
                recyclerViewReplies.setVisibility(View.GONE);
            }
        }

        private void setupRepliesRecyclerView(List<CommentDTO> replies) {
            CommentAdapter replyAdapter = new CommentAdapter(context, replies, listener);
            recyclerViewReplies.setLayoutManager(new LinearLayoutManager(context));
            recyclerViewReplies.setAdapter(replyAdapter);
            // Tắt animation và nested scrolling để tránh vấn đề về hiệu suất
            recyclerViewReplies.setNestedScrollingEnabled(false);
        }
    }
}