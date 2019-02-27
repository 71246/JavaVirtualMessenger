import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        final Path USER_LIST_PATH = Paths.get("MessengerUserList.txt");
        final String CSV_DELIMITER = ";";
        String answer;
        Map<String, String> userList;
        MessengerUtilities messUtil = new MessengerUtilities();
        MessagingUtilities messagingUtilities = new MessagingUtilities();
        Scanner scanner = new Scanner(System.in);

        //Create the user list file if it doesn't yet exist
        int resultOfFileCreation = messUtil.createTextFileIfNotCreated(USER_LIST_PATH);

        //Map the user list
        userList = messUtil.readFromCsvIntoMap(USER_LIST_PATH, CSV_DELIMITER);

        //Welcome the user and provide initial options
        System.out.println("Welcome to Java Virtual Messenger!\n" + "Please register or log in. (REG/LOGIN)");
        answer = scanner.next();

        //Initialize User class
        User user = null;

        //Registering and logging in
        while (true) {
            if (answer.equalsIgnoreCase("REG")) {
                //Registering process
                user = messUtil.registerNewUser(userList, CSV_DELIMITER, USER_LIST_PATH);
                break;
            } else if (answer.equalsIgnoreCase("LOGIN")) {
                //Log in process
                user = messUtil.logInUser(userList);
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
                answer = scanner.next();
            }
        }

        //Messaging processes
        while (true) {
            System.out.println("Do you wish to send a message, check new messages or log out? (SEND/CHECK/LOGOUT)");
            answer = scanner.next();

            if (answer.equalsIgnoreCase("SEND")) {
                messagingUtilities.composeMessage(user.getUserName(), userList, CSV_DELIMITER);
            } else if (answer.equalsIgnoreCase("CHECK")) {
                messagingUtilities.checkMessages(user, CSV_DELIMITER);

            } else if (answer.equalsIgnoreCase("LOGOUT")) {
                System.out.println("Goodbye, " + user.getUserName() + "!");
                System.exit(0);
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
            }
        }
    }
}
