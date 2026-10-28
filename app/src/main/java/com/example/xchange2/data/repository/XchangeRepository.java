package com.example.xchange2.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.xchange2.data.model.Category;
import com.example.xchange2.data.model.ChatMessage;
import com.example.xchange2.data.model.Condition;
import com.example.xchange2.data.model.Conversation;
import com.example.xchange2.data.model.ExchangeRequest;
import com.example.xchange2.data.model.ListingType;
import com.example.xchange2.data.model.Product;
import com.example.xchange2.data.model.RequestStatus;
import com.example.xchange2.data.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class XchangeRepository {

    private static XchangeRepository instance;

    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Set<String>> favoriteIds = new MutableLiveData<>(new HashSet<>());
    private final MutableLiveData<List<ExchangeRequest>> exchangeRequests = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Conversation>> conversations = new MutableLiveData<>(new ArrayList<>());
    private final Map<String, MutableLiveData<List<ChatMessage>>> chatMessagesMap = new HashMap<>();

    private XchangeRepository() {
        initSampleData();
    }

    public static synchronized XchangeRepository getInstance() {
        if (instance == null) {
            instance = new XchangeRepository();
        }
        return instance;
    }

    private void initSampleData() {
        User defaultUser = new User("user_demo_1", "Alex Morgan", "alex@example.com", "Campus North, Block B", null);
        currentUser.setValue(defaultUser);

        List<Product> sampleProducts = new ArrayList<>();
        long now = System.currentTimeMillis();

        sampleProducts.add(new Product(
                "p1", "user_sarah", "Sarah Connor",
                "Data Structures & Algorithms Textbook",
                "4th Edition in great shape. No missing pages. Giving away for free to help fellow CS students pass their exams!",
                Category.BOOKS, Condition.GOOD, ListingType.DONATE, 0.0,
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500",
                "University Library, Area A", now - 3600000L * 2, true
        ));

        sampleProducts.add(new Product(
                "p2", "user_john", "John Doe",
                "Warm North Face Winter Jacket",
                "Size Medium, black color. Super warm and water resistant. Worn for only one season.",
                Category.CLOTHING, Condition.LIKE_NEW, ListingType.SELL, 25.0,
                "https://images.unsplash.com/photo-1548883354-7622d03aca27?w=500",
                "Student Center, 2nd Floor", now - 3600000L * 10, true
        ));

        sampleProducts.add(new Product(
                "p3", "user_emily", "Emily Watson",
                "Casio FX-991EX Scientific Calculator",
                "Fully working solar calculator with engineering functions. Free for math/physics classes.",
                Category.ELECTRONICS, Condition.LIKE_NEW, ListingType.DONATE, 0.0,
                "https://images.unsplash.com/photo-1611125832047-1d7ad1e8e48f?w=500",
                "Engineering Block 3", now - 3600000L * 24, true
        ));

        sampleProducts.add(new Product(
                "p4", "user_mike", "Michael Scott",
                "Compact Wooden Study Table & Chair",
                "Sturdy wooden desk perfect for dorm rooms or small apartments. Easy to disassemble.",
                Category.FURNITURE, Condition.GOOD, ListingType.SELL, 35.0,
                "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=500",
                "West Dorms, Hall 4", now - 3600000L * 30, true
        ));

        sampleProducts.add(new Product(
                "p5", "user_demo_1", "Alex Morgan",
                "Nike Air Zoom Running Shoes",
                "Size 10 US. Lightweight and extremely comfortable for daily runs or gym workouts.",
                Category.SHOES, Condition.NEW, ListingType.SELL, 40.0,
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500",
                "Campus North, Block B", now - 3600000L * 48, true
        ));

        products.setValue(sampleProducts);

        List<ExchangeRequest> sampleRequests = new ArrayList<>();
        sampleRequests.add(new ExchangeRequest(
                "req_1", "p5", "Nike Air Zoom Running Shoes",
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500",
                "user_john", "John Doe", "user_demo_1", "Alex Morgan",
                RequestStatus.PENDING, "Hi Alex, I'd like to exchange items with you!",
                "Wireless Gaming Mouse", null, now - 1800000L
        ));

        exchangeRequests.setValue(sampleRequests);

        List<Conversation> sampleConvs = new ArrayList<>();
        sampleConvs.add(new Conversation(
                "conv_1", "p1", "Data Structures Textbook",
                "user_sarah", "Sarah Connor",
                "Sure! We can meet at the library tomorrow at 2 PM.",
                now - 900000L
        ));
        conversations.setValue(sampleConvs);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("m1", "conv_1", "user_demo_1", "Alex Morgan", "Hi Sarah! Is the Data Structures textbook still available?", now - 3600000L));
        messages.add(new ChatMessage("m2", "conv_1", "user_sarah", "Sarah Connor", "Hi Alex! Yes it is. Would you like to pick it up?", now - 1800000L));
        messages.add(new ChatMessage("m3", "conv_1", "user_demo_1", "Alex Morgan", "That would be awesome! Where and when can we meet?", now - 1200000L));
        messages.add(new ChatMessage("m4", "conv_1", "user_sarah", "Sarah Connor", "Sure! We can meet at the library tomorrow at 2 PM.", now - 900000L));

        chatMessagesMap.put("conv_1", new MutableLiveData<>(messages));
    }

    public LiveData<User> getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { currentUser.setValue(user); }
    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<Set<String>> getFavoriteIds() { return favoriteIds; }

    public void toggleFavorite(String productId) {
        Set<String> set = new HashSet<>(favoriteIds.getValue() != null ? favoriteIds.getValue() : new HashSet<>());
        if (set.contains(productId)) set.remove(productId);
        else set.add(productId);
        favoriteIds.setValue(set);
    }

    public void addProduct(Product product) {
        List<Product> currentList = new ArrayList<>(products.getValue() != null ? products.getValue() : new ArrayList<>());
        currentList.add(0, product);
        products.setValue(currentList);
    }

    public void deleteProduct(String productId) {
        List<Product> currentList = new ArrayList<>(products.getValue() != null ? products.getValue() : new ArrayList<>());
        Iterator<Product> iterator = currentList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(productId)) {
                iterator.remove();
                break;
            }
        }
        products.setValue(currentList);
    }

    public LiveData<List<ExchangeRequest>> getExchangeRequests() { return exchangeRequests; }

    public boolean sendExchangeRequest(String productId, String productTitle, String productImageUrl,
                                      String ownerId, String ownerName, String message) {
        return sendExchangeRequestFull(productId, productTitle, productImageUrl, ownerId, ownerName, message, null);
    }

    public boolean sendExchangeRequestFull(String productId, String productTitle, String productImageUrl,
                                         String ownerId, String ownerName, String message, String offeredItemTitle) {
        User user = currentUser.getValue();
        if (user == null) return false;

        List<ExchangeRequest> list = new ArrayList<>(exchangeRequests.getValue() != null ? exchangeRequests.getValue() : new ArrayList<>());

        for (ExchangeRequest req : list) {
            if (req.getProductId().equals(productId) && req.getRequesterId().equals(user.getId())
                    && (req.getStatus() == RequestStatus.PENDING || req.getStatus() == RequestStatus.ACCEPTED)) {
                return false;
            }
        }

        ExchangeRequest newReq = new ExchangeRequest(
                UUID.randomUUID().toString(),
                productId,
                productTitle,
                productImageUrl,
                user.getId(),
                user.getName(),
                ownerId,
                ownerName,
                RequestStatus.PENDING,
                message,
                offeredItemTitle,
                null,
                System.currentTimeMillis()
        );

        list.add(0, newReq);
        exchangeRequests.setValue(list);
        return true;
    }

    public void updateRequestStatus(String requestId, RequestStatus newStatus) {
        List<ExchangeRequest> list = new ArrayList<>(exchangeRequests.getValue() != null ? exchangeRequests.getValue() : new ArrayList<>());
        for (ExchangeRequest req : list) {
            if (req.getId().equals(requestId)) {
                req.setStatus(newStatus);
                if (newStatus == RequestStatus.ACCEPTED && (req.getVerificationCode() == null || req.getVerificationCode().isEmpty())) {
                    String code = String.format("%04d", new Random().nextInt(10000));
                    req.setVerificationCode(code);
                }
                break;
            }
        }
        exchangeRequests.setValue(list);
    }

    public LiveData<List<Conversation>> getConversations() { return conversations; }

    public LiveData<List<ChatMessage>> getChatMessages(String conversationId) {
        if (!chatMessagesMap.containsKey(conversationId)) {
            chatMessagesMap.put(conversationId, new MutableLiveData<>(new ArrayList<>()));
        }
        return chatMessagesMap.get(conversationId);
    }

    public void sendChatMessage(String conversationId, String text) {
        User user = currentUser.getValue();
        if (user == null || text.trim().isEmpty()) return;

        List<ChatMessage> list = new ArrayList<>();
        MutableLiveData<List<ChatMessage>> liveData = chatMessagesMap.get(conversationId);
        if (liveData != null && liveData.getValue() != null) {
            list.addAll(liveData.getValue());
        }

        ChatMessage newMsg = new ChatMessage(
                UUID.randomUUID().toString(), conversationId,
                user.getId(), user.getName(), text.trim(), System.currentTimeMillis()
        );
        list.add(newMsg);

        if (liveData == null) {
            liveData = new MutableLiveData<>();
            chatMessagesMap.put(conversationId, liveData);
        }
        liveData.setValue(list);

        List<Conversation> convs = new ArrayList<>(conversations.getValue() != null ? conversations.getValue() : new ArrayList<>());
        for (Conversation c : convs) {
            if (c.getId().equals(conversationId)) {
                c.setLastMessage(text.trim());
                c.setLastUpdated(System.currentTimeMillis());
                break;
            }
        }
        conversations.setValue(convs);
    }

    public String startOrGetConversation(String productId, String productTitle, String sellerId, String sellerName) {
        User user = currentUser.getValue();
        if (user == null) return null;

        List<Conversation> convs = new ArrayList<>(conversations.getValue() != null ? conversations.getValue() : new ArrayList<>());
        for (Conversation c : convs) {
            if (c.getProductId().equals(productId) && c.getOtherUserId().equals(sellerId)) {
                return c.getId();
            }
        }

        String newConvId = "conv_" + UUID.randomUUID().toString().substring(0, 8);
        Conversation newConv = new Conversation(
                newConvId, productId, productTitle, sellerId, sellerName,
                "Inquiry started", System.currentTimeMillis()
        );
        convs.add(0, newConv);
        conversations.setValue(convs);

        chatMessagesMap.put(newConvId, new MutableLiveData<>(new ArrayList<>()));
        return newConvId;
    }

    public String startOrGetConversationByUserId(String peerUserId) {
        if (peerUserId == null || peerUserId.trim().isEmpty()) return null;
        List<Conversation> convs = new ArrayList<>(conversations.getValue() != null ? conversations.getValue() : new ArrayList<>());
        for (Conversation c : convs) {
            if (c.getOtherUserId().equals(peerUserId.trim())) {
                return c.getId();
            }
        }
        String newConvId = "conv_" + UUID.randomUUID().toString().substring(0, 8);
        Conversation newConv = new Conversation(
                newConvId, "p_custom", "Direct Exchange",
                peerUserId.trim(), "User (" + peerUserId.trim() + ")",
                "Exchange inquiry started", System.currentTimeMillis()
        );
        convs.add(0, newConv);
        conversations.setValue(convs);
        chatMessagesMap.put(newConvId, new MutableLiveData<>(new ArrayList<>()));
        return newConvId;
    }
}