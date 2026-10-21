# Chalet-Time

App Android in Java per la gestione delle ore di lavoro, con Firebase Authentication e Cloud Firestore.

## Avvio locale

1. Aprire il progetto in Android Studio con JDK 17 o successivo e Android SDK 35.
2. Scaricare dalla console del proprio progetto Firebase il file `google-services.json` per l'app Android con package `com.francesco.chalettime` e copiarlo in `app/google-services.json`. Il file è escluso da Git.
3. Abilitare l'accesso Email/Password in Firebase Authentication e configurare Cloud Firestore con le regole di accesso del progetto.
4. Collegare un dispositivo o avviare un emulatore Android 7.0 (API 24) o successivo, selezionarlo in Android Studio e premere Run.

Per compilare da terminale: `./gradlew assembleDebug`.

Per installare su un dispositivo o emulatore collegato: `./gradlew installDebug`.

Il file Firebase è necessario per compilare e usare l'app; senza di esso il task `processDebugGoogleServices` termina con errore.

## Architettura e test

La struttura MVVM, i flussi e il ruolo di ciascun file sono descritti in [architettura.md](architettura.md).

Verifica locale: `./gradlew assembleDebug lintDebug testDebugUnitTest`.

Test di navigazione e dei layout (con emulatore collegato): `./gradlew connectedDebugAndroidTest`. Questi test non creano account o registrazioni Firebase.
