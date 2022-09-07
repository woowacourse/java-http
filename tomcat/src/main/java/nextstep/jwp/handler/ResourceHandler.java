package nextstep.jwp.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.coyote.exception.FileNotExistException;
import org.apache.coyote.http11.FrontController;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

public class ResourceHandler {

    private static final String STATIC_RESOURCE_PATH = "static";

    public static HttpResponse perform(HttpRequest httpRequest) {
        return returnResource(httpRequest.getUri());
    }

    public static HttpResponse returnResource(String fileName) {
        try {
            Path filePath = findFilePath(fileName);
            String content = new String(Files.readAllBytes(filePath));

            String contentType = ContentType.findContentType(fileName);
            return new HttpResponse.Builder()
                    .statusCode(HttpStatus.OK)
                    .header(HttpHeaderType.CONTENT_TYPE, contentType)
                    .responseBody(content)
                    .build();
        } catch (IOException | FileNotExistException e) {
            return HttpResponse.notFound();
        }
    }

    private static Path findFilePath(String fileName) {
        try {
            return Path.of(Objects.requireNonNull(FrontController.class.getClassLoader()
                    .getResource(STATIC_RESOURCE_PATH + fileName)).getPath());
        } catch (NullPointerException e) {
            throw new FileNotExistException(fileName + " 파일이 존재하지 않습니다.");
        }
    }
}
