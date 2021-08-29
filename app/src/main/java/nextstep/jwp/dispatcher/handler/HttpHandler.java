package nextstep.jwp.dispatcher.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.message.ContentType;
import nextstep.jwp.http.message.HttpHeaders;
import nextstep.jwp.http.message.HttpMethod;
import nextstep.jwp.http.message.HttpStatus;

public abstract class HttpHandler implements Handler {

    protected HttpHandler() {
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpMethod method = httpRequest.getMethod();
        if (method == HttpMethod.GET) {
            doGet(httpRequest, httpResponse);
        } else if (method == HttpMethod.POST) {
            doPost(httpRequest, httpResponse);
        }
    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
    }

    protected void renderPage(String fileName, HttpStatus httpStatus, HttpResponse httpResponse) {
        URL fileResource = getClass().getClassLoader().getResource(fileName);
        Path path = Paths.get(fileResource.getPath());
        try (InputStream fileInputStream = new FileInputStream(path.toFile())) {
            byte[] bytes = fileInputStream.readAllBytes();
            String content = new String(bytes);

            httpResponse.setContent(content);
        } catch (IOException e) {
            throw new NotFoundException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Length", String.valueOf(httpResponse.getContentAsString().length()));
        headers.addHeader("Content-Type", ContentType.HTML.getDescription());

        httpResponse.setHeaders(headers);
        httpResponse.setStatus(httpStatus);
        httpResponse.setVersionOfProtocol("HTTP/1.1");
    }

    protected void redirectTo(String redirectUrl, HttpResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Location", redirectUrl);

        response.setHeaders(headers);
        response.setStatus(HttpStatus.FOUND);
        response.setVersionOfProtocol("HTTP/1.1");
    }
}
