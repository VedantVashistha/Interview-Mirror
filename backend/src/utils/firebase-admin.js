import admin from "firebase-admin";

let initialized = false;

export function firebaseAdmin() {
  if (initialized) return admin;

  if (!process.env.FIREBASE_SERVICE_ACCOUNT) {
    return null;
  }

  const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  });
  initialized = true;
  return admin;
}
