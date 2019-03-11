package jvm;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jvm.MessagingMethods.*;

class UserList {
    private static Map<String, String> userList = new HashMap<>();

    static void populateUserList() {
        String[] splitLine;

        try {
            createTextFile(FinalClass.USER_LIST_PATH);
            List<String> lines = Files.readAllLines(FinalClass.USER_LIST_PATH);

            for (String line: lines) {
                splitLine = line.split(FinalClass.CSV_DELIMITER);
                userList.put(splitLine[0], splitLine[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("A problem occurred while trying to read from user list!");
        }
    }

    static boolean isUserNameInvalid(String nameToCheck) {
        return !userList.containsKey(nameToCheck);
    }

    static boolean isPasswordValid(String userName, String passwordToCheck) {
        return userList.get(userName).equals(passwordToCheck);
    }
}
