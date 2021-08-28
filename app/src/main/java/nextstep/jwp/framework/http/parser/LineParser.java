package nextstep.jwp.framework.http.parser;

import nextstep.jwp.framework.http.HttpRequest;

public interface LineParser {

    LineParser parseLine(String line);

    HttpRequest buildRequest();

    boolean canParse();
}
