package nextstep.jwp.infra;

import java.io.IOException;
import java.nio.file.Files;

public class FileReader {

    private final FilePath filePath;

    public FileReader(FilePath filePath) {
        this.filePath = filePath;
    }

    public String readAllFile() throws IOException {
        byte[] byteData = Files.readAllBytes(filePath.path());
        return new String(byteData);
    }
}
