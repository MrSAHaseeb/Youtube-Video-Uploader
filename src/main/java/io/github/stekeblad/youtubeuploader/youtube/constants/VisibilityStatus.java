package io.github.stekeblad.youtubeuploader.youtube.constants;

public enum VisibilityStatus {
    PUBLIC("public"),
    PRIVATE("private"),
    UNLISTED("unlisted");

    private final String status;

    VisibilityStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return status;
    }
}
