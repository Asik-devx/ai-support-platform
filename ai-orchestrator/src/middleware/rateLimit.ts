import type { Request, Response, NextFunction } from "express";
import { redis } from "../services/redis.service.js";

const LIMIT = 20; // requests
const WINDOW = 60; // seconds

export async function rateLimit(
  req: Request,
  res: Response,
  next: NextFunction
) {
  const key = `rate:${req.ip}`;

  const current = await redis.incr(key);

  if (current === 1) {
    await redis.expire(key, WINDOW);
  }

  if (current > LIMIT) {
    return res.status(429).json({
      error: "Too many AI requests, slow down"
    });
  }

  next();
}
