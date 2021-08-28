package nextstep.jwp.web;

import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int FIRST_INDEX_OF_STRING = 0;
    private static final String CONTENT_LENGTH = "Content-Length";

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String startLine = bufferedReader.readLine();
        String[] startLineElement = startLine.split(" ");

        HttpMethod method = HttpMethod.of(startLineElement[METHOD_INDEX]);
        URI requestUri = URI.create(startLineElement[URI_INDEX]);

        Map<String, String> headers = parseHeaders(bufferedReader);

        HttpRequest.HttpRequestBuilder httpRequestBuilder = HttpRequest.builder()
                .method(method)
                .requestUri(requestUri)
                .headers(headers);

        if (headers.containsKey(CONTENT_LENGTH)) {
            String body = parseBody(bufferedReader, Integer.parseInt(headers.get(CONTENT_LENGTH)));
            httpRequestBuilder.body(body);
        }

        return httpRequestBuilder.build();
    }

    private static String parseBody(Reader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while (!"".equals((line = bufferedReader.readLine()))) {
            if (line == null) {
                break;
            }
            int indexOfHeaderValueDelimiter = line.indexOf(":");

            String key = line.substring(FIRST_INDEX_OF_STRING, indexOfHeaderValueDelimiter).trim();
            String value = line.substring(indexOfHeaderValueDelimiter + 1).trim();
            headers.put(key, value);
        }

        return headers;
    }

}
