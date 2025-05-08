package com.example.myproject.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    private List<Uri> imageUris;
    private Context context;


    public ImageAdapter(Context context, List<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);

        // Sử dụng Glide để load ảnh (đừng quên thêm Glide dependency)
        Glide.with(context)
                .load(imageUri)
                .centerCrop()
                .into(holder.imageView);
        // Kiểm tra nếu chỉ có 1 item
        if (imageUris.size() == 1) {
            // Đặt căn giữa cho item
            holder.itemView.post(() -> {
                RecyclerView recyclerView = (RecyclerView) holder.itemView.getParent();
                if (recyclerView != null) {
                    int parentWidth = recyclerView.getWidth();
                    int itemWidth = holder.itemView.getWidth();

                    // Tính toán phần margin cần thiết để căn giữa
                    int horizontalMargin = (parentWidth - itemWidth) / 2;

                    // Áp dụng margins
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
                    params.leftMargin = horizontalMargin;
                    params.rightMargin = horizontalMargin;
                    holder.itemView.setLayoutParams(params);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public void updateImages(List<Uri> newImages) {
        this.imageUris = newImages;
        notifyDataSetChanged();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
        }
    }

}
