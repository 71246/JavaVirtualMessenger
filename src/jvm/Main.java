package jvm;

import java.io.*;
import java.text.ParseException;
import java.util.Map;
import java.util.Scanner;

import static jvm.MessagingMethods.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        String answer;
        Map<String, String> userList;
        Scanner scanner = new Scanner(System.in);

        //Create the user list file if it doesn't yet exist
        int resultOfFileCreation = createTextFileIfNotCreated(FinalClass.USER_LIST_PATH);

        //Map the user list
        userList = readFromCsvIntoMap();

        //Welcome the user and provide initial options
        System.out.println("Welcome to Java Virtual Messenger!\n" + "Please register or log in." +
                "\n--------------------------- REGISTER (1)|LOGIN (2) ----------------------------");
        answer = scanner.nextLine();

        //Initialize jvm.User class
        jvm.User user;

        //Registering and logging in
        while (true) {
            if (answer.equalsIgnoreCase("1")) {
                //Registering process
                user = registerNewUser(userList);
                break;
            } else if (answer.equalsIgnoreCase("2")) {
                //Log in process
                user = logInUser(userList);
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
                answer = scanner.nextLine();
            }
        }

        //Messaging processes
        while (true) {
            System.out.println("\n------------- CHECK MESSAGES (1)|CHAT (2)|SETTINGS (3)|LOGOUT (4) -------------");
            answer = scanner.nextLine();

            if (answer.equalsIgnoreCase("SEND")) {
                System.out.println("Regular of group chat? \n" +
                        "---------------------------- REGULAR (1)|GROUP (2) ----------------------------");
                answer = scanner.nextLine();

                if (answer.equalsIgnoreCase("2")) {
                    chooseRecipients(user.getUserName(), userList);
                } else if (answer.equalsIgnoreCase("1")) {
                    chooseRecipient(user.getUserName(), userList);
                }
            } else if (answer.equalsIgnoreCase("1")) {
                checkMessages(user);
            } else if (answer.equalsIgnoreCase("2")) {
                user.collectConversations();
                user.printConversations();
                chat(user);
            } else if (answer.equalsIgnoreCase("3")) {
                System.out.println("Feature under construction :)");
            } else if (answer.equalsIgnoreCase("4")) {
                System.out.println("\nGoodbye, " + user.getUserName() + "!");
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
            }
        }
    }
}
