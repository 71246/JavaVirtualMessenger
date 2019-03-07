package jvm;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class MessagingMethods {

    private static void chooseRecipient(User user, Map<String, String> userList) throws IOException, ParseException {
        String recipient;
        Path messagesPath;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Whom do you wish to contact?");
        recipient = scanner.nextLine();

        while (true) {
            if (!checkForKey(userList, recipient)) {
                System.out.println("Such user doesn't exist! Please enter another.");
                recipient = scanner.nextLine();
            } else {
                break;
            }
        }

        //Check if the message file exists, if not then create it
        messagesPath = Paths.get(createMessageTxtFileIfNotCreated(user.getUserName(), recipient));
        user.collectConversations();

        chat(user, findChatNumber(user.getConversations(), recipient, null), userList);
        //composeMessage(userName, "", recipient, null, messagesPath, "", true);
    }

    private static String findChatNumber(Map<String, String> listOfConversations, String recipient, ArrayList<String> recipients) {
        boolean found = true;
        String lastKey;

        if (!recipient.equals("")) {
            for (Map.Entry<String, String> entry : listOfConversations.entrySet()) {
                if (entry.getValue().contains(recipient)) {
                    return entry.getKey();
                }
            }
        } else {
            //For every conversation in the conversation list check if it contains ALL users of the entered chat name
            for (Map.Entry<String, String> entry : listOfConversations.entrySet()) {
                lastKey = entry.getKey();

                for (String user: recipients) {
                    if (!entry.getValue().contains(user)) {
                        found = false;
                        break;
                    }
                }

                if (found) {
                    return lastKey;
                }
            }
        }
        return "";
    }

    private static void chooseRecipients(User user, Map<String, String> userList) throws IOException, ParseException {
        String participant, groupName;
        ArrayList<String> recipients = new ArrayList<>();
        Path messagesPath;
        Scanner scanner = new Scanner(System.in);

        //Select group chat participants
        System.out.println("Please enter the participants of the chat one by one: (\"/END\"" + " to end the list)");

        do {
            participant = scanner.nextLine();

            while (!participant.equalsIgnoreCase("/END") && !checkForKey(userList, participant)) {
                System.out.println("Such user doesn't exist! Please enter another.");
                participant = scanner.nextLine();
            }

            if (!participant.equalsIgnoreCase("/END")) recipients.add(participant);
        } while (!participant.equalsIgnoreCase("/END"));

        if (recipients.size() == 1) {
            /*
            //Check if the message file exists, if not then create it
            messagesPath = Paths.get(createMessageTxtFileIfNotCreated(user.getUserName(), recipients.get(0)));
            //composeMessage(user.getUserName(), "", recipients.get(0), null, messagesPath, "", true);
            chat(user, findChatNumber(user.getConversations(), "", recipients));*/
            System.out.println("\nNot enough participants for a group chat!\n");
        } else {
            //Get the file path, or create it
            messagesPath = Paths.get(searchForFileNameContainingSubstring(recipients, user.getUserName(), ""));
            user.addToConversations(user.getNumberOfConversations() + 1, String.valueOf(messagesPath).replace(FinalClass.FILE_TYPE_SUFFIX, ""));

            //Extract group name from the message file path
            groupName = String.valueOf(messagesPath).replace(FinalClass.FILE_TYPE_SUFFIX, "");
            //composeMessage(userName, groupName, "", recipients, messagesPath, "", true);
            chat(user, findChatNumber(user.getConversations(), "", recipients), userList);
        }
    }

    static Integer createTextFileIfNotCreated(Path filePath) throws IOException {
        File file = new File(String.valueOf(filePath));

        if (filePath != null && !file.exists()) {
            file.createNewFile();
            return -1;
        }
        return 0;
    }

    private static boolean checkForKey(Map<String, String> userList, String nameToCheck) {
        return userList.containsKey(nameToCheck);
    }

    private static String checkForValue(Map<String, String> userList, String nameToCheck) {
        if (checkForKey(userList, nameToCheck))
            return userList.get(nameToCheck);

        return null;
    }

    private static void saveMessageToTextFile(String userName, String message, Path messagesPath, String timeStamp) throws IOException {
        int resultOfFileCreation = createTextFileIfNotCreated(messagesPath);

        FileWriter writer = new FileWriter(String.valueOf(messagesPath), true);
        if (resultOfFileCreation == 0) {
            writer.append("\n");
        }

        writer.append(FinalClass.TIME_STAMP_TAG);
        writer.append(timeStamp);
        writer.append("\n");
        writer.append(userName);
        writer.append(" says:\n");
        writer.append(message);
        writer.close();
    }

    static User logInUser(Map<String, String> userList) throws IOException {
        String enteredUserName, enteredPassword, menuText;
        int triesLeft = 3;
        Scanner scanner = new Scanner(System.in);

        menuText = " LOGIN ";
        printEqualLengthMenuLine(menuText);
        System.out.println("USER NAME:");
        enteredUserName = scanner.next();

        //Check for entered user name in the user list
        while (!checkForKey(userList, enteredUserName)) {
            System.out.print("Invalid user name, try again!\n");
            enteredUserName = scanner.next();
        }

        System.out.println("PASSWORD:");
        enteredPassword = scanner.next();

        while (triesLeft > 0) {
            triesLeft--;

            if (!checkForValue(userList, enteredUserName).equals(enteredPassword)) {
                switch (triesLeft) {
                    case 0:
                        System.out.print("Invalid password! You have exhausted your number of tries! \nThe account will be blocked for 3 hours.");
                        System.exit(-1);
                    case 1:
                        System.out.print("Invalid password, try again! " + "This is your last try.\n");
                        enteredPassword = scanner.next();
                        break;
                    case 2:
                        System.out.print("Invalid password, try again! " + "You have " + triesLeft + " tries left.\n");
                        enteredPassword = scanner.next();
                        break;
                    default:
                }
            }
        }

        return new User(enteredUserName, enteredPassword);
    }

    static Map<String, String> readFromCsvIntoMap() throws IOException {
        Map<String, String> map = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(FinalClass.USER_LIST_PATH)));
        String line;
        line = bufferedReader.readLine();
        String[] splitLines;

        while ((line = bufferedReader.readLine()) != null) {
            splitLines = line.split(FinalClass.CSV_DELIMITER);
            map.put(splitLines[0], splitLines[1]);
        }
        bufferedReader.close();
        return map;
    }

    private static void appendUserNamePswToCSV(String stringToAdd, Path filePath) throws IOException {
        FileWriter writer = new FileWriter(String.valueOf(filePath), true);
        writer.append("\n");
        writer.append(stringToAdd);
        writer.close();
    }

    private static String createTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat(FinalClass.TIME_STAMP_PATTERN);
        Date now = new Date();
        return sdfDate.format(now);
    }

    private static List<String> getNewMessagesLog(String userName) throws IOException {
        Path newMessageLogPath = Paths.get(userName + FinalClass.NEW_MESSAGE_LOG_SUFFIX);
        int resultOfFileCreation = createTextFileIfNotCreated(newMessageLogPath);
        return Files.readAllLines(Paths.get(String.valueOf(newMessageLogPath)));
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

    static User registerNewUser(Map<String, String> userList) throws IOException {
        String enteredUserName;
        String enteredPassword;
        Scanner scanner = new Scanner(System.in);

        //Prompt the user for a username
        System.out.println("Please enter your desired user name:");
        enteredUserName = scanner.next();

        //Check if the entered username is free to use
        while (checkForKey(userList, enteredUserName)) {
            System.out.print("The selected username already exists, choose another one!\n");
            enteredUserName = scanner.next();
        }

        //Prompt the user for a password, explain the requirements
        System.out.println("Your username is " + enteredUserName + ".\nPlease provide a password.\nThe password has to be at least 8 characters long and it must contain " +
                "\nat least one upper case, one lower case and one numeric character.");
        enteredPassword = scanner.next();

        //Check if entered password meets the requirements
        while (!enteredPassword.matches(FinalClass.PASSWORD_PATTERN)) {
            System.out.println("Password doesn't match the requirements. Try another one!");
            enteredPassword = scanner.next();
        }

        //Save the new username into user list
        appendUserNamePswToCSV(enteredUserName + FinalClass.CSV_DELIMITER + enteredPassword, FinalClass.USER_LIST_PATH);

        System.out.println("New user successfully created.\nWelcome to jvm, " + enteredUserName + "!");

        return new User(enteredUserName, enteredPassword);
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

    private static void fetchRecentMessages(String senders, Integer numberOfMessagesToLoad) throws IOException {
        String filename = senders + FinalClass.FILE_TYPE_SUFFIX;
        List<String> lines = Files.readAllLines(Paths.get(filename));
        int messageCounter = 0, numberOfMessages = 0;

        //Count number of messages
        for (String line: lines) {
            if (line.contains(FinalClass.TIME_STAMP_TAG)) numberOfMessages++;
        }

        //Count backwards the amount of messages to load or print all messages if there's less messages in the file
        if (numberOfMessagesToLoad <= numberOfMessages) {
            //Start from the last message and count backwards until the specified number of messages to show is reached
            for (int i = lines.size() - 1; i >= 0; i--) {
                if (lines.get(i).contains(FinalClass.TIME_STAMP_TAG)) {
                    messageCounter++;
                    if (messageCounter == numberOfMessagesToLoad) {
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

    static void regularOrGroupChatFork(User user, Map<String, String> userList) throws IOException, ParseException {
        Scanner scanner = new Scanner(System.in);
        String menuText, answer;

        menuText = " REGULAR (1)|GROUP (2) ";
        printEqualLengthMenuLine(menuText);
        answer = scanner.nextLine();

        if (answer.equals("2")) {
            chooseRecipients(user, userList);
        } else if (answer.equals("1")) {
            chooseRecipient(user, userList);
        }
    }

    static void chat(User user, String chosenOption, Map<String, String> userList) throws IOException, ParseException {
        Scanner scanner = new Scanner(System.in);
        String answer, chatName, userMessageFilePath, menuText;
        ArrayList<String> recipients;

        //If a chat has not been chosen then make the user do so
        if (user.getConversations().size() != 0 && chosenOption.equals("")) {
            menuText = " CHAT NUMBER|NEW CHAT (+) ";
            printEqualLengthMenuLine(menuText);
            chosenOption = scanner.nextLine();
        }

        if (chosenOption.equals("+")) {
            regularOrGroupChatFork(user, userList);
        } else if (user.getConversations().keySet().contains(chosenOption)) {
            chatName = user.getConversations().get(chosenOption);
            user.setCurrentConversation(chatName);

            System.out.println("You are in a chat between " + chatName + "\nEnter \"/MENU\" to return to the menu.");
            long count = chatName.chars().filter(ch -> ch == '-').count();

            fetchRecentMessages(user.getConversations().get(chosenOption), user.getAmountOfMessagesToShow());

            //Start thread to check for replies from recipients
            OngoingMessagesThread r = new OngoingMessagesThread(user, user.getCurrentConversation(), chatName + FinalClass.FILE_TYPE_SUFFIX);
            r.start();

            //Check for new messages from other users
            OtherMessagesThread s = new OtherMessagesThread(user, chatName);
            s.start();

            do {
                answer = scanner.nextLine();
                if (!answer.equals("") && !answer.equalsIgnoreCase("/MENU")) {
                    recipients = splitStringByDelimiterIntoArrayList(chatName, user.getUserName());

                    //If group chat or private
                    if (count >= 2) {
                        userMessageFilePath = searchForFileNameContainingSubstring(null, user.getUserName(), chatName);
                        composeMessage(user.getUserName(), chatName, "", recipients, Paths.get(userMessageFilePath), answer, true);
                    } else {
                        userMessageFilePath = createMessageTxtFileIfNotCreated(user.getUserName(), recipients.get(0));
                        composeMessage(user.getUserName(), "", recipients.get(0), null, Paths.get(userMessageFilePath), answer, true);
                    }
                }
            } while (!answer.equalsIgnoreCase("/MENU"));
            user.setCurrentConversation("");
        }
    }

    private static void composeMessage(String userName, String groupName, String recipient, ArrayList<String> recipients, Path messagesPath, String message, boolean instantMessaging) throws IOException {
        if (!instantMessaging) {
            Scanner scanner = new Scanner(System.in);
            String answer;

            do {
                System.out.println("\nPlease enter your message:");
                message = scanner.nextLine();

                String currentTimeStamp = createTimeStamp();
                saveMessageToTextFile(userName, message, messagesPath, currentTimeStamp);

                if (groupName.equals("")) {
                    saveSenderAndTimeStampToNewMessageLog(userName, "", recipient, null, currentTimeStamp);
                } else {
                    saveSenderAndTimeStampToNewMessageLog("", groupName, "", recipients, currentTimeStamp);
                }

                System.out.println("\nDo you want to send another? (Y/N)");
                answer = scanner.nextLine();
            } while (answer.equalsIgnoreCase("Y"));
        } else {
            String currentTimeStamp = createTimeStamp();
            saveMessageToTextFile(userName, message, messagesPath, currentTimeStamp);

            if (groupName.equals("")) {
                saveSenderAndTimeStampToNewMessageLog(userName, "", recipient, null, currentTimeStamp);
            } else {
                saveSenderAndTimeStampToNewMessageLog("", groupName, "", recipients, currentTimeStamp);
            }
        }
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

    private static void saveSenderAndTimeStampToNewMessageLog(String userName, String groupName, String recipient, ArrayList<String> recipients, String timeStamp) throws IOException {
        if (recipient.equals("")) {
            for (String user : recipients) {
                Path newMessageLogPath = Paths.get(user + FinalClass.NEW_MESSAGE_LOG_SUFFIX);
                int resultOfFileCreation = createTextFileIfNotCreated(newMessageLogPath);

                FileWriter writer = new FileWriter(String.valueOf(newMessageLogPath), true);

                if (resultOfFileCreation == 0) {
                    writer.append("\n");
                }

                writer.append(groupName);
                writer.append(FinalClass.CSV_DELIMITER);
                writer.append(timeStamp);
                writer.append(FinalClass.CSV_DELIMITER);
                writer.append("0");
                //writer.append(FinalClass.GROUP_CHAT_TAG);
                writer.close();
            }
        } else {
            Path newMessageLogPath = Paths.get(recipient + FinalClass.NEW_MESSAGE_LOG_SUFFIX);
            int resultOfFileCreation = createTextFileIfNotCreated(newMessageLogPath);

            FileWriter writer = new FileWriter(String.valueOf(newMessageLogPath), true);

            if (resultOfFileCreation == 0) {
                writer.append("\n");
            }

            writer.append(userName);
            writer.append(FinalClass.CSV_DELIMITER);
            writer.append(timeStamp);
            writer.append("0");
            writer.close();
        }
    }

    static void checkMessages(User user) throws IOException, ParseException {
        String answer = "", onlySender = "", answer2, userMessageFilePath;
        boolean groupChat = false;
        Scanner scanner = new Scanner(System.in);

        List<String> newMessagesLogList = getNewMessagesLog(user.getUserName());

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

            System.out.println("\nWould you like to reply? (Y/N)");
            answer2 = scanner.next();

            //Reply
            if (answer2.equalsIgnoreCase("Y")) {
                if (groupChat) {
                    //Split groupName to separate pieces, remove userName to get recipients array
                    ArrayList<String> recipients = splitStringByDelimiterIntoArrayList(answer, user.getUserName());
                    composeMessage(user.getUserName(), answer,  "", recipients, Paths.get(userMessageFilePath), "", false);
                } else {
                    composeMessage(user.getUserName(), answer, answer, null, Paths.get(userMessageFilePath), "", false);
                }
            }
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
