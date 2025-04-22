package com.example.myproject.fragments;

import android.app.Activity;
import android.content.Intent;
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

        binding.name.setText(loginActivity.user.getName());
        binding.headuname.setText(loginActivity.user.getName());
        binding.bio.setText(loginActivity.user.getBio());
        if(loginActivity.user.getImage() ==null)
        {
            binding.profileImage.setImageResource(R.drawable.blankprofile);
        }
        else {
            Glide.with(ProfileFragment.this)
                    .load(loginActivity.user.getImage())
                    .placeholder(R.drawable.blankprofile)
                    .into(binding.profileImage);
        }
        //Edit PROFILE button
        binding.editprofilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                editProfileLauncher.launch(intent);
            }
        });
        return binding.getRoot();
    }

    private void loadUserData() {
        binding.name.setText(loginActivity.user.getName());
        binding.headuname.setText(loginActivity.user.getName());
        binding.bio.setText(loginActivity.user.getBio());
        // Triển khai logic để tải lại dữ liệu người dùng
    }
}