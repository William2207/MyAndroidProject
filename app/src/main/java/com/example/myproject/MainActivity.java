package com.example.myproject;

import static com.example.myproject.LoginActivity.jwtToken;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        // Sử dụng Runnable đúng cách trong Java
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                jwtToken = sharedPreferences.getString("jwt_token", "");
                if (jwtToken != null && !jwtToken.isEmpty()) {
                    // Đã đăng nhập → vào HomeActivity (hoặc bất kỳ activity chính nào bạn có)
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                } else {
                    // Chưa đăng nhập → vào LoginActivity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 3000);

    }
}