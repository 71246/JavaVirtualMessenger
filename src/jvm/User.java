package jvm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class User {
    private String userName;
    private ArrayList<Conversation> conversations = new ArrayList<>();
    private Conversation currentConversation;
    private int amountOfMessagesToShow = 10;
    private String newMessageLogFileName = "";

    User(String userName) throws IOException {
        this.userName = userName;
        setNewMessageLogFileName();
        createNewMessageLogFile();
    }

    String getUserName() {
        return userName;
    }

    String getNewMessageLogFileName() {
        return newMessageLogFileName;
    }

    private void setNewMessageLogFileName() {
        this.newMessageLogFileName = userName + FinalClass.NEW_MESSAGE_LOG_SUFFIX;;
    }

    void collectConversations() {
        File directoryToSearchIn = new File("");

        if (directoryToSearchIn.isDirectory()) {
            String[] files = directoryToSearchIn.list();
            for (String file : files) {
                if (!file.contains(FinalClass.NEW_MESSAGE_LOG_SUFFIX) && file.contains(this.userName)) {
                    this.conversations.add(new Conversation(file));
                }
            }
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

    String getCurrentConversationName() {
        return currentConversation.getName();
    }

    void setCurrentConversation(Conversation conversation) {
        this.currentConversation = conversation;
    }

    synchronized private void createNewMessageLogFile() throws IOException {
        File file = new File(this.newMessageLogFileName);

        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
