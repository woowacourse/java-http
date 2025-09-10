package org.apache.coyote.http11.message;

public record StatusLine(
        HttpStatus statusCode,
        String path,
        String httpVersion
) {
    // TODO: 생성자로 request body 받기 -> path, httpversion 한 번에 받기
}
