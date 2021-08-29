package nextstep.jwp.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    public static File readFile(String fileUri) throws FileNotFoundException {
        URL resourceUrl = getFileUri("static" + fileUri);

        try {
            String content = Files.readString(Paths.get(resourceUrl.toURI()));
            ContentType type = ContentType.findType(resourceUrl);
            return new File(type, content);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static File readHtmlFile(String fileUri) throws FileNotFoundException {
        URL resourceUrl = getFileUri("static" + fileUri + ".html");

        try {
            String content = Files.readString(Paths.get(resourceUrl.toURI()));
            ContentType type = ContentType.findType(resourceUrl);
            return new File(type, content);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static File readErrorFile(String fileUri) {
        URL resourceUrl = Thread.currentThread()
                                .getContextClassLoader()
                                .getResource("static" + fileUri);

        if (resourceUrl == null) {
            throw new IllegalArgumentException("경로에 파일이 존재하지 않습니다.");
        }

        try {
            String content = Files.readString(Paths.get(resourceUrl.toURI()));
            ContentType type = ContentType.findType(resourceUrl);
            return new File(type, content);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private static URL getFileUri(String uri) throws FileNotFoundException {
        URL resourceUrl = Thread.currentThread()
                                .getContextClassLoader()
                                .getResource(uri);

        if (resourceUrl == null) {
            throw new FileNotFoundException("경로에 파일이 존재하지 않습니다.");
        }
        return resourceUrl;
    }
}
