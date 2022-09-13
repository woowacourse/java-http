package support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class ResponseParser {

    String statusCode;
    Map<String, String> headerElements;
    String body;

    private ResponseParser(String statusCode, Map<String, String> headerElements, String body) {
        this.statusCode = statusCode;
        this.headerElements = headerElements;
        this.body = body;
    }

    public static ResponseParser of(String rawResponse) {
        List<String> inputs = List.of(rawResponse.split("\r\n"));
        String[] firstLineElements = inputs.get(0).split(" ");
        String statusCode = firstLineElements[1];

        Map<String, String> headerElements = new HashMap<>();
        for (int i = 1; i < inputs.lastIndexOf(""); ++i) {
            String[] keyValue = inputs.get(i).split(": ");
            headerElements.put(keyValue[0], keyValue[1].replaceAll(" ", ""));
        }

        StringJoiner body = new StringJoiner("\r\n");
        for (int i = inputs.lastIndexOf("") + 1; i < inputs.size(); ++i) {
            body.add(inputs.get(i));
        }

        return new ResponseParser(statusCode, headerElements, body.toString());
    }

    public String getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaderElements() {
        return headerElements;
    }

    public String getBody() {
        return body;
    }
}
