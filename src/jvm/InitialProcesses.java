package jvm;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import static jvm.MessagingMethods.*;
import static jvm.MessagingMethods.printEqualLengthMenuLine;

class InitialProcesses {

    static void registerAndLogIn(Map<String, String> userList) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String answer, menuText;

        //Welcome the user and provide initial options
        menuText =  " REGISTER (1)|LOGIN (2) ";
        System.out.println("Welcome to Java Virtual Messenger!\n");
        printWelcomeText();
        printEqualLengthMenuLine(menuText);
        answer = scanner.nextLine();

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
    }
}
