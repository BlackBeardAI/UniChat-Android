# UniChat Android

Application Android dédiée pour communiquer avec ton serveur LLM local.

## Fonctionnalités

- Chat avec l'API OpenAI-compatible de ton serveur (`/v1/chat/completions`)
- Historique des conversations stocké localement (Room)
- Sélection du modèle parmi ceux disponibles sur le serveur
- Configuration simple de l'URL du serveur

## Configuration

1. Ouvrir l'app
2. Aller dans **Parametres** (icône en haut à droite)
3. Renseigner l'URL du serveur, par exemple :
   - `http://192.168.1.100:8080/v1/` (même réseau WiFi)
   - `http://nom-serveur.tailscale:8080/v1/` (via VPN Tailscale)
4. Cliquer sur **Recharger la liste des modeles**
5. Sélectionner le modèle souhaité
6. Sauvegarder

## Stack technique

- Kotlin
- Jetpack Compose
- Material3
- Room (historique local)
- Retrofit + OkHttp + Kotlinx Serialization
- DataStore Preferences (settings)
- Coroutines / Flow

## Build

```bash
cd /root/UniChat-Android
./gradlew :app:assembleDebug
```

APK généré : `app/build/outputs/apk/debug/app-debug.apk`

## Accès distant

L'app Android doit pouvoir joindre ton serveur. Options :

1. **Même WiFi** : IP locale, ex `http://192.168.1.100:8080/v1/`
2. **Tailscale** : VPN maillé, accès depuis n'importe où
3. **WireGuard** : VPN classique

⚠️ Ne jamais exposer l'API LLM directement sur Internet sans authentification.
