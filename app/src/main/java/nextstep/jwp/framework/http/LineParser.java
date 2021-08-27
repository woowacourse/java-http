package nextstep.jwp.framework.http;

public interface LineParser {

    LineParser parseLine(String line);

    HttpRequest buildRequest();

    boolean canParse();
}
