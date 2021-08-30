package nextstep.jwp.framework.http.formatter;

public interface HttpFormatter {

    boolean canRead();

    String transform();

    HttpFormatter convertNextFormatter();
}
