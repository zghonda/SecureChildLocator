# SecureChildLocator demonstration app

Required platforms and tools:

Linux 64-bit/MAC OS system with at least 8gb of ram 
Android studio with android sdk 28 (android 8.0)
Android emulator with nexus 4 phone ( good compromise for performance and memory usage ).
Java 8

Since all team members have only Iphones we couldn't test SCL on a real physical device so we used virtual emulator since they do pretty good the job.
The consequence of that we chose to not export the app to an .APK file for installing directly on phones. We can't provide you non-tested material even if it's 100% working virtually

*** Testing the app ***

1- Download/install android studio with sdk 28.

2- Create virtual phone nexus 4 with android 8.0 version in with android virtual manager device(AVD in tools tab) in android studio.
Then launch AVD menu again go to settings of nexus 4 then advanced settings then selected your webcam as back camera for the phone.

3- Clone from github and Make the project(in build tab).

4- Run on the virtual phone (high memory usage here).

5- Wait until the app is installed on the phone.

6- Be sure to provide camera and location authorization for SCL app in the phone settings otherwise it will crash or don't work.

7- Open the SCL app, choose parent, connect with user "mother@gmail.com" and password "abcd1234".

8- Generate a QR code, 
IMPORTANT: Since normally we need at least two phones for testing correctly we will just use one virtual so we would like you to take a picture of the QR code.

9- Don't start receiving data now just Go back to the main screen, open child , scan the qr code from the picture.

10- Now the child should start sending the data. 

11- You can go back to the parent and see the location of the child in realtime (in this example you will have predefined location). 

We will show the demonstration app at the presentation in case the instructions are not clear.
