package com.example.xchange2.ui.add;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.xchange2.R;
import com.example.xchange2.data.model.Category;
import com.example.xchange2.data.model.Condition;
import com.example.xchange2.data.model.ListingType;
import com.example.xchange2.databinding.FragmentAddItemBinding;

import java.util.ArrayList;
import java.util.List;

public class AddItemFragment extends Fragment {

    private FragmentAddItemBinding binding;
    private AddItemViewModel viewModel;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.ivPreview.setVisibility(View.VISIBLE);
                    binding.layoutPlaceholder.setVisibility(View.GONE);
                    Glide.with(this).load(uri).into(binding.ivPreview);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddItemBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AddItemViewModel.class);

        setupSpinners();

        binding.cardPickImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        binding.rgListingType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDonate) {
                binding.tilPrice.setVisibility(View.GONE);
                binding.etPrice.setText("0.0");
            } else {
                binding.tilPrice.setVisibility(View.VISIBLE);
                binding.etPrice.setText("");
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.tvError.setText(error);
                binding.tvError.setVisibility(View.VISIBLE);
            } else {
                binding.tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Listing Published Successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.homeFragment);
            }
        });

        binding.btnPublish.setOnClickListener(v -> publishListing());
    }

    private void setupSpinners() {
        List<String> categories = new ArrayList<>();
        for (Category c : Category.values()) {
            categories.add(c.getDisplayName());
        }
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        binding.spinnerCategory.setAdapter(catAdapter);

        List<String> conditions = new ArrayList<>();
        for (Condition c : Condition.values()) {
            conditions.add(c.getDisplayName());
        }
        ArrayAdapter<String> condAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, conditions);
        binding.spinnerCondition.setAdapter(condAdapter);
    }

    private void publishListing() {
        String title = binding.etTitle.getText() != null ? binding.etTitle.getText().toString() : "";
        String description = binding.etDescription.getText() != null ? binding.etDescription.getText().toString() : "";
        String priceStr = binding.etPrice.getText() != null ? binding.etPrice.getText().toString() : "0";
        String location = binding.etLocation.getText() != null ? binding.etLocation.getText().toString() : "";

        Category category = Category.values()[binding.spinnerCategory.getSelectedItemPosition()];
        Condition condition = Condition.values()[binding.spinnerCondition.getSelectedItemPosition()];
        ListingType type = (binding.rgListingType.getCheckedRadioButtonId() == R.id.rbDonate)
                ? ListingType.DONATE : ListingType.SELL;

        String imageStr = selectedImageUri != null ? selectedImageUri.toString() : null;

        viewModel.publishListing(title, description, category, condition, type, priceStr, location, imageStr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}