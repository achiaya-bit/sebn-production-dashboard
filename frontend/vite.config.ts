import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";
import { tanstackStart } from "@tanstack/react-start/plugin/vite";
import tsconfigPaths from "vite-tsconfig-paths";

// Standard development and default production build.
// Uses src/start.ts which exports startInstance (required by TanStack Start).
// For the Docker/Node.js production build use: npm run build:docker
export default defineConfig({
  plugins: [
    tsconfigPaths(),
    tailwindcss(),
    tanstackStart({
      // src/start.ts exports startInstance and configures CSRF + error middleware.
      start: { entry: "start" },
    }),
    react(),
  ],
});
