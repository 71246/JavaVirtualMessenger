import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class MessengerUtilities {

    boolean checkForKey(Map<String, String> userList, String nameToCheck) {
        if (userList.containsKey(nameToCheck)) return true;

        return false;
    }

    String checkForValue(Map<String, String> userList, String nameToCheck) {
        MessengerUtilities messUtil = new MessengerUtilities();

        if (messUtil.checkForKey(userList, nameToCheck))
            return userList.get(nameToCheck);

        return null;
    }

    void createTextFileIfNotCreated(Path filePath) throws IOException {
        File file = new File(String.valueOf(filePath));

        if (filePath != null && !file.exists())
            file.createNewFile();
    }

    String createMessageTxtFile(String stringToCheck, String anotherStringToCheck, String delimiter, String suffix) throws IOException {
        File file1 = new File(stringToCheck + delimiter + anotherStringToCheck + suffix);
        File file2 = new File(anotherStringToCheck + delimiter + stringToCheck + suffix);

        if (!file1.exists() && !file2.exists()) {
            file1.createNewFile();
            return file1.getName();
        } else if (file1.exists()) {
            return file1.getName();
        } else {
            return file2.getName();
        }
    }

    private void appendUserNamePswToCSV(String stringToAdd, Path filePath) throws IOException {
        FileWriter writer = new FileWriter(String.valueOf(filePath), true);
        writer.append("\n");
        writer.append(stringToAdd);
        writer.close();
    }
    /*
    void removeUserFromUserList(Map<String, String> userList, String nameToRemove, Path filePath, String delimiter) throws IOException {
        Iterator<String> iterator = userList.keySet().iterator();

        // Iterate over all the elements
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (userList.get(key).equals(nameToRemove)) {
                iterator.remove();
                //break;
            }
        }
        System.out.println(userList);

        FileWriter writer = new FileWriter(String.valueOf(filePath), false);

        writer.append("username;password");
        writer.append("\n");

        for(Map.Entry<String, String> user: userList.entrySet()) {
            writer.append(user.getKey() + delimiter + user.getValue());
            writer.append("\n");
        }
        writer.close();
    }*/

    Map<String, String> readFromCsvIntoMap(Path filepath, String delimiter) throws IOException {
        Map<String, String> map = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(filepath)));
        String line;
        line = bufferedReader.readLine();
        String[] splitLines;

        while ((line = bufferedReader.readLine()) != null) {
            splitLines = line.split(delimiter);
            map.put(splitLines[0], splitLines[1]);
        }
        bufferedReader.close();
        return map;
    }

    User registerNewUser(Map<String, String> userList, String csvDelimiter, Path userListPath) throws IOException {
        String enteredUserName;
        String enteredPassword;
        MessengerUtilities messUtil = new MessengerUtilities();
        Scanner scanner = new Scanner(System.in);
        String passwordPattern = "^(?=.{8,})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$";

        //Prompt the user for a username
        System.out.println("Please enter your desired user name:");
        enteredUserName = scanner.next();

        //Check if the entered username is free to use
        while (messUtil.checkForKey(userList, enteredUserName)) {
            System.out.print("The selected username already exists, choose another one!\n");
            enteredUserName = scanner.next();
        }

        //Prompt the user for a password, explain the requirements
        System.out.println("Your username is " + enteredUserName + ".\nPlease provide a password.\nThe password has to be at least 8 characters long and it must contain " +
                "\nat least one upper case, one lower case and one numeric character.");
        enteredPassword = scanner.next();

        //Check if entered password meets the requirements
        while (!enteredPassword.matches(passwordPattern)) {
            System.out.println("Password doesn't match the requirements. Try another one!");
            enteredPassword = scanner.next();
        }

        //Save the new username into user list
        messUtil.appendUserNamePswToCSV(enteredUserName + csvDelimiter + enteredPassword, userListPath);

        System.out.println("New user successfully created.\nWelcome to JVM, " + enteredUserName + "!");

        return new User(enteredUserName, enteredPassword);
    }

    User logInUser(Map<String, String> userList) {
        String enteredUserName;
        String enteredPassword = "";
        int triesLeft = 3;
        MessengerUtilities messUtil = new MessengerUtilities();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter your user name:");
        enteredUserName = scanner.next();

        //Check for entered user name in the user list
        while (!messUtil.checkForKey(userList, enteredUserName)) {
            System.out.print("Invalid user name, try again!\n");
            enteredUserName = scanner.next();
        }

        System.out.println("Please enter you password:");
        enteredPassword = scanner.next();

        while (triesLeft > 0) {
            triesLeft--;

            if (!messUtil.checkForValue(userList, enteredUserName).equals(enteredPassword)) {
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
}
