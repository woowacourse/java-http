package nextstep.jwp.utils;

import nextstep.jwp.exception.NotFoundFileException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static nextstep.jwp.model.Content.HTML;

public class ResponseUtil {

    public static String getResponseBody(final String uri, final Class<?> ClassType) {
        try {
            if (uri.equals("/")) {
                return "Hello world!";
            }
            final URL url = Objects.requireNonNull(ClassType.getClassLoader().getResource("static" + uri));
            final Path path = Paths.get(url.toURI());
            return new String(Files.readAllBytes(path));
        } catch (URISyntaxException | IOException | NullPointerException e) {
            throw new NotFoundFileException("파일 찾기에 실패했습니다.");
        }
    }

    public static String getExtension(final String uri) {
        Objects.requireNonNull(uri);
        if (uri.contains(".")) {
            return uri.substring(uri.lastIndexOf(".") + 1);
        }
        return HTML.getExtension();
    }

    public static String getPath(final String uri) {
        Objects.requireNonNull(uri);
        String path = uri;
        if (path.contains("?")) {
            path = path.substring(0, uri.indexOf("?"));
        }
        if (!path.contains(".")) {
            path += "." + HTML.getExtension();
        }
        return path;
    }

    public static Map<String, String> getParam(final String uri) {
        Objects.requireNonNull(uri);
        if (!uri.contains("?")) {
            return null;
        }
        List<String> inputs = Arrays.asList(uri.substring(uri.indexOf("?") + 1).split("&"));
        return calculateQueryParam(inputs);
    }

    private static Map<String, String> calculateQueryParam(List<String> inputs) {
        final Map<String, String> queryParams = new HashMap();
        for (String input : inputs) {
            List<String> query = Arrays.asList(input.split("="));
            queryParams.put(query.get(0), query.get(1));
        }
        return queryParams;
    }
}
