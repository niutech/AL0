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
import computer.fuji.al0.adapters.ListItemsAdapter;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.controllers.PhoneContactsFragmentController;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.ListItem;
import computer.fuji.al0.utils.PhoneActivityTabFragment;

import java.util.ArrayList;

public class PhoneContactsFragment extends Fragment implements PhoneActivityTabFragment, ListItemsAdapter.ListItemListener {
    private ArrayList<ListItem> contactsListItems;
    private PhoneTabsEventListener phoneTabsEventListener;

    private PhoneContactsFragmentController controller;

    // tabs buttons
    private Button numpadButton;
    private Button contactsButton;
    private Button activityButton;

    private Button closeButton;
    private Button findButton;
    private Button newButton;
    private RecyclerView contactsListView;
    private RecyclerView.Adapter contactsListViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private boolean isInitialized = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // PhoneActivity tabs button
        closeButton = (Button) getView().findViewById(R.id.phone_activity_close_button);
        numpadButton = (Button) getView().findViewById(R.id.phone_activity_numpad_button);
        contactsButton = (Button) getView().findViewById(R.id.phone_activity_contacts_button);
        activityButton  = (Button) getView().findViewById(R.id.phone_activity_activity_button);

        contactsButton.setIsActive(true);

        // init contacts list's RecyclerView
        contactsListView = (RecyclerView) view.findViewById(R.id.phone_contacts_list);
        layoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, true);
        contactsListView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(contactsListView.getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getActivity().getDrawable(R.drawable.list_item_divider));
        contactsListView.addItemDecoration(dividerItemDecoration);

        findButton = view.findViewById(R.id.fragment_phone_contacts_button_find);
        newButton =  view.findViewById(R.id.fragment_phone_contacts_button_new);
        // initialize Fragment's controller
        controller = new PhoneContactsFragmentController(this);
        // populate RecyclerView with contacts list items
        // contactsListItems = controller.getContactsListItems();
        contactsListItems = new ArrayList<>();
        contactsListViewAdapter = new ListItemsAdapter(contactsListItems, this, false);
        contactsListView.setAdapter(contactsListViewAdapter);

        // add click event listener on buttons
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

        findButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                controller.onFindButtonClick();
            }
        });

        newButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                controller.onNewButtonClick();
            }
        });
    }

    // update contacts list UI
    public void updateContactList (ArrayList<ListItem> contactsListItems) {
        this.contactsListItems.clear();
        this.contactsListItems.addAll(contactsListItems);
        contactsListViewAdapter.notifyItemRangeChanged(0, contactsListItems.size());
        contactsListViewAdapter.notifyDataSetChanged();
    }

    // scroll to contact list item
    public void scrollContactListToContact (Contact contact) {
        String contactName = contact.getName();
        for (int i = 0; i < contactsListItems.size(); i++) {
            String itemName = contactsListItems.get(i).getText();
            if (contactName.equals(itemName)) {
                layoutManager.scrollToPosition(i);
                return;
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
        controller.onContactsListItemClick(contactsListItems.get(position));
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
        // do nothing
    }
}
