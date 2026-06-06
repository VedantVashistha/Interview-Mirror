import { Router } from "express";
import { evaluateAnswer } from "../controllers/evaluation.controller.js";
import { validate } from "../middleware/validate.middleware.js";
import { evaluateAnswerSchema } from "../models/schemas.js";

const router = Router();

router.post("/", validate(evaluateAnswerSchema), evaluateAnswer);

export default router;
