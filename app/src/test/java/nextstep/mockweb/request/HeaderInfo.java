package nextstep.mockweb.request;

import java.util.HashMap;
import java.util.Map;

public class HeaderInfo {

    private static final String HTTP_FORM = "%s: %s\n";
    private Map<String, String> headers;

    public HeaderInfo() {
        this.headers = new HashMap<>();
        defaultHeader();
    }

    private void defaultHeader() {
        headers.put("Host", "localhost:8080");
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String asRequestForm() {
        StringBuilder sb = new StringBuilder();
        headers.forEach((key, value) -> sb.append(String.format(HTTP_FORM, key, value)));
        return sb.toString();
    }
}
