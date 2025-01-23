package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myproject.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // thietlap binding
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // edge
        EdgeToEdge.enable(this);
        // doi mau chu
        String text = "<font color='#FF000000'>Have an account?</font> <font color='#1E88E5'>Log in</font>";
        binding.login.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        // thietlap windows
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.login.setOnClickListener(view->{
            startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
        });
    }
}