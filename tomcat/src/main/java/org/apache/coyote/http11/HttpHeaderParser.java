package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Stream;
import org.apache.coyote.exception.ResourceNotFoundException;

public class HttpHeaderParser {

    private static final String HTTP_METHOD_GET = "GET";

    public String extractStrings(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        final StringJoiner stringJoiner = new StringJoiner("\r\n");

        String str = br.readLine();
        while (br.ready()) {
            stringJoiner.add(str);
            str = br.readLine();
        }
        stringJoiner.add("");

        return stringJoiner.toString();
    }

    public Path parseFilePath(String httpRequest) throws URISyntaxException, IOException {
        String[] firstLine = parseStartLine(httpRequest, HTTP_METHOD_GET);
        HttpMethod httpMethod = HttpMethod.valueOf(firstLine[0]);

        if (firstLine.length > 1 && (httpMethod == HttpMethod.GET)) {
            String fileName = extractFileName(firstLine);

            Path dirPath = Paths.get(System.getProperty("user.dir"), "tomcat/build/resources");

            Stream<Path> walk = Files.walk(dirPath);
            Path filePath = walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equalsIgnoreCase(fileName))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(fileName));
            
            return filePath.toAbsolutePath();
        }
        throw new URISyntaxException(httpRequest, "사용 불가능한 HTTP 요청입니다.");
    }

    private String[] parseStartLine(String httpRequest, String targetHeaderKey) {
        String[] lines = httpRequest.split("\r\n");
        for (String line : lines) {
            if (line.startsWith(targetHeaderKey)) {
                return line.split(" ");
            }
        }
        throw new IllegalArgumentException(targetHeaderKey + "가 존재하지 않습니다.");
    }

    public String extractFileName(String[] startLine) {
        String requestUri = startLine[1];
        String uri = requestUri.substring(requestUri.lastIndexOf("/") + 1);
        if (uri.isEmpty()) {
            return "index.html";
        }
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            String fileName = uri.substring(0, index);
            return fileName + ".html";
        }
        return uri;
    }

    public boolean containsQueryParameters(String httpRequest) {
        String startLine = parseStartLine(httpRequest, HTTP_METHOD_GET)[1];
        return startLine.contains("?");
    }

    public Map<String, String> extractQueryParameters(String httpRequest) {
        String startLine = parseStartLine(httpRequest, HTTP_METHOD_GET)[1];
        String queryParams = startLine.substring(startLine.indexOf("?") + 1);

        Map<String, String> paramMap = new HashMap<>();
        String[] pairs = queryParams.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");

            String key = keyValue[0];
            String value = "";

            if (keyValue.length > 1) {
                value = keyValue[1];
            }
            paramMap.put(key, value);
        }

        return paramMap;
    }
}
