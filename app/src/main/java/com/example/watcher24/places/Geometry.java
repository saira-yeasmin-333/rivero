package com.example.watcher24.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("viewport")
    @Expose
    private Viewport viewport;

    public Location getLocation() {
        return location;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
}
