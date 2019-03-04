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
        System.out.println("Welcome to Java Virtual Messenger!\n" + "Please register or log in. (REG/LOGIN)");
        answer = scanner.nextLine();

        //Initialize jvm.User class
        jvm.User user;

        //Registering and logging in
        while (true) {
            if (answer.equalsIgnoreCase("REG")) {
                //Registering process
                user = registerNewUser(userList);
                break;
            } else if (answer.equalsIgnoreCase("LOGIN")) {
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
            System.out.println("\nWhat do you wish to do? (SEND/CHECK/CHAT/SETTINGS/LOGOUT)");
            answer = scanner.nextLine();

            if (answer.equalsIgnoreCase("SEND")) {
                System.out.println("Do you wish to start a group chat? (Y/N)");
                answer = scanner.nextLine();

                if (answer.equalsIgnoreCase("Y")) {
                    chooseRecipients(user.getUserName(), userList);
                } else {
                    chooseRecipient(user.getUserName(), userList);
                }
            } else if (answer.equalsIgnoreCase("CHECK")) {
                checkMessages(user);
            } else if (answer.equalsIgnoreCase("CHAT")) {
                user.collectConversations();
                user.printConversations();

                MessagingMethods r = new MessagingMethods();
                new Thread(r).start();
                r.checkNewMessages(user);

                MessagingMethods s = new MessagingMethods();
                new Thread(s).start();
                s.sendMessages(user);

                //chat(user);
            } else if (answer.equalsIgnoreCase("SETTINGS")) {
                System.out.println("Feature under construction :)");
            } else if (answer.equalsIgnoreCase("LOGOUT")) {
                System.out.println("\nGoodbye, " + user.getUserName() + "!");
                break;
            } else {
                System.out.println("Unknown command. Please try again!");
            }
        }
    }
}
