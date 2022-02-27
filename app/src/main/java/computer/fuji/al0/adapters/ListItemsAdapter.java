package computer.fuji.al0.adapters;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import computer.fuji.al0.R;
import computer.fuji.al0.models.ListItem;

import java.util.ArrayList;

public class ListItemsAdapter extends RecyclerView.Adapter<ListItemsAdapter.ListItemsViewHolder> {
    private ArrayList<ListItem> listItems;
    private ListItemListener listItemListener;
    private boolean isCenteredLayout;
    private String selectedItemId;

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 0;

    public ListItemsAdapter (ArrayList<ListItem> listItems, ListItemListener listItemListener, boolean isCenteredLayout) {
        this.listItems = listItems;
        this.listItemListener = listItemListener;
        this.isCenteredLayout = isCenteredLayout;
    }

    public void setSelectedItemID (String id) {
        String previousSelectedItemId = selectedItemId;
        selectedItemId = id;

        // update current selected item if any
        for (int i = 0; i < listItems.size(); i++) {
            ListItem item = listItems.get(i);
            String currentItemId = item.getId();
            // check if currentItem is the current selected item
            if (currentItemId.equals(previousSelectedItemId)) {
                notifyItemChanged(i);
            }

            if (currentItemId.equals(selectedItemId)) {
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        // return itemsArrayList.get(position).getType() == Sms.Type.INBOUND ? INBOUND_SMS : OUTBOUND_SMS;
        ListItem item = listItems.get(position);
        switch (item.getType()) {
            case HEADER:
            case EMPTY:
                return VIEW_TYPE_HEADER;
            case ITEM:
            default:
                return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public ListItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int listItemLayout = isCenteredLayout ? R.layout.list_items_list_item_centered : R.layout.list_items_list_item;
        int listHeaderLayout = R.layout.list_items_list_header;
        // View view = (View) LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        View view;

        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = (View) LayoutInflater.from(parent.getContext()).inflate(listHeaderLayout, parent, false);
                break;
            case VIEW_TYPE_ITEM:
            default:
                view = (View) LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
                break;
        }

        ListItemsViewHolder listItemsViewHolder = new ListItemsViewHolder(view, listItemListener);

        return  listItemsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemsViewHolder holder, int position) {
        final ListItem listItem = listItems.get(position);
        TextView isMarkedView = (TextView) holder.itemView.findViewById(R.id.list_items_list_item_is_marked);
        TextView textView = (TextView) holder.itemView.findViewById(R.id.list_items_list_item_text);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        View rightTextSpacer = (View)  holder.itemView.findViewById(R.id.list_items_list_item_right_spacer);
        TextView rightTextView = (TextView) holder.itemView.findViewById(R.id.list_items_list_item_right_text);

        isMarkedView.setVisibility(listItem.getIsMarked() ? View.VISIBLE : View.INVISIBLE);

        // use custom mark when custom mark exsist and item is marked
        if (listItem.getCustomMark() != null && listItem.getIsMarked()) {
            isMarkedView.setText(listItem.getCustomMark());
        }

        textView.setText(listItem.getText());

        if (listItem.getIsActive()) {
            textView.setPaintFlags( textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags( textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        String rightText = listItem.getRightText();
        if (rightText != null) {
            rightTextSpacer.setVisibility(View.VISIBLE);
            rightTextView.setVisibility(View.VISIBLE);
            rightTextView.setText(rightText);
        } else {
            rightTextSpacer.setVisibility(View.GONE);
            rightTextView.setVisibility(View.GONE);
        }

        if (listItem.getId().equals(selectedItemId)) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ListItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View itemView;
        ListItemListener listItemListener;

        public ListItemsViewHolder(View view, ListItemListener listItemListener) {
            super(view);
            itemView = view;
            this.listItemListener = listItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listItemListener.onListItemClick(getAdapterPosition());
        }
    }

    public interface ListItemListener {
        public void onListItemClick (int position);
    }
}
