package com.example.xchange2.ui.messages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.xchange2.data.repository.XchangeRepository;
import com.example.xchange2.databinding.FragmentMessagesBinding;

public class MessagesFragment extends Fragment {

    private FragmentMessagesBinding binding;
    private ConversationAdapter adapter;
    private final XchangeRepository repository = XchangeRepository.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ConversationAdapter(conversation -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("conversation_id", conversation.getId());
            intent.putExtra("seller_name", conversation.getOtherUserName());
            intent.putExtra("product_title", conversation.getProductTitle());
            startActivity(intent);
        });

        binding.rvConversations.setAdapter(adapter);

        repository.getConversations().observe(getViewLifecycleOwner(), conversations -> {
            adapter.setConversations(conversations);
            if (conversations == null || conversations.isEmpty()) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.rvConversations.setVisibility(View.GONE);
            } else {
                binding.tvEmpty.setVisibility(View.GONE);
                binding.rvConversations.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}