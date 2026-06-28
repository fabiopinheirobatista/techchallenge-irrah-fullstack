FROM node:20.19-alpine AS builder

WORKDIR /workspace
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /workspace/dist/techchallenge-irrah-frontend/browser /usr/share/nginx/html
EXPOSE 80
