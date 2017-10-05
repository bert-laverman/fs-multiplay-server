package nl.rakis.fs.auth;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    static void createFile(File path, String content) throws IOException {
        FileWriter wr = new FileWriter(path);
        wr.write(content);
        wr.close();
    }
}
