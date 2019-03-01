package JVM;

import java.nio.file.Path;
import java.nio.file.Paths;

class FinalClass {
    final Path USER_LIST_PATH = Paths.get("MessengerUserList.txt");
    final String CSV_DELIMITER = ";";
    final String NEW_MESSAGE_LOG_SUFFIX = "-NewMessageLog.txt";
    final String MESSAGE_FILE_NAME_DELIMITER = "-";
}
