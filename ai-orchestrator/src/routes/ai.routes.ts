import { Router } from "express";
import { classifyTicket } from "../services/ticket-ai.service.js";
import { rateLimit } from "../middleware/rateLimit.js";
const router = Router();

router.post("/classify", rateLimit, classifyTicket);

export default router;
