import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class MessagingUtilities {
    FinalClass finalClass = new FinalClass();

    void composeMessage(String userName, Map<String, String> userList, ArrayList<String> recipients, boolean groupChat, boolean replying) throws IOException {
        String message, answer, participant, groupName = "";
        Path messagesPath;
        MessengerUtilities messUtil = new MessengerUtilities();
        Scanner scanner = new Scanner(System.in);

        if (!replying) {
            //Private messaging
            if (!groupChat) {
                System.out.println("Whom do you wish to contact?");
                participant = scanner.nextLine();

                while (true) {
                    if (!messUtil.checkForKey(userList, participant)) {
                        System.out.println("Such user doesn't exist! Please enter another.");
                        participant = scanner.nextLine();
                    } else {
                        break;
                    }
                }

                recipients.add(participant);
                messagesPath = Paths.get(messUtil.createMessageTxtFileIfNotCreated
                        (userName, recipients.get(0), ".txt"));
            } else {
                //Group messaging
                System.out.println("Please enter all the participants of the group chat one by one: (type " + "\"END\"" + " to end the list)");

                do {
                    participant = scanner.nextLine();

                    while (!participant.equalsIgnoreCase("END") && !messUtil.checkForKey(userList, participant)) {
                        System.out.println("Such user doesn't exist! Please enter another.");
                        participant = scanner.nextLine();
                    }

                    if (!participant.equalsIgnoreCase("END")) recipients.add(participant);
                } while (!participant.equalsIgnoreCase("END"));

                messagesPath = Paths.get(messUtil.searchForFileNameContainingSubstring(recipients, userName, false));
                groupName = String.valueOf(messagesPath).replace(".txt", "");
            }
        } else {
            if (!groupChat) {
                messagesPath = Paths.get(messUtil.createMessageTxtFileIfNotCreated
                        (userName, recipients.get(0), ".txt"));
            } else {
                messagesPath = Paths.get(messUtil.searchForFileNameContainingSubstring(recipients, userName, true));
                groupName = String.valueOf(messagesPath).replace(".txt", "");
            }
        }

        while (true) {
            System.out.println("\nPlease enter your message:");
            message = scanner.nextLine();

            MessagingUtilities messagingUtilities = new MessagingUtilities();
            String currentTimeStamp = createTimeStamp();
            messagingUtilities.saveMessageToCSV(userName, message, messagesPath, currentTimeStamp);
            messagingUtilities.saveSenderAndTimeStampToNewMessageLog(userName, recipients, currentTimeStamp, groupChat, groupName);

            System.out.println("\nDo you want to send another? (Y/N)");
            answer = scanner.nextLine();

            if (!answer.equalsIgnoreCase("Y")) break;
        }
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

    private void saveSenderAndTimeStampToNewMessageLog(String userName, ArrayList<String> recipients, String timeStamp, boolean groupChat, String groupName) throws IOException {
        MessengerUtilities messUtil = new MessengerUtilities();

        for (String recipient : recipients) {
            Path newMessageLogPath = Paths.get(recipient + finalClass.NEW_MESSAGE_LOG_SUFFIX);
            int resultOfFileCreation = messUtil.createTextFileIfNotCreated(newMessageLogPath);

            FileWriter writer = new FileWriter(String.valueOf(newMessageLogPath), true);

            if (resultOfFileCreation == 0) {
                writer.append("\n");
            }

            if (groupChat) {
                writer.append(groupName);
            } else {
                writer.append(userName);
            }

            writer.append(finalClass.CSV_DELIMITER);
            writer.append(timeStamp);

            if (groupChat) {
                writer.append(finalClass.CSV_DELIMITER);
                writer.append("G");
            }

            writer.close();
        }
    }

    private String createTimeStamp() {
        String timeStampPattern = "yyyy-MM-dd HH:mm:ss.SSS";
        SimpleDateFormat sdfDate = new SimpleDateFormat(timeStampPattern);
        Date now = new Date();
        return sdfDate.format(now);
    }

    private List<String> getNewMessagesLog(String userName) throws IOException {
        MessengerUtilities messUtil = new MessengerUtilities();
        Path newMessageLogPath = Paths.get(userName + finalClass.NEW_MESSAGE_LOG_SUFFIX);
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

    void checkMessages(User user, Map<String, String> userList) throws IOException, ParseException {
        String answer = "", answer2, onlyPersonWhoSentMessage = "", groupIdentifier = "", userMessageFilePath;
        MessengerUtilities messUtil = new MessengerUtilities();
        MessagingUtilities messagingUtilities = new MessagingUtilities();
        Scanner scanner = new Scanner(System.in);
        String numberOfMessagesSuffix;

        List<String> newMessagesLogList = messagingUtilities.getNewMessagesLog(user.getUserName());

        //Get a list of new messages by sender followed by the amount of new messages sent from them
        Map<String, Integer> newMessagesBySender = messagingUtilities.countNumberOfNewMessages(newMessagesLogList, finalClass.CSV_DELIMITER);

        if (newMessagesBySender.size() > 0) {
            System.out.println("\nYou have new message(s) from:");
            for (Map.Entry<String, Integer> entry : newMessagesBySender.entrySet()) {
                if (entry.getValue() > 1) {
                    numberOfMessagesSuffix = "s";
                } else {
                    numberOfMessagesSuffix = "";
                }

                groupIdentifier = "";
                if (entry.getKey().contains("-")) groupIdentifier = "Group";

                System.out.println(entry.getKey() + " (" + entry.getValue() + " message" + numberOfMessagesSuffix + ") " + groupIdentifier);

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
            String firstUnreadMessage = messagingUtilities.getEarliestUnreadMessageTime(answer, newMessagesLogList);
            Date date2 = new SimpleDateFormat(timeStampPattern).parse(firstUnreadMessage);

            ArrayList<String> groupName = new ArrayList<>();
            groupName.add(answer);

            if (!groupIdentifier.equals("")) {
                //Get the path of the current group's message file
                userMessageFilePath = messUtil.searchForFileNameContainingSubstring(groupName, user.getUserName(), true);
            } else {
                //Get the path of the current user's message file
                userMessageFilePath = messUtil.createMessageTxtFileIfNotCreated(user.getUserName(), answer, ".txt");
            }

            //Find the messages and print them out one after another
            fetchNewMessages(date2, userMessageFilePath, timeStampPattern);

            //Remove current sender's logs from user's NewMessageLog, because the messages have now been read
            removeSendersLinesFromNewMessageLog(answer, Paths.get(user.getUserName() + finalClass.NEW_MESSAGE_LOG_SUFFIX));

            System.out.println("\nWould you like to reply? (Y/N)");
            answer2 = scanner.next();

            if (answer2.equalsIgnoreCase("Y")) {
                if (!groupIdentifier.equals("")) {
                    //Compose a reply for a group
                    composeMessage(user.getUserName(), userList, groupName, true, true);
                } else {
                    //Compose a reply for a single user
                    ArrayList<String> tempRecipient = new ArrayList<>();
                    tempRecipient.add(answer);
                    composeMessage(user.getUserName(), userList, tempRecipient, false, true);
                }
            }
        } else {
            System.out.println("\nYou don't have any new messages!");
        }
    }

    private void removeSendersLinesFromNewMessageLog(String sender, Path newMessageLogPath) throws IOException {
        File inputFile = new File(String.valueOf(newMessageLogPath));
        File tempFile = new File("temp" + newMessageLogPath);

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;
        String[] splitLines;

        while ((currentLine = reader.readLine()) != null) {
            splitLines = currentLine.split(finalClass.CSV_DELIMITER);
            //Rewrite all lines that don't contain the sender's name
            if (!splitLines[0].equals(sender)) {
                writer.write(currentLine);
                writer.append("\n");
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

    private String getEarliestUnreadMessageTime(String sender, List<String> newMessageLogList) {
        for (String line : newMessageLogList) {
            String[] splitLine = line.split(finalClass.CSV_DELIMITER);

            if (splitLine[0].equals(sender)) {
                return splitLine[1];
            }
        }
        return null;
    }
}
