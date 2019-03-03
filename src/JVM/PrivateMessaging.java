package JVM;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static JVM.CommonMethods.*;

class PrivateMessaging {

    void chooseRecipient(String userName, Map<String, String> userList) throws IOException {
        String recipient, message, answer;
        Path messagesPath;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Whom do you wish to contact?");
        recipient = scanner.nextLine();

        while (true) {
            if (checkForKey(userList, recipient)) {
                System.out.println("Such user doesn't exist! Please enter another.");
                recipient = scanner.nextLine();
            } else {
                break;
            }
        }

        //Check if the message file exists, if not then create it
        messagesPath = Paths.get(createMessageTxtFileIfNotCreated(userName, recipient));
        composeMessage(userName, recipient, messagesPath);
    }

    static void composeMessage(String userName, String recipient, Path messagesPath) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String message, answer;

        do {
            System.out.println("\nPlease enter your message:");
            message = scanner.nextLine();

            String currentTimeStamp = createTimeStamp();
            saveMessageToTextFile(userName, message, messagesPath, currentTimeStamp);
            saveSenderAndTimeStampToNewMessageLog(userName, recipient, currentTimeStamp);

            System.out.println("\nDo you want to send another? (Y/N)");
            answer = scanner.nextLine();

        } while (answer.equalsIgnoreCase("Y"));
    }

    void saveSenderAndTimeStampToNewMessageLog(String userName, String recipient, String timeStamp) throws IOException {
        Path newMessageLogPath = Paths.get(recipient + FinalClass.NEW_MESSAGE_LOG_SUFFIX);
        int resultOfFileCreation = createTextFileIfNotCreated(newMessageLogPath);

        FileWriter writer = new FileWriter(String.valueOf(newMessageLogPath), true);

        if (resultOfFileCreation == 0) {
            writer.append("\n");
        }

        writer.append(userName);
        writer.append(FinalClass.CSV_DELIMITER);
        writer.append(timeStamp);
        writer.close();
    }

    private List<String> getNewMessagesLog(String userName) throws IOException {
        Path newMessageLogPath = Paths.get(userName + FinalClass.NEW_MESSAGE_LOG_SUFFIX);
        int resultOfFileCreation = createTextFileIfNotCreated(newMessageLogPath);
        return Files.readAllLines(Paths.get(String.valueOf(newMessageLogPath)));
    }

    private Map<String, Integer> countNumberOfNewMessages(List<String> newMessageLogList) {
        Map<String, Integer> newMessagesBySender = new HashMap<>();

        for (String item : newMessageLogList) {
            String[] splitLine = item.split(FinalClass.CSV_DELIMITER);

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
        PrivateMessaging privateMessaging = new PrivateMessaging();
        Scanner scanner = new Scanner(System.in);
        String numberOfMessagesSuffix;

        List<String> newMessagesLogList = privateMessaging.getNewMessagesLog(user.getUserName());

        //Get a list of new messages by sender followed by the amount of new messages sent from them
        Map<String, Integer> newMessagesBySender = privateMessaging.countNumberOfNewMessages(newMessagesLogList);

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
            String firstUnreadMessage = privateMessaging.getEarliestUnreadMessageTime(answer, newMessagesLogList);
            Date date2 = new SimpleDateFormat(timeStampPattern).parse(firstUnreadMessage);

            ArrayList<String> groupName = new ArrayList<>();
            groupName.add(answer);

            if (!groupIdentifier.equals("")) {
                //Get the path of the current group's message file
                userMessageFilePath = searchForFileNameContainingSubstring(groupName, user.getUserName(), true);
            } else {
                //Get the path of the current user's message file
                userMessageFilePath = createMessageTxtFileIfNotCreated(user.getUserName(), answer, ".txt");
            }

            //Find the messages and print them out one after another
            fetchNewMessages(date2, userMessageFilePath, timeStampPattern);

            //Remove current sender's logs from user's NewMessageLog, because the messages have now been read
            removeSendersLinesFromNewMessageLog(answer, Paths.get(user.getUserName() + FinalClass.NEW_MESSAGE_LOG_SUFFIX));

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
            splitLines = currentLine.split(FinalClass.CSV_DELIMITER);
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
            String[] splitLine = line.split(FinalClass.CSV_DELIMITER);

            if (splitLine[0].equals(sender)) {
                return splitLine[1];
            }
        }
        return null;
    }

    private String createMessageTxtFileIfNotCreated(String stringToCheck, String anotherStringToCheck) throws IOException {
        File file1 = new File(stringToCheck + FinalClass.MESSAGE_FILE_NAME_DELIMITER + anotherStringToCheck + FinalClass.FILE_TYPE_SUFFIX);
        File file2 = new File(anotherStringToCheck + FinalClass.MESSAGE_FILE_NAME_DELIMITER + stringToCheck + FinalClass.FILE_TYPE_SUFFIX);

        if (!file1.exists() && !file2.exists()) {
            file1.createNewFile();
            return file1.getName();
        } else if (file1.exists()) {
            return file1.getName();
        } else {
            return file2.getName();
        }
    }

    User registerNewUser(Map<String, String> userList) throws IOException {
        String enteredUserName;
        String enteredPassword;
        Scanner scanner = new Scanner(System.in);
        String passwordPattern = "^(?=.{8,})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$";

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
        while (!enteredPassword.matches(passwordPattern)) {
            System.out.println("Password doesn't match the requirements. Try another one!");
            enteredPassword = scanner.next();
        }

        //Save the new username into user list
        appendUserNamePswToCSV(enteredUserName + FinalClass.CSV_DELIMITER + enteredPassword, FinalClass.USER_LIST_PATH);

        System.out.println("New user successfully created.\nWelcome to JVM, " + enteredUserName + "!");

        return new User(enteredUserName, enteredPassword);
    }


}
