# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android\SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# repackage class
-repackageclasses 'o'
-allowaccessmodification

-keepattributes SourceFile,LineNumberTable


-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-optimizationpasses 5

-dontusemixedcaseclassnames

-dontskipnonpubliclibraryclasses

-dontoptimize

-dontpreverify

-verbose

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *Annotation*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-ignorewarning
-dontwarn android.support.**
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.free.wifi.update.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}


  -keep public abstract interface com.asqw.android.Listener{
  public protected <methods>;
  }
  -keep public class com.asqw.android{
  public void Start(java.lang.String);
  }
  -keepclasseswithmembernames class * {
  native <methods>;
  }
  -keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet);
  }
  -keepclassmembers class * extends android.app.Activity {
  public void *(android.view.View);
  }
  -keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
  }
  -keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
  }
-keep public class com.google.gson.**
-keep public class com.google.gson.** {public private protected *;}

-keepattributes Signature
-keepattributes *Annotation*
-keep public class com.project.mocha_patient.login.SignResponseData { private *; }


-dontwarn com.google.android.gms.**

##---------------Begin: proguard configuration for okhttp  ----------
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.*
##---------------End: proguard configuration for okhttp  ----------


##---------------Begin: proguard configuration for facebook  ----------
-dontwarn com.facebook.**
-keep class com.facebook.** { *; }
##---------------End: proguard configuration for facebook  ----------

-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-keepclasseswithmembers public class com.queries.alternate.service.DownloadManager {
	public <fields>;
	public <methods>;
}
-keep class com.moon.moder.ModerSdk.** { *;}
-keep class com.parser.sdk.ParserSdk.** { *;}
-keepclasseswithmembers public class com.queries.alternate.service.bean.** { *; }
-keepclasseswithmembers public class com.parser.sdk.bean.** { *; }
-keepclasseswithmembers public class com.moon.moder.request.** { *; }
-keepattributes *Annotation*
##---------------Begin: proguard configuration for okhttp  ----------
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.*
##---------------End: proguard configuration for okhttp  ----------

-dontwarn com.squareup.picasso.**
-keep class com.squareup.picasso.** {*;}

##---------------Begin: proguard configuration for facebook  ----------
-dontwarn com.facebook.**
-keep class com.facebook.** { *; }

-keep class com.solid.news.bean.** { *; }






