import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        String enteredUserName;
        String enteredPassWord;
        String answer;
        String userListName = "MessengerUserList.txt";
        String csvSplitBy = ";";
        boolean uniqueUserName = false;

        MessengerUtilities messUtil = new MessengerUtilities();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Java Virtual Messenger!\n" + "Please register or log in. (REG/LOG)");
        answer = scanner.next();

        if (answer.equalsIgnoreCase("REG")) {
            messUtil.createTextFileIfNotCreated(userListName);
            System.out.println("Please enter your desired user name:");
            enteredUserName = scanner.next();

            do {
                //Check if user name already exists
                if (messUtil.checkStringExistsInCSV(enteredUserName, csvSplitBy, userListName)) {
                    System.out.println("The chosen user name already exists, please choose another!");
                    enteredUserName = scanner.next();
                } else {
                    System.out.println("Your user name is: " + enteredUserName);

                    //Ask for password
                    System.out.println("Please provide a password:");
                    enteredPassWord = scanner.next();

                    //Enter the new user name and password to the user list
                    messUtil.appendToCSV(enteredUserName + csvSplitBy + enteredPassWord, userListName);
                    uniqueUserName = true;
                }
            } while (!uniqueUserName);
        } else {
            System.out.println("Please enter your user name:");
            enteredUserName = scanner.next();

            while(!messUtil.checkStringExistsInCSV(enteredUserName, csvSplitBy, userListName, 0)) {
                System.out.println("Please enter password:");

                while(!messUtil.checkStringExistsInCSV(enteredUserName, csvSplitBy, userListName, 1)) {
                    
                }
            }
        }

        //User user = new User(enteredUserName, enteredPassWord);
    }
}
