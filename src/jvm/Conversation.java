package jvm;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Conversation {
    private int id;
    private String name;
    private String fileName;
    private String alias;
    private ArrayList<String> participants;
    private ArrayList<String> recipients;
    private boolean groupChat;

    Conversation(String name, String alias, int id, String[] participants) {
        this.name = name;
        this.fileName = this.name + FinalClass.FILE_TYPE_SUFFIX;
        this.alias = alias;
        this.id = id;

        setParticipants(participants);
    }

    String getName() {
        return name;
    }

    String getFileName() {
        return fileName;
    }

    private String getAlias() {
        return alias;
    }

    void setAlias(String alias) {
        this.alias = alias;
    }

    private void setParticipants(String[] participants) {
        this.participants.addAll(Arrays.asList(participants));
    }

    private ArrayList<String> getParticipants() {
        return this.participants;
    }

    boolean getGroupChat() {
        return groupChat;
    }

    void setGroupChat(boolean groupChat) {
        this.groupChat = groupChat;
    }

    private void setRecipients(String userName) {
        for (String user: this.participants) {
            if (!user.equals(userName)) {
                this.recipients.add(user);
            }
        }
    }

    ArrayList<String> getRecipients(String userName) {
        setRecipients(userName);
        return this.recipients;
    }

    private int getId() {
        return id;
    }

    void addToConverationsFile(Conversation conversation) {
        FileWriter writer;

        try {
            writer = new FileWriter(String.valueOf(conversation.getFileName()), true);
            writer.append(conversation.getName());
            writer.append(FinalClass.CSV_DELIMITER);
            writer.append(conversation.getAlias());
            writer.append(FinalClass.CSV_DELIMITER);
            writer.append(String.valueOf(conversation.getId()));
            writer.close();
        } catch (IOException e) {
            System.out.println("A problem occurred while trying to write to " + conversation.getFileName() + "!");
        }
    }
}
