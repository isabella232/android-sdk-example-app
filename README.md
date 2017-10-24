# Standard Push Configuration

This is an example app using the Vibes Android SDK. It implements device
registration/unregistration and push registration/unregistration of the Vibes
SDK.

> _Note_ This application has been tested using Android Studio version 2.3.3, running on
JDK 1.8.0.
</aside>

## Setup

The setup is as follows:

1. In ``AndroidManifest.xml`` replace ``[YOUR KEY GOES HERE]`` with your
   vibes_app_id.
2. (Optional) Create a [Firebase][1] project using the package name.
3. Build and run.

> _Note_ The main purpose of this example app is to show a standard integration with the
Vibes Android SDK. Sending push notification is outside the scope of this
example app, but you have the option to configure your own Firebase project and
send out notifications.

[1]: https://firebase.google.com/docs/android/setup

## SDK Usage

The part of this application that is most relevant to the SDK integration is in
``ExampleController``.  In this file you can find calls to the Vibes SDK to
register and unregister a device or a push notification.
