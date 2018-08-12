# Privacy Policy

**Last update: August 2018**

Stekeblads Video Uploader cares about your privacy and sends a minimal amount 
of personal information over the internet (including both to servers controlled by 
Stekeblads Video Uploader and to third parties). If you have any questions about how 
your data is used after reading the policy you can [look at the source code](https://github.com/Stekeblad/Stekeblads-Video-Uploader/) and check what happens yourself or [check the issue tracker](https://github.com/Stekeblad/Stekeblads-Video-Uploader/issues?utf8=✓&q=) 
to see if a simmilar question has alredy been asked and [create a new one](https://github.com/Stekeblad/Stekeblads-Video-Uploader/issues/new) if you cant find anything.

The program integrates with YouTube, a third party service not related to Stekeblads 
Video uploader, that have a different policy about how they process your data. 
By granting the program access to your YouTube channel you give
Stekeblads Video Uploader permission to share data you entered in the program with YouTube. 
No data will be shared with Youtube before permission has been granted.

**Currently information you provide is handled in two different ways:**
- Saved locally on your computer in the "uploader data" directory located in the same 
directory as the program .jar-file
- Sent to YouTube in response to actions you perform in the program (after you have 
given permission to access your YouTube channel).
  - Currently no other party (including Stekeblads Video Uploader) directly recieve any data from the program.

**Saved Locally:** Stekeblads Video Uploader saves all its settings in one place, 
the "uploader data" directory located at the same place as the program's .jar-file. 
To delete all local data the program uses you can either delete said directory or 
go into the settings window and press the "CLEAR STORED DATA"-button (settings window {was/will be} added in version 1.2.) 
This is a (incomplete) list showing what type of information is saved and what it is used for:
- Data retrieved from YouTube 
  - A list of all your playlists - 
  So you can select a playlist to save the video to, saved to not have to get the list on every launch
  - A list of available YouTube video categories - 
  All videos need to belong to a category, saved to not have to get them on every launch
  - A permission token to your channel - 
  so you not need to log in every time, [You can at any time make the token invallid](https://github.com/Stekeblad/Stekeblads-Video-Uploader/wiki/Revoking-Youtube-Permission) 
- Data you enter
  - Preset details
  - Language and country code used last time when retrieving categories - 
  so you know what settings was used for the current categories
  - Your display language of choice - If you do not want to use the detected one (see next point)
- Automatically detected information
  - Your system locale - To show the program in your language, if a translation is availbale.
  
**Shared With YouTube:** In order to be able to upload videos to your channel must 
Stekeblads Video Uploader share some data with YouTube. No data is shared with Youtube before 
you have granted the program permission to do so and only the information required to perform 
the requested action is sent. When applicible, the information sent would be similar to what 
is required when doing the same thing on YouTube.
Here is some of the requset sent to youtube and what triggers them:
- Creating a playlist - Clicking on the create new playlist buttton in the manage playlists window
- Get playlists - The refresh playlists button in the manage playlists window
- Get categories - The get categorize button in the localize categories window
- Upload a video - Clicking on start upload for a item in the main window schedules 
the clicked video and its details for uploading (only one is uploading at a time)


