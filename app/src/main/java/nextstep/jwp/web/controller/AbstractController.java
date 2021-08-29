package nextstep.jwp.web.controller;

import nextstep.jwp.web.exception.InputException;
import nextstep.jwp.web.network.request.HttpMethod;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractController implements Controller {

    private final String resource;

    protected AbstractController(String resource) {
        this.resource = resource;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public HttpResponse execute(HttpRequest httpRequest) {
        if (HttpMethod.POST.equals(httpRequest.getHttpMethod())) {
            return doPost(httpRequest);
        }
        return doGet(httpRequest);
    }

    protected HttpResponse doGet(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }

    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }

    protected byte[] readIndex() {
        return readFile("/index.html");
    }

    protected byte[] readHtmlFile(String fileName) {
        return readFile(fileName + ".html");
    }

    protected byte[] readFile(String fileName) {
        try {
            final URL url = getClass().getClassLoader().getResource("static" + fileName);
            final Path path = Paths.get(Objects.requireNonNull(url).getPath());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new InputException("static" + fileName);
        }
    }

    protected Map<String, String> extractQuery(String queryString) {
        final Map<String, String> queryInfo = new HashMap<>();
        final String[] queries = queryString.split("&");
        for (String query : queries) {
            final int index = query.indexOf('=');
            final String key = query.substring(0, index);
            final String value = query.substring(index + 1);
            queryInfo.put(key, value);
        }
        return queryInfo;
    }
}
