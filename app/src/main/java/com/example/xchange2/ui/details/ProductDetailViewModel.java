package com.example.xchange2.ui.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xchange2.data.model.Product;
import com.example.xchange2.data.model.User;
import com.example.xchange2.data.repository.XchangeRepository;

import java.util.List;
import java.util.Set;

public class ProductDetailViewModel extends ViewModel {

    private final XchangeRepository repository = XchangeRepository.getInstance();

    private final MutableLiveData<String> productId = new MutableLiveData<>();
    private final MediatorLiveData<Product> product = new MediatorLiveData<>();
    private final MutableLiveData<String> requestMessageResult = new MutableLiveData<>();

    public ProductDetailViewModel() {
        product.addSource(productId, id -> updateProduct());
        product.addSource(repository.getProducts(), products -> updateProduct());
    }

    private void updateProduct() {
        String id = productId.getValue();
        List<Product> list = repository.getProducts().getValue();
        if (id == null || list == null) return;

        for (Product p : list) {
            if (p.getId().equals(id)) {
                product.setValue(p);
                return;
            }
        }
    }

    public void setProductId(String id) {
        productId.setValue(id);
    }

    public LiveData<Product> getProduct() {
        return product;
    }

    public LiveData<User> getCurrentUser() {
        return repository.getCurrentUser();
    }

    public LiveData<Set<String>> getFavoriteIds() {
        return repository.getFavoriteIds();
    }

    public LiveData<String> getRequestMessageResult() {
        return requestMessageResult;
    }

    public void toggleFavorite() {
        Product p = product.getValue();
        if (p != null) {
            repository.toggleFavorite(p.getId());
        }
    }

    public void sendRequest(String note) {
        Product p = product.getValue();
        User user = repository.getCurrentUser().getValue();
        if (p == null || user == null) return;

        if (p.getOwnerId().equals(user.getId())) {
            requestMessageResult.setValue("You cannot request your own listing!");
            return;
        }

        boolean success = repository.sendExchangeRequest(
                p.getId(),
                p.getTitle(),
                p.getImageUrl(),
                p.getOwnerId(),
                p.getOwnerName(),
                (note != null && !note.trim().isEmpty()) ? note : "I am interested in this item!"
        );

        if (success) {
            requestMessageResult.setValue("Exchange request sent successfully!");
        } else {
            requestMessageResult.setValue("You already have an active request for this item.");
        }
    }

    public String startChatWithSeller() {
        Product p = product.getValue();
        if (p == null) return null;
        return repository.startOrGetConversation(p.getId(), p.getTitle(), p.getOwnerId(), p.getOwnerName());
    }
}