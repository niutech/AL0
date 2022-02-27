package computer.fuji.al0.models;

public class KeyboardModel {
    private final static KeyboardButtonModel buttonNothing = new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_NOTHING, " ");
    private final static KeyboardButtonModel buttonDelete = new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_DELETE, "⌫");
    private final static KeyboardButtonModel buttonDot = new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_DOT, ".");
    public final static KeyboardButtonModel buttonSpace = new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_SPACE, " ");

    // Controls Line
    public final static KeyboardButtonModel[] controlsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_DIACRITIC, "△"),
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_MOVE_CURSOR_LEFT, "←"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_MOVE_CURSOR_RIGHT, "→")
    };

    public final static KeyboardButtonModel[] controlsKeyboardDiacriticsActiveButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_DIACRITIC, "▲"),
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_MOVE_CURSOR_LEFT, "←"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_MOVE_CURSOR_RIGHT, "→")
    };

    public final static KeyboardButtonModel[] emptyKeyboardButtonLineModels = new KeyboardButtonModel[] {
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel[] emptyKeyboardButtonLowerLineModels = new KeyboardButtonModel[] {
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonDelete,
    };

    // UPPERCASE
    public final static KeyboardButtonModel[] uppercaseKeyboardButtonLineUpperModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_Q, "Q"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_W, "W"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_E, "E"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_R, "R"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_T, "T"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_Y, "Y"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_U, "U"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_I, "I"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_O, "O"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_P, "P")
    };

    public final static KeyboardButtonModel[] uppercaseKeyboardButtonLineMiddleModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_A, "A"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_S, "S"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_D, "D"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_F, "F"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_G, "G"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_H, "H"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_J, "J"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_K, "K"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_L, "L")
    };

    public final static KeyboardButtonModel[] uppercaseKeyboardButtonLineLowerModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_LOWERCASE, "⬆"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_Z, "Z"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_X, "X"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_C, "C"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_V, "V"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_B, "B"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_N, "N"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_M, "M"),
            buttonNothing,
            buttonDelete,
    };

    // LOWERCASE
    public final static KeyboardButtonModel[] lowercaseKeyboardButtonLineUpperModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_q, "q"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_w, "w"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_e, "e"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_r, "r"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_t, "t"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_y, "y"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_u, "u"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_i, "i"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_o, "o"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_p, "p")
    };

    public final static KeyboardButtonModel[] lowercaseKeyboardButtonLineMiddleModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_a, "a"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_s, "s"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_d, "d"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_f, "f"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_g, "g"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_h, "h"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_j, "j"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_k, "k"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_l, "l")
    };

    public final static KeyboardButtonModel[] lowercaseKeyboardButtonLineLowerModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_UPPERCASE, "⇧"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_z, "z"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_x, "x"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_c, "c"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_v, "v"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_b, "b"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_n, "n"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_m, "m"),
            buttonNothing,
            buttonDelete,
    };

    // NUMBERS
    public final static KeyboardButtonModel[] numbersKeyboardButtonLineUpperModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_1, "1"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_2, "2"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_3, "3"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_4, "4"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_5, "5"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_6, "6"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_7, "7"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_8, "8"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_9, "9"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_0, "0")
    };

    public final static KeyboardButtonModel[] numbersKeyboardButtonLineMiddleModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_MINUS, "-"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_PLUS, "+"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_COLON, ":"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_SEMI_COLON, ";"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_OPEN_PARENTHESIS, "("),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_CLOSE_PARENTHESIS, ")"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_AT, "@"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_AMPERSAND, "&"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_DOUBLE_QUOTES, "\""),
    };

    public final static KeyboardButtonModel[] numbersKeyboardButtonLineLowerModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_SYMBOLS, "±"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_SLASH, "/"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_DOLLAR, "$"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_QUESTION_MARK, "?"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_EXCLAMATION_MARK, "!"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_SINGLE_QUOTE, "'"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_COMMA, ","),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_DOT, "."),
            buttonNothing,
            buttonDelete,
    };

    // SYMBOLS
    public final static KeyboardButtonModel[] symbolsKeyboardButtonLineUpperModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_OPEN_SQUARED, "["),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_CLOSE_SUARED, "]"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_OPEN_CURLY, "{"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_CLOSE_CURLY, "}"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_HASH, "#"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_PER_CENT, "%"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_POWER, "^"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_ASTERISK, "*"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_PLUS, "+"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_EQUAL, "=")
    };

    public final static KeyboardButtonModel[] symbolsKeyboardButtonLineMiddleModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_LOWDASH, "_"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_BACK_SLASH, "\\"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_BAR, "|"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_TILDE, "~"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_LESS_THAN, "<"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GREATER_THAN, ">"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_STERLING, "£"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_EURO, "€"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_YEN, "¥"),
    };

    public final static KeyboardButtonModel[] symbolsKeyboardButtonLineLowerModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_NUMBER, "½"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_MIDDLE_DOT, "•"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_DOLLAR, "$"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_QUESTION_MARK, "¿"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_EXCLAMATION_MARK, "¡"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_SINGLE_QUOTE, "'"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_COMMA, ","),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_DOT, "."),
            buttonNothing,
            buttonDelete,
    };

    // DIACRITICS UPPERCASE
    public final static KeyboardButtonModel [] uppercaseEDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_E, "E"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "È"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "É"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ê"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ë"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ę"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ė"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ē"),
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel [] uppercaseUDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_U, "U"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ū"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Û"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ü"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ú"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ù"),
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel [] uppercaseIDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_I, "I"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ī"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Į"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ï"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Î"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Í"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ì"),
            buttonNothing,
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel [] uppercaseODiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_O, "O"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ᵒ"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ō"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ø"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Œ"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Õ"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ö"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ô"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ó"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ò")
    };

    public final static KeyboardButtonModel [] uppercaseADiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_A, "A"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Á"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "À"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Â"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ä"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Æ"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ã"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Å"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ā"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ᵃ"),
    };

    public final static KeyboardButtonModel [] uppercaseSDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_S, "S"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ẞ"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ś"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ŝ"),
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel [] uppercaseCDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_C, "C"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ç"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ć"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Č"),
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel [] uppercaseNDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_N, "N"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "Ñ"),
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing
    };

    // DIACRITICS LOWERCASE
    public final static KeyboardButtonModel [] lowercaseEDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_e, "e"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "è"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "é"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ê"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ё"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ę"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ė"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ē"),
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel [] lowercaseUDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_u, "u"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ū"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "û"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ü"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ú"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ù"),
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel [] lowercaseIDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_i, "i"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ī"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "į"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ï"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "î"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "í"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ì"),
            buttonNothing,
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel [] lowercaseODiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_o, "o"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ᵒ"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ō"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ø"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "œ"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "õ"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ö"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ô"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ó"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ò")
    };

    public final static KeyboardButtonModel [] lowercaseADiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_a, "a"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "á"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "à"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "â"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ä"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "æ"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ã"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ȧ"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ā"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ᵃ"),
    };

    public final static KeyboardButtonModel [] lowercaseSDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_s, "s"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ß"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ś"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ŝ"),
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel [] lowercaseCDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_c, "c"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ç"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ć"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "č"),
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing
    };

    public final static KeyboardButtonModel [] lowercaseNDiacriticsKeyboardButtonLineModels = new KeyboardButtonModel[] {
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_n, "n"),
            new KeyboardButtonModel(KeyboardButtonModel.KeyboardButtonId.BUTTON_GENERIC, "ñ"),
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing,
            buttonNothing
    };
}
