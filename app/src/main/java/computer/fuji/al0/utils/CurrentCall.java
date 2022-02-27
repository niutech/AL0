package computer.fuji.al0.utils;

import android.telecom.Call;

public class CurrentCall {
    private static Call currentCall;
    private static int callTime;

    public static void setCurrentCall (Call call) {
        if (currentCall != null) {
            currentCall.disconnect();
            currentCall = null;
        }

        callTime = 0;
        currentCall = call;
    }

    public static void setCallTime (int time) {
        callTime = time;
    }

    public static Call getCurrentCall () {
        return currentCall;
    }

    public static int getCallTime () {
        return callTime;
    }
}
