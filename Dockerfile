# Production image: serves the built Momento APK for install / release distribution.
# Build the APK first (Codemagic or ./gradlew assembleRelease), then:
#   docker build --build-arg APK_PATH=app/build/outputs/apk/release/app-release.apk -t momento-apk .
FROM nginx:1.27-alpine

COPY infra/nginx/default.conf /etc/nginx/conf.d/default.conf
ARG APK_PATH=app/build/outputs/apk/release/app-release.apk
COPY ${APK_PATH} /usr/share/nginx/html/apk/momento.apk
COPY infra/nginx/index.html /usr/share/nginx/html/index.html

EXPOSE 80
HEALTHCHECK --interval=30s --timeout=5s --retries=3 CMD wget -qO- http://127.0.0.1/health || exit 1
