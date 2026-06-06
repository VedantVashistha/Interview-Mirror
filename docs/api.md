# API Documentation

Base URL:

```text
http://localhost:8080
```

Android emulator URL:

```text
http://10.0.2.2:8080
```

## Health

`GET /health`

## Generate Questions

`POST /api/interviews/questions`

```json
{
  "category": "Android Development",
  "difficulty": "Beginner",
  "mode": "Quick",
  "userId": "demo-user",
  "recentQuestionIds": []
}
```

## Evaluate Answer

`POST /api/evaluations`

```json
{
  "question": "Explain RecyclerView.",
  "answer": "RecyclerView displays lists efficiently by recycling item views.",
  "category": "Android Development",
  "difficulty": "Beginner"
}
```

## Analytics

`GET /api/analytics/{userId}`

## Recommendations

`GET /api/recommendations/{userId}`
