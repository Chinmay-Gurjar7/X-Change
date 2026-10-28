package com.example.xchange2.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.xchange2.data.model.ExchangeRequest;
import com.example.xchange2.data.model.Product;
import com.example.xchange2.data.model.RequestStatus;
import com.example.xchange2.data.model.User;
import com.example.xchange2.databinding.FragmentProfileBinding;
import com.example.xchange2.ui.details.ProductDetailActivity;
import com.example.xchange2.ui.home.ProductAdapter;
import com.example.xchange2.ui.onboarding.OnboardingActivity;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    private RequestsAdapter receivedAdapter;
    private RequestsAdapter sentAdapter;
    private ProductAdapter listingsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupAdapters();

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvUserName.setText(user.getName());
                binding.tvUserEmail.setText(user.getEmail());
                binding.tvUserLocation.setText("📍 " + user.getLocation());
            }
        });

        viewModel.getMyListings().observe(getViewLifecycleOwner(), products -> {
            listingsAdapter.setProducts(products);
            int count = products != null ? products.size() : 0;
            binding.tvListingsCount.setText(String.valueOf(count));

            if (count == 0) {
                binding.tvEmptyListings.setVisibility(View.VISIBLE);
                binding.rvMyListings.setVisibility(View.GONE);
            } else {
                binding.tvEmptyListings.setVisibility(View.GONE);
                binding.rvMyListings.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getDonationHistory().observe(getViewLifecycleOwner(), donations -> {
            int count = donations != null ? donations.size() : 0;
            binding.tvDonationsCount.setText(String.valueOf(count));
        });

        viewModel.getReceivedRequests().observe(getViewLifecycleOwner(), requests -> {
            receivedAdapter.setRequests(requests);
            if (requests == null || requests.isEmpty()) {
                binding.tvEmptyReceived.setVisibility(View.VISIBLE);
                binding.rvReceivedRequests.setVisibility(View.GONE);
            } else {
                binding.tvEmptyReceived.setVisibility(View.GONE);
                binding.rvReceivedRequests.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getSentRequests().observe(getViewLifecycleOwner(), requests -> {
            sentAdapter.setRequests(requests);
            if (requests == null || requests.isEmpty()) {
                binding.tvEmptySent.setVisibility(View.VISIBLE);
                binding.rvSentRequests.setVisibility(View.GONE);
            } else {
                binding.tvEmptySent.setVisibility(View.GONE);
                binding.rvSentRequests.setVisibility(View.VISIBLE);
            }
        });

        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        binding.btnExchange.setOnClickListener(v -> showExchangeDialog());

        binding.btnLogout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Logged out of demo session.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), OnboardingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void setupAdapters() {
        RequestsAdapter.OnRequestStatusActionListener listener = new RequestsAdapter.OnRequestStatusActionListener() {
            @Override
            public void onAccept(ExchangeRequest request) {
                viewModel.updateRequestStatus(request.getId(), RequestStatus.ACCEPTED);
                Toast.makeText(requireContext(), "Exchange Request Accepted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReject(ExchangeRequest request) {
                viewModel.updateRequestStatus(request.getId(), RequestStatus.REJECTED);
                Toast.makeText(requireContext(), "Exchange Request Rejected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(ExchangeRequest request) {
                viewModel.updateRequestStatus(request.getId(), RequestStatus.COMPLETED);
                Toast.makeText(requireContext(), "Exchange marked as Completed! Thank you for sharing.", Toast.LENGTH_SHORT).show();
            }
        };

        receivedAdapter = new RequestsAdapter(true, listener);
        binding.rvReceivedRequests.setAdapter(receivedAdapter);

        sentAdapter = new RequestsAdapter(false, listener);
        binding.rvSentRequests.setAdapter(sentAdapter);

        listingsAdapter = new ProductAdapter(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Product product) {
                // Not applicable inside my listings
            }
        });
        binding.rvMyListings.setAdapter(listingsAdapter);
    }

    private void showEditProfileDialog() {
        User current = viewModel.getCurrentUser().getValue();
        if (current == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Profile");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText etName = new EditText(requireContext());
        etName.setHint("Full Name");
        etName.setText(current.getName());
        layout.addView(etName);

        final EditText etLoc = new EditText(requireContext());
        etLoc.setHint("Campus Location");
        etLoc.setText(current.getLocation());
        layout.addView(etLoc);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString();
            String loc = etLoc.getText().toString();
            if (!name.trim().isEmpty()) {
                viewModel.updateProfile(name, loc);
                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showExchangeDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Propose Item Exchange");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText etOwnerId = new EditText(requireContext());
        etOwnerId.setHint("Target Owner ID (e.g. user_sarah)");
        layout.addView(etOwnerId);

        final EditText etProductTitle = new EditText(requireContext());
        etProductTitle.setHint("Target Item Title (e.g. Data Structures Book)");
        layout.addView(etProductTitle);

        final EditText etOfferedItem = new EditText(requireContext());
        etOfferedItem.setHint("Your Offered Item Details (What you are listing)");
        layout.addView(etOfferedItem);

        builder.setView(layout);

        builder.setPositiveButton("Send Offer", (dialog, which) -> {
            String ownerId = etOwnerId.getText() != null ? etOwnerId.getText().toString().trim() : "";
            String productTitle = etProductTitle.getText() != null ? etProductTitle.getText().toString().trim() : "";
            String offeredItem = etOfferedItem.getText() != null ? etOfferedItem.getText().toString().trim() : "";

            if (!ownerId.isEmpty() && !productTitle.isEmpty() && !offeredItem.isEmpty()) {
                boolean success = viewModel.sendExchangeRequest(
                        "p_custom_" + System.currentTimeMillis(),
                        productTitle,
                        ownerId,
                        "User (" + ownerId + ")",
                        "Hi! I'd like to exchange items with you.",
                        offeredItem
                );
                if (success) {
                    Toast.makeText(requireContext(), "Exchange offer sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Active request already exists.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields to list your item.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}