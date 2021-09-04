package nextstep.jwp.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import nextstep.jwp.exception.handler.BadRequestException;
import nextstep.jwp.exception.handler.DefaultFileNotFoundException;
import nextstep.jwp.exception.handler.InternalServerException;
import nextstep.jwp.exception.handler.NotFoundException;

public class FileReader {

    public static final String STATIC_FILE_URL_PREFIX = "static";

    public static File readFile(String fileUri) {
        try {
            URL resourceUrl = getFileUri(STATIC_FILE_URL_PREFIX + fileUri);
            String content = Files.readString(Paths.get(resourceUrl.toURI()));
            ContentType type = ContentType.findType(resourceUrl);
            return new File(type, content);
        } catch (FileNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (URISyntaxException e) {
            throw new BadRequestException(e.getMessage());
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public static File readErrorFile(String fileUri) {
        try {
            URL resourceUrl = getFileUri(STATIC_FILE_URL_PREFIX + fileUri);
            String content = Files.readString(Paths.get(resourceUrl.toURI()));
            ContentType type = ContentType.findType(resourceUrl);
            return new File(type, content);
        } catch (URISyntaxException | IOException e) {
            throw new DefaultFileNotFoundException(e.getMessage());
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
