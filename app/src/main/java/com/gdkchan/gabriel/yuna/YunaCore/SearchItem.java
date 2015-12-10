package com.gdkchan.gabriel.yuna.YunaCore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by gabriel on 06/10/2015.
 */
public class SearchItem implements Parcelable {
    public String Title;
    public String Description;
    public String Author;
    public String Duration;
    public String URL;
    public Bitmap Thumbnail;

    public SearchItem(String Title, String Description, String Author, String Duration, String URL, String Thumbnail) {
        this.Title = Title;
        this.Description = Description;
        this.Author = Author;
        this.Duration = Duration;
        this.URL = URL;

        try {
            InputStream in = new URL(Thumbnail).openStream();
            this.Thumbnail = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SearchItem() {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Title);
        out.writeString(Description);
        out.writeString(Author);
        out.writeString(Duration);
        out.writeString(URL);
        out.writeInt(Thumbnail != null ? 1 : 0);
        if (Thumbnail != null) Thumbnail.writeToParcel(out, 0);
    }

    public static final Parcelable.Creator<SearchItem> CREATOR = new Parcelable.Creator<SearchItem>() {
        public SearchItem createFromParcel(Parcel in) {
            return new SearchItem(in);
        }

        public SearchItem[] newArray(int size) {
            return new SearchItem[size];
        }
    };

    private SearchItem(Parcel in) {
        Title = in.readString();
        Description = in.readString();
        Author = in.readString();
        Duration = in.readString();
        URL = in.readString();
        int HasThumbnail = in.readInt();
        if (HasThumbnail != 0) Thumbnail = Bitmap.CREATOR.createFromParcel(in);
    }
}
