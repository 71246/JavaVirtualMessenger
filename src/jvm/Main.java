package jvm;

import java.io.*;
import java.text.ParseException;
import java.util.Map;
import java.util.Scanner;

import static jvm.MessagingMethods.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        String menuText;
        int answerInt;
        boolean newChat = false;
        Map<String, String> userList;
        Scanner scanner = new Scanner(System.in);

        //Create the user list file if it doesn't yet exist
        int resultOfFileCreation = createTextFileIfNotCreated(FinalClass.USER_LIST_PATH);

        //Map the user list
        userList = readFromCsvIntoMap();

        //Welcome the user and provide initial options
        menuText =  " REGISTER (1)|LOGIN (2) ";
        System.out.println("Welcome to Java Virtual Messenger!\n");
        printWelcomeText();
        printEqualLengthMenuLine(menuText);
        answerInt = scanner.nextInt();

        //Initialize jvm.User class
        jvm.User user;

        //Registering and logging in
        while (true) {
            if (answerInt == 1) {
                //Registering process
                user = registerNewUser(userList);
                break;
            } else if (answerInt == 2) {
                //Log in process
                user = logInUser(userList);
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
                answerInt = scanner.nextInt();
            }
        }

        //Display chat window after logging in
        user.collectConversations();

        if (user.getNumberOfConversations() >= 1) {
            menuText = " CHAT WINDOW ";
            printEqualLengthMenuLine(menuText);
            user.printConversations();
            menuText = " CHAT NUMBER|NEW CHAT (" + Math.addExact(user.getNumberOfConversations(), 1) + ")|MENU (" + Math.addExact(user.getNumberOfConversations(), 2) + ") ";
            printEqualLengthMenuLine(menuText);
            answerInt = scanner.nextInt();

            if (user.getConversations().keySet().contains(answerInt)) {
                chat(user, answerInt);
            } else if (answerInt == Math.addExact(user.getNumberOfConversations(), 1)) {
                newChat = true;
            }
        }

        //Main processes
        while (true) {
            if (!newChat) {
                menuText = " MAIN MENU ";
                printEqualLengthMenuLine(menuText);
                menuText = " CHECK MESSAGES (1)|CHAT (2)|SETTINGS (3)|LOGOUT (4) ";
                printEqualLengthMenuLine(menuText);
                answerInt = scanner.nextInt();
            }

            if (newChat) {
                //System.out.println("Regular or group chat? \n");
                menuText = " REGULAR (1)|GROUP (2) ";
                printEqualLengthMenuLine(menuText);
                answerInt = scanner.nextInt();

                if (answerInt == 2) {
                    chooseRecipients(user.getUserName(), userList);
                } else if (answerInt == 1) {
                    chooseRecipient(user.getUserName(), userList);
                }
                newChat = false;
            } else if (answerInt == 1) {
                checkMessages(user);
            } else if (answerInt == 2) {
                user.collectConversations();
                user.printConversations();
                chat(user, -1);
            } else if (answerInt == 3) {
                System.out.println("Feature under construction :)\n");
            } else if (answerInt == 4) {
                System.out.println("Ya'll come back now! Ya hear?");
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
            }
        }
    }
}
