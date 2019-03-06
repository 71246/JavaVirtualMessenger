package jvm;

import java.io.*;
import java.text.ParseException;
import java.util.Map;
import java.util.Scanner;

import static jvm.MessagingMethods.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        String menuText, answer;
        //int answerInt;
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
        answer = scanner.nextLine();

        //Initialize jvm.User class
        jvm.User user;

        //Registering and logging in
        while (true) {
            if (answer.equals("1")) {
                //Registering process
                user = registerNewUser(userList);
                break;
            } else if (answer.equals("2")) {
                //Log in process
                user = logInUser(userList);
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
                answer = scanner.nextLine();
            }
        }

        //Display chat window immediately after logging in
        user.collectConversations();

        if (user.getNumberOfConversations() >= 1) {
            menuText = " CHAT WINDOW ";
            printEqualLengthMenuLine(menuText);
            user.printConversations();
            menuText = " CHAT NUMBER|NEW CHAT (+)|MENU (-) ";
            printEqualLengthMenuLine(menuText);
            answer = scanner.nextLine();

            if (user.getConversations().keySet().contains(answer)) {
                chat(user, answer);
            } else if (answer.equals("+")) {
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
                answer = scanner.nextLine();
            }

            if (newChat) {
                menuText = " REGULAR (1)|GROUP (2) ";
                printEqualLengthMenuLine(menuText);
                answer = scanner.nextLine();

                if (answer.equals("2")) {
                    chooseRecipients(user, userList);
                } else if (answer.equals("1")) {
                    chooseRecipient(user, userList);
                }
                newChat = false;
            } else if (answer.equals("1")) {
                checkMessages(user);
            } else if (answer.equals("2")) {
                user.collectConversations();
                user.printConversations();
                chat(user, "");
                newChat = true;
            } else if (answer.equals("3")) {
                System.out.println("Feature under construction :)\n");
            } else if (answer.equals("4")) {
                System.out.println("Ya'll come back now! Ya hear?");
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
            }
        }
    }
}
