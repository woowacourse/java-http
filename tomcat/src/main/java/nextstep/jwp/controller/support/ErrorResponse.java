package nextstep.jwp.controller.support;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class ErrorResponse {

    public static HttpResponse getNotFound(final Class<?> classes, final HttpRequest httpRequest) {
        try{
            URL resource = classes.getResource("/static/404.html");
            String body = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
            return HttpResponse.of(ResponseStatusCode.NOT_FOUND, httpRequest.getVersion(), ContentType.HTML, body);
        } catch (Exception exception) {
            throw new IllegalArgumentException("파일을 불러올 수 없습니다.");
        }
    }
}
