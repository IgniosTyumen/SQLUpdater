

public class CellSeparator {

    public static String[] translateAdressToColAndRows(String input) {
        String result[] = {"", ""};
        for (int i = 0; i < input.length(); i++
                ) {
            Character ch = input.charAt(i);
            if (Character.isAlphabetic(ch)) {
                result[0] = result[0] + ch.toString();
            } else {
                result[1] = result[1] + ch.toString();
            }

        }
        return result;
    }

}
