package computer.fuji.al0.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class ClipboardUtils {
    public static String getPlainText (Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        String pasteData = "";

        if (!(clipboard.hasPrimaryClip())) {
            // clipboard empty
            // do nothing
        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
            // no text available
            // do nothing
        } else {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            CharSequence pasteDataCharSequence = item.getText();

            if (pasteData != null) {
                // found text
                pasteData = pasteDataCharSequence.toString();
                return pasteData;
            } else {
                // check if pasteboard clip is a URI
                Uri pasteUri = item.getUri();
                if (pasteUri != null) {

                    // calls a routine to resolve the URI and get data from it. This routine is not
                    // presented here.
                    pasteData = pasteUri.toString();
                    return pasteData;
                }
            }
        }



        return pasteData;
    }
}
