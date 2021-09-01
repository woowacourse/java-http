package nextstep.jwp.web;

import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class HttpRequestParser {
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final int METHOD_INDEX = 0;
    private static final int URI_LINE_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int REQUEST_URI_INDEX = 0;
    private static final int PATH_PARAMETER_INDEX = 1;
    private static final int FIRST_INDEX_OF_STRING = 0;

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest.HttpRequestBuilder httpRequestBuilder = HttpRequest.builder();

        String startLine = bufferedReader.readLine();
        parseStartLine(startLine, httpRequestBuilder);

        Map<String, String> headers = parseHeaders(bufferedReader);
        httpRequestBuilder.headers(headers);

        if (headers.containsKey(CONTENT_LENGTH)) {
            String body = parseBody(bufferedReader, Integer.parseInt(headers.get(CONTENT_LENGTH)));
            httpRequestBuilder.body(body);
        }

        return httpRequestBuilder.build();
    }

    private static void parseStartLine(String startLine, HttpRequest.HttpRequestBuilder httpRequestBuilder) {
        String[] startLineElement = startLine.split(" ");

        HttpMethod method = HttpMethod.of(startLineElement[METHOD_INDEX]);
        httpRequestBuilder
                .method(method);

        String rawRequestUri = startLineElement[URI_LINE_INDEX];

        parseRequestUri(rawRequestUri, httpRequestBuilder);
        httpRequestBuilder.httpVersion(HttpVersion.of(startLineElement[HTTP_VERSION_INDEX]));
    }

    private static void parseRequestUri(String rawRequestUri, HttpRequest.HttpRequestBuilder httpRequestBuilder) {
        if (rawRequestUri.contains("?")) {
            String[] uriElements = rawRequestUri.split("\\?");
            String requestUri = uriElements[REQUEST_URI_INDEX];
            String parameterLine = uriElements[PATH_PARAMETER_INDEX];

            Map<String, String> parameters = Arrays.stream(parameterLine.split("&"))
                    .map(row -> row.split("="))
                    .collect(toMap(element -> element[0], element -> element[1]));

            httpRequestBuilder
                    .requestUri(URI.create(requestUri))
                    .parameters(parameters);
            return;
        }
        httpRequestBuilder.requestUri(URI.create(rawRequestUri));
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

    private static String parseBody(Reader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

}
