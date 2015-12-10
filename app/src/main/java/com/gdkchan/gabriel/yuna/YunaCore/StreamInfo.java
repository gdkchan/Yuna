package com.gdkchan.gabriel.yuna.YunaCore;

/**
 * Created by gabriel on 08/10/2015.
 */
public class StreamInfo {
    public enum StreamQuality {
        Unknown,
        q144p,
        q240p,
        q480p,
        q720p,
        q1080p,
        q4k
    }

    public String URL;
    public StreamQuality Quality;

    public StreamInfo(String URL, StreamQuality Quality) {
        this.URL = URL;
        this.Quality = Quality;
    }

    public StreamInfo() {
    }
}
