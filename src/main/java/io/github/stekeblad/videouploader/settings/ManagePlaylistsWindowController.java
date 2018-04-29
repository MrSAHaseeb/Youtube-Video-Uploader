package io.github.stekeblad.videouploader.settings;

import io.github.stekeblad.videouploader.utils.AlertUtils;
import io.github.stekeblad.videouploader.utils.ConfigManager;
import io.github.stekeblad.videouploader.utils.Translations;
import io.github.stekeblad.videouploader.utils.TranslationsManager;
import io.github.stekeblad.videouploader.utils.background.OpenInBrowser;
import io.github.stekeblad.videouploader.youtube.LocalPlaylist;
import io.github.stekeblad.videouploader.youtube.utils.PlaylistUtils;
import io.github.stekeblad.videouploader.youtube.utils.VisibilityStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Optional;

public class ManagePlaylistsWindowController {
    public Button btn_refreshPlaylists;
    public Button btn_addNewPlaylist;
    public TextField txt_newPlaylistName;
    public ListView<CheckBox> list_playlists;
    public ChoiceBox<String> choice_privacyStatus;
    public GridPane window;
    public ToolBar toolbar;
    public Label label_description;

    private ConfigManager configManager = ConfigManager.INSTANCE;
    private PlaylistUtils playlistUtils = PlaylistUtils.INSTANCE;
    private Translations transPlaylistWindow;
    private Translations transBasic;

