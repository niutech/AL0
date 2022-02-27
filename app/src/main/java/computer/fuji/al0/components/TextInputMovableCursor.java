package computer.fuji.al0.components;

import android.content.Context;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/*
  TODO
  make text scroll when in overflow and moving cursor
 */

public class TextInputMovableCursor extends AppCompatTextView {
    private String prefixText = "";
    private String postfixText = "";
    private int previousCursorPosition = 0;
    private int cursorPosition = 0;
    private static final String cursor = "_";
    // use this flag to track cursor is added in string
    private static boolean shouldAddCursor = true;

    @Override
    public void setText(CharSequence text, BufferType type) {
        // always add "_" at cursor position
        String textString = text.toString();
        if (cursorPosition <= textString.length()) {
            StringBuilder stringBuilder = new StringBuilder(textString);
            // add cursor char, "_", only when
            // - text string is empty
            // - in textString cursor is not present, make check shouldAddCursor to make sure "getText" removed it
            if (shouldAddCursor || textString.length() == 0) {
                stringBuilder.insert(cursorPosition, cursor);
                shouldAddCursor = false;
            }

            String stringWithCursor = stringBuilder.toString();

            if (prefixText != null && postfixText != null) {
                super.setText(this.prefixText.concat(stringWithCursor).concat(this.postfixText), type);
            } else {
                super.setText(stringWithCursor, type);
            }
        } else {
            cursorPosition = textString.length();
        }

        updateScrollPosition();
    }

    public CharSequence getText () {
        // always remove last char, "_"
        CharSequence currentText = super.getText();
        // remove prefix
        currentText = currentText.subSequence(prefixText.length(), currentText.length());
        // remove postfix
        currentText = currentText.subSequence(0, currentText.length() - postfixText.length());
        // remove cursor
        StringBuilder stringBuilder = new StringBuilder(currentText.toString());
        if (cursorPosition <= currentText.length()) {
            stringBuilder.deleteCharAt(cursorPosition);
        } else {
            cursorPosition = currentText.length();
            stringBuilder.deleteCharAt(cursorPosition);
        }

        // cursor is removed, when drawing the text, cursor need to be added
        shouldAddCursor = true;
        return stringBuilder.toString();
    }

    public TextInputMovableCursor(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void moveCursorLeft (boolean shouldUpdateText) {
        CharSequence text = getText();
        previousCursorPosition = cursorPosition;
        cursorPosition = cursorPosition > 0 ? cursorPosition - 1 : cursorPosition;
        if (shouldUpdateText) {
            setText(text);
        }
    }

    public void moveCursorRight (boolean shouldUpdateText) {
        CharSequence text = getText();
        previousCursorPosition = cursorPosition;
        int endPosition = getText().length();
        cursorPosition = cursorPosition <= endPosition ? cursorPosition + 1 : endPosition;

        if (shouldUpdateText) {
            setText(text);
        }
    }

    public void moveCursorLeftWord () {
        CharSequence text = getText();
        int previousNonCharIndex = getPreviousNonWhiteSpaceCharIndex();
        cursorPosition = previousNonCharIndex > 0 ? previousNonCharIndex : 0;
        setText(text);
    }

    public void moveCursorRightWord () {
        CharSequence text = getText();
        int endPosition = getText().length();
        int nextNonCharIndex = getNextNonWhiteSpaceCharIndex();
        cursorPosition = nextNonCharIndex <= endPosition ? nextNonCharIndex : endPosition;
        setText(text);
    }

    public void addCharacter (String character) {
        StringBuilder stringBuilder = new StringBuilder(getText().toString());
        stringBuilder.insert(cursorPosition, character);
        moveCursorRight(false);
        setText(stringBuilder.toString());
    }

    public void deleteAtCursorPosition () {
        String text = getText().toString();
        if (text.length() > 0 && cursorPosition > 0) {
            StringBuilder stringBuilder = new StringBuilder(text);
            stringBuilder.deleteCharAt(cursorPosition - 1);
            moveCursorLeft(false);
            setText(stringBuilder.toString());
        }
    }

    public void deleteWordAtCursorPosition () {
        CharSequence text = getText();
        if (text.length() > 0 && Character.isLetter(text.charAt(cursorPosition - 1))) {
            StringBuilder stringBuilder = new StringBuilder(text);
            // stringBuilder.deleteCharAt(cursorPosition - 1);
            int deleteFrom = getPreviousNonWhiteSpaceCharIndex();
            int deleteTo = cursorPosition;
            stringBuilder.delete(deleteFrom, deleteTo);
            cursorPosition = deleteFrom;
            setText(stringBuilder.toString());
        } else if (text.length() > 0 && !Character.isLetter(text.charAt(cursorPosition - 1))) {
            deleteAtCursorPosition();
        } else {
            // do nothing
        }
    }

    private int getPreviousNonWhiteSpaceCharIndex() {
        CharSequence text = getText();
        int previousNonCharIndex = cursorPosition -1;
        for (int i = cursorPosition - 1; i >= 0; i--) {
            if (!Character.isWhitespace(text.charAt(i))) {
                previousNonCharIndex = i;
            } else {
                return previousNonCharIndex;
            }
        }

        return previousNonCharIndex >= 0 ? previousNonCharIndex : 0;
    }

    private int getNextNonWhiteSpaceCharIndex() {
        CharSequence text = getText();
        int nextNonCharIndex = cursorPosition;
        for (int i = cursorPosition; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                nextNonCharIndex = i;
            } else {
                return nextNonCharIndex + 1;
            }
        }

        return nextNonCharIndex + 1;
    }

    public void clearText () {
        cursorPosition = 0;
        setText("");
    }

    // scroll view on cursor position
    private void updateScrollPosition () {
        Layout layout = getLayout();
        if (layout != null) {
            // find cursor X and Y coordinates
            int linesOfText = layout.getLineForOffset(cursorPosition);
            int maxLines = getMaxLines();
            int cursorX = (int) layout.getPrimaryHorizontal(cursorPosition);
            int cursorY = layout.getLineTop(linesOfText);
            // find cursor dimensions
            int cursorWidth = ((int) layout.getPrimaryHorizontal(cursorPosition + 1)) - cursorX;
            // find text view sizes
            int textViewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            int textViewHeight = getHeight() - getPaddingTop() - getPaddingBottom();

            //

            // check if cursor position is outside from the textView visible chars
            boolean shouldScrollHorizontally = (cursorX + cursorWidth) > textViewWidth;
            boolean shouldScrollVertically = linesOfText >= maxLines;
            if (shouldScrollHorizontally || shouldScrollVertically) {
                // make sure the text view is scrollable
                if (getMovementMethod() == null) {
                    setMovementMethod(ScrollingMovementMethod.getInstance());
                }

                if (shouldScrollHorizontally) {
                    // calculate scroll x
                    int scrollX = cursorX + cursorWidth - textViewWidth;
                    scrollTo(scrollX, cursorY);
                } else {
                    // calculate scroll y
                    int maxLineY = layout.getLineTop(maxLines > 0 ? maxLines - 1 : maxLines);
                    int scrollY = cursorY - maxLineY;
                    scrollTo(0, scrollY);
                }

            } else {
                // cursor is visible in the text view with no need to scroll
                // make sure text view is not scrollable
                if (getMovementMethod() != null) {
                    scrollTo(0, 0);
                    setMovementMethod(null);
                }
            }
        } else {
            // no layout, do nothing
        }
    }
}