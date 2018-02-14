package io.github.stekeblad.videouploader.youtube;

import io.github.stekeblad.videouploader.youtube.utils.VisibilityStatus;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * Can represents a video that is being prepared for uploading or currently being uploaded.
 */
public class VideoUpload extends VideoInformationBase{

    public static final String VIDEO_FILE_FORMAT = "video/";
    public static final String NODE_ID_PROGRESS = "_progress";
    public static final String NODE_ID_UPLOADSTATUS = "_status";
    public static final String NODE_ID_BUTTONSBOX = "_buttons";

    private File videoFile;
    private GridPane uploadPane;

    /**
     * @return returns a File object with the video file set to be used when uploading
     */
    public File getVideoFile() {
        return this.videoFile;
    }

    /**
     * @return returns the entire UI pane for placement on screen
     */
    public GridPane getUploadPane() {
        return this.uploadPane;
    }

    /**
     * @return returns the Id of the button in the first button slot. Can be used to know what button is there at the moment
     */
    public String getButton1Id() {
        return ((HBox) uploadPane.lookup("#" + getPaneId() + NODE_ID_BUTTONSBOX)).getChildren().get(0).getId();
    }

    /**
     * @return returns the Id of the button in the second button slot. Can be used to know what button is there at the moment
     */
    public String getButton2Id() {
        return ((HBox) uploadPane.lookup("#" + getPaneId() + NODE_ID_BUTTONSBOX)).getChildren().get(1).getId();
    }

    /**
     * @return returns the Id of the button in the third button slot. Can be used to know what button is there at the moment
     */
    public String getButton3Id() {
        return ((HBox) uploadPane.lookup("#" + getPaneId() + NODE_ID_BUTTONSBOX)).getChildren().get(2).getId();
    }

    /**
     * Enables / Disables editing of all fields of the pane
     * @param newEditStatus true to allow edit, false to not allow
     */
    public void setEditable(boolean newEditStatus) {
        super.setEditable(newEditStatus);
        // Does not extend with any editable fields
    }

    /**
     * Place a button in the first button slot with your own text, click behavior etc.
     * @param btn1 A fully configured button
     */
    public void setButton1(Button btn1) {
        ((HBox) uploadPane.lookup("#" + getPaneId() + NODE_ID_BUTTONSBOX)).getChildren().set(0, btn1);
    }

    /**
     * Place a button in the second button slot with your own text, click behavior etc.
     * @param btn2 A fully configured button
     */
    public void setButton2(Button btn2) {
        ((HBox) uploadPane.lookup("#" + getPaneId() + NODE_ID_BUTTONSBOX)).getChildren().set(1, btn2);
    }

    /**
     * Place a button in the third button slot with your own text, click behavior etc.
     * @param btn3 A fully configured button
     */
    public void setButton3(Button btn3) {
        ((HBox) uploadPane.lookup("#" + getPaneId() + NODE_ID_BUTTONSBOX)).getChildren().set(2, btn3);
    }

    /**
     * Updates the progress bar in the UI
     * @param progress the upload progress as a value between 0 and 1
     */
    public void setProgressBarProgress(double progress) {
        if (progress >=0 && progress <= 1) {
            ((ProgressBar) uploadPane.lookup("#" + getPaneId() + NODE_ID_PROGRESS)).setProgress(progress);
        }
    }

    /**
     * Sets the text to be displayed on the status label in the UI
     * @param text the text to show
     */
    public void setStatusLabelText(String text) {
        ((Label) uploadPane.lookup("#" + getPaneId() + NODE_ID_UPLOADSTATUS)).setText(text);
    }

