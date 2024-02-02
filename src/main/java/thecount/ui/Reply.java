package thecount.ui;

/**
 * Represents a reply message to be displayed to the user.
 */
public class Reply {
    private String message;

    /**
     * Constructs a reply object with the given message.
     *
     * @param message The message to be displayed.
     */
    public Reply(String message) {
        this.message = message;
    }

    /**
     * Displays the reply message with appropriate formatting.
     */
    public void displayMessage() {
        String emptySpace = "      ";
        String horizLine = "    ____________________________________________________";
        System.out.println(horizLine.concat("\n").concat(emptySpace)
                .concat(this.message).concat("\n").concat(horizLine));
    }
}