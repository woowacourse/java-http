package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class AbstractController implements Controller {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if ("GET".equals(httpRequest.getMethod())) {
            doGet(httpRequest, httpResponse);
        }
        if ("POST".equals(httpRequest.getMethod())) {
            doPost(httpRequest, httpResponse);
        }
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

    }

    protected String createBody(String filePath) throws IOException {
        final URL url = getClass().getClassLoader().getResource("static" + filePath);
        File file = new File(Objects.requireNonNull(url).getFile());
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes);
    }
}
