package org.apache.coyote.http11;

import org.apache.Method;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.apache.Method.POST;

public class HttpRequest {

    private final String[] requestLine;
    private final Map<String, String> requestHeaders;
    private String requestBody = ""; // 추후 GET, POST 리팩토링

    public HttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        try {
            this.requestLine = readRequestLine(bufferedReader);
            this.requestHeaders = readRequestHeaders(bufferedReader);

            if (isMethod(POST)) {
                this.requestBody = readRequestBody(bufferedReader);
            }
        } catch (IOException ioException) {
            throw new IOException("IOException 발생했습니다.");
        }
    }

    private String[] readRequestLine(BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine().split(" ");
    }

    private Map<String, String> readRequestHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String headerLine = bufferedReader.readLine();

        while (!("".equals(headerLine))) {
            String[] headerLineValues = parseWithTrim(headerLine, ":");
            String headerName = headerLineValues[0];
            String headerValue = headerLineValues[1];

            requestHeaders.put(headerName, headerValue);

            headerLine = bufferedReader.readLine();
        }

        return requestHeaders;
    }

    private String readRequestBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
        char[] buffer = readRequestBodyByLength(bufferedReader, contentLength);
        return new String(buffer);
    }

    private char[] readRequestBodyByLength(BufferedReader bufferedReader, int length) throws IOException {
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);

        return buffer;
    }

    private String[] parseWithTrim(String line, String delimiter) {
        String[] splited = line.split(delimiter);

        for (int i = 0; i < splited.length; i++) {
            splited[i] = splited[i].trim();
        }

        return splited;
    }

    public boolean isMethod(Method method) {
        return method.name().equals(requestLine[0]);
    }

    public String getPath() {
        if (requestLine[0].equals("GET")) {
            if (requestLine[1].equals("/login") || requestLine[1].equals("/register")) {
                requestLine[1] = requestLine[1] + ".html";
            }

            if (requestLine[1].equals("/")) {
                requestLine[1] = "home.html";
            }
        }

        return requestLine[1];
    }

    public String getCookie() {
        String[] cookies = parseWithTrim(requestHeaders.get("Cookie"), ";");

        for (String cookie : cookies) {
            if (cookie.startsWith("JSESSIONID")) {
                return cookie.split("=")[1];
            }
        }

        return null;
    }

    public String getMimeType() {
        String fileExtension = getPath().split("\\.")[1];
        return "text/" + fileExtension;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
