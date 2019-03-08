package jvm;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static jvm.MessagingMethods.*;

class Users {
    private List<User> userList;

    public Users() {
        this.userList = getUserListFromFile();
    }

    private static List<User> getUserListFromFile() {
        try {
            if (createTextFile(FinalClass.USER_LIST_PATH)) {
                List<String> lines = Files.readAllLines(FinalClass.USER_LIST_PATH);

                for (String line: lines) {
                    
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
