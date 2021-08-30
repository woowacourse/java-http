package nextstep.jwp.framework.http.parser;

import java.io.IOException;

import nextstep.jwp.framework.http.HttpRequest;

public interface HttpParser {

    boolean isParsing();

    String readParsingContent() throws IOException;

    HttpParser parse() throws IOException;

    HttpRequest buildRequest();
}
