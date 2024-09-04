package org.apache.coyote.http11.response;

import static java.util.stream.Collectors.joining;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.coyote.http11.request.RequestStartLine;

public class HttpResponseCreator {
    private static final String STATIC_RESOURCE_LOCATION = "static";
    private static final Map<String, String> EXTENSION_TO_CONTENT_TYPE = Map.of(
            "html", "text/html", "css", "text/css", "js", "text/javascript");

    private HttpResponseCreator() {
    }

    public static String createResponse(RequestStartLine request) throws IOException {
        String resourceFilePath = request.getPath();
        URL resource = findResource(resourceFilePath);
        if (resource == null) {
            return makeDefaultResponse();
        }

        String responseBody = makeResponseBody(resource);
        return makeResponse(findExtension(resourceFilePath), responseBody);
    }

    @Nullable
    private static URL findResource(String path) {
        String fileName = STATIC_RESOURCE_LOCATION + path;
        return HttpResponseCreator.class.getClassLoader().getResource(fileName);
    }

    private static String makeResponseBody(URL resource) throws IOException {
        InputStream inputStream = resource.openStream();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.lines()
                    .collect(joining("\n"));
        }
    }

    private static String findExtension(String filePath) {
        int delimiterIndex = filePath.lastIndexOf(".");
        if (delimiterIndex == -1) {
            throw new UncheckedServletException("파일의 확장자가 존재하지 않습니다.");
        }
        return filePath.substring(delimiterIndex + 1);
    }

    private static String makeResponse(String extension, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + getContentType(extension) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private static String getContentType(String extension) {
        if (EXTENSION_TO_CONTENT_TYPE.containsKey(extension)) {
            return EXTENSION_TO_CONTENT_TYPE.get(extension);
        }
        return "text/html";
    }

    private static String makeDefaultResponse() {
        return makeResponse("html", "Hello world!");
    }
}
