package com.example.myproject.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<Boolean> postUpdated = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveUpdated = new MutableLiveData<>();
    private final MutableLiveData<Boolean> favouriteUpdated = new MutableLiveData<>();

    // Các LiveData để các fragment quan sát
    public LiveData<Boolean> getPostUpdated() {
        return postUpdated;
    }

    public LiveData<Boolean> getSaveUpdated() {
        return saveUpdated;
    }

    public LiveData<Boolean> getFavouriteUpdated() {
        return favouriteUpdated;
    }

    // Gọi khi có hành động tương ứng
    public void notifyPostUpdated() {
        postUpdated.setValue(true);
    }

    public void notifySaveUpdated() {
        saveUpdated.setValue(true);
    }

    public void notifyFavouriteUpdated() {
        favouriteUpdated.setValue(true);
    }
}
