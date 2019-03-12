package jvm;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static jvm.UserList.*;

class MessagingMethods {

    private static void chooseRecipients(User user) {
        String enteredParticipant;
        List<String> recipients = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        printEqualLengthMenuLine(" ENTER RECIPIENTS AND \"/END\" ");
        enteredParticipant = scanner.nextLine();

        while (true) {
            if (enteredParticipant.equalsIgnoreCase("/END")) {
                break;
            } else if (isUserNotInUserList(enteredParticipant)) {
                System.out.println("Invalid user name! Please enter another:");
                enteredParticipant = scanner.nextLine();
            } else if (enteredParticipant.equals(user.getUserName())) {
                System.out.println("There's no need to do that... Try again!");
                enteredParticipant = scanner.nextLine();
            } else {
                //Valid user name
                recipients.add(enteredParticipant);
                enteredParticipant = scanner.nextLine();
            }
        }

        Integer conversationIndex = checkIfConversationHasAlreadyBeenCreated(user, recipients);

        if (conversationIndex < 0) {
            addToConversationsFile(new Conversation(createConversationName(recipients), "", 0), recipients);
            conversationIndex = checkIfConversationHasAlreadyBeenCreated(user, recipients);
        }

        user.collectConversations();
        chat(user, String.valueOf(conversationIndex));
    }

    private static void addToConversationsFile(Conversation conversation, List<String> recipients) {
        FileWriter writer;
        String recipientError;

        for (String recipient: recipients) {
            recipientError = recipient;
            try {
                createConversationFile(recipient + FinalClass.USER_CONVERSATIONS_PATH_SUFFIX);
                writer = new FileWriter(recipient + FinalClass.USER_CONVERSATIONS_PATH_SUFFIX, true);

                writer.append("\n");
                writer.append(conversation.getName());
                writer.append(FinalClass.CSV_DELIMITER);
                writer.append(conversation.getAlias());
                writer.append(FinalClass.CSV_DELIMITER);
                writer.append(String.valueOf(conversation.getId()));
                writer.close();
            } catch (IOException e) {
                printEqualLengthMenuLine(" ERROR MESSAGE ");
                System.out.println("A problem occurred while trying to write to " + recipientError + "'s conversation file!");
            }
        }
    }

    static void createNewMessageLogFile(String newMessageLogFileName) {
        createTextFile(Paths.get(newMessageLogFileName));
    }

    static void createConversationFile(String conversationFilePath) {
        if (createTextFile(Paths.get(conversationFilePath))) {

            try {
                FileWriter writer = new FileWriter(conversationFilePath, true);
                writer.append("name;alias;id");
                writer.close();
            } catch (IOException e) {
                printEqualLengthMenuLine(" ERROR MESSAGE ");
                System.out.println("A problem occurred while trying to create conversation file!");

            }
        }
    }

    private static String createConversationName(List<String> participants) {
        StringBuilder result = new StringBuilder(participants.get(0));

        for (int i = 1; i < participants.size(); i++) {
            result.append(FinalClass.FILE_NAME_DELIMITER_DASH);
            result.append(participants.get(i));
        }
        return String.valueOf(result);
    }

    private static Integer checkIfConversationHasAlreadyBeenCreated(User user, List<String> participants) {
        //Returns -1 if new conversation, index in conversation list if is existing

        try {
            if (!participants.contains(user.getUserName())) participants.add(user.getUserName());
            List<String> lines = Files.readAllLines(Paths.get(user.getConversationFilePath()));
            String[] splitLine;
            int numberOfParticipants;
            boolean found;

            for (int i = 1; i < lines.size(); i++) {
                splitLine = lines.get(i).split(FinalClass.CSV_DELIMITER);
                numberOfParticipants = splitLine[0].split(FinalClass.FILE_NAME_DELIMITER_DASH).length;
                found = true;

                for (String participant: participants) {
                    if (!splitLine[0].contains(participant)) {
                        found = false;
                        break;
                    }
                }

                if (found && participants.size() == numberOfParticipants) {
                    return i;
                }
            }

            return -1;
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred while trying to read from " + user.getConversationFilePath() + "!");
        }

        return -1;
    }

    static boolean createTextFile(Path filePath) {
        File file = new File(String.valueOf(filePath));

        try {
            return file.createNewFile();
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("There was a problem when creating file " + filePath + "!");
            return false;
        }
    }

