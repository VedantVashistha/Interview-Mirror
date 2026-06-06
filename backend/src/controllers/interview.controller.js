import { createQuestions } from "../services/question.service.js";

export async function generateQuestions(req, res, next) {
  try {
    const questions = await createQuestions(req.body);
    res.json({ questions });
  } catch (error) {
    next(error);
  }
}
