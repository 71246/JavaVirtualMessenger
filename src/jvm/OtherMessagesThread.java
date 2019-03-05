package jvm;

import java.io.IOException;

public class OtherMessagesThread extends Thread {

    private User user;
    private String chatName;
    private InstantMessaging instantMessaging;

    OtherMessagesThread(User user, String chatName) {
        this.user = user;
        this.chatName = chatName;
        this.instantMessaging = new InstantMessaging();
    }

    @Override
    public void run() {

        try {
            while (!user.getCurrentConversation().equals("")) {
                Thread.sleep(5000);
                instantMessaging.checkOtherIncomingMessages(user, chatName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
