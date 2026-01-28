import type { Request, Response } from "express";
import { classify } from "./openai.service.js";
import { classifyWithOllama } from "./ollama.service.js";

export async function classifyTicket(req: Request, res: Response) {
  const { title, description } = req.body;

  if (!title || !description) {
    return res.status(400).json({ error: "Missing fields" });
  }

  //classify with open ai
  // const result = await classify(title, description);
  const result = await classifyWithOllama(title, description);
  res.json(result);
}
