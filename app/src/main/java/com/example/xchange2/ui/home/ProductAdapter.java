package com.example.xchange2.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xchange2.R;
import com.example.xchange2.data.model.Product;
import com.example.xchange2.databinding.ItemProductBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onFavoriteClick(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private final Set<String> favoriteIds = new HashSet<>();
    private final OnProductClickListener listener;

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> newProducts) {
        products.clear();
        if (newProducts != null) {
            products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    public void setFavoriteIds(Set<String> newFavoriteIds) {
        favoriteIds.clear();
        if (newFavoriteIds != null) {
            favoriteIds.addAll(newFavoriteIds);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        boolean isFavorite = favoriteIds.contains(product.getId());
        holder.bind(product, isFavorite);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        public ProductViewHolder(@NonNull ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product, boolean isFavorite) {
            Context context = itemView.getContext();

            binding.tvTitle.setText(product.getTitle());
            binding.tvCondition.setText(product.getCondition().getDisplayName());
            binding.tvCategory.setText(product.getCategory().getDisplayName());
            binding.tvLocation.setText(product.getLocation());

            if (product.isDonate()) {
                binding.tvPriceBadge.setText("FREE");
                binding.tvPriceBadge.setBackgroundResource(R.drawable.badge_bg);
            } else {
                binding.tvPriceBadge.setText(String.format("₹%.2f", product.getPrice()));
                binding.tvPriceBadge.setBackgroundResource(R.drawable.badge_bg);
            }

            binding.ivFavorite.setImageResource(
                    isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
            );

            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_xchange_logo)
                    .error(R.drawable.ic_xchange_logo)
                    .into(binding.ivProduct);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            binding.ivFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(product);
                }
            });
        }
    }
}