    /**
     * Sets a URL to open when the status label is clicked
     * @param url the url to open
     */
    public void setStatusLabelOnClickUrl(String url) {
        uploadPane.lookup("#" + getPaneId() + NODE_ID_UPLOADSTATUS)
                .setOnMouseClicked(event -> {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(new URI(url));
                        } catch (Exception e) {
                            System.err.println("Could not make upload label clickable");
                        }
                    }
                });
    }

    /**
     * Constructor for VideoUpload. There is also the VideoUpload.Builder class if that is preferred.
     * Everything set here can be edited later except paneId and videoFile
     * @param videoName Title of the video that will be uploaded
     * @param videoDescription Description of the video that will be uploaded
     * @param visibility The visibility status of the video that will be uploaded
     * @param videoTags The tags that the video that will be uploaded will have
     * @param playlist The name of a playlist that will be selected for added to after uploading
     * @param category The name of the category that should be selected
     * @param tellSubs Set to true if subscribers should be notified when this videos is uploaded, set to false to not notify
     * @param thumbNailPath File path to a thumbnail to use for this video or null to let thumbnail be selected automatically
     * @param paneName A string used for naming all UI elements
     * @param videoFile The video that will be uploaded
     */
    public VideoUpload(String videoName, String videoDescription, VisibilityStatus visibility, List<String> videoTags,
                       String playlist, String category, boolean tellSubs, String thumbNailPath, String paneName, File videoFile) {

        super(videoName, videoDescription, visibility, videoTags, playlist, category, tellSubs, thumbNailPath, paneName);
        this.videoFile = videoFile;
        makeUploadPane();
    }

    /**
     * Reconstructs a VideoUpload form its string version created by calling toString()
     * @param fromString The string representation of a VideoUpload
     * @param paneId A string used for naming all UI elements
     * @throws Exception If the string could not be converted to a VideoUpload
     */
    public VideoUpload(String fromString, String paneId) throws Exception{
        super(fromString, paneId);
        String[] lines = fromString.split("\n");
        for (String line : lines) {
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                switch (line.substring(0, colonIndex)) {
                    case "_videofile":
                        videoFile = new File(line.substring((colonIndex + 1)));
                        break;
                    default:
                        // likely belongs to parent
                }
            }
        }
        makeUploadPane();
    }

    /**
     *Creates a copy of this VideoUpload with the same or different paneId.
     * @param paneIdCopy The paneId used for naming the nodes in the copy, use null to get the same as original.
     *                      if null is given then the original and the copy may not be able to be on screen at the same time,
     *                      the nodes will be considered to be the same and when placing the second one of them it will cause the
     *                      nodes from the first to be moved to the location of the second.
     * @return a copy of this VideoUpload
     */
    public VideoUpload copy(String paneIdCopy) {
        if(paneIdCopy == null) {
            paneIdCopy = getPaneId();
        }
        String thumbnailPath;
        if(getThumbNail() == null) {
            thumbnailPath = null;
        } else {
            thumbnailPath = getThumbNail().getAbsolutePath();
        }
        return new VideoUpload(getVideoName(), getVideoDescription(), getVisibility(), getVideoTags(), getPlaylist(),
                getCategory(), isTellSubs(), thumbnailPath, paneIdCopy, getVideoFile());
    }

    /**
     *  Used for building a VideoUpload one attribute at the time.
     * Call build() to get a real VideoUpload when you are done setting attributes.
     */
    public static class Builder extends VideoInformationBase.Builder{
        File videoFile;

        public File getVideoFile() {
            return videoFile;
        }

        public VideoUpload.Builder setVideoFile(File videoFile) {
            this.videoFile = videoFile;
            return this;
        }

        // Re-implementation of setters in super to get the right return type
        public VideoUpload.Builder setVideoName(String videoName) {
            this.videoName = videoName;
            return this;
        }

        public VideoUpload.Builder setVideoDescription(String videoDescription) {
            this.videoDescription = videoDescription;
            return this;
        }

        public VideoUpload.Builder setVisibility(VisibilityStatus visibility) {
            this.visibility = visibility;
            return this;
        }

        public VideoUpload.Builder setVideoTags(List<String> videoTags) {
            this.videoTags = videoTags;
            return this;
        }

        public VideoUpload.Builder setPlaylist(String playlist) {
            this.playlist = playlist;
            return this;
        }

        public VideoUpload.Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public VideoUpload.Builder setTellSubs(boolean tellSubs) {
            this.tellSubs = tellSubs;
            return this;
        }

        public VideoUpload.Builder setThumbNailPath(String thumbNailPath) {
            this.thumbNailPath = thumbNailPath;
            return this;
        }

        public VideoUpload.Builder setPaneName(String paneName) {
            this.paneName = paneName;
            return this;
        }

        public VideoUpload build() {
            return new VideoUpload(getVideoName(), getVideoDescription(), getVisibility(), getVideoTags(), getPlaylist(),
                    getCategory(), isTellSubs(), getThumbNailPath(), getPaneName(), videoFile);
        }
    }

    /**
     * Creates the UI Pane so it can be be retrieved by front end code with getUploadPane()
     */
    protected void makeUploadPane() {
        // The base class has already done most of the work
        uploadPane = super.getPane();
        // Make the pane slightly larger to fit the extra nodes
        uploadPane.setPrefHeight(170);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setId(getPaneId() + NODE_ID_PROGRESS);
        progressBar.setPrefWidth(200);
        progressBar.setVisible(false);

        Label uploadStatus = new Label("Upload not started");
        uploadStatus.setId(getPaneId() + NODE_ID_UPLOADSTATUS);

        Button ghostBtn1 = new Button("");
        ghostBtn1.setVisible(false);
        Button ghostBtn2 = new Button("");
        ghostBtn2.setVisible(false);
        Button ghostBtn3 = new Button("");
        ghostBtn3.setVisible(false);
        HBox buttonsBox = new HBox(5, ghostBtn1, ghostBtn2, ghostBtn3);
        buttonsBox.setId(getPaneId() + NODE_ID_BUTTONSBOX);
        buttonsBox.setMinWidth(200);

        uploadPane.add(progressBar, 0, 4);
        uploadPane.add(uploadStatus, 1, 4);
        uploadPane.add(buttonsBox, 2, 4);
    }

    /**
     * Creates a string representation of the class that can be saved and later used to recreate the class as it
     * looked like before with the VideoUpload(String, String) constructor
     * @return A String representation of this class
     */
    public String toString() {
        String classString = super.toString();
        classString += "\n_videofile:" + videoFile.getAbsolutePath();
        return classString;
    }
}