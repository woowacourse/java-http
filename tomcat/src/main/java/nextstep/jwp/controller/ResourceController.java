package nextstep.jwp.controller;

import java.util.Set;
import org.apache.coyote.fileReader.FileReader;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class ResourceController extends AbstractController {

    private static final String URL = "";
    private static final Set<HttpMethod> AVAILABLE_HTTP_METHODS = Set.of(HttpMethod.GET);

    public ResourceController() {
        super(URL, AVAILABLE_HTTP_METHODS);
    }

    @Override
    protected void doGet(Request request, Response response) {
        FileReader fileReader = FileReader.from(request.getPath());
        String body = fileReader.read();

        response.setHttpStatus(getHttpStatusResponse(fileReader))
                .addHeaders(CONTENT_TYPE, request.getResourceTypes() + FINISH_VALUE + ENCODING_UTF_8)
                .addHeaders(CONTENT_LENGTH, String.valueOf(body.getBytes().length))
                .setResponseBody(body);
    }

    private HttpStatus getHttpStatusResponse(FileReader fileReader) {
        if (fileReader.isFound()) {
            return HttpStatus.OK;
        }
        return HttpStatus.NOT_FOUND;
    }
}
