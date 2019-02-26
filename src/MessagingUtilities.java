import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

public class MessagingUtilities {

    void composeMessage(String userName, Map<String, String> userList) throws IOException {
        String recipient, message = "";
        MessengerUtilities messUtil = new MessengerUtilities();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Whom do you wish to contact?");
        recipient = scanner.next();

        while (!messUtil.checkForKey(userList, recipient)) {
            System.out.println("Such user doesn't exist! Please enter another.");
            recipient = scanner.next();
        }

        Path messagesPath = Paths.get(messUtil.createMessageTxtFile(userName, recipient, "-", ".txt"));

        System.out.println("Please enter your message:");
        while (message.equalsIgnoreCase("")) {
            message = scanner.nextLine();
        }

        MessagingUtilities messagingUtilities = new MessagingUtilities();
        messagingUtilities.saveMessageToCSV(userName, message, messagesPath);
    }

    private void saveMessageToCSV(String userName, String message, Path messagesPath) throws IOException {
        MessengerUtilities messUtil = new MessengerUtilities();
        messUtil.createTextFileIfNotCreated(messagesPath);

        FileWriter writer = new FileWriter(String.valueOf(messagesPath), true);
        writer.append("\n");
        writer.append(userName + " says:\n");
        writer.append(message);
        writer.close();
    }
}
