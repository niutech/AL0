package computer.fuji.al0.utils;

public interface PhoneActivityTabFragment {
    public interface PhoneTabsEventListener {
        public void onCloseButtonPress();
        public void onNumpadButtonPress();
        public void onContactsButtonPress();
        public void onActivityButtonPress();
    }

    public void onShow ();
    public void onHide ();
    public void setActivityButtonText(String text);
    public void setPhoneTabsEventListener(PhoneTabsEventListener phoneTabsEventListener);
}
