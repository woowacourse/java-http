package org.apache.coyote.http11.http11request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Http11RequestHandler {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final String FIRST_LINE_DELIMITER = " ";

    public Http11Request makeRequest(BufferedReader bufferedReader) throws IOException {
        String[] firstLineDatas = bufferedReader.readLine().split(FIRST_LINE_DELIMITER);
        String httpMethod = firstLineDatas[HTTP_METHOD_INDEX];
        String uri = firstLineDatas[URI_INDEX];
        Map<String, String> headers = parseHeaders(bufferedReader);

        String body = parseBody(headers.get("Content-Length"), bufferedReader);

        return new Http11Request(httpMethod, uri, headers, body);
    }

    private Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String data = bufferedReader.readLine();
            if (data == null || data.equals("")) {
                break;
            }
            String[] header = data.split(":");
            headers.put(header[HEADER_NAME_INDEX].strip(), header[HEADER_VALUE_INDEX].strip());
        }
        return headers;
    }

    private String parseBody(String contentLength, BufferedReader bufferedReader) throws IOException {
        if (contentLength == null) {
            return "";
        }

        StringBuilder bodyBuilder = new StringBuilder();
        char[] buffer = new char[Integer.parseInt(contentLength)];
        bufferedReader.read(buffer);
        bodyBuilder.append(new String(buffer));

        return bodyBuilder.toString();
    }
}
