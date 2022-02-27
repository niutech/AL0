package computer.fuji.al0.adapters;

import android.content.Context;
import android.text.TextUtils;
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
import computer.fuji.al0.utils.PhoneActivityTabFragment;
import computer.fuji.al0.utils.PhoneNumber;
import computer.fuji.al0.utils.Time;

import java.util.ArrayList;

public class PhoneActivityAdapter extends RecyclerView.Adapter<PhoneActivityAdapter.PhoneActivityViewHolder> {
    private ArrayList<ActivityItem> itemsArrayList;
    Context context;
    PhoneActivityListener phoneActivityListener;

    private static final int INBOUND_SMS = 1;
    private static final int OUTBOUND_SMS = 0;
    private static final int INBOUND_CALL = 2;
    private static final int OUTBOUND_CALL = 3;

    public PhoneActivityAdapter (Context context, ArrayList<ActivityItem> itemsArrayList, PhoneActivityListener phoneActivityListener) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
        this.phoneActivityListener = phoneActivityListener;
    }

    @Override
    public int getItemViewType(int position) {
        // return itemsArrayList.get(position).getType() == Sms.Type.INBOUND ? INBOUND_SMS : OUTBOUND_SMS;
        ActivityItem item = itemsArrayList.get(position);
        if (item != null) {
            switch (item.getType()) {
                case SMS:
                    return item.getDirection() == ActivityItem.Direction.INBOUND ? INBOUND_SMS : OUTBOUND_SMS;
                case CALL:
                default:
                    return item.getDirection() == ActivityItem.Direction.INBOUND ? INBOUND_CALL : OUTBOUND_CALL;
            }
        } else {
            return -1;
        }

    }

    @NonNull
    @Override
    public PhoneActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        return new PhoneActivityViewHolder(view, phoneActivityListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneActivityViewHolder holder, int position) {
        final ActivityItem item = itemsArrayList.get(position);
        TextView date = (TextView) holder.itemView.findViewById(R.id.date);
        TextView body = (TextView) holder.itemView.findViewById(R.id.body);
        TextView info = (TextView) holder.itemView.findViewById(R.id.info);

        body.setMaxLines(1);
        body.setEllipsize(TextUtils.TruncateAt.END);

        if (item == null) {
            return;
        }

        date.setText(Time.formatDate(context, item.getDate()));
        boolean isNewContact = false;
        switch (item.getType()) {
            case SMS:
                Sms sms = item.getSms();
                Sms.Type smsType = sms.getType();
                String smsContactName = sms.getContact().getName();
                String smsContactNumber = sms.getContact().getPhoneNumber();
                isNewContact = smsContactName.equals(smsContactNumber);
                String smsName = isNewContact ? PhoneNumber.formatPhoneNumber(smsContactNumber) : smsContactName;
                if (smsType == Sms.Type.OUTBOUND) {
                    body.setText(context.getResources().getString(R.string.fragment_phone_activity_sms_to).concat(" ").concat(smsName));
                } else {
                    TextView isNew = (TextView) holder.itemView.findViewById(R.id.body_is_new);
                    if (sms.getIsRead()) {
                        isNew.setVisibility(View.INVISIBLE);
                    } else {
                        isNew.setVisibility(View.VISIBLE);
                    }
                    body.setText(context.getResources().getString(R.string.fragment_phone_activity_sms_from).concat(" ").concat(smsName));
                }
                break;
            case CALL:
                Call call = item.getCall();
                Call.Type callType = call.getType();
                String callContactName = call.getContact().getName();
                String callContactNumber = call.getContact().getPhoneNumber();
                isNewContact = callContactName.equals(callContactNumber);
                String callName = isNewContact ? PhoneNumber.formatPhoneNumber(callContactNumber) : callContactName;
                // check if is call missed
                if (callType == Call.Type.MISSED) {
                    info.setText(context.getResources().getString(R.string.fragment_phone_activity_call_detail_missed));
                    TextView isNew = (TextView) holder.itemView.findViewById(R.id.body_is_new);
                    if (call.getIsNew()) {
                        isNew.setVisibility(View.VISIBLE);
                    } else {
                        isNew.setVisibility(View.INVISIBLE);
                    }
                } else {
                    info.setText(Time.secondsToHMS(item.getCall().getDuration()));
                }
                // check call direction
                if (callType == Call.Type.OUTGOING) {
                    body.setText(context.getResources().getString(R.string.fragment_phone_activity_call_to).concat(" ").concat(callName));
                } else {
                    body.setText(context.getResources().getString(R.string.fragment_phone_activity_call_from).concat(" ").concat(callName));
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public static class PhoneActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View itemView;
        PhoneActivityListener phoneActivityListener;

        public PhoneActivityViewHolder(View view, PhoneActivityListener phoneActivityListener) {
            super(view);
            itemView = view;
            this.phoneActivityListener = phoneActivityListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            phoneActivityListener.onPhoneActivityClick(getAdapterPosition());
        }
    }

    public interface PhoneActivityListener {
        public void onPhoneActivityClick (int position);
    }
}
