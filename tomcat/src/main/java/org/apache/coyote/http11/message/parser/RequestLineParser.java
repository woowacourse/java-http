package org.apache.coyote.http11.message.parser;

import java.util.StringTokenizer;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.RequestLine;

public class RequestLineParser {

    private static final int CONTENT_LENGTH = 3;

    private RequestLineParser() {
    }

    public static RequestLine parse(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        if (tokenizer.countTokens() != CONTENT_LENGTH) {
            throw new IllegalArgumentException("Http Line 형식이 일치하지 않습니다.");
        }
        return new RequestLine(
                HttpMethod.from(tokenizer.nextToken()),
                tokenizer.nextToken(),
                tokenizer.nextToken()
        );
    }
}
