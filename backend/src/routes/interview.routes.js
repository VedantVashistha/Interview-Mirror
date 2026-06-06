import { Router } from "express";
import { generateQuestions } from "../controllers/interview.controller.js";
import { validate } from "../middleware/validate.middleware.js";
import { generateQuestionsSchema } from "../models/schemas.js";

const router = Router();

router.post("/questions", validate(generateQuestionsSchema), generateQuestions);

export default router;
