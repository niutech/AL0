package computer.fuji.al0.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import computer.fuji.al0.R;
import computer.fuji.al0.models.ActivityItem;
import computer.fuji.al0.models.Call;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.utils.Time;

import java.util.ArrayList;

public class PhoneContactActivityAdapter extends RecyclerView.Adapter<PhoneContactActivityAdapter.PhoneContactActivityViewHolder> {
    private ArrayList<ActivityItem> itemsArrayList;
    Context context;
    PhoneContactActivityListener phoneContactActivityListener;

    private static final int INBOUND_SMS = 1;
    private static final int OUTBOUND_SMS = 0;
    private static final int INBOUND_CALL = 2;
    private static final int OUTBOUND_CALL = 3;

    private String selectedItemId;

    public PhoneContactActivityAdapter (Context context, ArrayList<ActivityItem> itemsArrayList, PhoneContactActivityListener phoneContactActivityListener) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
        this.phoneContactActivityListener = phoneContactActivityListener;
    }

    public void setSelectedItemID (String id) {
        String previousSelectedItemId = selectedItemId;
        selectedItemId = id;

        // update current selected item if any
        for (int i = 0; i < itemsArrayList.size(); i++) {
            ActivityItem item = itemsArrayList.get(i);
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
        ActivityItem item = itemsArrayList.get(position);
        switch (item.getType()) {
            case SMS:
                return item.getDirection() == ActivityItem.Direction.INBOUND ? INBOUND_SMS : OUTBOUND_SMS;
            case CALL:
            default:
                return item.getDirection() == ActivityItem.Direction.INBOUND ? INBOUND_CALL : OUTBOUND_CALL;
        }
    }

    @NonNull
    @Override
    public PhoneContactActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case OUTBOUND_CALL:
                view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.call_outbound, parent, false);
                break;
            case INBOUND_CALL:
                view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.call_inbound, parent, false);
                break;
            case OUTBOUND_SMS:
                view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_outbound, parent, false);
                break;
            case INBOUND_SMS:
            default:
                view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_inbound, parent, false);
                break;
        }

        return new PhoneContactActivityViewHolder(view, phoneContactActivityListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneContactActivityViewHolder holder, int position) {
        final ActivityItem item = itemsArrayList.get(position);
        TextView date = (TextView) holder.itemView.findViewById(R.id.date);
        TextView isNew = (TextView) holder.itemView.findViewById(R.id.body_is_new);
        TextView body = (TextView) holder.itemView.findViewById(R.id.body);
        TextView info = (TextView) holder.itemView.findViewById(R.id.info);

        date.setText(Time.formatDate(context, item.getDate()));
        switch (item.getType()) {
            case SMS:
                Sms sms = item.getSms();
                // show notification symbol on inbound not read sms
                if (sms.getType() == Sms.Type.INBOUND) {
                    if (isNew != null && sms.getIsRead()) {
                        isNew.setVisibility(View.INVISIBLE);
                    } else {
                        isNew.setVisibility(View.VISIBLE);
                    }
                }

                body.setText(item.getSms().getBody());
                break;
            case CALL:
                Call.Type callType = item.getCall().getType();
                // check if is call missed
                if (callType == Call.Type.MISSED) {
                    info.setText(context.getResources().getString(R.string.call_info_missed));
                } else {
                    info.setText(Time.secondsToHMS(item.getCall().getDuration()));
                }
                // check call direction
                if (callType == Call.Type.OUTGOING) {
                    body.setText(context.getResources().getString(R.string.call_outbound_body));
                } else {
                    body.setText(context.getResources().getString(R.string.call_inbound_body));
                }
                break;
        }

        if (item.getId().equals(selectedItemId)) {
            body.setPaintFlags(body.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            body.setPaintFlags(body.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public static class PhoneContactActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View itemView;
        PhoneContactActivityListener phoneContactActivityListener;

        public PhoneContactActivityViewHolder(View view, PhoneContactActivityListener phoneContactActivityListener) {
            super(view);
            itemView = view;
            this.phoneContactActivityListener = phoneContactActivityListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            phoneContactActivityListener.onPhoneContactActivityClick(getAdapterPosition());
        }
    }

    public interface PhoneContactActivityListener {
        public void onPhoneContactActivityClick (int position);
    }
}
