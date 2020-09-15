package fr.media85.myradioapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("artist")
    @Expose
    private String artist;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("album")
    @Expose
    private Object album;
    @SerializedName("started_at")
    @Expose
    private String startedAt;
    @SerializedName("end_at")
    @Expose
    private String endAt;
    @SerializedName("next_track")
    @Expose
    private String nextTrack;
    @SerializedName("duration")
    @Expose
    private Double duration;
    @SerializedName("buy_link")
    @Expose
    private Object buyLink;
    @SerializedName("is_live")
    @Expose
    private Boolean isLive;
    @SerializedName("cover")
    @Expose
    private String cover;
    @SerializedName("default_cover")
    @Expose
    private Boolean defaultCover;
    @SerializedName("forced_title")
    @Expose
    private Boolean forcedTitle;


    public Attributes(String artist, String title, Object album, Object buyLink, String cover) {
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.buyLink = buyLink;
        this.cover = cover;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getAlbum() {
        return album;
    }

    public void setAlbum(Object album) {
        this.album = album;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getNextTrack() {
        return nextTrack;
    }

    public void setNextTrack(String nextTrack) {
        this.nextTrack = nextTrack;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Object getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(Object buyLink) {
        this.buyLink = buyLink;
    }

    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean isLive) {
        this.isLive = isLive;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Boolean getDefaultCover() {
        return defaultCover;
    }

    public void setDefaultCover(Boolean defaultCover) {
        this.defaultCover = defaultCover;
    }

    public Boolean getForcedTitle() {
        return forcedTitle;
    }

    public void setForcedTitle(Boolean forcedTitle) {
        this.forcedTitle = forcedTitle;
    }

}
