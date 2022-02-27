package computer.fuji.al0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import computer.fuji.al0.R;
import computer.fuji.al0.adapters.ClockTimerAdapter;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.controllers.ClockTimerFragmentController;
import computer.fuji.al0.models.ClockTimer;
import computer.fuji.al0.utils.ClockActivityTabFragment;

import java.util.ArrayList;
import java.util.Collections;

public class ClockTimerFragment extends Fragment implements ClockActivityTabFragment, ClockTimerAdapter.ClockTimerListener {
    private ArrayList<ClockTimer> timers;
    private ClockTabsEventListener clockTabsEventListener;

    private ClockTimerFragmentController controller;

    Button closeButton;
    Button alarmButton;
    Button stopwatchButton;
    Button timerButton;

    private Button addButton;
    private Button deleteButton;
    private RelativeLayout timersListViewWrapper;
    private TextView noTimerLabel;
    private RecyclerView timersListView;
    private ClockTimerAdapter timersListViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private boolean isInitialized = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clock_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeButton = view.findViewById(R.id.clock_activity_close_button);
        alarmButton = view.findViewById(R.id.clock_activity_alarm_button);
        stopwatchButton = view.findViewById(R.id.clock_activity_stopwatch_button);
        timerButton = view.findViewById(R.id.clock_activity_timer_button);

        timerButton.setIsActive(true);


        // init contacts list's RecyclerView
        timersListView = (RecyclerView) view.findViewById(R.id.clock_timer_list);
        layoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, true);
        timersListView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(timersListView.getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getActivity().getDrawable(R.drawable.list_item_divider));
        timersListView.addItemDecoration(dividerItemDecoration);

        noTimerLabel = (TextView) view.findViewById(R.id.clock_timer_no_timer_label);
        timersListViewWrapper = (RelativeLayout) view.findViewById(R.id.clock_timer_list_wrapper);

        addButton = view.findViewById(R.id.fragment_clock_timer_button_add);
        deleteButton =  view.findViewById(R.id.fragment_clock_timer_button_delete);
        deleteButton.setIsDisabled(true);
        // populate RecyclerView with timers list items
        // contactsListItems = controller.getContactsListItems();
        timers = new ArrayList<>();
        timersListViewAdapter = new ClockTimerAdapter(this.getContext(), timers, this);
        timersListView.setAdapter(timersListViewAdapter);

        // initialize Fragment's controller
        controller = new ClockTimerFragmentController(this);

        // add click event listener on buttons
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (clockTabsEventListener != null) {
                    clockTabsEventListener.onCloseButtonPress();
                }
            }
        });

        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (clockTabsEventListener != null) {
                    clockTabsEventListener.onClockButtonPress();
                }
            }
        });

        stopwatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (clockTabsEventListener != null) {
                    clockTabsEventListener.onStopwatchButtonPress();
                }
            }
        });

        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (clockTabsEventListener != null) {
                    clockTabsEventListener.onTimerButtonPress();
                }
            }
        });


        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                controller.onButtonAddPress();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                controller.onButtonDeletePress();
            }
        });
    }

    // update timers list UI
    public void updateTimerList (ArrayList<ClockTimer> timers) {
        this.timers.clear();
        this.timers.addAll(timers);
        Collections.reverse(this.timers);
        timersListViewAdapter.notifyItemRangeChanged(0, timers.size());
        timersListViewAdapter.notifyDataSetChanged();
        // disable delete button
        deleteButton.setIsDisabled(true);
        if (timers.size() > 0) {
            timersListViewWrapper.setVisibility(View.VISIBLE);
            noTimerLabel.setVisibility(View.GONE);
        } else {
            timersListViewWrapper.setVisibility(View.GONE);
            noTimerLabel.setVisibility(View.VISIBLE);
        }
    }

    // scroll to timer list item
    /*
    public void scrollTimerListToTimer (Timer timer) {
        String contactName = contact.getName();
        for (int i = 0; i < contactsListItems.size(); i++) {
            String itemName = contactsListItems.get(i).getText();
            if (contactName.equals(itemName)) {
                layoutManager.scrollToPosition(i);
                return;
            }
        }
    }

     */

    @Override
    public void onResume() {
        super.onResume();
        controller.onFragmentResume();
        setSelectedClockTimer(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        controller.onFragmentPause();
    }

    @Override
    public void onClockTimerClick(int position) {
        ClockTimer selectedTimer = timers.get(position);
        controller.onTimerListItemClick(selectedTimer);
    }

    public void setSelectedClockTimer (ClockTimer timer) {
        if (timer != null) {
            timersListViewAdapter.setSelectedItemID(timer.getId());
            deleteButton.setIsDisabled(false);
        } else {
            timersListViewAdapter.setSelectedItemID(null);
            // disable delete button when no timer is passed
            deleteButton.setIsDisabled(true);
        }
    }

    @Override
    public void onShow() {
        controller.onFragmentShow();
    }

    @Override
    public void onHide() {
        controller.onFragmentHide();
    }

    @Override
    public void setClockTabsEventListener(ClockTabsEventListener clockTabsEventListener) {
        this.clockTabsEventListener = clockTabsEventListener;
    }
}
