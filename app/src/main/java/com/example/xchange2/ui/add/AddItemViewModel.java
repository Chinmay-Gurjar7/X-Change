package com.example.xchange2.ui.add;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xchange2.data.model.Category;
import com.example.xchange2.data.model.Condition;
import com.example.xchange2.data.model.ListingType;
import com.example.xchange2.data.model.Product;
import com.example.xchange2.data.model.User;
import com.example.xchange2.data.repository.XchangeRepository;

import java.util.UUID;

public class AddItemViewModel extends ViewModel {

    private final XchangeRepository repository = XchangeRepository.getInstance();

    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public void publishListing(String title, String description, Category category,
                               Condition condition, ListingType listingType,
                               String priceStr, String location, String imageUri) {
        if (title == null || title.trim().isEmpty()) {
            error.setValue("Please enter an item title.");
            return;
        }
        if (description == null || description.trim().isEmpty()) {
            error.setValue("Please enter a short item description.");
            return;
        }
        if (location == null || location.trim().isEmpty()) {
            error.setValue("Please enter your location.");
            return;
        }

        double price = 0.0;
        if (listingType == ListingType.SELL) {
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    error.setValue("Price must be greater than zero for sell listings.");
                    return;
                }
            } catch (Exception e) {
                error.setValue("Please enter a valid price amount.");
                return;
            }
        }

        User user = repository.getCurrentUser().getValue();
        String ownerId = user != null ? user.getId() : "demo_user";
        String ownerName = user != null ? user.getName() : "Demo User";

        String finalImage = (imageUri != null && !imageUri.trim().isEmpty())
                ? imageUri
                : "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=500";

        Product product = new Product(
                "p_" + UUID.randomUUID().toString().substring(0, 8),
                ownerId,
                ownerName,
                title.trim(),
                description.trim(),
                category,
                condition,
                listingType,
                price,
                finalImage,
                location.trim(),
                System.currentTimeMillis(),
                true
        );

        repository.addProduct(product);
        success.setValue(true);
    }
}