package nextstep.joanne.server.http.request;

import nextstep.joanne.server.http.Headers;
import nextstep.joanne.server.http.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HttpRequestParser {
    public HttpRequestParser() {
    }

    public HttpRequest parse(final BufferedReader br) throws IOException {
        final StringBuilder stringBuilder = read(br);
        final String[] requests = stringBuilder.toString().split("\n");

        final RequestLine requestLine = parseRequestLine(requests[0]);
        final Headers requestHeaders = parseRequestHeaders(1,":",requests);
        final MessageBody messageBody = parseMessageBody(requestHeaders, br);

        return new HttpRequest(requestLine, requestHeaders, messageBody);
    }

    private RequestLine parseRequestLine(String request) {
        String[] requestLine = request.split(" ");
        final HttpMethod httpMethod = HttpMethod.valueOf(requestLine[0]);
        String uri = requestLine[1];
        Map<String, String> queryString = new HashMap<>();
        if (uri.contains("?")) {
            final int idx = uri.indexOf("?");
            queryString = parseExtraMessage(uri.substring(idx + 1));
            uri = uri.substring(0, idx);
        }
        final String version = requestLine[2];
        return new RequestLine(httpMethod, uri, queryString, version);
    }

    private Map<String, String> parseExtraMessage(String uri) {
        return Stream.of(uri.split("&"))
            .map(x -> x.split("="))
            .collect(Collectors.toMap(x -> x[0], x-> x[1]));
    }

    private Headers parseRequestHeaders(int index, String regex, String[] requests) {
        HashMap<String, String> headers = IntStream.range(index, requests.length)
                .mapToObj(i -> requests[i].split(regex))
                .collect(Collectors.toMap(
                        x -> x[0],
                        y -> y[1].trim(),
                        (a, b) -> b,
                        HashMap::new
                ));
        return checkHasCookie(new Headers(headers));
    }

    private Headers checkHasCookie(Headers headers) {
        if (headers.hasCookie()) {
            headers.putCookie(headers.get("Cookie"));
        }
        return headers;
    }

    private MessageBody parseMessageBody(Headers requestHeaders,
                                         BufferedReader br) throws IOException {
        if (requestHeaders.contains("Content-Length")) {
            int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            return new MessageBody(parseExtraMessage(new String(buffer, 0, br.read(buffer, 0, contentLength))));
        }
        return null;
    }

    private StringBuilder read(BufferedReader br) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line = br.readLine();
        while (!"".equals(line)) {
            if (line == null) break;
            stringBuilder.append(line).append("\n");
            line = br.readLine();
        }
        return stringBuilder;
    }

}
