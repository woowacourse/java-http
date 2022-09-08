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

public class ResourceController extends AbstractController {

    private static final String STATIC_RESOURCE_PATH = "static";

    private static final ResourceController INSTANCE = new ResourceController();

    public static ResourceController getInstance() {
        return INSTANCE;
    }

    private ResourceController() {
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        returnResource(request.getUri(), response);
    }

    public static void returnResource(String fileName, HttpResponse response) {
        try {
            Path filePath = findFilePath(fileName);
            String content = new String(Files.readAllBytes(filePath));

            String contentType = ContentType.findContentType(fileName);

            response.statusCode(HttpStatus.OK);
            response.addHeader(HttpHeaderType.CONTENT_TYPE, contentType);
            response.responseBody(content);
        } catch (IOException | FileNotExistException e) {
            response.statusCode(HttpStatus.NOT_FOUND);
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
