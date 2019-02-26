import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        final Path userListPath = Paths.get("MessengerUserList.txt");
        final String csvDelimiter = ";";
        String answer;
        Map<String, String> userList;
        MessengerUtilities messUtil = new MessengerUtilities();
        Scanner scanner = new Scanner(System.in);

        //Create the user list file if it doesn't yet exist
        messUtil.createTextFileIfNotCreated(userListPath);

        //Map the user list
        userList = messUtil.readFromCsvIntoMap(userListPath, csvDelimiter);

        String nameToRemove = "Sander";
        messUtil.removeUserFromUserList(userList, nameToRemove, userListPath, csvDelimiter);

        //Welcome the user and provide initial options
        System.out.println("Welcome to Java Virtual Messenger!\n" + "Please register or log in. (REG/LOGIN)");
        answer = scanner.next();

        if (answer.equalsIgnoreCase("REG")) {
            //Registering process
            messUtil.registerNewUser(userList, csvDelimiter, userListPath);
        } else if (answer.equalsIgnoreCase("LOGIN")){
            //Log in process
            messUtil.logInUser(userList, csvDelimiter, userListPath);
        }
    }
}
