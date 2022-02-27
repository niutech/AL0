package computer.fuji.al0.controllers;

import computer.fuji.al0.R;
import computer.fuji.al0.activities.CalculatorActivity;
import computer.fuji.al0.models.NumpadButtonModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CalculatorActivityController {
    private CalculatorActivity activity;
    private enum Operation { ADDITION, SUBTRACTION, DIVISION, MULTIPLICATION, NOTHING }
    private enum Input { INPUT_A, INPUT_B }
    private enum InputType { DIGIT, OPERATION, MODIFIER }

    private NumberFormat formatter;
    private NumberFormat localeFormatter;
    private String inputA;
    private String inputB;
    private Input currentInput;
    private Operation inputOperation;
    private final String emptyString = "";
    private final String zeroString = "0";
    private final String oneHundredString = "100";
    private final String commaString = ".";
    private boolean isCalculatorInErrorState = false;
    private final NumpadButtonModel clearButtonModel = new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_CLEAR, 0, "", "", 0);
    private final NumpadButtonModel deleteButtonModel = new NumpadButtonModel(NumpadButtonModel.NumpadButtonId.BUTTON_DELETE, 0, "", "", 0);

    public CalculatorActivityController (CalculatorActivity activity) {
        this.activity = activity;

        formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMaximumFractionDigits(8);
        formatter.setMinimumFractionDigits(0);

        localeFormatter = NumberFormat.getNumberInstance();
        localeFormatter.setMaximumFractionDigits(8);
        localeFormatter.setMinimumFractionDigits(0);

        clearCalculator();
        updateView();
    }

    private void updateView () {
        if (isCalculatorInErrorState) {
            activity.updateTextView(activity.getResources().getString(R.string.calculator_activity_error_message));
        } else if (isCurrentInputEmptyOrMinusOrOneDigitZero()) {
            activity.updateTextView(zeroString);
        } else {
            String currentInputString = getCurrentInputValue();
            BigDecimal bigDecimal = new BigDecimal(currentInputString);
            String formattedBigDecimal =  formatter.format(bigDecimal);
            String localizedFormattedBigDecimal = localeFormatter.format(bigDecimal);
            // make sure to show 0 fractions digits
            if (currentInputString.contains(commaString)) {
                int minimumNumberOfDigits = currentInputString.length() - currentInputString.indexOf(commaString) - 1;
                formatter.setMinimumFractionDigits(minimumNumberOfDigits);
                localeFormatter.setMinimumFractionDigits(minimumNumberOfDigits);
                formattedBigDecimal = formatter.format(bigDecimal);
                localizedFormattedBigDecimal = localeFormatter.format(bigDecimal);
                // reset minimum fraction digits to 0
                formatter.setMinimumFractionDigits(0);
                localeFormatter.setMinimumFractionDigits(0);
            }


            // activity.updateTextView(formattedBigDecimal);
            activity.updateTextView(localizedFormattedBigDecimal);
        }
    }

    private void clearCalculator () {
        inputA = emptyString;
        inputB = emptyString;
        currentInput = Input.INPUT_A;
        inputOperation = Operation.NOTHING;
        isCalculatorInErrorState = false;
    }

    private void calculateResult () {
        // default empty A/B input to 0
        BigDecimal inputABigDecimal = new BigDecimal(inputA.isEmpty() ? zeroString : inputA);
        BigDecimal inputBBigDecimal = new BigDecimal(inputB.isEmpty() ? zeroString : inputB);
        BigDecimal result = new BigDecimal(zeroString);
        boolean isError = false;
        switch (inputOperation) {
            case DIVISION:
                if (inputBBigDecimal.compareTo(BigDecimal.ZERO) != 0) {
                    result = inputABigDecimal.divide(inputBBigDecimal, 8, BigDecimal.ROUND_HALF_UP);
                } else {
                    isError = true;
                }
                break;
            case MULTIPLICATION:
                result = inputABigDecimal.multiply(inputBBigDecimal);
                break;
            case SUBTRACTION:
                result = inputABigDecimal.subtract(inputBBigDecimal);
                break;
            case ADDITION:
                result = inputABigDecimal.add(inputBBigDecimal);
                break;
        }

        if (isError) {
            isCalculatorInErrorState = true;
        } else {
            String formattedResultWithNoThousandSeparator = formatter.format(result).replace(",","");
            inputA = formattedResultWithNoThousandSeparator;
            // inputA = String.valueOf(result);
            currentInput = Input.INPUT_A;
        }

        updateView();
    }

    // process a digit, add the digit to the current input
    private void processDigitInput (NumpadButtonModel buttonModel) {
        String inputDigit = buttonModel.getNumber();
        String currentInputValue = getCurrentInputValue();
        switch (buttonModel.getId()) {
            case BUTTON_COMMA:
                if (isCurrentInputEmptyOrMinusOrOneDigitZero()) {
                    // add a leading 0 if input is empty or 0
                    setCurrentInputValue(zeroString.concat(commaString));
                } else if (!currentInputValue.contains(commaString)) {
                    // add comma if is a whole number
                    setCurrentInputValue(currentInputValue.concat(commaString));
                } else {
                    // do nothing, cannot have a number with multiple commas
                }
                // add comma only if current input doesnt contain a comma
                break;
            case BUTTON_0:
                if (isCurrentInputEmptyOrMinusOrOneDigitZero()) {
                    // do nothing, input is already 0
                } else {
                    setCurrentInputValue(currentInputValue.concat(inputDigit));
                }
                break;
            default:
                setCurrentInputValue(currentInputValue.concat(inputDigit));
                break;
        }
    }

    private void processOperationInput (NumpadButtonModel buttonModel) {
        // move current input from A to B
        if (currentInput == Input.INPUT_A) {
            inputOperation = numpadButtonModelToOperation(buttonModel);
            currentInput = Input.INPUT_B;
            inputB = emptyString;
        } else {
            // when user typed INPUT_B and type again an operation input the calculator should calculate the previous operation and clear INPUT_B
            calculateResult();
            inputOperation = numpadButtonModelToOperation(buttonModel);
            inputB = emptyString;
            currentInput = Input.INPUT_B;
        }
    }

    private void processModifierInput (NumpadButtonModel buttonModel) {
        BigDecimal currentInputBigDecimal;
        switch (buttonModel.getId()) {
            case BUTTON_CLEAR:
                clearCalculator();
                break;
            case BUTTON_PLUS_MINUS:
                // check current input is not 0
                if (!isCurrentInputEmptyOrMinusOrOneDigitZero()) {
                    currentInputBigDecimal = new BigDecimal(getCurrentInputValue());
                    setCurrentInputValue(String.valueOf(currentInputBigDecimal.negate()));
                }
                break;
            case BUTTON_PERCENT:
                currentInputBigDecimal = new BigDecimal(getCurrentInputValue());
                setCurrentInputValue(formatter.format(currentInputBigDecimal.divide(new BigDecimal(oneHundredString), 8, BigDecimal.ROUND_HALF_UP)));
                break;
            case BUTTON_EQUAL:
                calculateResult();
                break;
            case BUTTON_DELETE:
                // TO DO check why crash on delete negative number
                String currentInputValue = getCurrentInputValue();
                if (currentInputValue.length() > 0) {
                    // delete also - sign if is followed by 1 digit
                    if (currentInputValue.length() == 2 && currentInputValue.indexOf("-") == 0) {
                        setCurrentInputValue(emptyString);
                    } else {
                        setCurrentInputValue(currentInputValue.substring(0, currentInputValue.length() - 1));
                    }

                }
                break;
        }
    }

    // Process numpad input with the specific operation
    // digits, signs, modifiers need to be processed with different process
    private void processInputButton (NumpadButtonModel buttonModel) {
        // ignore everything but clear button when calculator is in error state
        if (isCalculatorInErrorState) {
            if (buttonModel.getId() == NumpadButtonModel.NumpadButtonId.BUTTON_CLEAR) {
                clearCalculator();
            } else {
                // do nothing
            }
        }

        switch (getInputType(buttonModel)) {
            case DIGIT:
                processDigitInput(buttonModel);
                updateView();
                break;
            case MODIFIER:
                processModifierInput(buttonModel);
                updateView();
                break;
            case OPERATION:
                processOperationInput(buttonModel);
                break;
        }
    }

    // events
    public void onButtonClosePress () {
        activity.finish();
    }

    public void onButtonClearPress () {
        processInputButton(clearButtonModel);
    }

    public void onButtonDeletePress () {
        processInputButton(deleteButtonModel);
    }

    public void onNumpadButtonPress(NumpadButtonModel buttonModel) {
        processInputButton(buttonModel);
    }

    public void onNumpadButtonTouchStart(NumpadButtonModel buttonModel) {
        // do nothing
    }

    public void onNumpadButtonTouchEnd(NumpadButtonModel buttonModel, boolean isTailTouchEvent) {
        // do nothing
    }

    // helpers
    private String getCurrentInputValue () {
        return currentInput == Input.INPUT_A ? inputA : inputB;
    }

    private void setCurrentInputValue (String value) {
        if (currentInput == Input.INPUT_A) {
            inputA = value;
        } else {
            inputB = value;
        }
    }

    // check if current input is an empty string or 0
    // "" -> true, "0" -> true, "1" -> false, "0.0" -> false
    private boolean isCurrentInputEmptyOrMinusOrOneDigitZero () {
        String currentInput = getCurrentInputValue();
        boolean isCurrentInputEmpty = currentInput.length() == 0;
        boolean isCurrentInputZero = currentInput.length() == 1 && currentInput.indexOf("0") == 0;
        boolean isCurrentInputMinus = currentInput.length() == 1 && currentInput.indexOf("-") == 0;
        return  isCurrentInputEmpty || isCurrentInputZero || isCurrentInputMinus;
    }

    private Operation numpadButtonModelToOperation (NumpadButtonModel buttonModel) {
        switch (buttonModel.getId()) {
            case BUTTON_DIVISION:
                return Operation.DIVISION;
            case BUTTON_MULTIPLICATION:
                return Operation.MULTIPLICATION;
            case BUTTON_MINUS:
                return Operation.SUBTRACTION;
            case BUTTON_PLUS:
                return Operation.ADDITION;
            default:
                return Operation.NOTHING;
        }
    }

    private InputType getInputType (NumpadButtonModel buttonModel) {
        switch (buttonModel.getId()) {
            case BUTTON_0:
            case BUTTON_1:
            case BUTTON_2:
            case BUTTON_3:
            case BUTTON_4:
            case BUTTON_5:
            case BUTTON_6:
            case BUTTON_7:
            case BUTTON_8:
            case BUTTON_9:
            case BUTTON_COMMA:
                return InputType.DIGIT;
            case BUTTON_DIVISION:
            case BUTTON_MULTIPLICATION:
            case BUTTON_MINUS:
            case BUTTON_PLUS:
                return InputType.OPERATION;
            case BUTTON_CLEAR:
            case BUTTON_PLUS_MINUS:
            case BUTTON_PERCENT:
            case BUTTON_EQUAL:
            case BUTTON_DELETE:
            default:
                return InputType.MODIFIER;
        }
    }
}
