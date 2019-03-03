package JVM;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import static JVM.CommonMethods.*;

class GroupMessaging {

    void chooseRecipients(String userName, Map<String, String> userList) throws IOException {
        String message, answer, participant, groupName;
        ArrayList<String> recipients = new ArrayList<>();
        Path messagesPath;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter all the participants of the group chat one by one: (type " + "\"END\"" + " to end the list)");

        do {
            participant = scanner.nextLine();

            while (!participant.equalsIgnoreCase("END") && !checkForKey(userList, participant)) {
                System.out.println("Such user doesn't exist! Please enter another.");
                participant = scanner.nextLine();
            }

            if (!participant.equalsIgnoreCase("END")) recipients.add(participant);
        } while (!participant.equalsIgnoreCase("END"));

        messagesPath = Paths.get(searchForFileNameContainingSubstring(recipients, userName, false));
        groupName = String.valueOf(messagesPath).replace(".txt", "");

        composeMessage(userName, );
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

    private void saveSenderAndTimeStampToNewMessageLog(String userName, ArrayList<String> recipients, String timeStamp, String groupName) throws IOException {
        for (String recipient : recipients) {
            Path newMessageLogPath = Paths.get(recipient + FinalClass.NEW_MESSAGE_LOG_SUFFIX);
            int resultOfFileCreation = createTextFileIfNotCreated(newMessageLogPath);

            FileWriter writer = new FileWriter(String.valueOf(newMessageLogPath), true);

            if (resultOfFileCreation == 0) {
                writer.append("\n");
            }

            writer.append(groupName);
            writer.append(FinalClass.CSV_DELIMITER);
            writer.append(timeStamp);
            writer.append(FinalClass.CSV_DELIMITER);
            writer.append("G");
            writer.close();
        }
    }

    String searchForFileNameContainingSubstring(ArrayList<String> recipients, String userName, boolean groupNameExists) throws IOException {
        //Create the file name to use if file hasn't been created
        String fileNameToUse = "", delimiter = "-", suffix = ".txt";

        if (!groupNameExists) {
            for (String recipient : recipients) {
                if (fileNameToUse.equals("")) {
                    fileNameToUse = recipient;
                } else {
                    fileNameToUse = fileNameToUse + delimiter + recipient;
                }
            }

            fileNameToUse = userName + delimiter + fileNameToUse + suffix;
        } else {
            fileNameToUse = recipients.get(0) + suffix;
        }

        //Search for it, if not found create it
        File file1 = new File("");
        boolean foundAllSubstrings = false;

        if (file1.isDirectory()) {
            String[] files = file1.list();
            for (String fileName : files) {
                foundAllSubstrings = true;
                for (String element: recipients) {
                    if (!fileName.contains(element)) {
                        foundAllSubstrings = false;
                        break;
                    }
                }

                if (foundAllSubstrings && fileName.contains(userName) && fileName.length() == fileNameToUse.length()) {
                    if (!groupNameExists) System.out.println("Group " + fileName.replace(".txt", "") + " already exists!");
                    return fileName;
                }
            }
        }

        if (!foundAllSubstrings) {
            File file3 = new File(fileNameToUse);
            file3.createNewFile();
            if (groupNameExists) System.out.println("Group " + file3.getName().replace(".txt", "") + " has been created!");
            return file3.getName();
        } else {
            return "";
        }
    }

}
