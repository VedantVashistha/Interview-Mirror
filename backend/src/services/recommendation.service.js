export async function buildRecommendations(userId) {
  return {
    userId,
    weakTopics: ["RecyclerView", "Coroutines", "Dependency Injection"],
    recommendations: [
      "Practice Kotlin Coroutines Interview",
      "Review Hilt dependency injection examples",
      "Explain RecyclerView recycling with ViewHolder optimization"
    ]
  };
}
