package com.example.xchange2.ui.messages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.xchange2.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatViewModel viewModel;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        String convId = getIntent().getStringExtra("conversation_id");
        String sellerName = getIntent().getStringExtra("seller_name");
        String productTitle = getIntent().getStringExtra("product_title");

        if (convId != null) {
            viewModel.setConversationId(convId);
        }

        binding.tvTitle.setText(sellerName != null ? sellerName : "Seller");
        binding.tvSubtitle.setText(productTitle != null ? "Re: " + productTitle : "Item Inquiry");

        binding.btnBack.setOnClickListener(v -> finish());

        adapter = new ChatAdapter();
        binding.rvMessages.setAdapter(adapter);

        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                adapter.setCurrentUserId(user.getId());
            }
        });

        viewModel.getMessages().observe(this, messages -> {
            adapter.setMessages(messages);
            if (messages != null && !messages.isEmpty()) {
                binding.rvMessages.smoothScrollToPosition(messages.size() - 1);
            }
        });

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.etMessage.getText() != null ? binding.etMessage.getText().toString() : "";
            if (!text.trim().isEmpty()) {
                viewModel.sendMessage(text);
                binding.etMessage.setText("");
            }
        });
    }
}