<div align="center">
<img width="900" height="500" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/6d40de99-dc2b-425e-9a04-63265cfa7664

## Build a debug APK

Install JDK 17 and Android SDK Platform 36 with Build Tools 36.0.0, then run `./gradlew :app:assembleDebug` (or `gradlew.bat :app:assembleDebug` on Windows). The APK is written to `app/build/outputs/apk/debug/` and is signed with the standard Android debug key.

In Codemagic, select the **Momento Android debug APK** workflow. It installs the required SDK packages and publishes the debug APK as a build artifact. This workflow does not need production signing credentials or a Supabase credentials group.
**---------------------------------------------------------------------------------------------------------------------------------**

<div align="center">
<img width="550" height="500" alt="Splash" src="splash.png" />
</div>
