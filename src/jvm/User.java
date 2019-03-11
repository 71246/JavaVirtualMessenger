package jvm;

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

    User(String userName) {
        this.userName = userName;
        this.newMessageLogFileName = userName + FinalClass.NEW_MESSAGE_LOG_SUFFIX;
        this.conversationFilePath = this.userName + FinalClass.USER_CONVERSATIONS_PATH_SUFFIX;
        createNewMessageLogFile(this.newMessageLogFileName);
        createConversationFile(this.conversationFilePath);
        collectConversations();
    }

    String getConversationFilePath() {
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
            this.conversations.clear();
            List<String> lines = Files.readAllLines(Paths.get(userName + FinalClass.USER_CONVERSATIONS_PATH_SUFFIX));
            String[] splitLine;

            for (int i = 1; i < lines.size(); i++) {
                splitLine = lines.get(i).split(FinalClass.CSV_DELIMITER);

                this.conversations.add(new Conversation(splitLine[0], splitLine[1], Integer.parseInt(splitLine[2])));
            }
        } catch (IOException e) {
            printEqualLengthMenuLine(" ERROR MESSAGE ");
            System.out.println("A problem occurred while trying to read from " + userName + FinalClass.USER_CONVERSATIONS_PATH_SUFFIX + "!");
        }
    }

    ArrayList<Conversation> getConversations() {
        return conversations;
    }

    Integer getConversationIndexByName(String conversationName) {
        for (int i = 0; i < this.getConversations().size(); i++) {
            if (this.getConversations().get(i).getName().equals(conversationName)) {
                return i;
            }
        }
        return -1;
    }

    void printConversations() {
        if (this.conversations.size() >= 1) {
            System.out.println("\nYOUR CHATS:");

            for (int i = 0; i < this.conversations.size(); i++) {
                System.out.println(i + 1 + ". " + this.conversations.get(i).getName());
            }
            System.out.println();
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

    Conversation findConversationByName(String conversationName) {
        for (int i = 0; i < this.getConversations().size(); i++) {
            if (this.getConversations().get(i).getName().equals(conversationName)) {
                return this.getConversations().get(i);
            }
        }
        return null;
    }

    void setCurrentConversation(Conversation conversation) {
        this.currentConversation = conversation;
    }

    String getPassword() {
        return password;
    }
}
