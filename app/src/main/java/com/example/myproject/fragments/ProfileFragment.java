package com.example.myproject.fragments;

import static com.example.myproject.LoginActivity.jwtToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.myproject.EditProfileActivity;
import com.example.myproject.LoginActivity;
import com.example.myproject.R;
import com.example.myproject.adapters.ViewPagerAdapter;
import com.example.myproject.databinding.FragmentProfileBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.tabs.TabLayout;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ViewPagerAdapter viewPagerAdapter;
    private LoginActivity loginActivity;
    private static final int REQUEST_CODE_EDIT_PROFILE = 1;
    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Xử lý kết quả: tải lại dữ liệu người dùng
                        loadUserData();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false);

        viewPagerAdapter = new ViewPagerAdapter(requireActivity());
        viewPagerAdapter.addFragment(new MyPostsFragment());
        viewPagerAdapter.addFragment(new SavedPostsFragment());
        viewPagerAdapter.addFragment(new FavouritePostsFragment());
        binding.viewPager.setAdapter(viewPagerAdapter);
        // Liên kết TabLayout với ViewPager2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.grid);
                    break;
                case 1:
                    tab.setIcon(R.drawable.save);
                    break;
                case 2:
                    tab.setIcon(R.drawable.favourite_saved);
                    break;
            }
        }).attach();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        jwtToken = sharedPreferences.getString("jwt_token", "");
        String uname = sharedPreferences.getString("username","");
        String bio = sharedPreferences.getString("bio","");
        String profileImage = sharedPreferences.getString("profile_image","");
        int followings = sharedPreferences.getInt("followings",-1);
        int followers = sharedPreferences.getInt("followers",-1);
        int posts = sharedPreferences.getInt("posts",-1);
        binding.name.setText(uname);
        binding.headuname.setText(uname);
        binding.bio.setText(bio);
        if(profileImage ==null)
        {
            binding.profileImage.setImageResource(R.drawable.blankprofile);
        }
        else {
            Glide.with(ProfileFragment.this)
                    .load(profileImage)
                    .placeholder(R.drawable.blankprofile)
                    .into(binding.profileImage);
        }
        binding.following.setText("followings "+String.valueOf(followings));
        binding.followers.setText("followers "+String.valueOf(followers));
        binding.profilepost.setText("posts "+String.valueOf(posts));
        //Edit PROFILE button
        binding.editprofilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                editProfileLauncher.launch(intent);
            }
        });
        //logout btn
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear(); // Xoá toàn bộ dữ liệu đăng nhập
                editor.apply();
                startActivity(new Intent(requireContext(), LoginActivity.class));

            }
        });

        return binding.getRoot();
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        jwtToken = sharedPreferences.getString("jwt_token", "");
        int followings = sharedPreferences.getInt("followings",-1);
        int followers = sharedPreferences.getInt("followers",-1);
        int posts = sharedPreferences.getInt("posts",-1);
        String uname = sharedPreferences.getString("username","");
        String bio = sharedPreferences.getString("bio","");
        String profileImage = sharedPreferences.getString("profile_image","");
        binding.name.setText(uname);
        binding.headuname.setText(uname);
        binding.bio.setText(bio);
        Glide.with(ProfileFragment.this)
                .load(profileImage)
                .placeholder(R.drawable.blankprofile)
                .into(binding.profileImage);
        binding.following.setText("followings "+String.valueOf(followings));
        binding.followers.setText("followers "+String.valueOf(followers));
        binding.profilepost.setText("posts "+String.valueOf(posts));
        // Triển khai logic để tải lại dữ liệu người dùng
    }
}