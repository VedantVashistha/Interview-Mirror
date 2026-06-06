import { buildAnalytics } from "../services/analytics.service.js";

export async function getAnalytics(req, res, next) {
  try {
    res.json(await buildAnalytics(req.params.userId));
  } catch (error) {
    next(error);
  }
}
