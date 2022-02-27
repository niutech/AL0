package computer.fuji.al0.utils;

import java.util.HashMap;
import java.util.Map;

import computer.fuji.al0.models.KeyboardButtonModel;
import static computer.fuji.al0.models.KeyboardButtonModel.KeyboardButtonId;

public class Keyboard {
    private static Map<KeyboardButtonModel.KeyboardButtonId, KeyboardButtonModel> keyboardButtonsConversionTable;
    static {
        keyboardButtonsConversionTable = new HashMap<>();
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_Q, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_q, "q"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_W, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_w, "w"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_E, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_e, "e"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_R, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_r, "r"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_T, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_t, "t"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_Y, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_y, "y"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_U, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_u, "u"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_I, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_i, "i"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_O, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_o, "o"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_P, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_p, "p"));

        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_A, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_a, "a"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_S, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_s, "s"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_D, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_d, "d"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_F, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_f, "f"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_G, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_g, "g"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_H, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_h, "h"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_J, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_j, "j"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_K, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_k, "k"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_L, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_l, "l"));

        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_Z, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_z, "z"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_X, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_x, "x"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_C, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_c, "c"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_V, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_v, "v"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_B, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_b, "b"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_N, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_n, "n"));
        keyboardButtonsConversionTable.put(KeyboardButtonId.BUTTON_M, new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_m, "m"));
    }

    public static KeyboardButtonModel keyboardButtonModelToLowerCase (KeyboardButtonModel keyboardButtonModel) {
        KeyboardButtonModel lowercasedKeyboardButtonModel = keyboardButtonsConversionTable.get(keyboardButtonModel.getId());

        if (lowercasedKeyboardButtonModel != null) {
            return lowercasedKeyboardButtonModel;
        } else {
            return keyboardButtonModel;
        }
    }
}