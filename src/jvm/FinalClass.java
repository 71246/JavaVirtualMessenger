package jvm;

import java.nio.file.Path;
import java.nio.file.Paths;

class FinalClass {
    final static String FILE_TYPE_SUFFIX = ".txt";
    final static String CSV_DELIMITER = ";";
    final static String FILE_NAME_DELIMITER_DASH = "-";
    final static String NEW_MESSAGE_LOG_SUFFIX = FILE_NAME_DELIMITER_DASH + "NewMessages" + FILE_TYPE_SUFFIX;
    final static String TIME_STAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    final static String TIME_STAMP_TAG = "<timestamp>";
    final static String PASSWORD_PATTERN = "^(?=.{8,})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$";
    final static Path USER_LIST_PATH = Paths.get("MessengerUserList" + FILE_TYPE_SUFFIX);
    final static Path USER_CONVERSATIONS_PATH_SUFFIX = Paths.get(FILE_NAME_DELIMITER_DASH + "Conversations" + FILE_TYPE_SUFFIX);
    final static int MENU_LINE_LENGTH = 80;
}
