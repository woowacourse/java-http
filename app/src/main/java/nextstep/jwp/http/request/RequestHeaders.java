package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestHeaders {

    private HashMap<String, ArrayList<String>> headers = new HashMap<>();

    public RequestHeaders(BufferedReader bufferedReader) throws IOException {

        while (bufferedReader.ready()) {
            String readLine = bufferedReader.readLine();

            if (readLine.equals("")) {
                break;
            }

            String[] headerContent = readLine.split(",|: ");
            String header = headerContent[0];
            ArrayList<String> contents = new ArrayList<>();

            for (int i = 1; i < headerContent.length; i++) {
                contents.add(headerContent[i]);
            }
            headers.put(header, contents);
        }
    }

    public List<String> getHeader(String header) {
        if (!headers.containsKey(header)) {
            throw new RuntimeException("요청 헤더가 없습니다.");
        }
        return headers.get(header);
    }

    public boolean isExistContentLength() {
        return headers.containsKey("Content-Length");
    }

    public int contentLength() {
        return Integer.parseInt(headers.get("Content-Length").get(0));
    }
}
