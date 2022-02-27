package computer.fuji.al0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import computer.fuji.al0.R;
import computer.fuji.al0.adapters.PhoneActivityAdapter;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.controllers.PhoneActivityFragmentController;
import computer.fuji.al0.models.ActivityItem;
import computer.fuji.al0.utils.PhoneActivityTabFragment;

import java.util.ArrayList;
import java.util.Collections;

public class PhoneActivityFragment extends Fragment implements PhoneActivityTabFragment, PhoneActivityAdapter.PhoneActivityListener {
    private PhoneActivityFragmentController controller;
    private PhoneTabsEventListener phoneTabsEventListener;
    private ArrayList<ActivityItem> activityList;

    // tabs buttons
    private Button closeButton;
    private Button numpadButton;
    private Button contactsButton;
    private Button activityButton;

    private RecyclerView activityListView;
    private RecyclerView.Adapter activityListViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // PhoneActivity tabs button
        closeButton =  (Button) getView().findViewById(R.id.phone_activity_close_button);
        numpadButton = (Button) getView().findViewById(R.id.phone_activity_numpad_button);
        contactsButton = (Button) getView().findViewById(R.id.phone_activity_contacts_button);
        activityButton  = (Button) getView().findViewById(R.id.phone_activity_activity_button);

        activityButton.setIsActive(true);

        // init contacts list's RecyclerView
        activityListView = (RecyclerView) view.findViewById(R.id.phone_activity_activity_list);
        layoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, true);
        activityListView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activityListView.getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getActivity().getDrawable(R.drawable.list_item_divider));
        activityListView.addItemDecoration(dividerItemDecoration);

        activityList = new ArrayList<>();

        activityListViewAdapter = new PhoneActivityAdapter(this.getContext(), activityList, this);
        activityListView.setAdapter(activityListViewAdapter);
        layoutManager.scrollToPosition(0);

        controller = new PhoneActivityFragmentController(this);

        activityListView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                boolean reachTop = !activityListView.canScrollVertically(-1);
                // boolean reachBottom = !activityListView.canScrollVertically(View.FOCUS_DOWN);
                if (reachTop) {
                    controller.onScrollReachTop();
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (phoneTabsEventListener != null) {
                    phoneTabsEventListener.onCloseButtonPress();
                }
            }
        });

        numpadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (phoneTabsEventListener != null) {
                    phoneTabsEventListener.onNumpadButtonPress();
                }
            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (phoneTabsEventListener != null) {
                    phoneTabsEventListener.onContactsButtonPress();
                }
            }
        });

        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (phoneTabsEventListener != null) {
                    phoneTabsEventListener.onActivityButtonPress();
                }
            }
        });

    }

    @Override
    public void onPhoneActivityClick(int position) {
        controller.onPhoneActivityClick(activityList.get(position));
    }

    // UI
    public void updateActivityList (ArrayList<ActivityItem> activityList) {
        if (this.activityList != null) {
            this.activityList.clear();
            this.activityList.addAll(activityList);
            Collections.reverse(this.activityList);
            activityListViewAdapter.notifyItemRangeChanged(0, activityList.size());
            activityListViewAdapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);
        }
    }

    public void updateActivityListItem (ActivityItem item) {
        for (int i = 0; i < activityList.size(); i++) {
            if (activityList.get(i).getId().equals(item.getId())) {
                activityList.set(i, item);
                activityListViewAdapter.notifyItemRangeChanged(i, 1);
                activityListViewAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    public void scrollActivityListTo (int index) {
        layoutManager.scrollToPosition(index);
    }

    public void scrollActivityListToBottom () {
        // layoutManager.scrollToPosition(activityList.size() -1);
        layoutManager.scrollToPosition(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        controller.onResume();
    }

    @Override
    public void onPause () {
        super.onPause();
        controller.onPause();
    }


    @Override
    public void setPhoneTabsEventListener(PhoneTabsEventListener eventListener) {
        this.phoneTabsEventListener = eventListener;
    }


    @Override
    public void setActivityButtonText(String text) {
        activityButton.setText(text);
    }


    @Override
    public void onShow() {
        controller.onFragmentShow();
    }

    @Override
    public void onHide() {
        controller.onFragmentHide();
    }
}
