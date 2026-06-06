import OpenAI from "openai";
import { GoogleGenerativeAI } from "@google/generative-ai";

export async function evaluateWithAi(payload) {
  if (process.env.AI_PROVIDER === "openai" && process.env.OPENAI_API_KEY) {
    return evaluateWithOpenAi(payload);
  }

  if (process.env.AI_PROVIDER === "gemini" && process.env.GEMINI_API_KEY) {
    return evaluateWithGemini(payload);
  }

  return evaluateLocally(payload);
}

async function evaluateWithOpenAi(payload) {
  const client = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });
  const response = await client.chat.completions.create({
    model: process.env.OPENAI_MODEL || "gpt-4o-mini",
    response_format: { type: "json_object" },
    messages: [
      { role: "system", content: "Return strict JSON for an interview answer evaluation." },
      { role: "user", content: buildPrompt(payload) }
    ]
  });
  return JSON.parse(response.choices[0].message.content);
}

async function evaluateWithGemini(payload) {
  const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
  const model = genAI.getGenerativeModel({ model: process.env.GEMINI_MODEL || "gemini-1.5-flash" });
  const response = await model.generateContent(buildPrompt(payload));
  return JSON.parse(response.response.text().replace(/```json|```/g, "").trim());
}

function evaluateLocally({ question, answer, category, difficulty }) {
  const words = answer.trim().split(/\s+/).filter(Boolean);
  const keywordHits = importantKeywords(category).filter((word) =>
    answer.toLowerCase().includes(word.toLowerCase())
  );
  const lengthScore = Math.min(4, words.length / 18);
  const keywordScore = Math.min(4, keywordHits.length * 0.8);
  const structureScore = /example|because|first|second|finally|for example/i.test(answer) ? 1.5 : 0.5;
  const score = Math.max(1, Math.min(10, Number((lengthScore + keywordScore + structureScore).toFixed(1))));

  return {
    score,
    correctness: score >= 7 ? "Good" : score >= 5 ? "Partial" : "Needs improvement",
    technicalDepth: keywordHits.length >= 3 ? "Detailed" : "Basic",
    communicationClarity: words.length > 25 ? "Clear enough" : "Too short",
    strengths: keywordHits.length ? [`Mentioned ${keywordHits.slice(0, 3).join(", ")}.`] : ["Answered the question directly."],
    weaknesses: score < 7 ? ["Add more depth, examples, and trade-offs."] : ["Can be improved with a sharper real-world example."],
    missingConcepts: importantKeywords(category).filter((word) => !keywordHits.includes(word)).slice(0, 4),
    suggestedAnswer: `A stronger answer to "${question}" should define the idea, explain why it matters in ${category}, include a short example, and mention one practical trade-off.`,
    difficulty
  };
}

function buildPrompt({ question, answer, category, difficulty }) {
  return JSON.stringify({
    task: "Evaluate this mock interview answer.",
    outputShape: {
      score: "number out of 10",
      correctness: "string",
      technicalDepth: "string",
      communicationClarity: "string",
      strengths: ["string"],
      weaknesses: ["string"],
      missingConcepts: ["string"],
      suggestedAnswer: "string"
    },
    question,
    answer,
    category,
    difficulty
  });
}

function importantKeywords(category) {
  const map = {
    "Android Development": ["Activity", "Lifecycle", "ViewModel", "Room", "RecyclerView", "Compose", "performance"],
    Kotlin: ["null safety", "coroutines", "data class", "sealed", "extension", "flow"],
    Java: ["OOP", "inheritance", "polymorphism", "collection", "exception", "thread"],
    DSA: ["complexity", "array", "hashmap", "recursion", "tree", "graph"],
    "HR Interview": ["team", "challenge", "learning", "communication", "ownership"],
    "Web Development": ["HTML", "CSS", "JavaScript", "API", "React", "database"],
    "Database/SQL": ["index", "join", "normalization", "transaction", "query", "schema"]
  };
  return map[category] || ["example", "trade-off", "concept", "implementation"];
}
