import "dotenv/config";

export const env = {
PORT: process.env.PORT ?? "4000",
OPENAI_API_KEY: process.env.OPENAI_API_KEY!,
REDIS_URL: process.env.REDIS_URL ?? "redis://localhost:6379",
};