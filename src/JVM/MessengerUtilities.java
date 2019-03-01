package JVM;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

class MessengerUtilities {
    FinalClass finalClass = new FinalClass();

    boolean checkForKey(Map<String, String> userList, String nameToCheck) {
        if (userList.containsKey(nameToCheck)) return true;

        return false;
    }

    private String checkForValue(Map<String, String> userList, String nameToCheck) {
        MessengerUtilities messUtil = new MessengerUtilities();

        if (messUtil.checkForKey(userList, nameToCheck))
            return userList.get(nameToCheck);

        return null;
    }

    Integer createTextFileIfNotCreated(Path filePath) throws IOException {
        File file = new File(String.valueOf(filePath));

        if (filePath != null && !file.exists()) {
            file.createNewFile();
            return -1;
        }
        return 0;
    }

    String searchForFileNameContainingSubstring(ArrayList<String> recipients, String userName, boolean groupNameExists) throws IOException {
        //Create the file name to use if file hasn't been created
        String fileNameToUse = "", delimiter = "-", suffix = ".txt";

        if (!groupNameExists) {
            for (String recipient : recipients) {
                if (fileNameToUse.equals("")) {
                    fileNameToUse = recipient;
                } else {
                    fileNameToUse = fileNameToUse + delimiter + recipient;
                }
            }

            fileNameToUse = userName + delimiter + fileNameToUse + suffix;
        } else {
            fileNameToUse = recipients.get(0) + suffix;
        }

        //Search for it, if not found create it
        File file1 = new File("");
        boolean foundAllSubstrings = false;

        if (file1.isDirectory()) {
            String[] files = file1.list();
            for (String fileName : files) {
                foundAllSubstrings = true;
                for (String element: recipients) {
                    if (!fileName.contains(element)) {
                        foundAllSubstrings = false;
                        break;
                    }
                }

                if (foundAllSubstrings && fileName.contains(userName) && fileName.length() == fileNameToUse.length()) {
                    if (!groupNameExists) System.out.println("Group " + fileName.replace(".txt", "") + " already exists!");
                    return fileName;
                }
            }
        }

        if (!foundAllSubstrings) {
            File file3 = new File(fileNameToUse);
            file3.createNewFile();
            if (groupNameExists) System.out.println("Group " + file3.getName().replace(".txt", "") + " has been created!");
            return file3.getName();
        } else {
            return "";
        }
    }

    String createMessageTxtFileIfNotCreated(String stringToCheck, String anotherStringToCheck, String suffix) throws IOException {
        File file1 = new File(stringToCheck + finalClass.MESSAGE_FILE_NAME_DELIMITER + anotherStringToCheck + suffix);
        File file2 = new File(anotherStringToCheck + finalClass.MESSAGE_FILE_NAME_DELIMITER + stringToCheck + suffix);

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

    Map<String, String> readFromCsvIntoMap() throws IOException {
        Map<String, String> map = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(finalClass.USER_LIST_PATH)));
        String line;
        line = bufferedReader.readLine();
        String[] splitLines;

        while ((line = bufferedReader.readLine()) != null) {
            splitLines = line.split(finalClass.CSV_DELIMITER);
            map.put(splitLines[0], splitLines[1]);
        }
        bufferedReader.close();
        return map;
    }

    User registerNewUser(Map<String, String> userList) throws IOException {
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
        messUtil.appendUserNamePswToCSV(enteredUserName + finalClass.CSV_DELIMITER + enteredPassword, finalClass.USER_LIST_PATH);

        System.out.println("New user successfully created.\nWelcome to JVM, " + enteredUserName + "!");

        return new User(enteredUserName, enteredPassword);
    }

    User logInUser(Map<String, String> userList) {
        String enteredUserName;
        String enteredPassword;
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

        System.out.println("Please enter your password:");
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
