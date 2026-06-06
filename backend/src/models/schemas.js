import Joi from "joi";

export const generateQuestionsSchema = Joi.object({
  category: Joi.string().required(),
  difficulty: Joi.string().valid("Beginner", "Intermediate", "Advanced").required(),
  mode: Joi.string().valid("Quick", "Standard", "Deep").required(),
  userId: Joi.string().allow("").optional(),
  recentQuestionIds: Joi.array().items(Joi.string()).default([])
});

export const evaluateAnswerSchema = Joi.object({
  question: Joi.string().min(4).required(),
  answer: Joi.string().min(1).required(),
  category: Joi.string().required(),
  difficulty: Joi.string().valid("Beginner", "Intermediate", "Advanced").required()
});
