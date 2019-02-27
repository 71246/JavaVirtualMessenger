import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;

public class MessagingUtilities {
    private String newMessageLogSuffix = "-NewMessageLog.txt";

    void composeMessage(String userName, Map<String, String> userList, String delimiter) throws IOException {
        String recipient, message = "";
        MessengerUtilities messUtil = new MessengerUtilities();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Whom do you wish to contact?");
        recipient = scanner.next();

        while (!messUtil.checkForKey(userList, recipient)) {
            System.out.println("Such user doesn't exist! Please enter another.");
            recipient = scanner.next();
        }

        Path messagesPath = Paths.get(messUtil.createMessageTxtFileIfNotCreated(userName, recipient, "-", ".txt"));

        System.out.println("Please enter your message:");
        while (message.equalsIgnoreCase("")) {
            message = scanner.nextLine();
        }

        MessagingUtilities messagingUtilities = new MessagingUtilities();
        String currentTimeStamp = createTimeStamp();
        messagingUtilities.saveMessageToCSV(userName, message, messagesPath, currentTimeStamp);
        messagingUtilities.saveSenderAndTimeStampToNewMessageLog(userName, recipient, delimiter, currentTimeStamp);
    }

    private void saveMessageToCSV(String userName, String message, Path messagesPath, String timeStamp) throws IOException {
        MessengerUtilities messUtil = new MessengerUtilities();
        messUtil.createTextFileIfNotCreated(messagesPath);

        FileWriter writer = new FileWriter(String.valueOf(messagesPath), true);
        writer.append("\n");
        writer.append(timeStamp);
        writer.append("\n");
        writer.append(userName);
        writer.append(" says:\n");
        writer.append(message);
        writer.append("\n");
        writer.close();
    }

    private void saveSenderAndTimeStampToNewMessageLog(String userName, String recipient, String delimiter, String timeStamp) throws IOException {
        MessengerUtilities messUtil = new MessengerUtilities();
        Path newMessageLogPath = Paths.get(recipient + newMessageLogSuffix);
        messUtil.createTextFileIfNotCreated(newMessageLogPath);

        FileWriter writer = new FileWriter(String.valueOf(newMessageLogPath), true);
        writer.append("\n");
        writer.append(userName);
        writer.append(delimiter);
        writer.append(timeStamp);
        writer.close();
    }

    private String createTimeStamp() {
        return String.valueOf(ZonedDateTime.now());
    }

    List<String> getNewMessagesLog(String userName, String delimiter) throws IOException {
        List<String> newMessageLogList = new ArrayList<>();
        MessengerUtilities messUtil = new MessengerUtilities();
        Path newMessageLogPath = Paths.get(userName + newMessageLogSuffix);

        messUtil.createTextFileIfNotCreated(newMessageLogPath);

        BufferedReader br = new BufferedReader(new FileReader(String.valueOf(newMessageLogPath)));
        String line;

        while ((line = br.readLine()) != null) {
            newMessageLogList.add(line);
        }

        return newMessageLogList;
    }

    Map<String, Integer> countNumberOfNewMessages(List<String> newMessageLogList, String delimiter) {
        Map<String, Integer> newMessagesBySender = new HashMap<>();

        for (String item: newMessageLogList) {
            String[] splitLine = item.split(delimiter);

            if (newMessagesBySender.containsKey(splitLine[0])) {
                newMessagesBySender.put(splitLine[0], newMessagesBySender.get(splitLine[0]) + 1);
            } else {
                newMessagesBySender.put(splitLine[0], 1);
            }
        }
        return newMessagesBySender;
    }

    void checkMessages(User user, String delimiter) throws IOException {
        MessagingUtilities messagingUtilities = new MessagingUtilities();
        Scanner scanner = new Scanner(System.in);
        String numberOfMessagesSuffix;

        List<String> newMessagesLogList = messagingUtilities.getNewMessagesLog(user.getUserName(), delimiter);
        Map<String, Integer> newMessagesBySender = messagingUtilities.countNumberOfNewMessages(newMessagesLogList, delimiter);

        if (newMessagesBySender.size() > 0) {
            System.out.println("You have new message(s) from:");
            for (Map.Entry<String, Integer> entry : newMessagesBySender.entrySet()) {
                if (entry.getValue() > 1) {
                    numberOfMessagesSuffix = "s";
                } else {
                    numberOfMessagesSuffix = "";
                }
                System.out.println(entry.getKey() + " (" + entry.getValue() + " message" + numberOfMessagesSuffix + ")");
            }

            System.out.println("Whose message would you like to read?");
            String answer = scanner.next();

            if (!newMessagesBySender.containsKey(answer)) {
                System.out.println("You don't have any message from that user!");
            }

            String firstUnreadMessage = messagingUtilities.getEarliestUnreadMessageTime(answer, newMessagesLogList, delimiter);
            System.out.println(firstUnreadMessage);
        } else {
            System.out.println("You don't have any new messages!");
        }
    }

    void fetchMessages(String sender, String timeStamp) {

    }

    String getEarliestUnreadMessageTime(String sender, List<String> newMessageLogList, String delimiter) {
        for (int i = 0; i < newMessageLogList.size(); i++) {
            String[] splitLine = newMessageLogList.get(i).split(delimiter);

            if (splitLine[0].equals(sender)) {
                 return splitLine[1];
            }
        }
        return null;
    }
}
