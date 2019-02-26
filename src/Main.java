import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        final Path USER_LIST_PATH = Paths.get("MessengerUserList.txt");
        final String CSV_DELIMITER = ";";
        String answer;
        Map<String, String> userList;
        MessengerUtilities messUtil = new MessengerUtilities();
        MessagingUtilities messagingUtilities = new MessagingUtilities();
        Scanner scanner = new Scanner(System.in);

        //Create the user list file if it doesn't yet exist
        messUtil.createTextFileIfNotCreated(USER_LIST_PATH);

        //Map the user list
        userList = messUtil.readFromCsvIntoMap(USER_LIST_PATH, CSV_DELIMITER);

        //Welcome the user and provide initial options
        System.out.println("Welcome to Java Virtual Messenger!\n" + "Please register or log in. (REG/LOGIN)");
        answer = scanner.next();

        User user = null;
        if (answer.equalsIgnoreCase("REG")) {
            //Registering process
            user = messUtil.registerNewUser(userList, CSV_DELIMITER, USER_LIST_PATH);
        } else if (answer.equalsIgnoreCase("LOGIN")){
            //Log in process
            user = messUtil.logInUser(userList);
        }

        System.out.println("Do you wish to send a message? (Y/N)");
        answer = scanner.next();

        if (answer.equalsIgnoreCase("Y")) {
            messagingUtilities.composeMessage(user.getUserName(), userList);
        }
    }
}
