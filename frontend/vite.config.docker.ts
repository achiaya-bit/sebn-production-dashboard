import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";
import { tanstackStart } from "@tanstack/react-start/plugin/vite";
import { nitro } from "nitro/vite";
import tsconfigPaths from "vite-tsconfig-paths";

// Docker / Node.js production build.
// Uses the Nitro "node" preset — output lands at .output/server/index.mjs
// and runs with plain Node 22 (no Cloudflare Workers runtime required).
// src/start.ts exports startInstance (required by TanStack Start).
export default defineConfig({
  plugins: [
    tsconfigPaths(),
    tailwindcss(),
    tanstackStart({
      start: { entry: "start" },
    }),
    react(),
    nitro({ preset: "node" }),
  ],
});
