package nextstep.jwp.framework.http.parser;

import java.io.BufferedReader;
import java.io.IOException;

import nextstep.jwp.framework.http.HttpRequest;

public class EndLineParser extends AbstractHttpParser {

    public EndLineParser(BufferedReader reader, HttpRequest.Builder builder) {
        super(reader, builder);
    }

    @Override
    public boolean isParsing() {
        return false;
    }

    @Override
    public String readParsingContent() throws IOException {
        throw new UnsupportedOperationException("파싱이 종료된 리퀘스트입니다.");
    }

    @Override
    public HttpParser parse() throws IOException {
        throw new UnsupportedOperationException("파싱이 종료된 리퀘스트입니다.");
    }
}
