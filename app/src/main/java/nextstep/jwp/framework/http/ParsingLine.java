package nextstep.jwp.framework.http;

public interface ParsingLine {

    ParsingLine parse(String line);

    HttpRequest buildRequest();

    boolean canParse();
}
