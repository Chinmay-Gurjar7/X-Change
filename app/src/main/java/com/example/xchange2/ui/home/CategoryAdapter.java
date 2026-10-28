package com.example.xchange2.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xchange2.R;
import com.example.xchange2.data.model.Category;
import com.example.xchange2.databinding.ItemCategoryBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final List<Category> categories;
    private final OnCategoryClickListener listener;
    private Category selectedCategory = Category.ALL;

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public void setSelectedCategory(Category category) {
        this.selectedCategory = category;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category, category == selectedCategory);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Category category, boolean isSelected) {
            Context context = itemView.getContext();
            binding.tvCategoryName.setText(category.getDisplayName());

            if (isSelected) {
                binding.cardCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_forest_green));
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.surface_card_white));
                binding.cardCategory.setStrokeWidth(0);
            } else {
                binding.cardCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface_card_white));
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_dark_charcoal));
                binding.cardCategory.setStrokeWidth(2);
                binding.cardCategory.setStrokeColor(ContextCompat.getColor(context, R.color.border_light_gray));
            }

            itemView.setOnClickListener(v -> {
                setSelectedCategory(category);
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
}