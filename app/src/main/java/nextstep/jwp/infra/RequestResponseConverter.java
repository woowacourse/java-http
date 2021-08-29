package nextstep.jwp.infra;

import nextstep.jwp.domain.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class RequestResponseConverter {

    private RequestResponseConverter() {
    }

    public static HttpRequest convertToHttpRequest(final BufferedReader bufferedReader) throws IOException {
        StringBuilder requestHeaders = new StringBuilder();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            if (line == null) {
                break;
            }
            requestHeaders.append(line).append("\n");
            line = bufferedReader.readLine();
        }

        String[] splitRequestHeaders = requestHeaders.toString().split("\n");
        String[] requestLine = splitRequestHeaders[0].split(" ");

//        String requestBody = "";
//        if (headers.containsKey("Content-Length")) {
//            int contentLength = Integer.parseInt(headers.get("Content-Length"));
//            char[] buffer = new char[contentLength];
//            requestBody = new String(buffer, 0, bufferedReader.read(buffer, 0, contentLength));
//        }

        return new HttpRequest.Builder()
                .method(requestLine[0])
                .uri(requestLine[1])
                .requestHeaders(splitRequestHeaders)
                //body 필요
                .build();
    }

//    .method(requestLine[0])
//                .uri(requestLine[1])
//                .versionOfProtocol(requestLine[2])
//                .requestHeaders(headers)
//                .requestMessageBody(splitMessageBodyByAmpersand(requestBody))
//            .build();
}
