package computer.fuji.al0.utils;

public interface ClockActivityTabFragment {
    public interface ClockTabsEventListener {
        public void onCloseButtonPress();
        public void onClockButtonPress();
        public void onStopwatchButtonPress();
        public void onTimerButtonPress();
    }

    public void onShow ();
    public void onHide ();
    public void setClockTabsEventListener(ClockTabsEventListener clockTabsEventListener);
}