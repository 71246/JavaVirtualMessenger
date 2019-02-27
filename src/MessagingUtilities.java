import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        int resultOfFileCreation = messUtil.createTextFileIfNotCreated(messagesPath);

        FileWriter writer = new FileWriter(String.valueOf(messagesPath), true);
        if (resultOfFileCreation == 0) {
            writer.append("\n");
        }

        writer.append("<timestamp>");
        writer.append(timeStamp);
        writer.append("\n");
        writer.append(userName);
        writer.append(" says:\n");
        writer.append(message);
        writer.close();
    }

    private void saveSenderAndTimeStampToNewMessageLog(String userName, String recipient, String delimiter, String timeStamp) throws IOException {
        MessengerUtilities messUtil = new MessengerUtilities();
        Path newMessageLogPath = Paths.get(recipient + newMessageLogSuffix);
        int resultOfFileCreation = messUtil.createTextFileIfNotCreated(newMessageLogPath);

        FileWriter writer = new FileWriter(String.valueOf(newMessageLogPath), true);
        if (resultOfFileCreation == 0) {
            writer.append("\n");
        }

        writer.append(userName);
        writer.append(delimiter);
        writer.append(timeStamp);
        writer.close();
    }

    private String createTimeStamp() {
        String timeStampPattern = "yyyy-MM-dd HH:mm:ss.SSS";
        SimpleDateFormat sdfDate = new SimpleDateFormat(timeStampPattern);
        Date now = new Date();
        return sdfDate.format(now);
    }

    private List<String> getNewMessagesLog(String userName) throws IOException {
        MessengerUtilities messUtil = new MessengerUtilities();
        Path newMessageLogPath = Paths.get(userName + newMessageLogSuffix);
        int resultOfFileCreation = messUtil.createTextFileIfNotCreated(newMessageLogPath);
        return Files.readAllLines(Paths.get(String.valueOf(newMessageLogPath)));
    }

    private Map<String, Integer> countNumberOfNewMessages(List<String> newMessageLogList, String delimiter) {
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

    void checkMessages(User user, String delimiter) throws IOException, ParseException {
        MessengerUtilities messUtil = new MessengerUtilities();
        MessagingUtilities messagingUtilities = new MessagingUtilities();
        Scanner scanner = new Scanner(System.in);
        String numberOfMessagesSuffix;

        List<String> newMessagesLogList = messagingUtilities.getNewMessagesLog(user.getUserName());
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
                return;
            }

            String timeStampPattern = "yyyy-MM-dd HH:mm:ss.SSS";
            String firstUnreadMessage = messagingUtilities.getEarliestUnreadMessageTime(answer, newMessagesLogList, delimiter);
            Date date2 = new SimpleDateFormat(timeStampPattern).parse(firstUnreadMessage);

            String userMessageFilePath = messUtil.createMessageTxtFileIfNotCreated(user.getUserName(), answer, "-", ".txt");
            fetchNewMessages(date2, userMessageFilePath, timeStampPattern);
        } else {
            System.out.println("You don't have any new messages!");
        }
    }

    private void fetchNewMessages(Date firstUnreadMessage, String filePath, String timeStampPattern) throws IOException, ParseException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("<timestamp>") ) {
                Date dateOfMessage = new SimpleDateFormat(timeStampPattern).parse(lines.get(i).replace("<timestamp>", ""));
                if (dateOfMessage.compareTo(firstUnreadMessage) <= 0) {
                    if (!lines.get(i).contains("<timestamp>") && !lines.get(i).equals("")) {
                        System.out.println(lines.get(i));
                    }
                }
            }
        }
    }

    private String getEarliestUnreadMessageTime(String sender, List<String> newMessageLogList, String delimiter) {
        for (int i = 0; i < newMessageLogList.size(); i++) {
            String[] splitLine = newMessageLogList.get(i).split(delimiter);

            if (splitLine[0].equals(sender)) {
                 return splitLine[1];
            }
        }
        return null;
    }
}
