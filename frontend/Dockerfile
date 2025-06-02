FROM node:20.13.1-alpine AS build-stage
WORKDIR /app

ARG VITE_API_URL
ARG BASE_URL

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM nginx:1.28.0-alpine
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
