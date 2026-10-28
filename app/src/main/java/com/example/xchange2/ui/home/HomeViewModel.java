package com.example.xchange2.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xchange2.data.model.Category;
import com.example.xchange2.data.model.ListingType;
import com.example.xchange2.data.model.Product;
import com.example.xchange2.data.repository.XchangeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HomeViewModel extends ViewModel {

    private final XchangeRepository repository = XchangeRepository.getInstance();

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Category> selectedCategory = new MutableLiveData<>(Category.ALL);

    private final MediatorLiveData<List<Product>> filteredProducts = new MediatorLiveData<>();
    private final MediatorLiveData<List<Product>> freeProducts = new MediatorLiveData<>();

    public HomeViewModel() {
        filteredProducts.addSource(repository.getProducts(), products -> updateFilteredProducts());
        filteredProducts.addSource(searchQuery, query -> updateFilteredProducts());
        filteredProducts.addSource(selectedCategory, category -> updateFilteredProducts());

        freeProducts.addSource(repository.getProducts(), products -> updateFreeProducts());
    }

    private void updateFilteredProducts() {
        List<Product> all = repository.getProducts().getValue();
        String query = searchQuery.getValue() != null ? searchQuery.getValue().toLowerCase().trim() : "";
        Category category = selectedCategory.getValue() != null ? selectedCategory.getValue() : Category.ALL;

        if (all == null) {
            filteredProducts.setValue(new ArrayList<>());
            return;
        }

        List<Product> result = new ArrayList<>();
        for (Product p : all) {
            boolean matchesSearch = query.isEmpty() || p.getTitle().toLowerCase().contains(query)
                    || p.getDescription().toLowerCase().contains(query)
                    || p.getLocation().toLowerCase().contains(query);
            boolean matchesCategory = category == Category.ALL || p.getCategory() == category;

            if (matchesSearch && matchesCategory) {
                result.add(p);
            }
        }
        filteredProducts.setValue(result);
    }

    private void updateFreeProducts() {
        List<Product> all = repository.getProducts().getValue();
        if (all == null) {
            freeProducts.setValue(new ArrayList<>());
            return;
        }

        List<Product> result = new ArrayList<>();
        for (Product p : all) {
            if (p.isDonate() || p.getListingType() == ListingType.DONATE) {
                result.add(p);
            }
        }
        freeProducts.setValue(result);
    }

    public LiveData<List<Product>> getFilteredProducts() {
        return filteredProducts;
    }

    public LiveData<List<Product>> getFreeProducts() {
        return freeProducts;
    }

    public LiveData<Set<String>> getFavoriteIds() {
        return repository.getFavoriteIds();
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void setSelectedCategory(Category category) {
        selectedCategory.setValue(category);
    }

    public Category getSelectedCategory() {
        return selectedCategory.getValue() != null ? selectedCategory.getValue() : Category.ALL;
    }

    public void toggleFavorite(String productId) {
        repository.toggleFavorite(productId);
    }
}