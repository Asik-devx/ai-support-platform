import { redis } from "./redis.service.js";
import { ticketCacheKey } from "./cache.util.js";
import axios from "axios";

const OLLAMA_URL = "http://host.docker.internal:11434";

export async function classifyWithOllama(
  title: string,
  description: string
) {
  const cacheKey = ticketCacheKey(title, description);
  console.log("[AI] cache key:", cacheKey);

  // 1️⃣ Check cache
  try {
    const cached = await redis.get(cacheKey);
    if (cached) {
      console.log("[AI] cache HIT");
      return JSON.parse(cached);
    }
    console.log("[AI] cache MISS");
  } catch (err) {
    console.error("[AI] Redis GET failed:", (err as Error).message);
  }

  // 2️⃣ Call Ollama
  const prompt = `
Classify the ticket priority as one word:
LOW, MEDIUM, or HIGH.

Title: ${title}
Description: ${description}
`;

  console.log("[AI] calling Ollama:", OLLAMA_URL);

  let responseText: string;
  try {
    const response = await axios.post(`${OLLAMA_URL}/api/generate`, {
      model: "llama3",
      prompt,
      stream: false
    });

    responseText = response.data.response;
    console.log("[AI] Ollama response:", responseText);
  } catch (err) {
    console.error("[AI] Ollama call failed:", (err as Error).message);
    throw err;
  }

  const priority = extractPriority(responseText);
  console.log("[AI] extracted priority:", priority);

  const result = { priority };

  // 3️⃣ Cache result (TTL 1 hour)
  try {
    await redis.setex(cacheKey, 3600, JSON.stringify(result));
    console.log("[AI] cached result (TTL 3600s)");
  } catch (err) {
    console.error("[AI] Redis SET failed:", (err as Error).message);
  }

  return result;
}

function extractPriority(text: string): "LOW" | "MEDIUM" | "HIGH" {
  const normalized = text.toUpperCase();
  if (normalized.includes("HIGH")) return "HIGH";
  if (normalized.includes("LOW")) return "LOW";
  return "MEDIUM";
}
