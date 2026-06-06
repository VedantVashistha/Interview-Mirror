export function validate(schema) {
  return (req, _res, next) => {
    const { error, value } = schema.validate(req.body, {
      abortEarly: false,
      stripUnknown: true
    });

    if (error) {
      error.status = 400;
      error.message = error.details.map((detail) => detail.message).join(", ");
      next(error);
      return;
    }

    req.body = value;
    next();
  };
}
