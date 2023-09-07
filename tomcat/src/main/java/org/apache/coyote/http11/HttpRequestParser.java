package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public HttpRequest convertToHttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String firstLine = bufferedReader.readLine();
        Map<String, String> header = readHeader(bufferedReader);
        return new HttpRequest(new StartLine(firstLine), header, readMessageBody(bufferedReader, header));
    }

    private Map<String, String> readHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String line = bufferedReader.readLine();
        while (line != null && !line.isBlank()) {
            String[] split = line.split(":");
            header.put(split[KEY_INDEX], split[VALUE_INDEX].trim());
            line = bufferedReader.readLine();
        }
        return header;
    }

    private String readMessageBody(BufferedReader bufferedReader, Map<String, String> header) throws IOException {
        String messageBody = "";
        String key = "Content-Length";
        if (header.containsKey(key)) {
            int contentLength = Integer.parseInt(header.get(key));
            char[] body = new char[contentLength];
            bufferedReader.read(body, 0, contentLength);
            messageBody = new String(body);
        }
        return messageBody;
    }
}
