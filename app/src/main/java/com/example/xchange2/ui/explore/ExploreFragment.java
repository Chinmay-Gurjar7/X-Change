package com.example.xchange2.ui.explore;

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

import com.example.xchange2.data.model.Category;
import com.example.xchange2.data.model.Product;
import com.example.xchange2.databinding.FragmentExploreBinding;
import com.example.xchange2.ui.details.ProductDetailActivity;
import com.example.xchange2.ui.home.CategoryAdapter;
import com.example.xchange2.ui.home.ProductAdapter;

import java.util.Arrays;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private ExploreViewModel viewModel;
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ExploreViewModel.class);

        CategoryAdapter categoryAdapter = new CategoryAdapter(Arrays.asList(Category.values()), category -> {
            viewModel.setSelectedCategory(category);
        });
        binding.rvCategories.setAdapter(categoryAdapter);

        adapter = new ProductAdapter(new ProductAdapter.OnProductClickListener() {
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
        });
        binding.rvProducts.setAdapter(adapter);

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

        binding.chipFreeOnly.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.setFreeOnly(isChecked));

        binding.chipSort.setOnClickListener(v -> {
            if (viewModel.getSortOrder() == ExploreViewModel.SortOrder.NEWEST) {
                viewModel.setSortOrder(ExploreViewModel.SortOrder.LOWEST_PRICE);
                binding.chipSort.setText("Sort: Price (Low)");
            } else {
                viewModel.setSortOrder(ExploreViewModel.SortOrder.NEWEST);
                binding.chipSort.setText("Sort: Newest");
            }
        });

        viewModel.getExploreProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
            int count = products != null ? products.size() : 0;
            binding.tvCount.setText(count + " items");
            if (count == 0) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmpty.setVisibility(View.GONE);
            }
        });

        viewModel.getFavoriteIds().observe(getViewLifecycleOwner(), favoriteIds -> {
            adapter.setFavoriteIds(favoriteIds);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}