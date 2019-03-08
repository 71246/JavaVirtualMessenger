package jvm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import static jvm.MessagingMethods.*;

class InitialProcesses {

    static User registerAndLogIn(Map<String, String> userList) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String answer;

        //Welcome the user and provide initial options
        System.out.println("Welcome to Java Virtual Messenger!\n");
        printWelcomeText();
        printEqualLengthMenuLine(" REGISTER (1)|LOGIN (2) ");
        answer = scanner.nextLine();

        //Registering and logging in
        while (true) {
            if (answer.equals("1")) {
                //Registering process
                return registerNewUser(userList);
            } else if (answer.equals("2")) {
                //Log in process
                return logInUser(userList);
            } else {
                System.out.println("Unknown command. Please try again!");
                answer = scanner.nextLine();
            }
        }
    }

    private static User logInUser(Map<String, String> userList) throws IOException {
        String enteredUserName, enteredPassword, menuText;
        int triesLeft = 3;
        Scanner scanner = new Scanner(System.in);

        printEqualLengthMenuLine(" LOGIN ");
        System.out.println("USER NAME:");
        enteredUserName = scanner.next();

        //Check for entered user name in the user list
        while (!checkForKey(userList, enteredUserName)) {
            System.out.print("Invalid user name, try again!\n");
            enteredUserName = scanner.next();
        }

        System.out.println("PASSWORD:");
        enteredPassword = scanner.next();

        while (triesLeft > 0) {
            triesLeft--;

            if (!checkForValue(userList, enteredUserName).equals(enteredPassword)) {
                switch (triesLeft) {
                    case 0:
                        System.out.print("Invalid password! You have exhausted your number of tries! \nThe account will be blocked for 3 hours.");
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

        return new User(enteredUserName);
    }

    private static User registerNewUser(Map<String, String> userList) throws IOException {
        String enteredUserName;
        String enteredPassword;
        Scanner scanner = new Scanner(System.in);

        printEqualLengthMenuLine(" REGISTER ");

        //Prompt the user for a username
        System.out.println("Please enter your desired user name:");
        enteredUserName = scanner.next();

        //Check if the entered username is free to use
        while (checkForKey(userList, enteredUserName)) {
            System.out.print("The selected username already exists, choose another one!\n");
            enteredUserName = scanner.next();
        }

        //Prompt the user for a password, explain the requirements
        System.out.println("Your username is " + enteredUserName + ".\nPlease provide a password.\nThe password has to be at least 8 characters long and it must contain " +
                "\nat least one upper case, one lower case and one numeric character.");
        enteredPassword = scanner.next();

        //Check if entered password meets the requirements
        while (!enteredPassword.matches(FinalClass.PASSWORD_PATTERN)) {
            System.out.println("Password doesn't match the requirements. Try another one!");
            enteredPassword = scanner.next();
        }

        //Save the new username into user list
        appendUserNamePswToUserList(enteredUserName + FinalClass.CSV_DELIMITER + enteredPassword);

        System.out.println("New user successfully created.\nWelcome to jvm, " + enteredUserName + "!");

        return new User(enteredUserName);
    }

    private static void appendUserNamePswToUserList(String stringToAdd) throws IOException {
        FileWriter writer = new FileWriter(String.valueOf(FinalClass.USER_LIST_PATH), true);
        writer.append("\n");
        writer.append(stringToAdd);
        writer.close();
    }
}
