package jvm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class User {
    private String userName;
    private String password;
    private ArrayList<String> friendsList = new ArrayList<>();
    private Map<Integer, String> conversations = new HashMap<>();
    private String currentConversation = "";
    private int amountOfMessagesToShow = 10;

    User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    String getUserName() {
        return userName;
    }

    void collectConversations() {
        File directoryToSearchIn = new File(System.getProperty("user.dir"));
        int conversationCounter = 1;

        if (directoryToSearchIn.isDirectory()) {
            String[] files = directoryToSearchIn.list();
            for (String fileName : files) {
                if (!fileName.contains(FinalClass.NEW_MESSAGE_LOG_SUFFIX) && fileName.contains(this.userName)) {
                    addToConversations(conversationCounter, fileName.replace(FinalClass.FILE_TYPE_SUFFIX, ""));
                    conversationCounter++;
                }
            }
        }
    }

    public void addToFriendsList(String newFriend) {
        this.friendsList.add(newFriend);
    }

    public ArrayList<String> getFriendsList() {
        return friendsList;
    }

    private void addToConversations(Integer queueNumber, String conversationName) {
        this.conversations.put(queueNumber, conversationName);
    }

    Map<Integer, String> getConversations() {
        return conversations;
    }

    void printConversations() {
        if (this.conversations.size() >= 1) {
            System.out.println("\nHere are your ongoing chats:");

            for (Map.Entry<Integer, String> conversation : this.conversations.entrySet()) {
                System.out.println(conversation.getKey() + ". " + conversation.getValue());
            }
        } else {
            System.out.println("You don't have any ongoing conversations.");
        }
    }

    int getAmountOfMessagesToShow() {
        return amountOfMessagesToShow;
    }

    public void setAmountOfMessagesToShow(int amountOfMessagesToShow) {
        this.amountOfMessagesToShow = amountOfMessagesToShow;
    }

    String getCurrentConversation() {
        return currentConversation;
    }

    void setCurrentConversation(String currentConversation) {
        this.currentConversation = currentConversation;
    }
}
