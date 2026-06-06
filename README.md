# Interview Mirror

Interview Mirror is an AI-powered mock interview app for students and job seekers.
This repository contains:

- `android/`: Kotlin + Jetpack Compose app using MVVM-style layers.
- `backend/`: Node.js + Express REST API for questions, evaluation, analytics, and recommendations.
- `docs/`: beginner setup notes, Firebase schema, and API documentation.

## Beginner Roadmap

1. Install tools:
   - Android Studio Koala or newer
   - Node.js 20+
   - Git
   - Firebase account
2. Open `backend/` in a terminal and run:

```bash
npm install
cp .env.example .env
npm run dev
```

3. Open `android/` in Android Studio.
4. Create a Firebase project named `Interview Mirror`.
5. Add an Android app in Firebase with package name:

```text
com.interviewmirror.app
```

6. Download `google-services.json` and place it in:

```text
android/app/google-services.json
```

7. In Android Studio, sync Gradle and run the app.
8. Use the default backend URL for emulator:

```text
http://10.0.2.2:8080/
```

Note: this project pins Gradle to `8.9`. If Android Studio tries to use a Gradle 9 milestone, choose the project Gradle wrapper from Android Studio settings and sync again.

## What Works In This Scaffold

- Compose navigation between splash, auth, dashboard, category, interview, result, history, profile, and settings screens.
- Backend question generation with category, difficulty, and mode.
- Backend answer evaluation with score, strengths, weaknesses, missing concepts, and suggested answer.
- Backend analytics and recommendations.
- Firebase-ready backend auth middleware.
- Clean folder structure that you can expand module by module.

## Suggested Build Order

1. Run the backend locally.
2. Run the Android app with the sample API.
3. Connect Firebase Authentication.
4. Save interview results to Firestore.
5. Add real AI provider keys.
6. Improve voice UI, charts, and report export.

See `docs/setup-beginner.md` for detailed step-by-step instructions.
