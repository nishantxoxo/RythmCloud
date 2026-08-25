# RythmCloud

RythmCloud is an Android music streaming application backed by a Node.js API. It supports browsing songs, streaming audio, displaying artwork, and background playback with media notifications.

## Tech Stack

### Android app

- Kotlin
- Android SDK 24+
- Android SDK 36
- ExoPlayer
- AndroidX MediaSession
- Retrofit
- Hilt
- Kotlin Coroutines
- Glide
- Firebase Firestore and Storage

### Server

- Node.js
- Express 5
- PostgreSQL
- Prisma
- Multer
- CORS
- dotenv


## Requirements

- Android Studio
- JDK 17
- Node.js and npm
- PostgreSQL
- An Android emulator or physical Android device


## API Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/songs` | Retrieve all songs |
| `POST` | `/songs` | Create a song record |
| `DELETE` | `/songs` | Delete all song records |
| `POST` | `/upload/audio` | Upload an audio file using the `audio` field |
| `POST` | `/upload/image` | Upload an image using the `image` field |
| `GET` | `/uploads/audio/<filename>` | Access an uploaded audio file |
| `GET` | `/uploads/images/<filename>` | Access an uploaded image |


## Database Model

Songs contain:

- `id`
- `mediaId`
- `title`
- `subtitle`
- `songUrl`
- `imageUrl`
- `createdAt`
- `updatedAt`

## Testing

Run Android unit tests with:

```bash
.\gradlew.bat test
```

Run instrumentation tests with:

```bash
.\gradlew.bat connectedAndroidTest
```

The server currently does not define automated tests.


