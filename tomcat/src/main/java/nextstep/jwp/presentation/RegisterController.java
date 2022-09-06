package nextstep.jwp.presentation;

import nextstep.jwp.presentation.dto.UserRegisterRequest;
import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.LinkedHashMap;

public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    public HttpResponse render() throws IOException {
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        return new HttpResponse(HttpStatus.OK, httpHeaders, ResourceLoader.getContent("register.html"));
    }

    public HttpResponse register(final UserRegisterRequest userRegisterRequest) throws IOException {
        log.info("userRegisterRequest: {}", userRegisterRequest);
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        return new HttpResponse(HttpStatus.OK, httpHeaders, ResourceLoader.getContent("index.html"));
    }
}
