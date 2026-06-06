import { buildRecommendations } from "../services/recommendation.service.js";

export async function getRecommendations(req, res, next) {
  try {
    res.json(await buildRecommendations(req.params.userId));
  } catch (error) {
    next(error);
  }
}
