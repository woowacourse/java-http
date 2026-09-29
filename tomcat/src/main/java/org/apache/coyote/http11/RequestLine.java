package org.apache.coyote.http11;

import com.techcourse.controller.PathAliasesResolver;
import org.apache.coyote.error.HttpException;

public record RequestLine(
    HttpMethod method,
    String path,
    HttpVersion version
) {

    public static RequestLine from(final String rawRequestLine) {
        if (rawRequestLine == null || rawRequestLine.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다: " + rawRequestLine);
        }
        final String[] split = rawRequestLine.trim().split("\\s+");
        if (split.length != 3) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다: " + rawRequestLine);
        }
        final HttpMethod method = HttpMethod.pick(split[0]);
        final String path = PathAliasesResolver.normalize(split[1]);
        final HttpVersion version = HttpVersion.pick(split[2]);

        return new RequestLine(method, path, version);
    }
}
