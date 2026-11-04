# Build agent image for the Momento Jenkins pipeline.
#
# Provides a reproducible Android toolchain (JDK 17, Android SDK, Gradle) so
# that builds do not depend on whatever happens to be installed on the
# Jenkins node. Used by the `agent { dockerfile { ... } }` block in the root
# Jenkinsfile; it can also be built and run by hand:
#
#   docker build -t momento-android-ci ci/jenkins
#
FROM eclipse-temurin:17-jdk-jammy

ARG ANDROID_CMDLINE_TOOLS_VERSION=13114758
ARG ANDROID_PLATFORM=android-36.1
ARG ANDROID_BUILD_TOOLS=36.0.0
ARG GRADLE_VERSION=9.3.1

ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_ROOT=/opt/android-sdk \
    GRADLE_HOME=/opt/gradle
ENV PATH="${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${GRADLE_HOME}/bin:${PATH}"

RUN apt-get update \
    && apt-get install -y --no-install-recommends git unzip wget ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Android command-line tools
RUN mkdir -p "${ANDROID_HOME}/cmdline-tools" \
    && wget -q "https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_CMDLINE_TOOLS_VERSION}_latest.zip" -O /tmp/cmdline-tools.zip \
    && unzip -q /tmp/cmdline-tools.zip -d "${ANDROID_HOME}/cmdline-tools" \
    && mv "${ANDROID_HOME}/cmdline-tools/cmdline-tools" "${ANDROID_HOME}/cmdline-tools/latest" \
    && rm /tmp/cmdline-tools.zip

# SDK packages required by app/build.gradle.kts (compileSdk 36.1)
RUN yes | sdkmanager --licenses > /dev/null \
    && sdkmanager --install \
        "platform-tools" \
        "platforms;${ANDROID_PLATFORM}" \
        "build-tools;${ANDROID_BUILD_TOOLS}" \
    # Jenkins runs the container as the node's (non-root) user; keep the SDK
    # writable so the Android Gradle Plugin can fetch any extra components.
    && chmod -R a+rwX "${ANDROID_HOME}"

# Gradle (the repository does not commit a Gradle wrapper)
RUN wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -O /tmp/gradle.zip \
    && unzip -q /tmp/gradle.zip -d /opt \
    && mv "/opt/gradle-${GRADLE_VERSION}" "${GRADLE_HOME}" \
    && rm /tmp/gradle.zip
