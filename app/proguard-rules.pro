# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
#
# Add any project specific keep options here:
#
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:

#-renamesourcefileattribute SourceFile
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

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.v4.app.Fragment


-ignorewarning

-dump proguard/class_files.txt
-printseeds proguard/seeds.txt
-printusage proguard/unused.txt
-printmapping proguard/mapping.txt

-dontwarn android.support.**

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-dontwarn net.poemcode.**
-keep class android.content.* {*;}
-dontwarn android.content.**

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class [com.cover.load].R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


-keepnames class * implements java.io.Serializable


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
  -keepclasseswithmembers class org.jboss.netty.util.internal.LinkedTransferQueue {
    volatile transient org.jboss.netty.util.internal.LinkedTransferQueue$Node head;
    volatile transient org.jboss.netty.util.internal.LinkedTransferQueue$Node tail;
    volatile transient int sweepVotes;

  }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    public <init>(java.lang.Throwable);
}
-keep public abstract interface com.asqw.android.Listener{
public protected <methods>;

}
-keep class com.newsdk.bean.** {
     *;
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

-dontwarn com.squareup.picasso.**
-keep class com.squareup.picasso.** {*;}

##---------------Begin: proguard configuration for facebook  ----------
-dontwarn com.facebook.**
-keep class com.facebook.** { *; }

-keep class com.solid.news.bean.** { *; }






