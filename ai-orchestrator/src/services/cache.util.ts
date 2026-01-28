import crypto from "crypto";

export function ticketCacheKey(title: string, description: string): string {
  const hash = crypto
    .createHash("sha256")
    .update(title + description)
    .digest("hex");

  return `ticket:classify:${hash}`;
}
