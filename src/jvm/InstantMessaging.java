package jvm;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class InstantMessaging implements Runnable {

    @Override
    public void run() {

    }

    void checkIncomingOngoingChatMessages(User user, String chatName, String messageFilePath) throws IOException {
        while (!user.getCurrentConversation().equals("")) {
            printMatchingMessages(getMessagesForOngoingChat(user, chatName), messageFilePath);
            removeLinesFromNewMessageLog(chatName, Paths.get(user.getNewMessageLogFileName()));
        }
    }

    void checkOtherIncomingMessages(User user, String chatName) throws IOException {
        while (!user.getCurrentConversation().equals("")) {
            notifyUserOfIncomingNewMessages(countNumberOfNewMessages(getAllNewMessagesNotFromCurrentChat(user, chatName)));
        }
    }

    synchronized private static ArrayList<String> getMessagesForOngoingChat(User user, String chatName) throws IOException {
        //Get all lines from NewMessageLog that are from a specific sender into an ArrayList
        List<String> lines = Files.readAllLines(Paths.get(user.getNewMessageLogFileName()));
        ArrayList<String> linesFromSender = new ArrayList<>();
        ArrayList<String> currentSenders = splitStringByDelimiterIntoArrayList(chatName, user.getUserName());
        String[] splitLine;

        for (String line: lines) {
            if (line.contains(FinalClass.CSV_DELIMITER)) {
                splitLine = line.split(FinalClass.CSV_DELIMITER);

                for (String recipient: currentSenders) {
                    if (splitLine[0].equals(recipient)) {
                        linesFromSender.add(line);
                    }
                }
            }
        }

        return linesFromSender;
    }

    synchronized private static void printMatchingMessages(ArrayList<String> linesFromSender, String chatName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(chatName));

        for (String line: linesFromSender) {
            for (int c = lines.size() - 1; c >= 0; c--) {
                if (lines.get(c).contains(FinalClass.TIME_STAMP_TAG)) {
                    if (lines.get(c).replace(FinalClass.TIME_STAMP_TAG, "").equals(line.replace(FinalClass.CSV_DELIMITER, ""))) {
                        System.out.println(lines.get(c + 1) + "\n" + lines.get(c + 2));
                    }
                }
            }
        }
    }

    synchronized private static void removeLinesFromNewMessageLog(String chatName, Path newMessageLogPath) throws IOException {
        List<String> fileContents = Files.readAllLines(newMessageLogPath);
        List<String> newContent = new ArrayList<>();
        String[] splitLines;

        for (String line: fileContents) {
            splitLines = line.split(FinalClass.CSV_DELIMITER);
            if (!splitLines[0].equals(chatName)) {
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

    synchronized private static List<String> getAllNewMessagesNotFromCurrentChat(User user, String chatName) throws IOException {
        //Get all lines from NewMessageLog that are from a specific sender into an ArrayList
        List<String> lines = Files.readAllLines(Paths.get(user.getNewMessageLogFileName()));
        ArrayList<String> linesFromSender = new ArrayList<>();
        String[] splitLine;
        ArrayList<String> currentSenders = splitStringByDelimiterIntoArrayList(chatName, user.getUserName());

        for (String line: lines) {
            if (line.contains(FinalClass.CSV_DELIMITER)) {
                splitLine = line.split(FinalClass.CSV_DELIMITER);

                for (String recipient: currentSenders) {
                    if (!splitLine[0].equals(recipient)) {
                        linesFromSender.add(line);
                    }
                }
            }
        }

        return linesFromSender;
    }

    synchronized private static Map<String, Integer> countNumberOfNewMessages(List<String> newMessageLogList) {
        //For displaying a notification to user about new messages
        Map<String, Integer> newMessagesBySenders = new HashMap<>();

        for (String item : newMessageLogList) {
            String[] splitLine = item.split(FinalClass.CSV_DELIMITER);

            if (!splitLine[0].equals("")) {
                if (newMessagesBySenders.containsKey(splitLine[0])) {
                    newMessagesBySenders.put(splitLine[0], newMessagesBySenders.get(splitLine[0]) + 1);
                } else {
                    newMessagesBySenders.put(splitLine[0], 1);
                }
            }
        }

        return newMessagesBySenders;
    }

     synchronized private void notifyUserOfIncomingNewMessages(Map<String, Integer> newMessageListBySenders) {
        for (Map.Entry<String, Integer> entry: newMessageListBySenders.entrySet()) {
            if (entry.getValue() == 1) {
                System.out.println("(You have " + 1 + " new message from " + entry.getKey() + ")");
            } else {
                System.out.println("(You have " + entry.getValue() + " new messages from " + entry.getKey() + ")");
            }
        }
    }

    private static ArrayList<String> splitStringByDelimiterIntoArrayList(String stringToSplit, String userName) {
        String[] splitString = stringToSplit.split(FinalClass.MESSAGE_FILE_NAME_DELIMITER);
        ArrayList<String> result = new ArrayList<>(Arrays.asList(splitString));
        result.remove(userName);
        return result;
    }
}
