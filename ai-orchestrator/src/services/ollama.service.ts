import axios from "axios";

const OLLAMA_URL = "http://host.docker.internal:11434";

export async function classifyWithOllama(
  title: string,
  description: string
) {
  const prompt = `
You are a customer support system.
Classify the ticket priority as one word only:
LOW, MEDIUM, or HIGH.

Title: ${title}
Description: ${description}
`;

  const response = await axios.post(`${OLLAMA_URL}/api/generate`, {
    model: "llama3",
    prompt,
    stream: false
  });

  const text: string = response.data.response.trim();

  return {
    priority: extractPriority(text)
  };
}

function extractPriority(text: string): "LOW" | "MEDIUM" | "HIGH" {
  if (text.includes("HIGH")) return "HIGH";
  if (text.includes("LOW")) return "LOW";
  return "MEDIUM";
}
