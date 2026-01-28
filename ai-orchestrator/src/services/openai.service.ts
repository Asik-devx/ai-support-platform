import OpenAI from "openai";
import { env } from "../config/env.js";

const openai = new OpenAI({
  apiKey: env.OPENAI_API_KEY
});

export async function classify(title: string, description: string) {
  const prompt = `
You are a support system.
Classify ticket priority as LOW, MEDIUM, or HIGH.

Title: ${title}
Description: ${description}
`;

  const response = await openai.chat.completions.create({
    model: "gpt-4o-mini",
    messages: [{ role: "user", content: prompt }],
    temperature: 0
  });

  return {
    priority: response?.choices[0]?.message.content ?? "UNKNOWN"
  };
}
