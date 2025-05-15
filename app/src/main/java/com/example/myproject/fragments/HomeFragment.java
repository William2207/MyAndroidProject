package com.example.myproject.fragments;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myproject.R;
import com.example.myproject.adapters.PostAdapter;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.FragmentHomeBinding;
import com.example.myproject.dto.PostDTO;
import com.example.myproject.models.PostCollection;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<PostDTO> postList;
    private ApiService apiService;

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        // Khởi tạo RecyclerView sử dụng binding
        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách bài đăng
        postList = new ArrayList<>();
        // Thiết lập adapter
        postAdapter = new PostAdapter(getContext(), postList);
        binding.recyclerViewPosts.setAdapter(postAdapter);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");

        if (userId != -1 && !jwtToken.isEmpty()) {
            // Tải dữ liệu từ API
            loadPostsFromApi(userId);
        } else {
            Toast.makeText(getContext(), "Bạn cần đăng nhập lại", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(getActivity(), LoginActivity.class));
            //getActivity().finish();
        }
        //Refresh Scroll
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Làm mới dữ liệu cho cả hai RecyclerView
                refreshAllData();
            }
        });


        return binding.getRoot();
    }
    private void loadPostsFromApi(int userId) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("jwt_token", "");

        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        // Log để debug
        //Log.d("HomeFragment", "Gọi API với userId: " + userId + ", token: " + jwtToken);
        apiService.getPosts("Bearer "+jwtToken,userId).enqueue(new Callback<List<PostDTO>>() {
            @Override
            public void onResponse(Call<List<PostDTO>> call, Response<List<PostDTO>> response) {
                // Kết thúc animation refresh khi có dữ liệu trả về
                binding.swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    postList.clear();
                    postList.addAll(response.body());
                    postAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getContext(), "Error " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostDTO>> call, Throwable t) {
                // Kết thúc animation refresh khi có dữ liệu trả về
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshAllData() {
        // Hiển thị loading indicator
        binding.swipeRefreshLayout.setRefreshing(true);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");
        loadPostsFromApi(userId);
    }

}