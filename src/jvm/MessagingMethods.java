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

    private static void chooseRecipients(User user) throws IOException, ParseException {
        String enteredParticipant;
        List<String> recipients = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Whom do you wish to contact?\n You can enter one name or several. (\"/END\" to end)");
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

        checkIfConversationHasAlreadyBeenCreated(user, recipients);
        user.collectConversations();

        chat(user, checkIfConversationHasAlreadyBeenCreated(user, recipients));
    }

    private static String checkIfConversationHasAlreadyBeenCreated(User user, List<String> participants) {

        try {
            List<String> lines = Files.readAllLines(Paths.get(user.getConversationFilePath()));
            String[] splitLine;
            int numberOfParticipants;
            boolean found;

            for (String line: lines) {
                splitLine = line.split(FinalClass.CSV_DELIMITER);
                numberOfParticipants = splitLine[0].split(FinalClass.FILE_NAME_DELIMITER_DASH).length;
                found = true;

                for (String participant: participants) {
                    if (!splitLine[0].contains(participant)) {
                        found = false;
                        break;
                    }
                }

                if (found && participants.size() == numberOfParticipants) {
                    return splitLine[0];
                }
            }

            return "";
        } catch (IOException e) {
            System.out.println("A problem occurred while trying to read from " + user.getConversationFilePath() + "!");
        }

        return "";
    }

    static boolean createTextFile(Path filePath) {
        File file = new File(String.valueOf(filePath));

        try {
            return file.createNewFile();
        } catch (IOException e) {
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
            System.out.println("A problem occurred whiles trying to write to " + user.getNewMessageLogFileName() + "!");
            return null;
        }
    }

    private static Map<String, Integer> countNumberOfNewMessages(List<String> newMessageLogList) {
        Map<String, Integer> newMessagesBySender = new HashMap<>();

        for (String item : newMessageLogList) {
            String[] splitLine = item.split(FinalClass.CSV_DELIMITER);

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

    private static void displayNewMessages(Map<String, Integer> newMessagesList) {
        String numberOfMessagesSuffix, groupIdentifier = "";

        System.out.println("\nYou have new message(s) from:");
        for (Map.Entry<String, Integer> entry : newMessagesList.entrySet()) {
            if (entry.getValue() > 1) {
                numberOfMessagesSuffix = "s";
            } else {
                numberOfMessagesSuffix = "";
            }

            if (entry.getKey().contains(FinalClass.FILE_NAME_DELIMITER_DASH)) groupIdentifier = "Group chat";

            System.out.println(entry.getKey() + " (" + entry.getValue() + " message" + numberOfMessagesSuffix + ") " + groupIdentifier);
        }
    }

    private static String getEarliestUnreadMessageTime(String sender, List<String> newMessageLogList) {
        for (String line : newMessageLogList) {
            String[] splitLine = line.split(FinalClass.CSV_DELIMITER);

            if (splitLine[0].equals(sender)) {
                return splitLine[1];
            }
        }
        return null;
    }

    private static void fetchNewMessages(Date firstUnreadMessage, String filePath) throws IOException, ParseException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        //First loop determines the first/earliest message to be shown
        for (int i = 0; i < lines.size(); i++) {
            //Look for "<timestamp>" tag
            if (lines.get(i).contains(FinalClass.TIME_STAMP_TAG)) {
                //Parse the String into a Date
                Date dateOfCurrentMessage = new SimpleDateFormat(FinalClass.TIME_STAMP_PATTERN).parse(lines.get(i).replace(FinalClass.TIME_STAMP_TAG, ""));
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
            }
        }
    }

    private static void fetchRecentMessages(User user) throws IOException {
        String filename = user.getCurrentConversation().getName() + FinalClass.FILE_TYPE_SUFFIX;
        List<String> lines = Files.readAllLines(Paths.get(filename));
        int messageCounter = 0, numberOfMessages = 0;

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
    }

    private static void regularOrGroupChatFork(User user) throws IOException, ParseException {
        Scanner scanner = new Scanner(System.in);
        String answer;

        printEqualLengthMenuLine(" REGULAR (1)|GROUP (2)|MENU (-) ");
        answer = scanner.nextLine();

        if (answer.equals("2")) {
            chooseRecipients(user);
        } else if (answer.equals("1")) {
            chooseRecipients(user);
        }
    }

    static void chat(User user, String chosenOption) throws IOException, ParseException {
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
            regularOrGroupChatFork(user);
        } else if (Integer.parseInt(chosenOption) <= user.getUserName().length()) {
            user.setCurrentConversation(user.getConversations().get(Integer.parseInt(chosenOption)));
            Conversation currentConversation = user.getCurrentConversation();

            System.out.println("You are in a chat between " + currentConversation.getName() + "\nEnter \"/MENU\" to return to the menu.");
            printEqualLengthMenuLine(" " + currentConversation.getName() + " ");

            fetchRecentMessages(user);

            //Start thread to check for replies from recipients
            //OngoingMessagesThread r = new OngoingMessagesThread(user, user.getCurrentConversation(), chatName + FinalClass.FILE_TYPE_SUFFIX);
            //r.start();

            do {
                answer = scanner.nextLine();
                if (!answer.equals("") && !answer.equalsIgnoreCase("/MENU")) {
                    composeMessage(user, answer);
                }
            } while (!answer.equalsIgnoreCase("/MENU"));
            user.setCurrentConversation(null);
        }
    }

    //private static void composeMessage(String userName, String groupName, String recipient, ArrayList<String> recipients, Path messagesPath, String message, boolean instantMessaging) throws IOException {
    private static void composeMessage(User user, String message) {
        String currentTimeStamp = createTimeStamp();
        saveMessageToTextFile(user, message, currentTimeStamp);
        saveSenderAndTimeStampToNewMessageLog(user, currentTimeStamp);
    }

    private static void removeSendersLinesFromNewMessageLog(String sender, Path newMessageLogPath) throws IOException {
        List<String> fileContents = Files.readAllLines(newMessageLogPath);
        List<String> newContent = new ArrayList<>();

        String[] splitLines;

        for (String line: fileContents) {
            splitLines = line.split(FinalClass.CSV_DELIMITER);
            if (!splitLines[0].equals(sender)) {
                newContent.add(line);
            }
        }

        FileWriter fileWriter = new FileWriter(String.valueOf(newMessageLogPath));
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (String line: newContent) {
            printWriter.write(line);
        }

        printWriter.close();
    }

    private static void saveSenderAndTimeStampToNewMessageLog(User user, String timeStamp) {
        FileWriter writer;

        try {
            writer = new FileWriter(String.valueOf(user.getNewMessageLogFileName()), true);

            writer.append("\n");
            writer.append(user.getUserName());
            writer.append(FinalClass.CSV_DELIMITER);
            writer.append(timeStamp);
            writer.append(FinalClass.CSV_DELIMITER);
            writer.append("0");
            writer.close();
        } catch (IOException e) {
            System.out.println("A problem occurred whiles trying to write to " + user.getNewMessageLogFileName() + "!");
        }
    }

    static void checkMessages(User user) throws IOException, ParseException {
        String answer = "", onlySender = "", answer2, userMessageFilePath;
        boolean groupChat = false;
        Scanner scanner = new Scanner(System.in);

        List<String> newMessagesLogList = getNewMessagesLog(user);

        //Get a list of new messages by sender followed by the amount of new messages sent from them
        Map<String, Integer> newMessagesBySender = countNumberOfNewMessages(newMessagesLogList);

        //Display the amount of new messages from senders
        if (newMessagesBySender.size() > 0) {
            displayNewMessages(newMessagesBySender);

            //Offer to read the messages
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
                    for (Map.Entry<String, Integer> entry: newMessagesBySender.entrySet()) {
                        onlySender = entry.getKey();
                    }

                    answer = onlySender;
                    System.out.println();
                } else {
                    return;
                }
            }

            if (answer.contains(FinalClass.FILE_NAME_DELIMITER_DASH)) groupChat = true;

            //Determine the time of the first unread message by chosen sender
            String firstUnreadMessage = getEarliestUnreadMessageTime(answer, newMessagesLogList);
            Date date2 = new SimpleDateFormat(FinalClass.TIME_STAMP_PATTERN).parse(firstUnreadMessage);

            //Get the path of the current user's message file
            if (groupChat) {
                userMessageFilePath = searchForFileNameContainingSubstring(null, user.getUserName(), answer);
            } else {
                userMessageFilePath = createMessageTxtFileIfNotCreated(user.getUserName(), answer);
            }

            //Find the messages and print them out one after another
            fetchNewMessages(date2, userMessageFilePath);

            //Remove current sender's logs from user's NewMessageLog, because the messages have now been read
            removeSendersLinesFromNewMessageLog(answer, Paths.get(user.getUserName() + FinalClass.NEW_MESSAGE_LOG_SUFFIX));
        } else {
            System.out.println("\nYou don't have any new messages!\n");
        }
    }

    private static ArrayList<String> splitStringByDelimiterIntoArrayList(String stringToSplit, String userName) {
        String[] splitString = stringToSplit.split(FinalClass.FILE_NAME_DELIMITER_DASH);
        ArrayList<String> result = new ArrayList<>(Arrays.asList(splitString));
        result.remove(userName);
        return result;
    }

    synchronized private static String createMessageTxtFileIfNotCreated(String stringToCheck, String anotherStringToCheck) throws IOException {
        File file1 = new File(stringToCheck + FinalClass.FILE_NAME_DELIMITER_DASH + anotherStringToCheck + FinalClass.FILE_TYPE_SUFFIX);
        File file2 = new File(anotherStringToCheck + FinalClass.FILE_NAME_DELIMITER_DASH + stringToCheck + FinalClass.FILE_TYPE_SUFFIX);

        if (!file1.exists() && !file2.exists()) {
            file1.createNewFile();
            return file1.getName();
        } else if (file1.exists()) {
            return file1.getName();
        } else {
            return file2.getName();
        }
    }

    synchronized private static String searchForFileNameContainingSubstring(ArrayList<String> recipients, String userName, String groupName) throws IOException {
        String fileNameToCheckAgainst = "";

        //Create the file name to use if file hasn't been created
        if (recipients != null && groupName.equals("")) {
            for (String recipient : recipients) {
                if (fileNameToCheckAgainst.equals("")) {
                    fileNameToCheckAgainst = recipient;
                } else {
                    fileNameToCheckAgainst = fileNameToCheckAgainst + FinalClass.FILE_NAME_DELIMITER_DASH + recipient;
                }
            }

            fileNameToCheckAgainst = userName + FinalClass.FILE_NAME_DELIMITER_DASH + fileNameToCheckAgainst + FinalClass.FILE_TYPE_SUFFIX;
        } else {
            //When the groupName already exists
            fileNameToCheckAgainst = groupName + FinalClass.FILE_TYPE_SUFFIX;
        }

        //Search for it, if not found create it
        File directoryToSearchIn = new File("");
        boolean foundAllSubstrings = false;

        if (directoryToSearchIn.isDirectory()) {
            String[] files = directoryToSearchIn.list();
            for (String fileName : files) {
                foundAllSubstrings = true;
                for (String element : recipients) {
                    if (!fileName.contains(element)) {
                        foundAllSubstrings = false;
                        break;
                    }
                }

                if (foundAllSubstrings && fileName.contains(userName) && fileName.length() == fileNameToCheckAgainst.length()) {
                    if (groupName.equals(""))
                        System.out.println("Group " + fileName.replace(FinalClass.FILE_TYPE_SUFFIX, "") + " already exists!");
                    return fileName;
                }
            }
        }

        if (!foundAllSubstrings) {
            File file3 = new File(fileNameToCheckAgainst);
            file3.createNewFile();
            if (groupName.equals("")) System.out.println("Group " + file3.getName().replace(".txt", "") + " has been created!\n");
            return file3.getName();
        } else {
            return "";
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
