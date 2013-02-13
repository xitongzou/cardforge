Memory Issues:

In the past, some people noticed java heap space errors and lengthy pauses. The memory requirements for Forge have increased over time. The default setting on your computer for the java heap space may not be enough to prevent the above problems.

The technically proficient can launch the forge jar with an argument from the CLI. The argument "-Xmx512m" may work for computers with 1 Gig of memory. Computers with 2 Gigs or more of memory should be able to use "-Xmx1024m" as an argument.

We have created several scripts that will launch the Forge jar with "-Xmx1024m" as an argument. People using Windows OS should double click the "forge.exe" file. People using Apple's Mac OS X should double click the "forge.command" file. People using one of the other *nix OS should double click the "forge.sh" file.

The script file must be located in the same folder as the "run-forge.jar" file and the "run-forge.jar" file name can not be changed. Otherwise, the scripts will not work.

If you have a low end machine you may find that the scripts above will prevent java heap space errors but will find that forge still runs very slowly at times.

In this case you can try the following. Remove the background jpg picture from /res/images/ui/ folder. You can try using low quality pictures rather than the high quality pictures. Or you can try removing all of the jpg pictures from the pics folder. You can also try using the old style battlefield UI rather than the newer battlefield UI.

We have changed the archiving format to ".tar.bz2" since this may help to store the file permissions for the Mac OS X launcher. The Windows launcher in this version does not require you to rename the forge JAR file to work properly. Please keep the forge JAR file name set to "run-forge.jar".


Card Picture Issues:

The server which contained the high quality card pictures is now off line and these high quality card pictures are no longer available as a download from within the forge application. We apologize, but the current dev team do not maintain this server and this matter is out of our control.

Some people are choosing to re-download all of the low quality card and card set pictures when they install the next version of forge. This consumes large amounts of bandwidth needlessly.

The server containing the set pictures is limited to 30 gigs per month. At the current rate the server will hit the maximum of 30 gigs per month long before we reach the end of the month. Please be careful!

When you install the new version of forge find the forge/res/pics/ folder. Either move it or copy and paste the pics folder over to the recently installed new version of forge. This way you will only have to download the pictures for the new cards.

This should save enough bandwidth that everyone will be able to download the new set pictures from the cardforge server. We do appreciate your efforts to save bandwidth.


How to file a bug report via google code:

1) Go to http://code.google.com/p/cardforge/issues/list.

2) Get a Googlemail account if you don't have one already.

3) Search for existing open issues with key words from your bug report (e.g. "Sphinx-Bone Wand"). If an issue describing your bug does already exist, you can provide additional information in its comment section.

4) Post your issue by clicking on "new issue". You will receive a template that you can fill out. Under "version", write the release date of the beta (e.g. "BETA 06/12/2011") or the revision you used to compile your Forge version if you use the SVN (e.g. "Revision 9875"; command: "./svn.exe info forge-svn").

5) If there are news regarding your issue, you will automatically be notified via your Googlemail address.


Our Lawyers Made Us Do This:

This product includes software developed by the Indiana University Extreme! Lab (http://www.extreme.indiana.edu/).