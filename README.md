# AL0
AL0 [A•L•Zero] is not just an application launcher.

We rebuilt the smartphone user interface to enable you, to follow your intention. We specifically designed it for those of you who want to overcome smartphone addiction and be in control over their own devices.

AL0 is honest. Essential functionalities such as phone calls or sms are exposed through a clean and easy to use interface. On the other hand demanding apps and notifications are not first-class citizen it's you who decide what to do with the smartphone.

https://fuji.computer/al0/

## Development
I encourage you to explore, learn, fix, hack and find new ways to use this code.

### Setup
TODO.

### Keys
1. Make sure gradle.properties is avaialable in the root folder with the following lines:
```
android.useAndroidX=true
android.enableJetifier=true
AL0_UPLOAD_STORE_FILE=your-keystore-file-name
AL0_UPLOAD_KEY_ALIAS=your-keystore-alias
AL0_UPLOAD_STORE_PASSWORD=your-store-password-here
AL0_UPLOAD_KEY_PASSWORD=your-key-password-here
```

2. Make sure fujicomputer-al0.keystore is available in /app folder.

### Build
To assemble the production apk run the following command from the repo root folder:
```
./gradlew assembleRelease
```

## License
The source code is under the MIT Licence.

Do not sell or distribute this project under a different name.

 
