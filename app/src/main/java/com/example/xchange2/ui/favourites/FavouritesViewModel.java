package com.example.xchange2.ui.favourites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xchange2.data.model.Product;
import com.example.xchange2.data.repository.XchangeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavouritesViewModel extends ViewModel {

    private final XchangeRepository repository = XchangeRepository.getInstance();
    private final MediatorLiveData<List<Product>> favoriteProducts = new MediatorLiveData<>();

    public FavouritesViewModel() {
        favoriteProducts.addSource(repository.getProducts(), p -> updateFavorites());
        favoriteProducts.addSource(repository.getFavoriteIds(), f -> updateFavorites());
    }

    private void updateFavorites() {
        List<Product> all = repository.getProducts().getValue();
        Set<String> favIds = repository.getFavoriteIds().getValue();

        if (all == null || favIds == null || favIds.isEmpty()) {
            favoriteProducts.setValue(new ArrayList<>());
            return;
        }

        List<Product> result = new ArrayList<>();
        for (Product p : all) {
            if (favIds.contains(p.getId())) {
                result.add(p);
            }
        }
        favoriteProducts.setValue(result);
    }

    public LiveData<List<Product>> getFavoriteProducts() {
        return favoriteProducts;
    }

    public LiveData<Set<String>> getFavoriteIds() {
        return repository.getFavoriteIds();
    }

    public void removeFavorite(String productId) {
        repository.toggleFavorite(productId);
    }
}