import { evaluateWithAi } from "../services/ai.service.js";

export async function evaluateAnswer(req, res, next) {
  try {
    const evaluation = await evaluateWithAi(req.body);
    res.json(evaluation);
  } catch (error) {
    next(error);
  }
}
