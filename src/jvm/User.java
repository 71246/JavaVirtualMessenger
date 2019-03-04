package jvm;

import java.util.ArrayList;

class User {
    private String userName;
    private String password;
    private ArrayList<String> friendsList;
    private ArrayList<String> conversations;

    User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    String getUserName() {
        return userName;
    }

    public void addToFriendsList(String newFriend) {
        this.friendsList.add(newFriend);
    }

    public ArrayList<String> getFriendsList() {
        return friendsList;
    }

    public void addToConversations(String conversationName) {
        this.conversations.add(conversationName);
    }

    public ArrayList<String> getConversations() {
        return conversations;
    }
}
