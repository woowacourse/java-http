package nextstep.jwp.infrastructure.http.handler;

import java.nio.charset.Charset;
import java.util.Locale;
import nextstep.jwp.infrastructure.http.FileResolver;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.ResponseLine;
import nextstep.jwp.infrastructure.http.response.StatusCode;

public class FileHandler implements Handler {

    private static final String NOT_FOUND_FILE = "/404.html";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE_DELIMITER = ";";
    private static final String CHARSET_KEY = "charset=";

    private final FileResolver fileResolver;

    public FileHandler(final FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) throws Exception {
        final String filePath = request.getBaseUri();

        if (fileResolver.hasFile(filePath)) {
            respondWithFile(response, filePath, StatusCode.OK);
            return;
        }

        respondWithFile(response, NOT_FOUND_FILE, StatusCode.NOT_FOUND);
    }

    private void respondWithFile(final HttpResponse response, final String filePath, final StatusCode statusCode) {
        final String contentType = fileResolver.contentType(filePath);
        final String charSet = Charset.defaultCharset().displayName().toLowerCase(Locale.ROOT);
        final String responseBody = fileResolver.read(filePath);

        response.setResponseLine(new ResponseLine(statusCode));
        response.addHeader(CONTENT_TYPE,
            String.join(
                CONTENT_TYPE_DELIMITER,
                contentType,
                CHARSET_KEY + charSet
            )
        );
        response.addHeader(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
        response.setMessageBody(responseBody);
    }
}
