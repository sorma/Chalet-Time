package com.francesco.chalettime.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.francesco.chalettime.data.repository.*;
import com.francesco.chalettime.model.WorkLog;
import com.francesco.chalettime.ui.common.BaseViewModel;
import com.francesco.chalettime.ui.common.UiEvent;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class HomeViewModel extends BaseViewModel {
    private final AuthRepository auth;
    private final UserWorkLogRepository logs;
    private final MutableLiveData<UiEvent<Boolean>> saved = new MutableLiveData<>();
    public HomeViewModel() { this(new AuthRepositoryImpl(), new UserWorkLogRepositoryImpl()); }
    public HomeViewModel(AuthRepository auth, UserWorkLogRepository logs) { this.auth = auth; this.logs = logs; }
    public LiveData<UiEvent<Boolean>> getSaved() { return saved; }
    public void logout() { auth.signOut(); }
    public void save(String date, String textHours) {
        if (busy()) return;
        String id = auth.getCurrentUserId();
        if (id == null) { message("Utente non autenticato"); return; }
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        format.setLenient(false);
        ParsePosition position = new ParsePosition(0);
        if (date.length() != 10 || format.parse(date, position) == null || position.getIndex() != date.length()) {
            message("Seleziona una data valida"); return;
        }
        double hours;
        try { hours = Double.parseDouble(textHours); }
        catch (NumberFormatException e) { message("Seleziona le ore lavorate"); return; }
        if (!Double.isFinite(hours) || hours < 1 || hours > 16 || hours * 2 != Math.rint(hours * 2)) {
            message("Seleziona da 1 a 16 ore, a intervalli di mezz’ora"); return;
        }
        loading.setValue(true);
        logs.saveWorkLog(id, new WorkLog(id, date, hours), new UserWorkLogRepository.SaveWorkLogCallback() {
            @Override public void onSuccess(String result) {
                if (cleared) return;
                loading.setValue(false); message("Ore salvate correttamente"); saved.setValue(new UiEvent<>(true));
            }
            @Override public void onFailure(String error) {
                if (cleared) return;
                loading.setValue(false); message("Errore nel salvataggio: " + error);
            }
        });
    }
}
