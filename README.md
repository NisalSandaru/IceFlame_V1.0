# IceFlame

IceFlame is an Android food-ordering and delivery app. Users can browse restaurants/food,
manage addresses, add items to a cart, check out, track orders, and pay online — all backed by
a remote API with push notifications.

## Features

- **Onboarding & Auth** — splash, onboarding, and login/sign-up screens.
- **Home & Search** — browse items, search, and explore categories.
- **Cart & Checkout** — quantity controls, cart management, and a checkout flow.
- **Orders** — view current/active orders and order history.
- **Addresses** — manage delivery addresses with a map picker.
- **Maps & Location** — Google Maps integration and device location (fine/coarse).
- **Payments** — online payments via the PayHere Android SDK.
- **Push Notifications** — Firebase Cloud Messaging.
- **Camera & Media** — capture/select images for profiles or listings.

## Tech Stack

- **Language:** Java
- **UI:** Android ViewBinding, Material Components, ConstraintLayout
- **Architecture:** Activities + Fragments, with adapters for lists
- **Networking:** Retrofit 2, OkHttp (with logging interceptor), Gson, Volley
- **Images:** Glide
- **Maps:** Google Play Services Maps & Location
- **Payments:** PayHere Android SDK
- **Messaging:** Firebase Cloud Messaging (Firebase BOM)
- **Utilities:** Lombok, Toasty, Shimmer, DotsIndicator
- **Min SDK:** 28 · **Target/Compile SDK:** 36

## Requirements

- Android Studio (latest stable)
- JDK 11
- An Android device or emulator running API 28+
- A Google Maps API key (see *Setup*)

## Setup

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd IceFlame
   ```

2. **Google Maps API key**
   The key is currently set in `app/src/main/AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY" />
   ```
   Replace it with your own key from the [Google Cloud Console](https://console.cloud.google.com/).

3. **Firebase**
   Add your `google-services.json` to the `app/` directory (the project applies the
   `com.google.gms.google-services` plugin). Push notifications require a configured Firebase project.

4. **Backend API**
   The app communicates with a remote API via Retrofit/OkHttp. Update the base URL in the
   network layer under `app/src/main/java/com/nisal/iceflame/network/` to point at your backend.

5. **Build & run**
   Open the project in Android Studio and run the `app` module on a device/emulator, or:
   ```bash
   ./gradlew assembleDebug
   ```

## Project Structure

```
app/src/main/java/com/nisal/iceflame/
├── activity/   # Screens (Splash, Onboarding, LogIn, SignUp, Main, ...)
├── fragment/   # UI fragments (Cart, Checkout, Address, Orders, ...)
├── adapters/   # RecyclerView adapters
├── model/      # Data models / POJOs
├── network/    # Retrofit/OkHttp API clients
├── service/    # Firebase messaging service
├── local/      # Local storage / preferences
└── enums/      # Shared enumerations
```

## License

This project is for personal/educational use. Add your license here if you intend to distribute it.
