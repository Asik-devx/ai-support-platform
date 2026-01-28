import { redis } from "./redis.service.js";
import { ticketCacheKey } from "./cache.util.js";
import axios from "axios";

const OLLAMA_URL = "http://host.docker.internal:11434";

export async function classifyWithOllama(
  title: string,
  description: string
) {
  const cacheKey = ticketCacheKey(title, description);

  // 1️⃣ Check cache
  const cached = await redis.get(cacheKey);
  if (cached) {
    return JSON.parse(cached);
  }

  // 2️⃣ Call Ollama
  const prompt = `
Classify the ticket priority as one word:
LOW, MEDIUM, or HIGH.

Title: ${title}
Description: ${description}
`;

  const response = await axios.post(`${OLLAMA_URL}/api/generate`, {
    model: "llama3",
    prompt,
    stream: false
  });

  const priority = extractPriority(response.data.response);

  const result = { priority };

  // 3️⃣ Cache result (TTL 1 hour)
  await redis.setex(cacheKey, 3600, JSON.stringify(result));

  return result;
}

function extractPriority(text: string): "LOW" | "MEDIUM" | "HIGH" {
  if (text.includes("HIGH")) return "HIGH";
  if (text.includes("LOW")) return "LOW";
  return "MEDIUM";
}
