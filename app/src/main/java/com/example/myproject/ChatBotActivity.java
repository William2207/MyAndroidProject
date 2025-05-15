package com.example.myproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.adapters.ChatAdapter;
import com.example.myproject.apiservice.ApiService;
import com.example.myproject.apiservice.RetrofitClient;
import com.example.myproject.models.ChatBotMessage;
import com.example.myproject.models.ChatRequest;
import com.example.myproject.models.ChatResponse;
import com.example.myproject.models.MessageBot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBotActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private ApiService openRouterApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_bot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        // Thiết lập RecyclerView
        chatAdapter = new ChatAdapter();
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1); // Cuộn xuống cuối

        // Khởi tạo OpenRouterApi
        openRouterApi = RetrofitClient.getOpenRouterRetrofit().create(ApiService.class);

        // Xử lý sự kiện nhấn nút Gửi
        sendButton.setOnClickListener(v -> {
            String userMessage = messageEditText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                // Thêm tin nhắn người dùng vào RecyclerView
                chatAdapter.addMessage(new ChatBotMessage(userMessage, true));
                chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                messageEditText.setText(""); // Xóa ô nhập

                // Gửi tin nhắn đến OpenRouter
                sendMessageToOpenRouter(userMessage);
            } else {
                Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToOpenRouter(String userMessage) {
        List<MessageBot> messages = new ArrayList<>();
        messages.add(new MessageBot("user", userMessage));
        ChatRequest request = new ChatRequest("deepseek/deepseek-prover-v2:free", messages); // Thay bằng model bạn chọn

        Call<ChatResponse> call = openRouterApi.sendMessage(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String botReply = response.body().choices.get(0).message.content;
                    // Thêm phản hồi của chatbot vào RecyclerView
                    chatAdapter.addMessage(new ChatBotMessage(botReply, false));
                    chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                } else {
                    Toast.makeText(ChatBotActivity.this, "Lỗi khi nhận phản hồi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Toast.makeText(ChatBotActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}