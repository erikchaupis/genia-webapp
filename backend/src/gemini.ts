import { GoogleGenerativeAI } from "@google/generative-ai";
import dotenv from "dotenv";

dotenv.config();
const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY!);
const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });

export async function getGeminiReply(message: string) {

  const result = await model.generateContent(message);
  console.log(result);
  const response = result.response;
  return response.text();

}