    /**
     * Initialize a few things when the window is opened, used instead of initialize as that one does not have access to the scene
     */
    public void myInit() {
        // Insert the stored playlists into the list
        updatePlaylistList();

        // Load Translations
        transBasic = TranslationsManager.getTranslation("baseStrings");
        transPlaylistWindow = TranslationsManager.getTranslation("manPlayWindow");
        transPlaylistWindow.autoTranslate(window);

        // cant autoTranslate Nodes in Toolbar (bug)
        txt_newPlaylistName.setPromptText(transPlaylistWindow.getString("txt_newPlaylistName_pt"));
        choice_privacyStatus.setTooltip(new Tooltip(transPlaylistWindow.getString("choice_privacyStatus_tt")));
        btn_addNewPlaylist.setText(transPlaylistWindow.getString("btn_addNewPlaylist"));

        // Set choices in playlist privacy choiceBox
        ArrayList<VisibilityStatus> statuses = new ArrayList<>(EnumSet.allOf(VisibilityStatus.class));
        ArrayList<String> visibilityStrings = new ArrayList<>();
        for (VisibilityStatus status : statuses) {
            visibilityStrings.add(status.getStatusName());
        }
        choice_privacyStatus.setItems(FXCollections.observableArrayList(visibilityStrings));
        choice_privacyStatus.getSelectionModel().select(VisibilityStatus.PUBLIC.getStatusName());

        // Set so pressing F1 opens the wiki page for this window
        window.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F1) {
                OpenInBrowser.openInBrowser("https://github.com/Stekeblad/Stekeblads-Video-Uploader/wiki/Manage-Playlists");
                event.consume();
            }
        });
    }

    /**
     * Executed when the window's close button is triggered
     * @param windowEvent event generated by FXML
     */
    public void onWindowClose(WindowEvent windowEvent) {
        ObservableList<CheckBox> listItems = list_playlists.getItems();
        if (listItems.size() > 0) {
            for (CheckBox listItem : listItems) {
                playlistUtils.setVisible(listItem.getText(), listItem.isSelected());
            }
            playlistUtils.saveCache();
        }
        // event not consumed, it would cause the window to remain open
    }

    /**
     * Downloads a list of all playlists on the user's channel and updates the list on screen
     * @param actionEvent the button click event
     */
    public void onRefreshPlaylistsClicked(ActionEvent actionEvent) {
        if (configManager.getNeverAuthed()) {
            Optional<ButtonType> buttonChoice = AlertUtils.yesNo(transBasic.getString("auth_short"),
                    transBasic.getString("auth_full")).showAndWait();
            if (buttonChoice.isPresent()) {
                if (buttonChoice.get() == ButtonType.YES) {
                    configManager.setNeverAuthed(false);
                    configManager.saveSettings();
                } else { // ButtonType.NO or closed [X]
                    actionEvent.consume();
                    return;
                }
            }
        }
        // Auth done or user is ready to allow it
        // Do not allow the button to be clicked again until the window is closed and reopened
        btn_refreshPlaylists.setDisable(true);

        // Send the request in the background
        Task<Void> backgroundTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                playlistUtils.refreshPlaylist(); // Get playlists from Youtube on background thread
                Platform.runLater(() -> updatePlaylistList()); // update list in window on UI thread
                return null;
            }
        };

        Thread backgroundThread = new Thread(backgroundTask);
        // Define a handler for exceptions
        backgroundThread.setUncaughtExceptionHandler((t, e) -> Platform.runLater(() -> {
            AlertUtils.simpleClose(transBasic.getString("error"),
                    transPlaylistWindow.getString("diag_downloadFailed")).showAndWait();
            e.printStackTrace();
            btn_refreshPlaylists.setDisable(false);
        }));

        // Start downloading playlists in the background and return
        backgroundThread.start();
        actionEvent.consume();
    }

    /**
     * Creates a new playlist on the user's channel using the content of txt_newPlaylistName_pt and choice_privacyStatus
     * @param actionEvent the button click event
     */
    public void onAddNewPlaylistClicked(ActionEvent actionEvent) {
        if(txt_newPlaylistName.getText().isEmpty()) {
            AlertUtils.simpleClose(transPlaylistWindow.getString("diag_noPlaylistName_short"),
                    transPlaylistWindow.getString("diag_noPlaylistName_full")).show();
            return;
        }
        if (configManager.getNeverAuthed()) {
            Optional<ButtonType> buttonChoice = AlertUtils.yesNo(transBasic.getString("auth_short"),
                    transBasic.getString("auth_full")).showAndWait();
            if (buttonChoice.isPresent()) {
                if (buttonChoice.get() == ButtonType.YES) {
                    configManager.setNeverAuthed(false);
                    configManager.saveSettings();
                } else { // ButtonType.NO or closed [X]
                    actionEvent.consume();
                    return;
                }
            }
        }

        // Auth OK, add the playlist
        btn_addNewPlaylist.setDisable(true);
        btn_addNewPlaylist.setText(transPlaylistWindow.getString("creating"));
        final String listName = txt_newPlaylistName.getText();
        final String privacyLevel = choice_privacyStatus.getSelectionModel().getSelectedItem();

        // Perform the request in a background thread
        Task<Void> backgroundTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                LocalPlaylist localPlaylist = playlistUtils.addPlaylist(listName, privacyLevel);
                if (localPlaylist == null) {
                    Platform.runLater(() -> AlertUtils.simpleClose(transBasic.getString("error"),
                            transPlaylistWindow.getString("diag_creatingFailed")).show());
                    return null;
                }
                CheckBox cb = new CheckBox(localPlaylist.getName());
                cb.setSelected(true);
                Platform.runLater(() -> {
                    list_playlists.getItems().add(cb);
                    txt_newPlaylistName.setText(""); // visually indicate its done by clearing the new playlist name textField
                    btn_addNewPlaylist.setDisable(false);
                    btn_addNewPlaylist.setText(transPlaylistWindow.getString("btn_addNewPlaylist"));
                });
                return null;
            }
        };
        Thread backgroundThread = new Thread(backgroundTask);
        // Exception handler
        backgroundThread.setUncaughtExceptionHandler((t, e) -> Platform.runLater(() -> {
            AlertUtils.simpleClose(transBasic.getString("error"),
                    transPlaylistWindow.getString("diag_creatingFailed")).showAndWait();
            e.printStackTrace();
        }));

        // Start the background thread and return
        backgroundThread.start();
        actionEvent.consume();
    }

    /**
     * Makes sure the playlist list is up to date
     */
    private void updatePlaylistList() {
        ArrayList<CheckBox> playlistCheckBoxes = new ArrayList<>();
        ArrayList<LocalPlaylist> playlists = playlistUtils.getAllPlaylists();
        if (playlists != null) {
            for (LocalPlaylist playlist : playlists) {
                CheckBox cb = new CheckBox(playlist.getName());
                cb.setSelected(playlist.isVisible());
                playlistCheckBoxes.add(cb);
            }
            // Sorts the playlists lexicographically
            playlistCheckBoxes.sort(Comparator.comparing(Labeled::getText));
            list_playlists.setItems(FXCollections.observableArrayList(playlistCheckBoxes));
        }
    }
}
