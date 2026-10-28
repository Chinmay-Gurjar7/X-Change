package com.example.xchange2.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.xchange2.R;
import com.example.xchange2.data.model.Product;
import com.example.xchange2.databinding.ActivityProductDetailBinding;
import com.example.xchange2.ui.messages.ChatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ProductDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);

        String productId = getIntent().getStringExtra("product_id");
        if (productId != null) {
            viewModel.setProductId(productId);
        }

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnFavorite.setOnClickListener(v -> viewModel.toggleFavorite());

        viewModel.getProduct().observe(this, product -> {
            if (product == null) return;

            binding.tvTitle.setText(product.getTitle());
            binding.tvDescription.setText(product.getDescription());
            binding.tvCondition.setText(product.getCondition().getDisplayName());
            binding.tvCategory.setText(product.getCategory().getDisplayName());
            binding.tvLocation.setText(product.getLocation());
            binding.tvSellerName.setText(product.getOwnerName());

            if (product.isDonate()) {
                binding.tvPrice.setText("FREE");
                binding.tvPrice.setTextColor(getColor(R.color.free_badge_color));
            } else {
                binding.tvPrice.setText(String.format("$%.2f", product.getPrice()));
                binding.tvPrice.setTextColor(getColor(R.color.primary_forest_green));
            }

            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_xchange_logo)
                    .error(R.drawable.ic_xchange_logo)
                    .into(binding.ivProduct);
        });

        viewModel.getFavoriteIds().observe(this, favoriteIds -> {
            Product p = viewModel.getProduct().getValue();
            if (p != null) {
                boolean isFav = favoriteIds != null && favoriteIds.contains(p.getId());
                binding.btnFavorite.setImageResource(
                        isFav ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
                );
            }
        });

        viewModel.getRequestMessageResult().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnRequestItem.setOnClickListener(v -> {
            viewModel.sendRequest("Hi! I would love to get this item. Thank you!");
        });

        binding.btnContactSeller.setOnClickListener(v -> {
            String convId = viewModel.startChatWithSeller();
            if (convId != null) {
                Product p = viewModel.getProduct().getValue();
                Intent intent = new Intent(ProductDetailActivity.this, ChatActivity.class);
                intent.putExtra("conversation_id", convId);
                if (p != null) {
                    intent.putExtra("seller_name", p.getOwnerName());
                    intent.putExtra("product_title", p.getTitle());
                }
                startActivity(intent);
            }
        });
    }
}