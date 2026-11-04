# Jenkins CI

This project builds on Jenkins with the declarative pipeline in the root
[`Jenkinsfile`](../../Jenkinsfile). Every build runs inside the Docker image
defined in [`Dockerfile`](Dockerfile). That image holds JDK 17, the Android SDK
(platform `android-36.1`, build-tools `36.0.0`) and Gradle `9.3.1`, so the
Jenkins node itself only needs Docker.

## Pipeline stages

| Stage             | Runs when                                   | Gradle task         |
| ----------------- | ------------------------------------------- | ------------------- |
| Prepare           | always                                      | –                   |
| Lint              | `RUN_LINT` is checked                       | `lintDebug`         |
| Unit Tests        | `RUN_UNIT_TESTS` is checked                 | `testDebugUnitTest` |
| Build Debug APK   | always                                      | `assembleDebug`     |
| Build Release APK | the branch is `main`, or `BUILD_RELEASE` is checked | `assembleRelease` |

**Prepare** writes `local.properties` and generates `debug.keystore`. Both
build types in `app/build.gradle.kts` are signed with that keystore. This
mirrors the existing `codemagic.yaml` workflow.

When the build succeeds, the APKs under `app/build/outputs/apk/` are archived
on the build page. Lint HTML reports and JUnit results are published when
those stages run.

> **Note:** Unit tests are off by default. `GreetingScreenshotTest` refers to
> `Greeting` and `MyApplicationTheme`, and neither exists in the app sources,
> so the test source set does not compile yet. Turn `RUN_UNIT_TESTS` on (or
> change its default in the `Jenkinsfile`) once that is fixed.

## Jenkins requirements

- A node with Docker installed. The Jenkins agent user must be allowed to run
  `docker`.
- Plugins: **Pipeline**, **Git**, **Docker Pipeline**, **JUnit** and
  **Timestamper**. The usual "suggested plugins" install includes all of them
  except Docker Pipeline.

To pin builds to particular nodes, add `label 'docker'` (or whatever label you
use) inside the `dockerfile { ... }` block of the `Jenkinsfile`.

## Creating the job

### Multibranch Pipeline (recommended)

1. **New Item → Multibranch Pipeline**.
2. Under **Branch Sources**, add a GitHub (or Git) source for this repository.
3. Under **Build Configuration**, keep *Mode: by Jenkinsfile* and
   *Script Path: `Jenkinsfile`*.
4. Save. Jenkins scans the branches and builds each one that has a
   `Jenkinsfile`. Pushes to `main` also build the release APK.

### Single-branch Pipeline

1. **New Item → Pipeline**.
2. Under **Pipeline**, choose *Pipeline script from SCM* with Git, point it at
   this repository and set *Script Path* to `Jenkinsfile`.
3. Run **Build with Parameters**. The release build runs only when
   `BUILD_RELEASE` is checked.

The parameters show up after the first run, because Jenkins learns them from
the `Jenkinsfile`.

## Caching

`GRADLE_USER_HOME` and `ANDROID_USER_HOME` point inside the workspace
(`.gradle-home`, `.android-home`; both are git-ignored). Dependencies are
therefore reused between builds on the same node. Delete the workspace to start
from a clean cache.

## Testing the image locally

```bash
docker build -t momento-android-ci ci/jenkins
docker run --rm -v "$PWD":/workspace -w /workspace momento-android-ci \
  bash -c 'echo "sdk.dir=$ANDROID_HOME" > local.properties && gradle assembleDebug --no-daemon'
```

The debug and release builds need `debug.keystore` in the project root. To
generate it, run the `keytool` command from the **Prepare** stage in the
`Jenkinsfile`.
