package jvm;

import java.io.*;
import java.text.ParseException;
import java.util.Map;
import java.util.Scanner;

import static jvm.MessagingMethods.*;
import static jvm.InitialProcesses.*;

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

        //Initialize jvm.User class
        jvm.User user;

        registerAndLogIn(userList);

        //Display chat window immediately after
        //logging in if user has ongoing chats
        user.collectConversations();

        if (user.getNumberOfConversations() >= 1) {
            menuText = " CHAT WINDOW ";
            printEqualLengthMenuLine(menuText);
            user.printConversations();
            menuText = " CHAT NUMBER|NEW CHAT (+)|MENU (-) ";
            printEqualLengthMenuLine(menuText);
            answer = scanner.nextLine();

            if (user.getConversations().keySet().contains(answer)) {
                chat(user, answer, userList);
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
                regularOrGroupChatFork(user, userList);
                newChat = false;
            } else if (answer.equals("1")) {
                checkMessages(user);
            } else if (answer.equals("2")) {
                user.collectConversations();
                user.printConversations();
                chat(user, "", userList);
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
