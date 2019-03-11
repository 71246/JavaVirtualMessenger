package jvm;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static jvm.UserList.*;

class MessagingMethods {

    private static void chooseRecipients(User user) {
        String enteredParticipant;
        List<String> recipients = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        //System.out.println("Whom do you wish to contact?\nYou can enter one name or several (\"/END\" to end).");
        printEqualLengthMenuLine(" ENTER RECIPIENTS AND \"/END\" ");
        enteredParticipant = scanner.nextLine();

        while (true) {
            if (enteredParticipant.equalsIgnoreCase("/END")) {
                break;
            } else if (isUserNameInvalid(enteredParticipant)) {
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

        Integer doesConversationExist = checkIfConversationHasAlreadyBeenCreated(user, recipients);

        if (doesConversationExist <= 0) {
            addToConversationsFile(user, new Conversation(createConversationName(user, recipients), "", 0), recipients);
        }

        user.collectConversations();
        chat(user, String.valueOf(doesConversationExist));
    }

    private static void addToConversationsFile(User user, Conversation conversation, List<String> recipients) {
        recipients.add(user.getUserName());
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

    private static String createConversationName(User user, List<String> participants) {
        StringBuilder result = new StringBuilder(user.getUserName());

        for (String participant: participants) {
            result.append(FinalClass.FILE_NAME_DELIMITER_DASH);
            result.append(participant);
        }
        return String.valueOf(result);
    }

    private static Integer checkIfConversationHasAlreadyBeenCreated(User user, List<String> participants) {
        //Returns -1 if new conversation, index in conversation list is existing

        try {
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

    private static List<String> getNewMessagesLog(User user) {
        try {
            return Files.readAllLines(Paths.get(String.valueOf(user.getNewMessageLogFileName())));
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred while trying to write to " + user.getNewMessageLogFileName() + "!");
            return null;
        }
    }

    private static List<String> countNumberOfNewMessagesBySenderAndChat(List<String> newMessageLogList) {
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

    private static String getEarliestUnreadMessageTime(String chatName, List<String> newMessageLogList) {
        for (String line : newMessageLogList) {
            if (!line.equals("")) {
                String[] splitLine = line.split(FinalClass.CSV_DELIMITER);

                if (splitLine[1].equals(chatName)) {
                    return splitLine[2];
                }
            }
        }
        return null;
    }

    private static void fetchNewMessages(User user, Date firstUnreadMessage) {
        List<String> lines;

        try {
            lines = Files.readAllLines(Paths.get(user.getCurrentConversation().getName() + FinalClass.FILE_TYPE_SUFFIX));

            //First loop determines the first/earliest message to be shown
            for (int i = 0; i < lines.size(); i++) {
                //Look for "<timestamp>" tag
                if (lines.get(i).contains(FinalClass.TIME_STAMP_TAG)) {
                    //Parse the String into a Date
                    Date dateOfCurrentMessage;

                    try {
                        dateOfCurrentMessage = new SimpleDateFormat(FinalClass.TIME_STAMP_PATTERN).parse(lines.get(i).replace(FinalClass.TIME_STAMP_TAG, ""));

                        //Check if the parsed Date is earlier than firstUnreadMessage
                        if (dateOfCurrentMessage.compareTo(firstUnreadMessage) == 0) {
                            //Print out all remaining lines, except the lines where timestamp tag is present
                            for (int c = i; c < lines.size(); c++) {
                                if (!lines.get(c).contains(FinalClass.TIME_STAMP_TAG)) {
                                    System.out.println(lines.get(c));
                                }
                            }
                            break;
                        }

                    } catch (ParseException e) {
                        printEqualLengthMenuLine(" ERROR MESSAGE ");
                        System.out.println("A problem occurred while trying to parse a date!");
                    }
                }
            }
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred while trying to fetch new messages!");
        }
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
                user.printConversations();
                printEqualLengthMenuLine(" CHAT NUMBER|NEW CHAT (+)|MENU (-) ");
                chosenOption = scanner.nextLine();
            } else {
                printEqualLengthMenuLine(" NEW CHAT (+)|MENU (-) ");
                chosenOption = scanner.nextLine();
            }
        }

        //chosenOption = "+": start a new chat
        //chosenOption = existing conversation key: enter the existing chat
        if (chosenOption.equals("+")) {
            chooseRecipients(user);
        } else if(!chosenOption.equals("-") && Integer.parseInt(chosenOption) <= user.getConversations().size()) {
            user.setCurrentConversation(user.getConversations().get(Integer.parseInt(chosenOption) - 1));
            Conversation currentConversation = user.getCurrentConversation();

            //System.out.println("You are in a chat between " + currentConversation.getName() + "\nEnter \"/MENU\" to return to the menu.\n");
            printEqualLengthMenuLine(" " + currentConversation.getName() + " ");
            printEqualLengthMenuLine(" /MENU|/CHAT ");

            fetchRecentMessages(user);

            //Start thread to check for replies from recipients
            //OngoingMessagesThread r = new OngoingMessagesThread(user, user.getCurrentConversation(), chatName + FinalClass.FILE_TYPE_SUFFIX);
            //r.start();

            do {
                answer = scanner.nextLine();
                if (!answer.equals("") && !answer.equalsIgnoreCase("/MENU") && !answer.equalsIgnoreCase("/CHAT")) {
                    composeMessage(user, answer);
                } else if (answer.equalsIgnoreCase("/CHAT")) {
                    chat(user, "");
                }
            } while (!answer.equalsIgnoreCase("/MENU"));
            user.setCurrentConversation(null);
        }
    }

    static void checkMessages(User user) {
        String answer;
        Scanner scanner = new Scanner(System.in);
        List<String> newMessagesLogList = getNewMessagesLog(user);

        //Get a list of new messages by sender followed by the amount of new messages sent from them
        List<String> newMessagesBySender = countNumberOfNewMessagesBySenderAndChat(newMessagesLogList);

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

    private static void removeSendersLinesFromNewMessageLog(User user, String sender) {
        List<String> fileContents;
        Path newMessageLogPath = Paths.get(user.getUserName() + FinalClass.NEW_MESSAGE_LOG_SUFFIX);

        try {
            fileContents = Files.readAllLines(newMessageLogPath);

            List<String> newContent = new ArrayList<>();

            String[] splitLines;

            for (String line: fileContents) {
                splitLines = line.split(FinalClass.CSV_DELIMITER);
                String stringToSearch = splitLines[0] + FinalClass.CSV_DELIMITER + splitLines[1];

                if (!stringToSearch.equals(sender)) {
                    newContent.add(line);
                }
            }

            FileWriter fileWriter = new FileWriter(String.valueOf(newMessageLogPath));
            PrintWriter printWriter = new PrintWriter(fileWriter);

            for (String line: newContent) {
                printWriter.write(line);
            }

            printWriter.close();
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred while trying to write to NML!");
        }
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
        return Math.round((FinalClass.MENU_LINE_LENGTH - menuTextLength) / 2);
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