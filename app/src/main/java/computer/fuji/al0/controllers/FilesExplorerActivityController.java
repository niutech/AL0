package computer.fuji.al0.controllers;

import android.view.View;

import computer.fuji.al0.R;
import computer.fuji.al0.activities.FilesExplorerActivity;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Dialog;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.models.Media;
import computer.fuji.al0.services.MediaService;

import java.util.ArrayList;

public class FilesExplorerActivityController {
    FilesExplorerActivity activity;
    ArrayList<ListItem> filesItems;
    ArrayList<Media> filesMedia;
    Media selectedMedia;
    // use this flag to understand when media viewer is open
    boolean isInShowMediaMode = false;
    // use this variable to understand the number of media item to fetch
    private int mediaItemToFetch = 20;

    // use this variable to increment the number of media to fetch
    private int mediaItemToFetchIncrement = 20;

    public FilesExplorerActivityController (FilesExplorerActivity activity) {
        this.activity = activity;
        filesItems = new ArrayList<>();
        filesMedia = new ArrayList<>();
        updateFiles();
    }

    private void updateFiles () {
        filesItems.clear();
        filesMedia.clear();
        filesMedia = MediaService.getMediaList(activity, mediaItemToFetch);
        for (Media media: filesMedia) {
            filesItems.add(new ListItem(media.getId(), media.getName(), false));
        }
    }

    public ArrayList<ListItem> getFilesListItems () {
        return filesItems;
    }

    // events
    public void onActivityReady () {
        // update files when the activity is ready
        // scroll to bottom, recent items are at bottom of the list
        updateFiles();

        new Thread() {
            @Override
            public void run () {
               updateFiles();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.updateFilesList(filesItems);
                        activity.scrollFilesListToBottom();
                    }
                });
            }
        }.start();
    }

    public void onFilesListItemClick (ListItem item) {
        Media clickedMediaItem = listItemToMediaItem(item);
        if (clickedMediaItem != null) {
            // check if user didn't press the same item
            if (selectedMedia == null || !selectedMedia.getId().equals(clickedMediaItem.getId())) {
                selectedMedia = clickedMediaItem;
            } else {
                selectedMedia = null;
            }
        } else {
            selectedMedia = null;
        }

        activity.setSelectedFileItem(getSelectedMediaId());
    }

    // on top reach
    // load asynchronously media incrementing number of items to fetch
    // when updating the list scroll to the position before the fetch happened
    public void onScrollReachTop () {
        final int previousActivityListSize = filesMedia.size();
        mediaItemToFetch = mediaItemToFetch + mediaItemToFetchIncrement;

        new Thread() {
            @Override
            public void run () {
                // update media list data
                updateFiles();

                // update activity list UI
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        activity.updateFilesList(filesItems);
                        activity.scrollFilesListTo(previousActivityListSize);
                    }
                });
            }
        }.start();
    }

    public void onButtonClosePress () {
        if (isInShowMediaMode) {
            activity.setMediaViewVisible(false, null);
            isInShowMediaMode = false;
        } else {
            activity.finish();
        }
    }

    public void onButtonOpenPress () {
        if (selectedMedia != null) {
            activity.setMediaViewVisible(true, selectedMedia.getType());
            activity.setMediaViewMedia(selectedMedia);
            isInShowMediaMode = true;
        } else {
            selectedMedia = null;
        }
    }

    public void onButtonDeletePress () {
        if (selectedMedia != null) {
            showDeleteItemDialog();
        } else {
            // do nothing
        }
    }

    private void deleteSelectedItem () {
        if (selectedMedia != null) {
            MediaService.deleteMedia(activity, selectedMedia.getPath());
            activity.removeFile(selectedMedia.getId());
            selectedMedia = null;
        } else {
            // do nothing
        }
    }

    private void showDeleteItemDialog () {
        final Dialog dialog = activity.addGenericDialog();
        dialog.setText(
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_item_title),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_item_body),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_cancel_button),
                activity.getResources().getString(R.string.phone_contact_activity_dialog_delete_button));



        Button deleteButton = dialog.getDialogButtonActionRight();
        Button cancelButton = dialog.getDialogButtonActionLeft();

        // on click ok
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItem();
                activity.removeGenericDialog(dialog);
            }
        });

        // on click cancel
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.removeGenericDialog(dialog);
            }
        });
    }

    public void onResume () {
        // do nothing
    }

    private Media listItemToMediaItem (ListItem item) {
        if (item != null) {
            for (Media media : filesMedia) {
                if (item.getId().equals(media.getId())) {
                    return media;
                }
            }
        }

        return null;
    }

    private String getSelectedMediaId () {
        if (selectedMedia != null) {
            return  selectedMedia.getId();
        } else {
            return null;
        }
    }
}
