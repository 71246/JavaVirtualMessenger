package jvm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jvm.MessagingMethods.*;

class InstantMessaging {

    static void checkNewChatMessages(User user) {
        //Get all new messages
        List<String> newMessagesLog = getNewMessagesLog(user);

        //Get them sorted by sender and conversation (for displaying or incoming new messages only)
        List<String> newMessagesBySender = countNumberOfNewMessagesBySenderAndConversationName(newMessagesLog);

        //Get two lists: first one containing all new messages for
        //current conversation the second containing all other new messages
        List<List<String>> newMessagesForBothCurrentAndOther = separateNewMessagesByCurrentAndOther(user);

        //Display current conversation's messages immediately
        try {
            Date earliestUnreadMessage = new SimpleDateFormat(FinalClass.TIME_STAMP_PATTERN).parse(getEarliestUnreadMessageTime(newMessagesForBothCurrentAndOther.get(0)));
            fetchNewMessages(user, earliestUnreadMessage);

            //Remove the lines of messages just displayed
            removeNewMessageLinesFromNewMessageLog(user, newMessagesForBothCurrentAndOther.get(0));
        } catch (NullPointerException e) {
            //e.printStackTrace();
        } catch (ParseException e) {
            //System.out.println(e.getMessage());
        }

        collectAndNotifyUserOfOtherNewMessages(newMessagesForBothCurrentAndOther.get(1));
    }

    private static void changeOtherMessagesNotificationStatus(User user, List<String> otherNewMessages) {

    }

    private static void collectAndNotifyUserOfOtherNewMessages(List<String> messagesToDisplay) {
        Map<String, Integer> collectedArray = new HashMap<>();
        String pluralitySuffix;
        String[] splitLine;

        for (String line: messagesToDisplay) {
            splitLine = line.split(FinalClass.CSV_DELIMITER);

            if (Integer.parseInt(splitLine[2]) == 0) {
                if (collectedArray.containsKey(splitLine[1])) {
                    collectedArray.put(splitLine[1], collectedArray.get(splitLine[1]) + 1);
                } else {
                    collectedArray.put(splitLine[1], 1);
                }
            }
        }

        if (!collectedArray.isEmpty()) {
            for (Map.Entry<String, Integer> entry: collectedArray.entrySet()) {
                if (entry.getValue() > 1) {
                    pluralitySuffix = "s";
                } else {
                    pluralitySuffix = "";
                }

                System.out.println("You have " + entry.getValue() + "message" + pluralitySuffix + " from " + entry.getKey());
            }
        }
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
}
