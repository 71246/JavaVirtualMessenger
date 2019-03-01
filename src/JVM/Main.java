package JVM;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        FinalClass finalClass = new FinalClass();
        String answer;
        Map<String, String> userList;
        MessengerUtilities messUtil = new MessengerUtilities();
        MessagingUtilities messagingUtilities = new MessagingUtilities();
        Scanner scanner = new Scanner(System.in);

        //Create the user list file if it doesn't yet exist
        int resultOfFileCreation = messUtil.createTextFileIfNotCreated(finalClass.USER_LIST_PATH);

        //Map the user list
        userList = messUtil.readFromCsvIntoMap();

        //Welcome the user and provide initial options
        System.out.println("Welcome to Java Virtual Messenger!\n" + "Please register or log in. (REG/LOGIN)");
        answer = scanner.nextLine();

        //Initialize JVM.User class
        User user;

        //Registering and logging in
        while (true) {
            if (answer.equalsIgnoreCase("REG")) {
                //Registering process
                user = messUtil.registerNewUser(userList);
                break;
            } else if (answer.equalsIgnoreCase("LOGIN")) {
                //Log in process
                user = messUtil.logInUser(userList);
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
                answer = scanner.nextLine();
            }
        }

        ArrayList<String> recipients = new ArrayList<>();

        //Messaging processes
        while (true) {
            System.out.println("\nDo you wish to send a message, check new messages or log out? (SEND/CHECK/LOGOUT)");
            answer = scanner.nextLine();

            if (answer.equalsIgnoreCase("SEND")) {
                System.out.println("Do you wish to start a group chat? (Y/N)");
                answer = scanner.nextLine();

                boolean groupChat = false;

                if (answer.equalsIgnoreCase("Y"))
                    groupChat = true;

                messagingUtilities.composeMessage(user.getUserName(), userList, recipients, groupChat, false);
            } else if (answer.equalsIgnoreCase("CHECK")) {
                messagingUtilities.checkMessages(user, userList);
            } else if (answer.equalsIgnoreCase("LOGOUT")) {
                System.out.println("\nGoodbye, " + user.getUserName() + "!");
                System.exit(0);
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
            }
        }
    }
}
