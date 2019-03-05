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
        this.instantMessaging = new InstantMessaging();
    }

    @Override
    public void run() {

        try {
            while (!user.getCurrentConversation().equals("")) {
                Thread.sleep(5000);
                instantMessaging.checkIncomingOngoingChatMessages(user, chatName, messageFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
