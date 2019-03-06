package jvm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class User {
    private String userName;
    private String password;
    private ArrayList<String> friendsList = new ArrayList<>();
    private String friendsListFileName;
    private Map<Integer, String> conversations = new HashMap<>();
    private String currentConversation = "";
    private int amountOfMessagesToShow = 10;
    private String newMessageLogFileName = "";
    private int numberOfConversations;

    User(String userName, String password) throws IOException {
        this.userName = userName;
        this.password = password;
        this.friendsListFileName = userName + FinalClass.FRIEND_LIST_SUFFIX;
        setNewMessageLogFileName();
        createNewMessageLogFile();
    }

    private void createFriendListFile() throws IOException {
        File friendList = new File(getFriendsListFileName());

        if (!friendList.exists()) {
            friendList.createNewFile();
        }
    }

    public void addToFriendsList(String newFriend) {
        this.friendsList.add(newFriend);
    }

    public ArrayList<String> getFriendsList() {
        return friendsList;
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
            numberOfConversations = conversationCounter - 1;
        }
    }

    private void addToConversations(Integer queueNumber, String conversationName) {
        this.conversations.put(queueNumber, conversationName);
    }

    Map<Integer, String> getConversations() {
        return conversations;
    }

    void printConversations() {
        if (this.conversations.size() >= 1) {
            System.out.println("\nYOUR CHATS:");

            for (Map.Entry<Integer, String> conversation : this.conversations.entrySet()) {
                System.out.println(conversation.getKey() + ". " + conversation.getValue());
            }
            System.out.println();
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

    synchronized private void createNewMessageLogFile() throws IOException {
        File file = new File(this.newMessageLogFileName);

        if (!file.exists()) {
            file.createNewFile();
        }
    }

    String getFriendsListFileName() {
        return friendsListFileName;
    }

    public int getNumberOfConversations() {
        return numberOfConversations;
    }
}
