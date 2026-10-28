package com.example.xchange2.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xchange2.R;
import com.example.xchange2.data.model.ExchangeRequest;
import com.example.xchange2.data.model.RequestStatus;
import com.example.xchange2.databinding.ItemRequestBinding;

import java.util.ArrayList;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

    public interface OnRequestStatusActionListener {
        void onAccept(ExchangeRequest request);
        void onReject(ExchangeRequest request);
        void onComplete(ExchangeRequest request);
    }

    private final List<ExchangeRequest> requests = new ArrayList<>();
    private final OnRequestStatusActionListener listener;
    private final boolean isReceivedMode; // True for Received Requests, False for Sent Requests

    public RequestsAdapter(boolean isReceivedMode, OnRequestStatusActionListener listener) {
        this.isReceivedMode = isReceivedMode;
        this.listener = listener;
    }

    public void setRequests(List<ExchangeRequest> list) {
        requests.clear();
        if (list != null) {
            requests.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRequestBinding binding = ItemRequestBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        holder.bind(requests.get(position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private final ItemRequestBinding binding;

        public RequestViewHolder(@NonNull ItemRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ExchangeRequest request) {
            Context context = itemView.getContext();

            binding.tvProductTitle.setText(request.getProductTitle());
            binding.tvNote.setText(request.getMessage() != null ? request.getMessage() : "No message provided.");

            if (isReceivedMode) {
                binding.tvRequester.setText("From: " + request.getRequesterName());
            } else {
                binding.tvRequester.setText("Seller: " + request.getOwnerName());
            }

            RequestStatus status = request.getStatus();
            binding.tvStatusBadge.setText(status.getDisplayName());

            int statusColor = ContextCompat.getColor(context, R.color.status_pending);
            if (status == RequestStatus.ACCEPTED) statusColor = ContextCompat.getColor(context, R.color.status_accepted);
            else if (status == RequestStatus.REJECTED) statusColor = ContextCompat.getColor(context, R.color.status_rejected);
            else if (status == RequestStatus.COMPLETED) statusColor = ContextCompat.getColor(context, R.color.status_completed);

            binding.tvStatusBadge.setBackgroundColor(statusColor);

            Glide.with(context)
                    .load(request.getProductImageUrl())
                    .placeholder(R.drawable.ic_xchange_logo)
                    .into(binding.ivProduct);

            // Action Buttons visibility
            if (isReceivedMode) {
                binding.layoutActions.setVisibility(View.VISIBLE);
                if (status == RequestStatus.PENDING) {
                    binding.btnAccept.setVisibility(View.VISIBLE);
                    binding.btnReject.setVisibility(View.VISIBLE);
                    binding.btnComplete.setVisibility(View.GONE);
                } else if (status == RequestStatus.ACCEPTED) {
                    binding.btnAccept.setVisibility(View.GONE);
                    binding.btnReject.setVisibility(View.GONE);
                    binding.btnComplete.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutActions.setVisibility(View.GONE);
                }
            } else {
                binding.layoutActions.setVisibility(View.GONE);
            }

            binding.btnAccept.setOnClickListener(v -> {
                if (listener != null) listener.onAccept(request);
            });

            binding.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(request);
            });

            binding.btnComplete.setOnClickListener(v -> {
                if (listener != null) listener.onComplete(request);
            });
        }
    }
}