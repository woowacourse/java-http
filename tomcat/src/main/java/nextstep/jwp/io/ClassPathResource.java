package nextstep.jwp.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import nextstep.jwp.exception.FileNotFoundException;

public class ClassPathResource {

    public String getStaticContent(final String url) {
        URL resource = getClass().getClassLoader().getResource("static" + url);
        try {
            File file = new File(Objects.requireNonNull(resource).getFile());
            byte[] fileContents = Files.readAllBytes(file.toPath());
            return new String(fileContents);
        } catch (IOException | NullPointerException e) {
            throw new FileNotFoundException("해당 경로로 파일이 존재하지 않습니다.");
        }
    }
}
