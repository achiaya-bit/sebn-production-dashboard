# ── Stage 1: build ────────────────────────────────────────────────────────────
# Nitro 3.x and @napi-rs/wasm-runtime require Node >=20.19.0 or >=22.12.0.
FROM node:22-alpine AS build

WORKDIR /app

# Copy manifests first so npm ci layer is cached
COPY package.json package-lock.json ./

# Install all deps (including devDeps — needed for vite build)
RUN npm ci --ignore-scripts

# Copy source files
COPY . .

# Build with the Docker/Node preset config.
# VITE_API_BASE_URL is baked into the client bundle at build time.
# Browser requests go to http://localhost:8080/api on the demo machine,
# so the host machine's exposed port 8080 is always reachable from the browser.
ARG VITE_API_BASE_URL=http://localhost:8080/api
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

RUN npm run build:docker

# ── Stage 2: runtime ──────────────────────────────────────────────────────────
FROM node:22-alpine AS runtime

WORKDIR /app

# The Nitro node preset produces a self-contained bundle in .output/
# with its own package.json declaring "main": "./server/index.mjs".
COPY --from=build /app/.output ./

EXPOSE 3000

# Nitro node server reads PORT from the environment (default 3000).
ENV PORT=3000
ENV HOST=0.0.0.0

CMD ["node", "./server/index.mjs"]
