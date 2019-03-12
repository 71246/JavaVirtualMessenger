package jvm;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jvm.MessagingMethods.*;

class Conversation {
    private int id;
    private String name;
    private String fileName;
    private String alias;
    private List<String> participants = new ArrayList<>();
    private List<String> recipients = new ArrayList<>();
    private boolean groupChat;

    Conversation(String name, String alias, int id) {
        this.name = name;
        this.fileName = this.name + FinalClass.FILE_TYPE_SUFFIX;
        createTextFile(Paths.get(this.fileName));
        this.alias = alias;
        this.id = id;

        setParticipants(this.name.split(FinalClass.FILE_NAME_DELIMITER_DASH));
    }

    String getName() {
        return name;
    }

    String getFileName() {
        return fileName;
    }

    String getAlias() {
        return alias;
    }

    void setAlias(String alias) {
        this.alias = alias;
    }

    private void setParticipants(String[] participants) {
        this.participants.addAll(Arrays.asList(participants));
    }

    private List<String> getParticipants() {
        return this.participants;
    }

    boolean getGroupChat() {
        return groupChat;
    }

    void setGroupChat(boolean groupChat) {
        this.groupChat = groupChat;
    }

    private void setRecipients(String userName) {
        this.recipients.clear();
        for (String user: this.participants) {
            if (!user.equals(userName)) {
                this.recipients.add(user);
            }
        }
    }

    List<String> getRecipients(String userName) {
        setRecipients(userName);
        return this.recipients;
    }

    int getId() {
        return id;
    }
}
