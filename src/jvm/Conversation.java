package jvm;

import java.util.ArrayList;

class Conversation {
    private String name;
    private String fileName;
    private String alias;
    private ArrayList<User> participants;

    Conversation(String name) {
        this.name = name;
        this.fileName = name + FinalClass.FILE_TYPE_SUFFIX;
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

    void setParticipants(User user) {
        this.participants.add(user);
    }

    ArrayList<User> getParticipants() {
        return this.participants;
    }
}
