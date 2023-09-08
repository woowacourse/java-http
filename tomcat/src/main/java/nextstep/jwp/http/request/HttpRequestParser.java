package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.http.common.HeaderType;
import nextstep.jwp.http.common.HttpBody;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpUri;
import nextstep.jwp.http.common.HttpVersion;

public class HttpRequestParser {

    private static final String START_LINE_DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private HttpRequestParser() {
    }

    public static HttpRequest parse(BufferedReader br) throws IOException {
        HttpStartLine httpStartLine = readHttpStartLine(br);
        HttpHeaders httpHeaders = readHeaders(br);
        HttpBody httpBody = readBody(httpHeaders, br);

        return new HttpRequest(httpHeaders, httpStartLine, httpBody);
    }

    private static HttpStartLine readHttpStartLine(BufferedReader br) throws IOException {
        String startLine = br.readLine();

        if (startLine == null) {
            throw new BadRequestException("잘못된 http 요청 입니다.");
        }

        String[] elements = startLine.split(START_LINE_DELIMITER);

        HttpMethod httpMethod = HttpMethod.from(elements[METHOD_INDEX]);
        HttpUri httpUri = HttpUri.from(elements[URI_INDEX]);
        HttpVersion httpVersion = HttpVersion.from(elements[VERSION_INDEX]);

        return new HttpStartLine(httpMethod, httpUri, httpVersion);
    }

    private static HttpHeaders readHeaders(BufferedReader br) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;

        while (!"".equals(line = br.readLine())) {
            lines.add(line);
        }

        return HttpHeaders.from(lines);
    }

    private static HttpBody readBody(HttpHeaders headers, BufferedReader br) throws IOException {
        if (!headers.containsKey(HeaderType.CONTENT_TYPE.getValue())) {
            return HttpBody.createEmptyHttpBody();
        }

        int contentLength = Integer.parseInt(headers.get(HeaderType.CONTENT_LENGTH.getValue()));
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);

        return HttpBody.from(new String(buffer));
    }
}
