package com.example.xchange2.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xchange2.data.model.ExchangeRequest;
import com.example.xchange2.data.model.Product;
import com.example.xchange2.data.model.RequestStatus;
import com.example.xchange2.data.model.User;
import com.example.xchange2.data.repository.XchangeRepository;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {

    private final XchangeRepository repository = XchangeRepository.getInstance();

    private final MediatorLiveData<List<Product>> myListings = new MediatorLiveData<>();
    private final MediatorLiveData<List<ExchangeRequest>> sentRequests = new MediatorLiveData<>();
    private final MediatorLiveData<List<ExchangeRequest>> receivedRequests = new MediatorLiveData<>();
    private final MediatorLiveData<List<Product>> donationHistory = new MediatorLiveData<>();

    public ProfileViewModel() {
        myListings.addSource(repository.getProducts(), p -> updateData());
        myListings.addSource(repository.getCurrentUser(), u -> updateData());

        sentRequests.addSource(repository.getExchangeRequests(), r -> updateData());
        sentRequests.addSource(repository.getCurrentUser(), u -> updateData());

        receivedRequests.addSource(repository.getExchangeRequests(), r -> updateData());
        receivedRequests.addSource(repository.getCurrentUser(), u -> updateData());

        donationHistory.addSource(repository.getProducts(), p -> updateData());
        donationHistory.addSource(repository.getCurrentUser(), u -> updateData());
    }

    private void updateData() {
        User user = repository.getCurrentUser().getValue();
        if (user == null) return;

        // My Listings
        List<Product> allProducts = repository.getProducts().getValue();
        List<Product> myProds = new ArrayList<>();
        List<Product> donations = new ArrayList<>();

        if (allProducts != null) {
            for (Product p : allProducts) {
                if (p.getOwnerId().equals(user.getId())) {
                    myProds.add(p);
                    if (p.isDonate()) {
                        donations.add(p);
                    }
                }
            }
        }
        myListings.setValue(myProds);
        donationHistory.setValue(donations);

        // Requests
        List<ExchangeRequest> allReqs = repository.getExchangeRequests().getValue();
        List<ExchangeRequest> sent = new ArrayList<>();
        List<ExchangeRequest> received = new ArrayList<>();

        if (allReqs != null) {
            for (ExchangeRequest r : allReqs) {
                if (r.getRequesterId().equals(user.getId())) {
                    sent.add(r);
                }
                if (r.getOwnerId().equals(user.getId())) {
                    received.add(r);
                }
            }
        }
        sentRequests.setValue(sent);
        receivedRequests.setValue(received);
    }

    public LiveData<User> getCurrentUser() { return repository.getCurrentUser(); }
    public LiveData<List<Product>> getMyListings() { return myListings; }
    public LiveData<List<ExchangeRequest>> getSentRequests() { return sentRequests; }
    public LiveData<List<ExchangeRequest>> getReceivedRequests() { return receivedRequests; }
    public LiveData<List<Product>> getDonationHistory() { return donationHistory; }

    public void updateRequestStatus(String requestId, RequestStatus newStatus) {
        repository.updateRequestStatus(requestId, newStatus);
    }

    public void deleteListing(String productId) {
        repository.deleteProduct(productId);
    }

    public void updateProfile(String newName, String newLocation) {
        User user = repository.getCurrentUser().getValue();
        if (user != null) {
            user.setName(newName);
            user.setLocation(newLocation);
            repository.setCurrentUser(user);
        }
    }

    public String startOrGetConversationByUserId(String peerUserId) {
        return repository.startOrGetConversationByUserId(peerUserId);
    }

    public boolean sendExchangeRequest(String productId, String productTitle, String ownerId, String ownerName, String message, String offeredItemTitle) {
        return repository.sendExchangeRequestFull(productId, productTitle, "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=500", ownerId, ownerName, message, offeredItemTitle);
    }
}