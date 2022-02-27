package computer.fuji.al0.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import computer.fuji.al0.models.Media;
import java.util.ArrayList;

public class MediaService {
    public static ArrayList<Media> getMediaList (Context context, int numberOfMedia) {
        return getAllMediaList(context, numberOfMedia);
    }

    public static void addMedia (Context context, Media.Type type, String path, String mimeType) {
        ContentValues values = new ContentValues();
        switch (type) {
            case Image:
                values.put(MediaStore.Images.Media.DATA, path);
                values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                break;
            case Video:
                values.put(MediaStore.Video.Media.DATA, path);
                values.put(MediaStore.Video.Media.MIME_TYPE, mimeType);
                context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                break;
        }
    }

    public static void deleteMedia (Context context, String path) {
        Uri uri = MediaStore.Files.getContentUri("external");

        String selection = MediaStore.Files.FileColumns.DATA + "=?";

        String[] selectionArgs = new String[] { path };
        context.getContentResolver().delete(uri, selection, selectionArgs);
    }

    // get all images
    private static ArrayList<Media> getAllMediaList (Context context, int numberOfMedia) {
        ArrayList<Media> mediaList = new ArrayList<>();

        Uri uri = MediaStore.Files.getContentUri("external");

        String[] projection = new String[] {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };


        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        String[] selectionArgs = new String[] {};

        String sortOrder =
                MediaStore.Video.Media.DATE_ADDED + " desc".concat(" LIMIT ").concat(String.valueOf(numberOfMedia));

        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
        int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
        int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        int mimeColumn =  cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);

        while (cursor.moveToNext()) {
            // Get values of columns for a given image.
            long id = cursor.getLong(idColumn);
            String name = cursor.getString(nameColumn);
            String path = cursor.getString(pathColumn);
            Media.Type type = cursorColumnToMediaType(cursor.getInt(mimeColumn));
            // add element at head to prevent reversing the ArrayList later
            // recent items need to be a the end of the list
            mediaList.add(0, new Media(String.valueOf(id), name, path, type));
        }

        return mediaList;
    }

    private static Media.Type cursorColumnToMediaType (int value) {
        switch (value) {
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                return Media.Type.Video;
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
            default:
                return Media.Type.Image;
        }
    }
}
