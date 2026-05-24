FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY dist /usr/share/nginx/html
COPY dist-widget /usr/share/nginx/html/widget

EXPOSE 80
