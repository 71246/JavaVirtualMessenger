package jvm;

import java.io.IOException;

public class OngoingMessagesThread extends Thread {

    private User user;
    private String chatName;
    private String messageFilePath;
    private InstantMessaging instantMessaging;

    OngoingMessagesThread(User user, String chatName, String messageFilePath) {
        this.user = user;
        this.chatName = chatName;
        this.messageFilePath = messageFilePath;
    }

    @Override
    public void run() {
        try {
            while (!user.getCurrentConversation().equals("")) {
                instantMessaging.checkIncomingOngoingChatMessages(user, chatName, messageFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
