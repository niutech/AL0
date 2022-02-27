package computer.fuji.al0.models;

import android.net.Uri;

public class SoundModel {
    private String name;
    private Uri uri;

    public SoundModel (String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }
}
