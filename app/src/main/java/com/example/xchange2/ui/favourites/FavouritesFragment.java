package com.example.xchange2.ui.favourites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.xchange2.data.model.Product;
import com.example.xchange2.databinding.FragmentFavouritesBinding;
import com.example.xchange2.ui.details.ProductDetailActivity;
import com.example.xchange2.ui.home.ProductAdapter;

public class FavouritesFragment extends Fragment {

    private FragmentFavouritesBinding binding;
    private FavouritesViewModel viewModel;
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);

        adapter = new ProductAdapter(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Product product) {
                viewModel.removeFavorite(product.getId());
            }
        });

        binding.rvFavourites.setAdapter(adapter);

        viewModel.getFavoriteProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
            if (products == null || products.isEmpty()) {
                binding.layoutEmpty.setVisibility(View.VISIBLE);
                binding.rvFavourites.setVisibility(View.GONE);
            } else {
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.rvFavourites.setVisibility(View.VISIBLE);
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