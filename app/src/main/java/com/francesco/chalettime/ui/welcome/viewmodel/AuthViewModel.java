package com.francesco.chalettime.ui.welcome.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.francesco.chalettime.data.repository.AuthRepository;
import com.francesco.chalettime.data.repository.AuthRepositoryImpl;
import com.francesco.chalettime.model.User;
import com.francesco.chalettime.ui.common.BaseViewModel;
import com.francesco.chalettime.ui.common.UiEvent;

/** Coordinates authentication; Firebase and Android Views remain outside this class. */
public class AuthViewModel extends BaseViewModel {
    private final AuthRepository repository;
    private final MutableLiveData<UiEvent<Boolean>> authenticated = new MutableLiveData<>();
    public AuthViewModel() { this(new AuthRepositoryImpl()); }
    public AuthViewModel(AuthRepository repository) { this.repository = repository; }
    public LiveData<UiEvent<Boolean>> getAuthenticated() { return authenticated; }
    public void login(String email, String password) {
        if (busy()) return;
        loading.setValue(true);
        repository.signInWithEmailAndPassword(email.trim(), password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess() { success(); }
            @Override public void onFailure(Exception error) { failure("Accesso non riuscito. Controlla e-mail e password."); }
        });
    }
    public void resetPassword(String email) {
        if (busy()) return;
        if (email.trim().isEmpty()) { message("Inserisci il tuo indirizzo e-mail"); return; }
        loading.setValue(true);
        repository.sendPasswordResetEmail(email.trim(), new AuthRepository.AuthCallback() {
            @Override public void onSuccess() {
                if (cleared) return;
                loading.setValue(false); message("E-mail di recupero inviata");
            }
            @Override public void onFailure(Exception error) { failure("Invio non riuscito. Controlla l’indirizzo e riprova."); }
        });
    }
    public void register(String email, String password, String name, String surname, String date, String gender) {
        if (busy()) return;
        loading.setValue(true);
        repository.createUserWithEmailAndPassword(email.trim(), password, new AuthRepository.AuthCallback() {
            @Override public void onSuccess() {
                if (cleared) return;
                String id = repository.getCurrentUserId();
                if (id == null) { failure("Sessione non disponibile. Riprova ad accedere."); return; }
                // Preserve the existing rule. Only server-side rules can authorize administrator access.
                User user = new User(name.trim(), surname.trim(), date, email.trim(),
                        email.trim().equals("admin@gmail.com") ? "admin" : "user", gender);
                repository.saveUserToFirestore(id, user, new AuthRepository.AuthCallback() {
                    @Override public void onSuccess() { success(); }
                    @Override public void onFailure(Exception error) { failure("Errore nel salvataggio del profilo: " + error.getMessage()); }
                });
            }
            @Override public void onFailure(Exception error) { failure("Registrazione fallita: " + error.getMessage()); }
        });
    }
    private void success() {
        if (cleared) return;
        loading.setValue(false); authenticated.setValue(new UiEvent<>(true));
    }
    private void failure(String text) {
        if (cleared) return;
        loading.setValue(false); message(text);
    }
}
