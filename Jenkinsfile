// Jenkins declarative pipeline for the Momento Android app.
//
// The build runs inside the Docker image defined in ci/jenkins/Dockerfile, so
// the Jenkins node only needs Docker. See ci/jenkins/README.md for setup.

def gradlew(String tasks) {
    // Prefer a committed Gradle wrapper if one is ever added; otherwise fall
    // back to the Gradle distribution baked into the build image.
    sh """
        if [ -x ./gradlew ]; then GRADLE_CMD=./gradlew; else GRADLE_CMD=gradle; fi
        \$GRADLE_CMD ${tasks} --no-daemon --stacktrace
    """
}

pipeline {
    agent {
        dockerfile {
            dir 'ci/jenkins'
            filename 'Dockerfile'
        }
    }

    parameters {
        booleanParam(name: 'BUILD_RELEASE', defaultValue: false,
            description: 'Also build the release APK (always built on the main branch).')
        booleanParam(name: 'RUN_UNIT_TESTS', defaultValue: false,
            description: 'Run JVM/Robolectric unit tests (testDebugUnitTest).')
        booleanParam(name: 'RUN_LINT', defaultValue: false,
            description: 'Run Android Lint on the debug variant.')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '5'))
        disableConcurrentBuilds()
        timeout(time: 60, unit: 'MINUTES')
        timestamps()
    }

    environment {
        // Keep Gradle and Android user state inside the workspace so it is
        // writable by the container user and cached between builds.
        GRADLE_USER_HOME  = "${WORKSPACE}/.gradle-home"
        ANDROID_USER_HOME = "${WORKSPACE}/.android-home"
    }

    stages {
        stage('Prepare') {
            steps {
                sh '''
                    echo "sdk.dir=${ANDROID_HOME}" > local.properties

                    # Both build types are signed with this keystore (see
                    # signingConfigs.debugConfig in app/build.gradle.kts).
                    if [ ! -f debug.keystore ]; then
                        keytool -genkey -v -keystore debug.keystore \
                            -storepass android -alias androiddebugkey -keypass android \
                            -keyalg RSA -keysize 2048 -validity 10000 \
                            -dname "CN=Android Debug,O=Android,C=US"
                    fi

                    java -version
                '''
            }
        }

        stage('Lint') {
            when { expression { params.RUN_LINT } }
            steps {
                gradlew 'lintDebug'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'app/build/reports/lint-results-*.html',
                                     allowEmptyArchive: true
                }
            }
        }

        stage('Unit Tests') {
            when { expression { params.RUN_UNIT_TESTS } }
            steps {
                gradlew 'testDebugUnitTest'
            }
            post {
                always {
                    junit testResults: 'app/build/test-results/testDebugUnitTest/*.xml',
                          allowEmptyResults: true
                }
            }
        }

        stage('Build Debug APK') {
            steps {
                gradlew 'assembleDebug'
            }
        }

        stage('Build Release APK') {
            when {
                anyOf {
                    branch 'main'
                    expression { params.BUILD_RELEASE }
                }
            }
            steps {
                gradlew 'assembleRelease'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'app/build/outputs/apk/**/*.apk', fingerprint: true
        }
    }
}
