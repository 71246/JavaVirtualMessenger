package jvm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static jvm.MessagingMethods.*;
import static jvm.UserList.*;

class InitialProcesses {

    static User registerAndLogIn() throws IOException {
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
                return registerNewUser();
            } else if (answer.equals("2")) {
                //Log in process
                return logInUser();
            } else {
                System.out.println("Unknown command. Please try again!");
                answer = scanner.nextLine();
            }
        }
    }

    private static User logInUser() {
        String enteredUserName, enteredPassword;
        int triesLeft = 3;
        Scanner scanner = new Scanner(System.in);

        printEqualLengthMenuLine(" LOGIN ");
        System.out.println("USER NAME:");
        enteredUserName = scanner.next();

        //Check for entered user name in the user list
        while (isUserNameInvalid(enteredUserName)) {
            System.out.print("Invalid user name, try again!\n");
            enteredUserName = scanner.next();
        }

        System.out.println("PASSWORD:");
        enteredPassword = scanner.next();

        while (triesLeft > 0) {
            triesLeft--;

            if (!isPasswordValid(enteredUserName, enteredPassword)) {
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

    private static User registerNewUser() {
        String enteredUserName;
        String enteredPassword;
        Scanner scanner = new Scanner(System.in);

        printEqualLengthMenuLine(" REGISTERING ");

        //Prompt the user for a username
        System.out.println("Please enter your desired user name:");
        enteredUserName = scanner.next();

        //Check if the entered username is free to use
        while (isUserNameInvalid(enteredUserName)) {
            System.out.print("Invalid user name, please choose another one!\n");
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

    private static void appendUserNamePswToUserList(String userNameAndPassword) {
        FileWriter writer;

        try {
            writer = new FileWriter(String.valueOf(FinalClass.USER_LIST_PATH), true);

            writer.append("\n");
            writer.append(userNameAndPassword);
            writer.close();
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred while registering!");
        }
    }
}
