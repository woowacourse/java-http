package nextstep.jwp.framework.http;

public interface ParsingLine {

    ParsingLine parseLine(String line);

    HttpRequest buildRequest();

    boolean canParse();
}
