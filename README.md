# CampusTribuneApp
Android application - Campus Tribune

```
Tools used to run the app
=========================

	Java 8
	Android SDK
	Android Studio
	Gradle
```

```

API keys to be added
=====================

	Google API keys:
			Set google_maps_key value to your google api key in google_maps_api.xml file.
			File path: CampusTribuneApp/app/src/debug/res/values/google_maps_api.xml

	Amazon S3 keys:
			Set MY_ACCESS_KEY_ID value to your amazon access key id.
			Set MY_SECRET_KEY to value of your secret key.
			Set BUCKET to value of your S3 bucket name.
			File path: CampusTribuneApp/app/src/main/java/com/campustribune/helper/Util.java

```


```

Server host specifications
==========================
If server is deployed on Amazon EC2, set SERVER_URL in Util.java file to the EC2 public DNS.
If server is run on localhost, set SERVER_URL in Util.java file to localhost ip.

```