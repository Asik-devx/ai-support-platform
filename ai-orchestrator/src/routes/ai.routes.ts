import { Router } from "express";
import { classifyTicket } from "../services/ticket-ai.service.js";

const router = Router();

router.post("/classify", classifyTicket);

export default router;
