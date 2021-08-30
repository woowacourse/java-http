package nextstep.joanne.converter;

import nextstep.joanne.http.HttpStatus;
import nextstep.joanne.http.request.HttpRequest;
import nextstep.joanne.http.response.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class HttpRequestResponseConverter {

    private HttpRequestResponseConverter() {
    }

    public static HttpRequest convertToHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder requestHeaders = new StringBuilder();

        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            if (line == null) break;
            requestHeaders.append(line).append("\n");
            line = bufferedReader.readLine();
        }

        String[] splitRequestHeaders = requestHeaders.toString().split("\n");
        String[] requestLine = splitRequestHeaders[0].split(" ");

        HashMap<String, String> headers = splitBy(1, ":", splitRequestHeaders);

        String requestBody = "";
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            requestBody = new String(buffer, 0, bufferedReader.read(buffer, 0, contentLength));
        }

        return new HttpRequest.Builder()
                .method(requestLine[0])
                .uri(requestLine[1])
                .versionOfProtocol(requestLine[2])
                .requestHeaders(headers)
                .requestMessageBody(splitMessageBodyByAmpersand(requestBody))
                .build();
    }

    private static HashMap<String, String> splitBy(int startIdx, String regex, String[] splitRequestHeaders) {
        HashMap<String, String> headers = new HashMap<>();
        for (int i = startIdx; i < splitRequestHeaders.length; i++) {
            String[] separateHeaderByColon = splitRequestHeaders[i].split(regex);
            headers.put(separateHeaderByColon[0], separateHeaderByColon[1].trim());
        }
        return headers;
    }

    private static HashMap<String, String> splitMessageBodyByAmpersand(String messageBody) {
        if (messageBody.isBlank()) {
            return null;
        }
        return splitBy(0, "=", messageBody.split("&"));
    }

    public static HttpResponse convertToHttpResponse(HttpStatus httpStatus, String uri, String contentType) {
        String byteBody = FileConverter.getResource(uri);
        return new HttpResponse(httpStatus, makeBody(httpStatus, contentType, byteBody));
    }

    private static String makeBody(HttpStatus httpStatus, String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.value() + " " + httpStatus.responsePhrase() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
