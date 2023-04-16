import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Upload {

    public static void pathToFile(String filePath, String goingTo) throws FileNotFoundException {
        // Check if file path is local or remote
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();

        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            // Remote file path
            try {

                if (aux(filePath).equals("csv")) {
                    Functions.csvToJSON(filePath, goingTo);
                    System.out.println("Remote file type is valid and its csv.");

                } else if(aux(filePath).equals("json")){
                    //chamar Functions.jsonTocsv()
                    System.out.println("Remote file type is valid and its json.");
                }else {
                    System.out.println("Remote file type is invalid.");
                }
            } catch (Exception e) {
                System.out.println("Failed to read remote file: " + e.getMessage());
            }
        } else {
            // Local file path
            String extension = null;
            try {
                extension = aux(filePath);
            } catch (Exception e) {
                System.out.println("Failed to read file type: " + e.getMessage());
            }
            if (extension!= null && extension.equals("csv")) {
                Functions.csvToJSON(filePath, goingTo);
                System.out.println("local file type is valid and its csv.");

            } else if(aux(filePath).equals("json")){
                //chamar Functions.jsonTocsv
                System.out.println("local file type is valid and its json.");
            } else {
                System.out.println("Local file type is invalid.");
            }
        }
    }

    public static String aux(String filePath) throws NullPointerException{

        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();

        String extension = null;
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            return extension = fileName.substring(index + 1).toLowerCase();
        }else
            return extension;
    }
}