    private static void saveMessageToTextFile(User user, String message, String timeStamp) {
        FileWriter writer;

        try {
            writer = new FileWriter(String.valueOf(user.getCurrentConversation().getFileName()), true);
            writer.append("\n");
            writer.append(FinalClass.TIME_STAMP_TAG);
            writer.append(timeStamp);
            writer.append("\n");
            writer.append(user.getUserName());
            writer.append(" says:\n");
            writer.append(message);
            writer.close();
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred whiles trying to write to " + user.getCurrentConversation().getFileName() + "!");
        }
    }

    private static String createTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat(FinalClass.TIME_STAMP_PATTERN);
        Date now = new Date();
        return sdfDate.format(now);
    }

    synchronized static List<String> getNewMessagesLog(User user) {
        try {
            return Files.readAllLines(Paths.get(String.valueOf(user.getNewMessageLogFileName())));
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred while trying to write to " + user.getNewMessageLogFileName() + "!");
            return Collections.emptyList();
        }
    }

    static List<String> countNumberOfNewMessagesBySenderAndConversationName(List<String> newMessageLogList) {
        List<String> newMessagesBySender = new ArrayList<>();
        String stringToSearch, stringToCompareTo;
        int amount;
        boolean found = false;

        for (String item : newMessageLogList) {
            if (!item.isEmpty()) {
                String[] splitLine = item.split(FinalClass.CSV_DELIMITER);
                stringToSearch = splitLine[0] + FinalClass.CSV_DELIMITER + splitLine[1];

                if (newMessagesBySender.size() == 0) {
                    newMessagesBySender.add(stringToSearch + FinalClass.CSV_DELIMITER + 1);
                    found = true;
                } else {
                    for (int i = 0; i < newMessagesBySender.size(); i++) {
                        found = false;
                        String[] splitLine2 = newMessagesBySender.get(i).split(FinalClass.CSV_DELIMITER);
                        stringToCompareTo = splitLine2[0] + FinalClass.CSV_DELIMITER + splitLine2[1];

                        if (stringToCompareTo.equals(stringToSearch)) {
                            amount = Integer.parseInt(splitLine2[2]);
                            newMessagesBySender.set(i, splitLine2[0] + FinalClass.CSV_DELIMITER + splitLine2[1] + FinalClass.CSV_DELIMITER + Math.addExact(amount, 1));
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    newMessagesBySender.add(stringToSearch + FinalClass.CSV_DELIMITER + 1);
                }
            }
        }

        return newMessagesBySender;
    }

    private static void displayNewMessages(List<String> newMessagesList) {
        String numberOfMessagesSuffix;
        String[] splitLine;

        System.out.println("\nYou have new message(s) from:");
        for (int i = 0; i < newMessagesList.size(); i++) {
            splitLine = newMessagesList.get(i).split(FinalClass.CSV_DELIMITER);

            if (Integer.parseInt(splitLine[2]) > 1) {
                numberOfMessagesSuffix = "s";
            } else {
                numberOfMessagesSuffix = "";
            }

            System.out.println(i + 1 + ". " + splitLine[0] + " from chat " + splitLine[1] + " (" + splitLine[2] + " message" + numberOfMessagesSuffix + ") ");
        }
        System.out.println();
    }

    private static void fetchRecentMessages(User user) {
        String filename = user.getCurrentConversation().getName() + FinalClass.FILE_TYPE_SUFFIX;
        List<String> lines;
        int messageCounter = 0, numberOfMessages = 0;

        try {
            lines = Files.readAllLines(Paths.get(filename));

            //Count number of messages
            for (String line: lines) {
                if (line.contains(FinalClass.TIME_STAMP_TAG)) numberOfMessages++;
            }

            //Count backwards the amount of messages to load or print all messages if there's less messages in the file
            if (user.getAmountOfMessagesToShow() <= numberOfMessages) {
                //Start from the last message and count backwards until the specified number of messages to show is reached
                for (int i = lines.size() - 1; i >= 0; i--) {
                    if (lines.get(i).contains(FinalClass.TIME_STAMP_TAG)) {
                        messageCounter++;
                        if (messageCounter == user.getAmountOfMessagesToShow()) {
                            for (int c = i; c < lines.size(); c++) {
                                if (!lines.get(c).contains(FinalClass.TIME_STAMP_TAG)) {
                                    System.out.println(lines.get(c));
                                }
                            }
                            break;
                        }
                    }
                }
            } else {
                //Take all messages
                for (String line : lines) {
                    if (!line.contains(FinalClass.TIME_STAMP_TAG)) {
                        System.out.println(line);
                    }
                }
            }
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred while fetching recent messages!");
        }
    }

    static void chat(User user, String chosenOption) {
        Scanner scanner = new Scanner(System.in);
        String answer;

        //chosenOption = "": go into chat menu
        if (chosenOption.equals("")) {
            if (user.getConversations().size() != 0) {
                printEqualLengthMenuLine(" CHAT WINDOW ");
                user.printConversations();
                printEqualLengthMenuLine(" CHAT NUMBER|NEW CHAT (+)|MENU (-) ");
                chosenOption = scanner.nextLine();
            } else {
                printEqualLengthMenuLine(" NEW CHAT (+)|MENU (-) ");
                chosenOption = scanner.nextLine();
            }
        }

        if (chosenOption.equals("-")) return;

        //chosenOption = "+": start a new chat
        //chosenOption = existing conversation key: enter the existing chat
        if (chosenOption.equals("+")) {
            chooseRecipients(user);
        } else if(Integer.parseInt(chosenOption) <= user.getConversations().size()) {
            user.setCurrentConversation(user.getConversations().get(Integer.parseInt(chosenOption) - 1));

            printEqualLengthMenuLine(" " + user.getCurrentConversation().getName() + " ");
            printEqualLengthMenuLine(" /MENU|/CHAT ");

            List<List<String>> newMessagesForBothCurrentAndOther = separateNewMessagesByCurrentAndOther(user);

            if (newMessagesForBothCurrentAndOther.get(0) != null) {
                removeNewMessageLinesFromNewMessageLog(user, newMessagesForBothCurrentAndOther.get(0));
            }

            fetchRecentMessages(user);

            ThreadClass r = new ThreadClass(user);
            r.start();

            do {
                answer = scanner.nextLine();

                if (answer.equalsIgnoreCase("/MENU") || answer.equals("-")) {
                    break;
                } else if (answer.equalsIgnoreCase("/CHAT")) {
                    chat(user, "");
                    break;
                } else if (!answer.equals("")) {
                    composeMessage(user, answer);
                }
            } while (!answer.equalsIgnoreCase("/MENU"));

            user.setCurrentConversation(null);
        }
    }

    static void checkMessages(User user) {
        String answer;
        Scanner scanner = new Scanner(System.in);
        List<String> newMessagesLog = getNewMessagesLog(user);

        //Get a list of new messages by sender followed by the amount of new messages sent from that sender
        List<String> newMessagesBySender = countNumberOfNewMessagesBySenderAndConversationName(newMessagesLog);

        //Display the new message list
        if (newMessagesBySender.size() > 0) {
            displayNewMessages(newMessagesBySender);
            printEqualLengthMenuLine(" CHAT NUMBER ");
            answer = scanner.nextLine();

            Integer chatIndex = user.getConversationIndexByName(newMessagesBySender.get(Integer.parseInt(answer) - 1).split(FinalClass.CSV_DELIMITER)[1]);

            if (chatIndex < 0) {
                System.out.println("\nInvalid chat number!");
                return;
            }

            String[] splitLine = newMessagesBySender.get(Integer.parseInt(answer) - 1).split(FinalClass.CSV_DELIMITER);
            user.setCurrentConversation(user.findConversationByName(splitLine[1]));
            chat(user, String.valueOf(chatIndex + 1));
            //removeSendersLinesFromNewMessageLog(splitLine[0] + FinalClass.CSV_DELIMITER + splitLine[1]);
        } else {
            System.out.println("\nYou don't have any new messages!\n");
        }
    }

    private static void composeMessage(User user, String message) {
        String currentTimeStamp = createTimeStamp();
        saveMessageToTextFile(user, message, currentTimeStamp);
        saveSenderAndTimeStampToNewMessageLog(user, currentTimeStamp);
    }

    static void removeNewMessageLinesFromNewMessageLog(User user, List<String> linesToRemove) {
        List<String> newMessageLog;
        Path newMessageLogPath = Paths.get(user.getNewMessageLogFileName());
        boolean newContentFound;

        try {
            newMessageLog = Files.readAllLines(newMessageLogPath);
            List<String> newContent = new ArrayList<>();

            for (String line: newMessageLog) {
                newContentFound = true;

                for (String line2: linesToRemove) {
                    if (line.equals(line2)) {
                        newContentFound = false;
                        break;
                    }
                }

                if (newContentFound) {
                    newContent.add(line);
                }
            }

            FileWriter writer = new FileWriter(String.valueOf(newMessageLogPath));
            PrintWriter printer = new PrintWriter(writer);

            for (String line: newContent) {
                printer.write(line + "\n");
            }

            printer.close();
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred while trying to write to NML!");
        }
    }

    static String getEarliestUnreadMessageTime(List<String> newMessages) {
        return newMessages.get(0).split(FinalClass.CSV_DELIMITER)[2];
        /*
        for (String line : newMessages) {
            return line.split(FinalClass.CSV_DELIMITER)[2];
        }
        return null;*/
    }

    synchronized static List<List<String>> separateNewMessagesByCurrentAndOther(User user) {
        List<List<String>> result = new ArrayList<>();
        List<String> messagesForCurrentConversation = new ArrayList<>();
        List<String> messagesForOtherConversations = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(user.getNewMessageLogFileName()));
            String[] splitLine;

            for (String line: lines) {
                if (!line.equals("")) {
                    splitLine = line.split(FinalClass.CSV_DELIMITER);

                    if (splitLine[1].equals(user.getCurrentConversation().getName())) {
                        messagesForCurrentConversation.add(line);
                    } else {
                        messagesForOtherConversations.add(line);
                    }
                }
            }

            result.add(messagesForCurrentConversation);
            result.add(messagesForOtherConversations);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private static void saveSenderAndTimeStampToNewMessageLog(User user, String timeStamp) {
        FileWriter writer;
        List<String> recipients = user.getCurrentConversation().getRecipients(user.getUserName());

        try {
            for (String recipient: recipients) {
                writer = new FileWriter(recipient + FinalClass.NEW_MESSAGE_LOG_SUFFIX, true);

                writer.append("\n");
                writer.append(user.getUserName());
                writer.append(FinalClass.CSV_DELIMITER);
                writer.append(user.getCurrentConversation().getName());
                writer.append(FinalClass.CSV_DELIMITER);
                writer.append(timeStamp);
                writer.append(FinalClass.CSV_DELIMITER);
                writer.append("0");
                writer.close();
            }
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred whiles trying to write to " + user.getNewMessageLogFileName() + "!");
        }
    }

    private static int calculateNumberOfCharactersToMakeLineEqual(int menuTextLength) {
        return (FinalClass.MENU_LINE_LENGTH - menuTextLength) / 2;
    }

    private static StringBuilder printCertainAmountOfCharacters(int amountToPrint) {
        StringBuilder str = new StringBuilder();

        for (int i = 1; i <= amountToPrint; i++) {
            str.append(FinalClass.FILE_NAME_DELIMITER_DASH);
        }
        return str;
    }

    static void printEqualLengthMenuLine(String menuLineText) {
        String extraCharacter = "";
        if ((calculateNumberOfCharactersToMakeLineEqual(menuLineText.length()) * 2) + menuLineText.length() != FinalClass.MENU_LINE_LENGTH) {
            extraCharacter = FinalClass.FILE_NAME_DELIMITER_DASH;
        }
        System.out.println(printCertainAmountOfCharacters(calculateNumberOfCharactersToMakeLineEqual(menuLineText.length())) +
                menuLineText + printCertainAmountOfCharacters(calculateNumberOfCharactersToMakeLineEqual(menuLineText.length())) + extraCharacter);
    }

    static void printWelcomeText() {
        System.out.println("   __      _____ _    ___ ___  __  __ ___   _____ ___       ___   ____  __ ");
        System.out.println("   \\ \\    / / __| |  / __/ _ \\|  \\/  | __| |_   _/ _ \\   _ | \\ \\ / /  \\/  |");
        System.out.println("    \\ \\/\\/ /| _|| |_| (_| (_) | |\\/| | _|    | || (_) | | || |\\ V /| |\\/| |");
        System.out.println("     \\_/\\_/ |___|____\\___\\___/|_|  |_|___|   |_| \\___/   \\__/  \\_/ |_|  |_|");
        System.out.println();
    }
}