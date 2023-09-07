package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.Protocol;

public class RequestParser {

    private static final String ACCEPT_HEADER = "Accept: ";
    private static final String LINE_SPLIT_DELIMITER = " ";
    private static final String FINISH_SPLIT_DELIMITER = ";";
    private static final String SPLIT_VALUE_DELIMITER = ",";

    private final BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    public Request parse() throws IOException {
        RequestLine requestLine = readRequestUrl();
        ContentType contentType = getResourceType();

        return new Request(requestLine, contentType);
    }

    private RequestLine readRequestUrl() throws IOException {
        String message = bufferedReader.readLine();

        String httpMethod = message.split(LINE_SPLIT_DELIMITER)[0];
        String url = message.split(LINE_SPLIT_DELIMITER)[1];
        String protocol = message.split(LINE_SPLIT_DELIMITER)[2];
        return new RequestLine(HttpMethod.from(httpMethod), RequestUrl.from(url), Protocol.from(protocol));
    }

    private String readLine(String message) {
        return message.split(LINE_SPLIT_DELIMITER)[1]
                .split(FINISH_SPLIT_DELIMITER)[0];
    }

    private ContentType getResourceType() throws IOException {
        String header;
        while ((header = bufferedReader.readLine()) != null) {
            if (header.startsWith(ACCEPT_HEADER)) {
                String resourceType = readLine(header);
                return ContentType.from(resourceType.split(SPLIT_VALUE_DELIMITER)[0]);
            }
        }
        return ContentType.HTML;
    }
}
