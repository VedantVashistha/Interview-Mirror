# Firestore Schema

## users/{userId}

```json
{
  "name": "Asha Sharma",
  "email": "asha@example.com",
  "profilePicture": "",
  "collegeOrProfession": "Final year CSE student",
  "experienceLevel": "Beginner",
  "createdAt": "serverTimestamp",
  "updatedAt": "serverTimestamp"
}
```

## interviews/{interviewId}

```json
{
  "userId": "uid",
  "category": "Android Development",
  "difficulty": "Beginner",
  "mode": "Quick",
  "questionCount": 5,
  "startedAt": "serverTimestamp",
  "completedAt": "serverTimestamp",
  "overallScore": 72
}
```

## interviews/{interviewId}/answers/{answerId}

```json
{
  "questionId": "android-b-2",
  "question": "What is RecyclerView and why is it used?",
  "answer": "RecyclerView is used for efficient lists.",
  "score": 7.5,
  "missingConcepts": ["ViewHolder", "recycling"],
  "createdAt": "serverTimestamp"
}
```

## weak_topics/{userId}

```json
{
  "topics": [
    { "name": "Coroutines", "count": 4, "lastSeenAt": "serverTimestamp" }
  ]
}
```

## Beginner Firestore Rules

Use these while testing authenticated users:

```text
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /interviews/{interviewId} {
      allow create: if request.auth != null
        && request.resource.data.userId == request.auth.uid;
      allow read: if request.auth != null
        && resource.data.userId == request.auth.uid;
    }

    match /users/{userId} {
      allow read, write: if request.auth != null
        && request.auth.uid == userId;
    }
  }
}
```

If you use **Use Demo Account**, Firestore saving may fail with secure rules because there is no real Firebase user. Use Login or Create Account for saved history.
