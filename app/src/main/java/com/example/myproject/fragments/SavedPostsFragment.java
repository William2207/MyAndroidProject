package com.example.myproject.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myproject.R;
import com.example.myproject.adapters.ProfilePostAdapter;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.dto.PostDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedPostsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProfilePostAdapter postsAdapter;
    private List<PostDTO> postList;
    private ApiService apiService;


    public SavedPostsFragment() {
        // Required empty public constructor
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        // Khởi tạo danh sách bài đăng (giả lập dữ liệu)
        postList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_posts, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_save_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postsAdapter = new ProfilePostAdapter(postList);
        recyclerView.setAdapter(postsAdapter);
        // data
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");
        apiService.getSavedPosts("Bearer "+jwtToken, userId).enqueue(new Callback<List<PostDTO>>() {
            @Override
            public void onResponse(Call<List<PostDTO>> call, Response<List<PostDTO>> response) {
                if(response.isSuccessful()){
                    postList.clear();
                    postList.addAll(response.body());
                    postsAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getContext(), "Error " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}