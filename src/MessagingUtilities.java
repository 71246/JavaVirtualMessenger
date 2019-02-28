import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class MessagingUtilities {
    private final String newMessageLogSuffix = "-NewMessageLog.txt";

    void composeMessage(String userName, Map<String, String> userList, String delimiter, String recipient) throws IOException {
        String message = "";
        MessengerUtilities messUtil = new MessengerUtilities();
        Scanner scanner = new Scanner(System.in);

        if (recipient.equals("")) {
            System.out.println("Whom do you wish to contact?");
            recipient = scanner.next();

            while (!messUtil.checkForKey(userList, recipient)) {
                System.out.println("Such user doesn't exist! Please enter another.");
                recipient = scanner.next();
            }
        }

        Path messagesPath = Paths.get(messUtil.createMessageTxtFileIfNotCreated(userName, recipient, "-", ".txt"));

        System.out.println("\nPlease enter your message:");
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

        for (String item : newMessageLogList) {
            String[] splitLine = item.split(delimiter);

            //No empty lines in the list!!!
            if (!splitLine[0].equals("")) {
                if (newMessagesBySender.containsKey(splitLine[0])) {
                    newMessagesBySender.put(splitLine[0], newMessagesBySender.get(splitLine[0]) + 1);
                } else {
                    newMessagesBySender.put(splitLine[0], 1);
                }
            }
        }
        return newMessagesBySender;
    }

    void checkMessages(User user, Map<String, String> userList, String delimiter) throws IOException, ParseException {
        String answer = "", answer2, onlyPersonWhoSentMessage = "";
        MessengerUtilities messUtil = new MessengerUtilities();
        MessagingUtilities messagingUtilities = new MessagingUtilities();
        Scanner scanner = new Scanner(System.in);
        String numberOfMessagesSuffix;

        List<String> newMessagesLogList = messagingUtilities.getNewMessagesLog(user.getUserName());
        Map<String, Integer> newMessagesBySender = messagingUtilities.countNumberOfNewMessages(newMessagesLogList, delimiter);

        if (newMessagesBySender.size() > 0) {
            System.out.println("\nYou have new message(s) from:");
            for (Map.Entry<String, Integer> entry : newMessagesBySender.entrySet()) {
                if (entry.getValue() > 1) {
                    numberOfMessagesSuffix = "s";
                } else {
                    numberOfMessagesSuffix = "";
                }
                System.out.println(entry.getKey() + " (" + entry.getValue() + " message" + numberOfMessagesSuffix + ")");

                if (newMessagesBySender.size() == 1 && onlyPersonWhoSentMessage.equals("")) {
                    onlyPersonWhoSentMessage = entry.getKey();
                }
            }

            if (newMessagesBySender.size() > 1) {
                System.out.println("\nWhose message would you like to read?");
                answer = scanner.next();

                if (!newMessagesBySender.containsKey(answer)) {
                    System.out.println("\nYou don't have any messages from that user!");
                    return;
                }
            } else if (newMessagesBySender.size() == 1) {
                System.out.println("\nWould you like to read them? (Y/N)");
                answer2 = scanner.next();

                if (answer2.equalsIgnoreCase("Y")) {
                    answer = onlyPersonWhoSentMessage;
                    System.out.println();
                } else {
                    return;
                }
            }

            //Set the timestamp format
            String timeStampPattern = "yyyy-MM-dd HH:mm:ss.SSS";

            //Determine the time of the first unread message by chosen sender
            String firstUnreadMessage = messagingUtilities.getEarliestUnreadMessageTime(answer, newMessagesLogList, delimiter);
            Date date2 = new SimpleDateFormat(timeStampPattern).parse(firstUnreadMessage);

            //Get the path of the current user's message file
            String userMessageFilePath = messUtil.createMessageTxtFileIfNotCreated(user.getUserName(), answer, "-", ".txt");

            //Find the messages and print them out one after another
            fetchNewMessages(date2, userMessageFilePath, timeStampPattern);

            //Remove current sender's logs from user's NewMessageLog, because the messages have now been read
            removeSendersLinesFromNewMessageLog(answer, Paths.get(user.getUserName() + newMessageLogSuffix), delimiter);

            System.out.println("\nWould you like to reply? (Y/N)");
            answer2 = scanner.next();

            if (answer2.equalsIgnoreCase("Y")) {
                composeMessage(user.getUserName(), userList, delimiter, answer);
            }
        } else {
            System.out.println("\nYou don't have any new messages!");
        }
    }

    private void removeSendersLinesFromNewMessageLog(String sender, Path newMessageLogPath, String delimiter) throws IOException {
        File inputFile = new File(String.valueOf(newMessageLogPath));
        File tempFile = new File("temp" + newMessageLogPath);

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;
        String[] splitLines;

        while((currentLine = reader.readLine()) != null) {
            splitLines = currentLine.split(delimiter);
            //Rewrite all lines that don't contain the sender's name
            if(!splitLines[0].equals(sender)) {
                writer.write(currentLine);
            }
        }

        writer.close();
        reader.close();

        //Delete the original
        inputFile.delete();

        //Rename the temp to original
        tempFile.renameTo(inputFile);
    }

    private void fetchNewMessages(Date firstUnreadMessage, String filePath, String timeStampPattern) throws IOException, ParseException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        //First loop determines the first/earliest message to be shown
        for (int i = 0; i < lines.size(); i++) {
            //Look for "<timestamp>" tag
            if (lines.get(i).contains("<timestamp>")) {
                //Parse the String into a Date
                Date dateOfCurrentMessage = new SimpleDateFormat(timeStampPattern).parse(lines.get(i).replace("<timestamp>", ""));
                //Check if the parsed Date is earlier than firstUnreadMessage
                if (dateOfCurrentMessage.compareTo(firstUnreadMessage) == 0) {
                    //Print out all remaining lines, except the lines where timestamp tag is present
                    for (int c = i; c < lines.size(); c++) {
                        if (!lines.get(c).contains("<timestamp>")) {
                            System.out.println(lines.get(c));
                        }
                    }
                    break;
                }
            }
        }
    }

    private String getEarliestUnreadMessageTime(String sender, List<String> newMessageLogList, String delimiter) {
        for (String line: newMessageLogList) {
            String[] splitLine = line.split(delimiter);

            if (splitLine[0].equals(sender)) {
                return splitLine[1];
            }
        }
        return null;
    }
}
