package jvm;

import static jvm.InstantMessaging.*;

public class ThreadClass extends Thread {

    private User user;

    ThreadClass(User user) {
        this.user = user;
    }

    @Override
    public void run() {

        try {
            while (!(this.user.getCurrentConversation() == null)) {
                Thread.sleep(3000);
                checkNewChatMessages(user);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
