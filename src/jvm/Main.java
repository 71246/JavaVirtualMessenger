package jvm;

import java.io.*;
import java.text.ParseException;
import java.util.Map;
import java.util.Scanner;

import static jvm.MessagingMethods.*;
import static jvm.InitialProcesses.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        String menuText, answer = "";
        boolean newChat = false, exitCondition = false;
        Map<String, String> userList;
        Scanner scanner = new Scanner(System.in);

        //Create of find path of user list file
        int resultOfFileCreation = createTextFileIfNotCreated(FinalClass.USER_LIST_PATH);
        userList = readFromCsvIntoMap();

        //Initialize User class
        User user = registerAndLogIn(userList);
        user.collectConversations();

        //Chat window
        if (user.getNumberOfConversations() >= 1) {
            menuText = " CHAT WINDOW ";
            printEqualLengthMenuLine(menuText);
            user.printConversations();
            menuText = " CHAT NUMBER|NEW CHAT (+)|MENU (-) ";
            printEqualLengthMenuLine(menuText);
            answer = scanner.nextLine();

            if (user.getConversations().containsKey(answer)) {
                chat(user, answer, userList);
            }
        } else {
            answer = "-";
        }

        //Main processes
        while (!exitCondition) {
            switch (answer) {
                case "-":
                    System.out.println();
                    menuText = " MAIN MENU ";
                    printEqualLengthMenuLine(menuText);
                    menuText = " CHECK MESSAGES (1)|CHAT (2)|SETTINGS (3)|LOGOUT (4) ";
                    printEqualLengthMenuLine(menuText);
                    answer = scanner.nextLine();
                    break;
                case "1":
                    checkMessages(user);
                    answer = "-";
                    break;
                case "2":
                    chat(user, "", userList);
                    answer = "-";
                    break;
                case "+":
                    user.collectConversations();
                    user.printConversations();
                    chat(user, "", userList);
                    answer = "-";
                    break;
                case "3":
                    System.out.println("Feature under construction :)\n");
                    answer = "-";
                    break;
                case "4":
                    menuText = " SESSION TERMINATED ";
                    printEqualLengthMenuLine(menuText);
                    exitCondition = true;
                    break;
                default:
                    System.out.println("\nUnknown command. Please try again!\n");
                    answer = "-";
            }
        }
    }
}
