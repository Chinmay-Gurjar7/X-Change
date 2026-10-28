package com.example.xchange2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.xchange2.R;
import com.example.xchange2.data.model.Category;
import com.example.xchange2.data.model.Product;
import com.example.xchange2.databinding.FragmentHomeBinding;
import com.example.xchange2.ui.details.ProductDetailActivity;

import java.util.Arrays;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private ProductAdapter mainAdapter;
    private ProductAdapter freeAdapter;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupCategories();
        setupProductList();

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnSellDonate.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.addItemFragment));

        viewModel.getFilteredProducts().observe(getViewLifecycleOwner(), products -> {
            mainAdapter.setProducts(products);
            if (products == null || products.isEmpty()) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmpty.setVisibility(View.GONE);
            }
        });

        viewModel.getFreeProducts().observe(getViewLifecycleOwner(), products -> {
            freeAdapter.setProducts(products);
        });

        viewModel.getFavoriteIds().observe(getViewLifecycleOwner(), favoriteIds -> {
            mainAdapter.setFavoriteIds(favoriteIds);
            freeAdapter.setFavoriteIds(favoriteIds);
        });
    }

    private void setupCategories() {
        categoryAdapter = new CategoryAdapter(Arrays.asList(Category.values()), category -> {
            viewModel.setSelectedCategory(category);
        });
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void setupProductList() {
        ProductAdapter.OnProductClickListener listener = new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Product product) {
                viewModel.toggleFavorite(product.getId());
            }
        };

        mainAdapter = new ProductAdapter(listener);
        binding.rvProducts.setAdapter(mainAdapter);

        freeAdapter = new ProductAdapter(listener);
        binding.rvFreeItems.setAdapter(freeAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}