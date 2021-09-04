package nextstep.jwp.http.file;

import nextstep.jwp.http.exception.FileReadFailureException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticFile {

    private final File file;

    public StaticFile(File file) {
        this.file = file;
    }

    public byte[] toBytes() {
        Path path = file.toPath();
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new FileReadFailureException(String.format("File 을 읽는데 실패했습니다. (%s)", path));
        }
    }

    public int byteLength() {
        return toBytes().length;
    }

    public final FileExtension fileExtension() {
        return FileExtension.extractExtension(file.getPath());
    }
}
