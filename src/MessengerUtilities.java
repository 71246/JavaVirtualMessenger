import java.io.*;

public class MessengerUtilities {
    public boolean checkStringExistsInCSV(String stringToFind, String delimiter, String filePath, int posToCheck) {
        String line;
        BufferedReader br = null;
        String[] userName;

        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                userName = line.split(delimiter);
                if (userName[posToCheck].equals(stringToFind)) {
                    return true;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public void createTextFileIfNotCreated(String fileName) throws IOException {
        File userList = new File(fileName);

        if (!userList.exists()) {
            userList.createNewFile();
            System.out.println("The file is already in the current root directory.");
        }
    }

    public void appendToCSV(String stringToAdd, String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath, true);
        writer.append("\n" + stringToAdd);
        writer.close();
    }
}
