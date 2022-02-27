package computer.fuji.al0.components;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import computer.fuji.al0.R;

public class ListItemsDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable drawable;

    public ListItemsDividerItemDecoration(Drawable divider) {
        drawable = divider;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int dividerLeft = parent.getPaddingLeft();
        int dividerRight = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            // prevent drawing separator on header/spacer items
            if (child.findViewById(R.id.list_item_header) != null) {
                continue;
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int dividerTop = child.getBottom() + params.bottomMargin - 4;
            int dividerBottom = dividerTop + drawable.getIntrinsicHeight();

            drawable.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            drawable.draw(canvas);
        }
    }
}
