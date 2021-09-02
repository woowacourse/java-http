package nextstep.common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import nextstep.jwp.framework.infrastructure.http.content.ContentType;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse.Builder;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;

public class TestUtil {

    private static final MockIdGenerator GENERATOR = new MockIdGenerator();

    private TestUtil() {
    }

    public static String writeResponseWithCookie(String url, HttpStatus httpStatus) {
        try {
            url = "/static" + url;
            Path path = Paths.get(TestUtil.class.getResource(url).toURI());
            String responseBody = String.join("\r\n", Files.readAllLines(path));
            Builder builder = new Builder()
                .httpStatus(httpStatus)
                .protocol(Protocol.HTTP1_1)
                .contentType(ContentType.find(url))
                .responseBody(responseBody)
                .cookie("JSESSIONID", GENERATOR.generateId());
            if (httpStatus == HttpStatus.FOUND) {
                builder.location("/index.html");
            }
            return builder.build().writeResponseMessage();
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public static String writeResponse(String url, HttpStatus httpStatus) {
        try {
            url = "/static" + url;
            Path path = Paths.get(TestUtil.class.getResource(url).toURI());
            String responseBody = String.join("\r\n", Files.readAllLines(path));
            Builder builder = new Builder()
                .httpStatus(httpStatus)
                .protocol(Protocol.HTTP1_1)
                .contentType(ContentType.find(url))
                .responseBody(responseBody);
            if (httpStatus == HttpStatus.FOUND) {
                builder.location("/index.html");
            }
            return builder.build().writeResponseMessage();
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
