package com.example.xchange2.ui.messages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xchange2.data.model.ChatMessage;
import com.example.xchange2.data.model.User;
import com.example.xchange2.data.repository.XchangeRepository;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final XchangeRepository repository = XchangeRepository.getInstance();
    private final MutableLiveData<String> conversationId = new MutableLiveData<>();

    public void setConversationId(String id) {
        conversationId.setValue(id);
    }

    public LiveData<List<ChatMessage>> getMessages() {
        if (conversationId.getValue() != null) {
            return repository.getChatMessages(conversationId.getValue());
        }
        return new MutableLiveData<>();
    }

    public LiveData<User> getCurrentUser() {
        return repository.getCurrentUser();
    }

    public void sendMessage(String text) {
        String convId = conversationId.getValue();
        if (convId != null) {
            repository.sendChatMessage(convId, text);
        }
    }
}