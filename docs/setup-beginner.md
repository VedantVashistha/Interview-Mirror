# Beginner Setup Guide

## Step 1: Understand The Two Parts

Interview Mirror has two apps:

- Android app: what the user sees.
- Backend API: the server that generates questions and evaluates answers.

The Android app calls the backend using HTTP.

## Step 2: Run Backend

Open a terminal in `backend/`.

```bash
npm install
cp .env.example .env
npm run dev
```

Test it in a browser:

```text
http://localhost:8080/health
```

You should see:

```json
{ "ok": true, "service": "interview-mirror-api" }
```

## Step 3: Open Android App

Open Android Studio, choose `Open`, then select the `android/` folder.

Wait for Gradle sync to finish.

The project is pinned to Gradle `8.9`. If Android Studio shows a Gradle 9 milestone error, go to:

```text
File > Settings > Build, Execution, Deployment > Build Tools > Gradle
```

Set Gradle JDK to a recent JDK, and make sure Gradle uses the project wrapper.

If Android Studio says `google-services.json is missing`, finish Step 4 first. The scaffold also avoids applying the Google Services plugin until that file exists.

## Step 4: Firebase

Create a Firebase project, add an Android app, and use this package name:

```text
com.interviewmirror.app
```

Download `google-services.json` into `android/app/`.

## Step 5: AI Provider

For development, the backend works without an AI key using local scoring.

For real AI feedback, edit `backend/.env`:

```bash
AI_PROVIDER=openai
OPENAI_API_KEY=your_key_here
```

or:

```bash
AI_PROVIDER=gemini
GEMINI_API_KEY=your_key_here
```

## Step 6: What To Build Next

Build in this order:

1. Email login/register.
2. Dashboard cards.
3. Category and difficulty selection.
4. Interview question flow.
5. Answer evaluation.
6. History persistence.
7. Voice input.
8. Charts and report download.
