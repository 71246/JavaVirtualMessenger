package JVM;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommonMethods {

    static Integer createTextFileIfNotCreated(Path filePath) throws IOException {
        File file = new File(String.valueOf(filePath));

        if (filePath != null && !file.exists()) {
            file.createNewFile();
            return -1;
        }
        return 0;
    }

    static boolean checkForKey(Map<String, String> userList, String nameToCheck) {
        return userList.containsKey(nameToCheck);
    }

    static String checkForValue(Map<String, String> userList, String nameToCheck) {
        if (checkForKey(userList, nameToCheck))
            return userList.get(nameToCheck);

        return null;
    }

    static void saveMessageToTextFile(String userName, String message, Path messagesPath, String timeStamp) throws IOException {
        int resultOfFileCreation = createTextFileIfNotCreated(messagesPath);

        FileWriter writer = new FileWriter(String.valueOf(messagesPath), true);
        if (resultOfFileCreation == 0) {
            writer.append("\n");
        }

        writer.append("<timestamp>");
        writer.append(timeStamp);
        writer.append("\n");
        writer.append(userName);
        writer.append(" says:\n");
        writer.append(message);
        writer.close();
    }

    static User logInUser(Map<String, String> userList) {
        String enteredUserName;
        String enteredPassword;
        int triesLeft = 3;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter your user name:");
        enteredUserName = scanner.next();

        //Check for entered user name in the user list
        while (!checkForKey(userList, enteredUserName)) {
            System.out.print("Invalid user name, try again!\n");
            enteredUserName = scanner.next();
        }

        System.out.println("Please enter your password:");
        enteredPassword = scanner.next();

        while (triesLeft > 0) {
            triesLeft--;

            if (!checkForValue(userList, enteredUserName).equals(enteredPassword)) {
                switch (triesLeft) {
                    case 0:
                        System.out.print("Invalid password! You have exhausted your number of tries! The account will be blocked for 3 hours.");
                        System.exit(-1);
                    case 1:
                        System.out.print("Invalid password, try again! " + "This is your last try.\n");
                        enteredPassword = scanner.next();
                        break;
                    case 2:
                        System.out.print("Invalid password, try again! " + "You have " + triesLeft + " tries left.\n");
                        enteredPassword = scanner.next();
                        break;
                    default:
                }
            }
        }

        System.out.println("\nWelcome back, " + enteredUserName + "!");
        return new User(enteredUserName, enteredPassword);
    }

    static Map<String, String> readFromCsvIntoMap() throws IOException {
        Map<String, String> map = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(FinalClass.USER_LIST_PATH)));
        String line;
        line = bufferedReader.readLine();
        String[] splitLines;

        while ((line = bufferedReader.readLine()) != null) {
            splitLines = line.split(FinalClass.CSV_DELIMITER);
            map.put(splitLines[0], splitLines[1]);
        }
        bufferedReader.close();
        return map;
    }

    static void appendUserNamePswToCSV(String stringToAdd, Path filePath) throws IOException {
        FileWriter writer = new FileWriter(String.valueOf(filePath), true);
        writer.append("\n");
        writer.append(stringToAdd);
        writer.close();
    }

    static String createTimeStamp() {
        String timeStampPattern = "yyyy-MM-dd HH:mm:ss.SSS";
        SimpleDateFormat sdfDate = new SimpleDateFormat(timeStampPattern);
        Date now = new Date();
        return sdfDate.format(now);
    }
}
