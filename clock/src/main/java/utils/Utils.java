package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<String> readFileByLine(File file) throws IOException {
        List<String> lineList = new ArrayList<String>();
        try (BufferedReader bReader = new BufferedReader(
                                      new FileReader(file))) {
            String line = null;
            while (null != (line = bReader.readLine()))
                lineList.add(line);
        } catch (IOException e) {
            throw e;
        }
        return lineList;
    }

    private Utils() {};
}
