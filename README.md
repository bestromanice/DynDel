# DynDel

# It is working correctly when the feature module is installed by default:
# In the MainActivity from the base module we click on the button with feature and it opens DynamicActivity{number} from dynamicfeature{number}

# To build apk:
java -jar bundletool.jar build-apks --bundle=app-debug.aab --output=app-debug.apks --local-testing

# To install apk:
java -jar bundletool.jar install-apks --adb=D:\AndroidSdk\platform-tools\adb.exe --apks=app-debug.apks
