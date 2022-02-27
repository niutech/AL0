package computer.fuji.al0.utils;

public class StringUtils {
    public static String capitalize (String input) {
        return input.substring(0, 1).toUpperCase().concat(input.substring(1));
    }

    public static String capitalizeAll (String input) {
        String [] words = input.split(" ");
        String output = "";

        for (String word:words) {
            output = output.concat(" ").concat(capitalize(word));
        }

        return output.substring(1);
    }
}
