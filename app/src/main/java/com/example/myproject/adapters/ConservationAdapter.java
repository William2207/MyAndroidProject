package com.example.myproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConservationAdapter extends RecyclerView.Adapter<ConservationAdapter.ConservationViewHolder> {
    private List<User> userList;
    private final OnUserClickListener listener;
    interface OnUserClickListener {
        void onUserClick(User user);
    }
    public ConservationAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConservationAdapter.ConservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conservation_item, parent, false);
        return new ConservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConservationAdapter.ConservationViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getName());
        Glide.with(holder.itemView.getContext())
                .load(user.getImage())
                .into(holder.imageProfile);
        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    static class ConservationViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        CircleImageView imageProfile;
        View notification;

        public ConservationViewHolder(@NonNull View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            imageProfile = itemView.findViewById(R.id.image_profile);
            notification = itemView.findViewById(R.id.unread_indicator);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
