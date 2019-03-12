package jvm;

import java.util.Scanner;

import static jvm.MessagingMethods.*;
import static jvm.InitialProcesses.*;
import static jvm.UserList.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String answer;
        boolean exitCondition = false;
        populateUserList();
        User user = registerAndLogIn();

        //Chat window
        if (user.getConversations().size() >= 1) {
            printEqualLengthMenuLine(" CHAT WINDOW ");
            user.printConversations();
            printEqualLengthMenuLine(" CHAT NUMBER|NEW CHAT (+)|MENU (-) ");
            answer = scanner.nextLine();

            if (!answer.equals("+") && !answer.equals("-") && Integer.parseInt(answer) <= user.getConversations().size()) {
                chat(user, String.valueOf(Integer.parseInt(answer)));
                answer = "-";
            }
        } else {
            answer = "-";
        }

        //Main processes
        while (!exitCondition) {
            switch (answer) {
                case "-":
                    System.out.println();
                    printEqualLengthMenuLine(" MAIN MENU ");
                    printEqualLengthMenuLine(" CHECK MESSAGES (1)|CHAT (2)|SETTINGS (3)|LOGOUT (4) ");
                    answer = scanner.nextLine();
                    break;
                case "+":
                    printEqualLengthMenuLine(" CHAT WINDOW ");
                    user.collectConversations();
                    user.printConversations();
                    chat(user, answer);
                    answer = "-";
                    break;
                case "1":
                    checkMessages(user);
                    answer = "-";
                    break;
                case "2":
                    chat(user, "");
                    answer = "-";
                    break;
                case "3":
                    System.out.println("Feature under construction :)\n");
                    answer = "-";
                    break;
                case "4":
                    printEqualLengthMenuLine(" SESSION TERMINATED ");
                    exitCondition = true;
                    break;
                default:
                    System.out.println("\nUnknown command. Please try again!\n");
                    answer = "-";
            }
        }
    }
}
