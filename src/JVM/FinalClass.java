package JVM;

import java.nio.file.Path;
import java.nio.file.Paths;

class FinalClass {
    final static String FILE_TYPE_SUFFIX = ".txt";
    final static Path USER_LIST_PATH = Paths.get("MessengerUserList" + FILE_TYPE_SUFFIX);
    final static String CSV_DELIMITER = ";";
    final static String NEW_MESSAGE_LOG_SUFFIX = "-NewMessageLog" + FILE_TYPE_SUFFIX;
    final static String MESSAGE_FILE_NAME_DELIMITER = "-";
}
