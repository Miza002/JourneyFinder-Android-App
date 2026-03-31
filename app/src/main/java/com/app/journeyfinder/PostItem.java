package com.app.journeyfinder;

//stored for all posts contents
public class PostItem {
    private final String username;
    private final String userProfileUrl;
    private final String timePosted;
    private final String caption;
    private final String imageUrl;

    public PostItem(String username, String userProfileUrl, String timePosted, String caption, String imageUrl) {
        this.username = username;
        this.userProfileUrl = userProfileUrl;
        this.timePosted = timePosted;
        this.caption = caption;
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }
    public String getUserProfileUrl() {
        return userProfileUrl;
    }
    public String getTimePosted() {
        return timePosted;
    }
    public String getCaption() {
        return caption;
    }
    public String getImageUrl() {
        return imageUrl;
    }
}
