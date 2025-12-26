# Music Android

Android application with the same functionality and UI as the iOS music app, using the same backend API.

## Features

- **Home Screen**: Stories, discover cards, quick play, and mixes
- **Samples Screen**: TikTok-style vertical scrolling shorts (video/audio)
- **Profile Screen**: User info, stats, and liked songs
- **Search**: Search songs by title or artist
- **Playlists**: Liked and disliked songs playlists
- **Music Player**: Full-screen player with controls
- **Authentication**: Email sign-in (Google Sign-In can be added)

## Tech Stack

- **Kotlin** with **Jetpack Compose** for UI
- **Retrofit** for API calls
- **ExoPlayer** for media playback
- **Coil** for image loading
- **DataStore** for local storage
- **Material 3** for design system

## Setup

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run the app

## Backend API

The app uses the same backend as the iOS app:
- Base URL: `https://music-back-g2u6.onrender.com`
- Endpoints:
  - `/api/v1/auth/authenticate` - Email authentication
  - `/api/v1/users/{userId}` - Get user info
  - `/api/v1/songs/{userId}` - Get songs
  - `/api/v1/stories/user/{userId}` - Get stories
  - `/api/v1/shorts/{userId}` - Get shorts
  - `/api/v1/playlists/{userId}` - Get playlists
  - `/api/v1/songs/{songId}/like` - Like song
  - `/api/v1/songs/{songId}/dislike` - Dislike song

## Project Structure

```
app/src/main/java/com/music/android/
├── data/
│   ├── api/          # Retrofit API service
│   ├── model/        # Data models
│   └── repository/   # Data repositories
├── domain/
│   └── player/       # Media player service
├── ui/
│   ├── component/    # Reusable UI components
│   ├── screen/       # Screen composables
│   ├── theme/         # App theme
│   └── viewmodel/    # ViewModels
└── MainActivity.kt   # Main entry point
```

## Notes

- The app defaults to guest mode if not authenticated
- Guest user ID: `3762deba-87a9-482e-b716-2111232148ca`
- Dark theme is used throughout the app
- The UI closely matches the iOS app design

