package nextstep.jwp.framework.http.formatter;

public interface LineFormatter {

    boolean canRead();

    String transform();

    LineFormatter convertNextFormatter();
}
