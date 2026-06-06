export async function buildAnalytics(userId) {
  return {
    userId,
    overallScore: 72,
    practiceCount: 8,
    streak: 3,
    breakdown: {
      technicalKnowledge: 70,
      confidence: 68,
      communication: 75,
      problemSolving: 73
    },
    trend: [
      { label: "Week 1", score: 58 },
      { label: "Week 2", score: 64 },
      { label: "Week 3", score: 72 }
    ],
    weakAreas: ["Coroutines", "Firebase Security Rules", "OOP Concepts"],
    summary: "You have solid fundamentals and should now focus on deeper examples and architecture discussions."
  };
}
