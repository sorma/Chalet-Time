package com.francesco.chalettime.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel extends ViewModel {
    protected final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    protected final MutableLiveData<UiEvent<String>> messages = new MutableLiveData<>();
    protected boolean cleared;
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<UiEvent<String>> getMessages() { return messages; }
    protected void message(String text) { if (!cleared) messages.setValue(new UiEvent<>(text)); }
    protected boolean busy() { return Boolean.TRUE.equals(loading.getValue()); }
    @Override protected void onCleared() { cleared = true; }
}
