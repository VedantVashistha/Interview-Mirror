import { firebaseAdmin } from "../utils/firebase-admin.js";

export async function verifyFirebaseToken(req, res, next) {
  const admin = firebaseAdmin();

  if (!admin) {
    req.user = { uid: "local-dev-user" };
    next();
    return;
  }

  const header = req.headers.authorization || "";
  const token = header.startsWith("Bearer ") ? header.slice(7) : "";

  if (!token) {
    res.status(401).json({ message: "Missing Firebase token" });
    return;
  }

  try {
    req.user = await admin.auth().verifyIdToken(token);
    next();
  } catch {
    res.status(401).json({ message: "Invalid Firebase token" });
  }
}
