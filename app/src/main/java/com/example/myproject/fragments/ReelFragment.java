package com.example.myproject.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myproject.R;
import com.example.myproject.adapters.ReelsAdapter;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.databinding.FragmentReelBinding;
import com.example.myproject.dto.PostDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReelFragment extends Fragment {
    private FragmentReelBinding binding;
    private List<PostDTO> postList;
    private ApiService apiService;
    private ReelsAdapter reelsAdapter;

    public ReelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getRetrofit().create(ApiService.class); // Khởi tạo ApiService
        postList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =FragmentReelBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo adapter
        reelsAdapter = new ReelsAdapter(requireContext(), postList);

        // Thiết lập ViewPager2
        binding.reelsViewpager.setAdapter(reelsAdapter);
        binding.reelsViewpager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        binding.reelsViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Cập nhật vị trí video đang phát trong adapter
                reelsAdapter.setCurrentPlayingPosition(position);
                reelsAdapter.notifyDataSetChanged();
            }
        });

        // Tải dữ liệu từ ApiService
        loadReelsData();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Tạm dừng video khi Fragment không hiển thị
        if (reelsAdapter != null) {
            reelsAdapter.pausePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tiếp tục phát video nếu có player
        if (reelsAdapter != null) {
            reelsAdapter.resumePlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Giải phóng player
        if (reelsAdapter != null) {
            reelsAdapter.releaseCurrentPlayer();
        }
        binding = null;
    }

    private void loadReelsData() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String jwtToken = sharedPreferences.getString("jwt_token", "");
        String uname = sharedPreferences.getString("username","");
        String bio = sharedPreferences.getString("bio","");
        String profileImage = sharedPreferences.getString("profile_image","");

        apiService.getReels("Bearer "+jwtToken,userId).enqueue(new Callback<List<PostDTO>>() {
            @Override
            public void onResponse(Call<List<PostDTO>> call, Response<List<PostDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());
                    reelsAdapter.notifyDataSetChanged();
                    // Phát video đầu tiên nếu có dữ liệu
                    if (!postList.isEmpty()) {
                        reelsAdapter.setCurrentPlayingPosition(0);
                    }
                }
                binding.loadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<PostDTO>> call, Throwable t) {
                binding.loadingIndicator.setVisibility(View.GONE);
                // Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}