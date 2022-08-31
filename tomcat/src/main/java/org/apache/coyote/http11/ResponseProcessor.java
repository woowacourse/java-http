package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ResponseProcessor {

    private static final String STATIC_PATH = "static/";

    private String path;
    private final Map<String, String> queryParameterMap = new HashMap<>();

    private final String responseBody;

    public ResponseProcessor(String uri) throws URISyntaxException, IOException {
        path = uri;
        int queryParameterIndex = uri.indexOf("?");
        if (queryParameterIndex != -1) {
            path = uri.substring(0, queryParameterIndex);
            String queryParameters = uri.substring(queryParameterIndex + 1);
            String[] eachQueryParameters = queryParameters.split("&");
            for (String queryParameter : eachQueryParameters) {
                String[] keyValues = queryParameter.split("=");
                queryParameterMap.put(keyValues[0], keyValues[1]);
            }
        }

        responseBody = processResponseBody();
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + extractContentType() + ";charset=utf-8 ",
                "Content-Length: " + extractContentLength() + " ",
                "",
                responseBody);
    }

    public boolean existQueryParameter() {
        return !queryParameterMap.isEmpty();
    }

    public Map<String, String> getQueryParameterMap() {
        return queryParameterMap;
    }

    private String processResponseBody() throws URISyntaxException, IOException {
        if (isFileRequest()) {
            String fileName = getFileName();
            return readFile(fileName);
        }
        if (login()) {
            String fileName = getFileName().concat(".html");
            return readFile(fileName);
        }
        return "Hello world!";
    }

    private String readFile(String fileName) throws URISyntaxException, IOException {
        final URI fileUri = getClass().getClassLoader().getResource(STATIC_PATH + fileName).toURI();
        byte[] lines = Files.readAllBytes(Paths.get(fileUri));
        return new String(lines);
    }

    private boolean login() {
        return path.contains("login");
    }

    private boolean isFileRequest() {
        return path.contains(".");
    }

    private String getFileName() {
        return path.substring(1);
    }

    private String extractContentType() {
        if (path.endsWith("css")) {
            return "css";
        }
        return "html";
    }

    private int extractContentLength() {
        return responseBody.getBytes().length;
    }
}
