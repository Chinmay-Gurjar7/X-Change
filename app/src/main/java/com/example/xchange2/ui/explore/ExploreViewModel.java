package com.example.xchange2.ui.explore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xchange2.data.model.Category;
import com.example.xchange2.data.model.ListingType;
import com.example.xchange2.data.model.Product;
import com.example.xchange2.data.repository.XchangeRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ExploreViewModel extends ViewModel {

    public enum SortOrder { NEWEST, LOWEST_PRICE }

    private final XchangeRepository repository = XchangeRepository.getInstance();

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Category> selectedCategory = new MutableLiveData<>(Category.ALL);
    private final MutableLiveData<Boolean> freeOnly = new MutableLiveData<>(false);
    private final MutableLiveData<SortOrder> sortOrder = new MutableLiveData<>(SortOrder.NEWEST);

    private final MediatorLiveData<List<Product>> exploreProducts = new MediatorLiveData<>();

    public ExploreViewModel() {
        exploreProducts.addSource(repository.getProducts(), products -> updateList());
        exploreProducts.addSource(searchQuery, q -> updateList());
        exploreProducts.addSource(selectedCategory, c -> updateList());
        exploreProducts.addSource(freeOnly, f -> updateList());
        exploreProducts.addSource(sortOrder, s -> updateList());
    }

    private void updateList() {
        List<Product> all = repository.getProducts().getValue();
        if (all == null) {
            exploreProducts.setValue(new ArrayList<>());
            return;
        }

        String query = searchQuery.getValue() != null ? searchQuery.getValue().toLowerCase().trim() : "";
        Category category = selectedCategory.getValue() != null ? selectedCategory.getValue() : Category.ALL;
        boolean isFreeOnly = Boolean.TRUE.equals(freeOnly.getValue());
        SortOrder order = sortOrder.getValue() != null ? sortOrder.getValue() : SortOrder.NEWEST;

        List<Product> result = new ArrayList<>();
        for (Product p : all) {
            boolean matchesSearch = query.isEmpty() || p.getTitle().toLowerCase().contains(query)
                    || p.getDescription().toLowerCase().contains(query)
                    || p.getLocation().toLowerCase().contains(query);
            boolean matchesCategory = category == Category.ALL || p.getCategory() == category;
            boolean matchesFree = !isFreeOnly || p.isDonate() || p.getListingType() == ListingType.DONATE;

            if (matchesSearch && matchesCategory && matchesFree) {
                result.add(p);
            }
        }

        if (order == SortOrder.LOWEST_PRICE) {
            Collections.sort(result, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        } else {
            Collections.sort(result, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
        }

        exploreProducts.setValue(result);
    }

    public LiveData<List<Product>> getExploreProducts() { return exploreProducts; }
    public LiveData<Set<String>> getFavoriteIds() { return repository.getFavoriteIds(); }

    public void setSearchQuery(String query) { searchQuery.setValue(query); }
    public void setSelectedCategory(Category category) { selectedCategory.setValue(category); }
    public void setFreeOnly(boolean isFree) { freeOnly.setValue(isFree); }
    public void setSortOrder(SortOrder order) { sortOrder.setValue(order); }

    public boolean isFreeOnly() { return Boolean.TRUE.equals(freeOnly.getValue()); }
    public SortOrder getSortOrder() { return sortOrder.getValue() != null ? sortOrder.getValue() : SortOrder.NEWEST; }

    public void toggleFavorite(String productId) { repository.toggleFavorite(productId); }
}