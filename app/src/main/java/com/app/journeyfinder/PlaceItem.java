package com.app.journeyfinder;

import com.google.android.libraries.places.api.model.PhotoMetadata;

//store the info of places
public class PlaceItem {
    private final String name;
    private final String address;
    private final String photoUrl;
    private final PhotoMetadata photoMetadata;

    public PlaceItem(String name, String address, String photoUrl, PhotoMetadata photoMetadata, Object type) {
        this.name = name;
        this.address = address;
        this.photoUrl = photoUrl;
        this.photoMetadata = photoMetadata;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public PhotoMetadata getPhotoMetadata() {
        return photoMetadata;
    }
}
