import "dotenv/config";
import cors from "cors";
import express from "express";
import helmet from "helmet";
import morgan from "morgan";
import rateLimit from "express-rate-limit";
import interviewRoutes from "./routes/interview.routes.js";
import evaluationRoutes from "./routes/evaluation.routes.js";
import analyticsRoutes from "./routes/analytics.routes.js";
import recommendationRoutes from "./routes/recommendation.routes.js";
import { errorHandler } from "./middleware/error.middleware.js";

export function createApp() {
  const app = express();

  app.use(helmet());
  app.use(cors());
  app.use(express.json({ limit: "1mb" }));
  app.use(morgan("dev"));
  app.use(rateLimit({ windowMs: 60_000, limit: 80 }));

  app.get("/health", (_req, res) => {
    res.json({ ok: true, service: "interview-mirror-api" });
  });

  app.use("/api/interviews", interviewRoutes);
  app.use("/api/evaluations", evaluationRoutes);
  app.use("/api/analytics", analyticsRoutes);
  app.use("/api/recommendations", recommendationRoutes);
  app.use(errorHandler);

  return app;
}
