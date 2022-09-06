package nextstep.jwp.presentation;

import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.web.response.HttpResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

public class RegisterController {

    public HttpResponse render() throws IOException {
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        return new HttpResponse(HttpStatus.OK, httpHeaders, ResourceLoader.getContent("register.html"));
    }

    public HttpResponse register(final String requestBody) throws IOException {
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        return new HttpResponse(HttpStatus.OK, httpHeaders, ResourceLoader.getContent("index.html"));
    }
}
