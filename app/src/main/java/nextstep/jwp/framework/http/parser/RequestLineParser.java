package nextstep.jwp.framework.http.parser;

import java.io.BufferedReader;
import java.io.IOException;

import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpVersion;
import nextstep.jwp.framework.http.URI;
import nextstep.jwp.framework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLineParser extends AbstractHttpParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLineParser.class);

    private static final int STATUS_LINE_TOKEN_COUNT = 3;

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    public RequestLineParser(BufferedReader reader) {
        super(reader, new HttpRequest.Builder());
    }

    @Override
    public boolean isParsing() {
        return true;
    }

    @Override
    public String readParsingContent() throws IOException {
        return StringUtils.requireNonBlank(reader.readLine());
    }

    @Override
    public HttpParser parse() throws IOException {
        final String[] statusTokens = readParsingContent().split(" ");
        if (statusTokens.length != STATUS_LINE_TOKEN_COUNT) {
            throw new IllegalArgumentException("상태 줄에 4개 이상의 토큰들이 포함되어 있습니다.");
        }

        final HttpMethod httpMethod = HttpMethod.resolve(statusTokens[METHOD_INDEX]);
        final String uri = statusTokens[URI_INDEX];
        final HttpVersion version = HttpVersion.resolve(statusTokens[VERSION_INDEX]);

        LOGGER.debug("RequestLine - [HTTP Method : {}, Path : {}, HTTP Version : {}", httpMethod, uri, version);

        return new HeaderParser(reader, builder.requestLine(httpMethod, new URI(uri), version));
    }
}
