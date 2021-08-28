package nextstep.jwp;

import nextstep.jwp.network.HttpMethod;
import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
        if (HttpMethod.GET.equals(httpRequest.getHttpMethod())) {
            return doGet(httpRequest);
        }
        if (HttpMethod.POST.equals(httpRequest.getHttpMethod())) {
            return doPost(httpRequest);
        }
        // TODO
        throw new IllegalArgumentException();
    }

    protected HttpResponse doGet(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }

    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }

    protected byte[] readFile(String resoureName) {
        try {
            final URL url = getClass().getClassLoader().getResource("static" + resoureName + ".html");
            final Path path = Paths.get(url.getPath());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            // TODO
            return "error".getBytes();
        }
    }

    protected byte[] readIndex() {
        return readFile("/index");
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
