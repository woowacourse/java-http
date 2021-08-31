package nextstep.jwp.resource;

import java.io.IOException;
import java.nio.file.Files;

public class FileReader {

    private final FilePath filePath;

    public FileReader(FilePath filePath) {
        this.filePath = filePath;
    }

    public String readAllFile() {
        byte[] byteData;
        try {
            byteData = Files.readAllBytes(filePath.path());
        } catch (IOException e) {
            throw new IllegalArgumentException(
                String.format("파일을 읽는데 실패했습니다. -> %s", filePath.path()), e.getCause());
        }
        return new String(byteData);
    }

}
