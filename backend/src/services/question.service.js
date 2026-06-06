import { questionBank } from "../utils/question-bank.js";

const modeCount = {
  Quick: 5,
  Standard: 10,
  Deep: 20
};

export async function createQuestions({ category, difficulty, mode, recentQuestionIds = [] }) {
  const count = modeCount[mode] || 5;
  const normalizedCategory = category.toLowerCase();
  const pool = questionBank.filter((item) => {
    const sameCategory = item.category.toLowerCase() === normalizedCategory;
    return sameCategory && item.difficulty === difficulty && !recentQuestionIds.includes(item.id);
  });

  const fallback = questionBank.filter((item) => item.category.toLowerCase() === normalizedCategory);
  const source = pool.length >= count ? pool : fallback;

  return source
    .sort(() => Math.random() - 0.5)
    .slice(0, count)
    .map(({ id, category: itemCategory, difficulty: itemDifficulty, type, text }) => ({
      id,
      category: itemCategory,
      difficulty: itemDifficulty,
      type,
      text
    }));
}
