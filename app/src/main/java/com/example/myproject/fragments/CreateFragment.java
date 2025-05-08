package com.example.myproject.fragments;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myproject.databinding.FragmentCreateBinding;
import com.example.myproject.post.PostActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CreateFragment extends BottomSheetDialogFragment {
    private FragmentCreateBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateBinding.inflate(inflater,container,false);
        //post
        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PostActivity.class));
            }
        });

        //reel
        binding.reel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ReelFragment.class));
            }
        });

        return binding.getRoot();
    }
}