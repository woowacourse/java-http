package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.exception.HttpMethodNotAllowedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Headers;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        RequestMapping requestMapping = new RequestMapping();

        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
        throw new HttpMethodNotAllowedException("유효하지 않은 HTTP method 입니다.");
    }

    protected HttpResponse doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */
        return null;
    }

    protected HttpResponse doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */
        return null;
    }

    protected String readResourceBody(String url) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + url);
        final Path path = new File(resource.getFile()).toPath();
        return new String(Files.readAllBytes(path));
    }

    protected Headers readResourceHeader(final String url, final String body) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + url);
        final Path path = new File(resource.getFile()).toPath();

        Headers headers = new Headers();
        headers.setContentType(Files.probeContentType(path));
        headers.setContentLength(body.getBytes().length);

        return headers;
    }
}
