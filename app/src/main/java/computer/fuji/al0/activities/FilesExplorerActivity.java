package computer.fuji.al0.activities;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import computer.fuji.al0.R;
import computer.fuji.al0.adapters.ListItemsAdapter;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.components.Dialog;
import computer.fuji.al0.components.VideoViewClickable;
import computer.fuji.al0.controllers.FilesExplorerActivityController;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.models.Media;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.UI;

import java.util.ArrayList;
import java.util.Collections;

public class FilesExplorerActivity extends AppCompatActivity implements ListItemsAdapter.ListItemListener {
    FilesExplorerActivityController controller;
    private ArrayList<ListItem> filesListItems;

    private RelativeLayout activityWrapper;
    private Button buttonClose;
    private Button buttonOpen;
    private Button buttonDelete;
    private ImageView imageView;
    private VideoViewClickable videoView;
    private LinearLayout footer;

    private RecyclerView filesListView;
    private ListItemsAdapter filesListViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_files_explorer);
        UI.hideNavigationBar(this);

        controller = new FilesExplorerActivityController(this);

        activityWrapper = (RelativeLayout) findViewById(R.id.file_explorer_activity_wrapper);
        buttonClose = (Button) findViewById(R.id.files_explorer_activity_button_close);
        buttonOpen = (Button) findViewById(R.id.files_explorer_activity_button_open);
        buttonDelete = (Button) findViewById(R.id.files_explorer_activity_button_delete);
        imageView = (ImageView) findViewById(R.id.files_explorer_activity_image_view);
        videoView = (VideoViewClickable) findViewById(R.id.files_explorer_activity_video_view);
        videoView.setBackgroundColor(Color.TRANSPARENT);
        footer = findViewById(R.id.files_explorer_activity_wrapper);

        filesListView = (RecyclerView) findViewById(R.id.files_explorer_activity_list);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        filesListView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(filesListView.getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.list_item_divider));
        filesListView.addItemDecoration(dividerItemDecoration);

        filesListItems = new ArrayList<>();

        filesListViewAdapter = new ListItemsAdapter(filesListItems, this, false);
        filesListView.setAdapter(filesListViewAdapter);

        filesListView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                boolean reachTop = !filesListView.canScrollVertically(-1);
                if (reachTop) {
                    controller.onScrollReachTop();
                }
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonClosePress();
            }
        });

        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonOpenPress();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onButtonDeletePress();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFooterVisibility();
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (videoView.isPlaying()) {
                            footer.setVisibility(View.VISIBLE);
                            videoView.pause();
                        } else {
                            footer.setVisibility(View.GONE);
                            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                            videoView.setMinimumWidth(screenWidth);
                            videoView.start();
                            videoView.setRight(90);
                        }
                        return true;
                }
                return false;
            }
        });

        // disable open and delete button
        setOpenDeleteButtonsEnabled(false);
        // hide image view
        setMediaViewVisible(false, null);

        controller.onActivityReady();
    }

    // update files list UI
    public void updateFilesList (ArrayList<ListItem> filesListItems) {
        this.filesListItems.clear();
        this.filesListItems.addAll(filesListItems);
        Collections.reverse(this.filesListItems);
        filesListViewAdapter.notifyItemRangeChanged(0, filesListItems.size());
        filesListViewAdapter.notifyDataSetChanged();
    }

    public void removeFile (String fileToRemoveId) {
        for (int i = 0; i < filesListItems.size(); i++) {
            if (filesListItems.get(i).getId().equals(fileToRemoveId)) {
                this.filesListItems.remove(i);
                filesListViewAdapter.notifyItemRemoved(i);
                setSelectedFileItem(null);
                return;
            }
        }
    }

    public void scrollFilesListTo (int index) {
        layoutManager.scrollToPosition(index);
    }

    public void scrollFilesListToBottom () {
        layoutManager.scrollToPosition(0);
    }

    public void setSelectedFileItem (String itemId) {
        if (itemId != null) {
            filesListViewAdapter.setSelectedItemID(itemId);
            setOpenDeleteButtonsEnabled(true);
        } else {
            filesListViewAdapter.setSelectedItemID(null);
            setOpenDeleteButtonsEnabled(false);
        }
    }

    public void setOpenDeleteButtonsEnabled (boolean enabled) {
        buttonOpen.setIsDisabled(!enabled);
        buttonDelete.setIsDisabled(!enabled);
    }

    private void toggleFooterVisibility() {
        if (footer.getVisibility() == View.VISIBLE) {
            footer.setVisibility(View.GONE);
        } else {
            footer.setVisibility(View.VISIBLE);
        }
    }

    // show/hide image viewer
    // when the image viewer is visible list and open/delete button are hidden
    public void setMediaViewVisible (boolean visible, Media.Type type) {
        if (visible) {
            filesListView.setVisibility(View.GONE);
            footer.setVisibility(View.GONE);
            buttonOpen.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
            if (type == Media.Type.Image) {
                imageView.setVisibility(View.VISIBLE);
            } else if (type == Media.Type.Video) {
                videoView.setVisibility(View.VISIBLE);
            }
        } else {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
            filesListView.setVisibility(View.VISIBLE);
            footer.setVisibility(View.VISIBLE);
            buttonOpen.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        }
    }

    public void setMediaViewMedia (Media media) {
        switch (media.getType()) {
            case Image:
                Bitmap image = BitmapFactory.decodeFile(media.getPath());
                if (image != null) {
                    int imageWidth = image.getWidth();
                    int imageHeight = image.getHeight();
                    // rotate image if is width is greater than height
                    if (imageWidth > imageHeight) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, imageWidth, imageHeight, true);
                        image = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                    }
                    imageView.setImageBitmap(image);
                }

                break;
            case Video:
                videoView.setVideoPath(media.getPath());
                videoView.start();
                break;
        }
    }

    /*
     * Generic Dialog, used to inform user about contextual confirmation
     * eg. confirm delete an image
     */

    public void removeGenericDialog (Dialog dialog) {
        if (dialog != null) {
            activityWrapper.removeView(dialog);
        }

        setAllActivityWrapperChildVisible(true);
        setMediaViewVisible(false, null);
    }

    public Dialog addGenericDialog () {
        setAllActivityWrapperChildVisible(false);
        Dialog genericDialog = new Dialog(this);
        activityWrapper.addView(genericDialog, 0);
        return genericDialog;
    }

    private void setAllActivityWrapperChildVisible (boolean areVisible) {
        for (int i = 0; i < activityWrapper.getChildCount(); i++) {
            // ignore video view child
            if ((activityWrapper.getChildAt(i) instanceof VideoViewClickable)) {
                // do nothing
            } else {
                activityWrapper.getChildAt(i).setVisibility(areVisible ? View.VISIBLE : View.GONE);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        controller.onResume();
    }

    @Override
    public void onListItemClick(int position) {
        controller.onFilesListItemClick(filesListItems.get(position));
    }
}
