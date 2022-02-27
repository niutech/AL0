package computer.fuji.al0.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import computer.fuji.al0.R;
import computer.fuji.al0.models.ClockTimer;
import computer.fuji.al0.utils.Time;

import java.util.ArrayList;

public class ClockTimerAdapter extends RecyclerView.Adapter<ClockTimerAdapter.ClockTimerViewHolder> {
    private ArrayList<ClockTimer> itemsArrayList;
    Context context;
    ClockTimerListener clockTimerListener;

    private String selectedItemId;

    public ClockTimerAdapter (Context context, ArrayList<ClockTimer> itemsArrayList, ClockTimerListener clockTimerListener) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
        this.clockTimerListener = clockTimerListener;
    }

    public void setSelectedItemID (String id) {
        String previousSelectedItemId = selectedItemId;
        selectedItemId = id;

        // update current selected item if any
        for (int i = 0; i < itemsArrayList.size(); i++) {
            ClockTimer item = itemsArrayList.get(i);
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

    @NonNull
    @Override
    public ClockTimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_list_item, parent, false);
        return new ClockTimerViewHolder(view, clockTimerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClockTimerViewHolder holder, int position) {
        if (holder.updateTimer != null) {
            holder.updateTimer.cancel();
        }

        final ClockTimer item = itemsArrayList.get(position);
        final TextView textView = (TextView) holder.itemView.findViewById(R.id.list_items_list_item_text);
        final TextView markSymbol = (TextView) holder.itemView.findViewById(R.id.list_items_list_item_is_marked);
        textView.setText(timerToTitle(item));
        markSymbol.setVisibility(View.INVISIBLE);

        if (item.getId().equals(selectedItemId)) {
            // textView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            // textView.setPaintFlags(0);
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        long timerDuration = item.getEndTime().getTime() - System.currentTimeMillis();
        holder.updateTimer = new CountDownTimer(timerDuration, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText(timerToTitle(item));
            }

            @Override
            public void onFinish() {
                textView.setText(timerToTitle(item));
                markSymbol.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private String timerToTitle (ClockTimer timer) {
        int elapsedTimeSeconds = (int) (timer.getEndTime().getTime() - System.currentTimeMillis()) / 1000;
        if (elapsedTimeSeconds <= 0) {
            elapsedTimeSeconds = 0;
        }
        return timer.getName().concat("  ").concat(Time.secondsToHMS(elapsedTimeSeconds));
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public static class ClockTimerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View itemView;
        ClockTimerListener clockTimerListener;
        CountDownTimer updateTimer;

        public ClockTimerViewHolder(View view, ClockTimerListener clockTimerListener) {
            super(view);
            itemView = view;
            this.clockTimerListener = clockTimerListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clockTimerListener.onClockTimerClick(getAdapterPosition());
        }
    }

    public interface ClockTimerListener {
        public void onClockTimerClick (int position);
    }
}
