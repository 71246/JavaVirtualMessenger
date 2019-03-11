package jvm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static jvm.MessagingMethods.*;

class User {
    private String userName;
    private String password;
    private ArrayList<Conversation> conversations = new ArrayList<>();
    private Conversation currentConversation;
    private int amountOfMessagesToShow = 10;
    private String newMessageLogFileName;
    private String conversationFilePath;

    User(String userName) throws IOException {
        this.userName = userName;
        this.newMessageLogFileName = userName + FinalClass.NEW_MESSAGE_LOG_SUFFIX;
        this.conversationFilePath = this.userName + FinalClass.USER_CONVERSATIONS_PATH_SUFFIX;
        createNewMessageLogFile();
        createTextFile(Paths.get(conversationFilePath));
        collectConversations();
    }

    public String getConversationFilePath() {
        return conversationFilePath;
    }

    String getUserName() {
        return userName;
    }

    String getNewMessageLogFileName() {
        return newMessageLogFileName;
    }

    void collectConversations() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(userName + FinalClass.USER_CONVERSATIONS_PATH_SUFFIX));
            String[] splitLine;
            String[] participants;

            for (int i = 1; i < lines.size(); i++) {
                splitLine = lines.get(i).split(FinalClass.CSV_DELIMITER);
                participants = splitLine[0].split(FinalClass.FILE_NAME_DELIMITER_DASH);

                this.conversations.add(new Conversation(splitLine[0], splitLine[1], Integer.parseInt(splitLine[2]), participants));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("A problem occurred while trying to read from " + userName + FinalClass.USER_CONVERSATIONS_PATH_SUFFIX + "!");
        }
    }

    ArrayList<Conversation> getConversations() {
        return conversations;
    }

    void printConversations() {
        if (this.conversations.size() >= 1) {
            System.out.println("\nYOUR CHATS:");

            for (int i = 0; i < this.conversations.size(); i++) {
                System.out.println(i + 1 + ". " + this.conversations.get(i));
            }
        } else {
            System.out.println("You don't have any ongoing conversations.\n");
        }
    }

    int getAmountOfMessagesToShow() {
        return amountOfMessagesToShow;
    }

    public void setAmountOfMessagesToShow(int amountOfMessagesToShow) {
        this.amountOfMessagesToShow = amountOfMessagesToShow;
    }

    Conversation getCurrentConversation() {
        return this.currentConversation;
    }

    void setCurrentConversation(Conversation conversation) {
        this.currentConversation = conversation;
    }

    synchronized private void createNewMessageLogFile() {
        createTextFile(Paths.get(this.newMessageLogFileName));
    }

    String getPassword() {
        return password;
    }


}
