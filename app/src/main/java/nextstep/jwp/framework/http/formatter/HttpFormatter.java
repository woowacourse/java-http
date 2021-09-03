package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpMessage;

public interface HttpFormatter {

    String transform();

    HttpFormatter convertNextFormatter(HttpMessage httpMessage);

    default boolean canRead() {
        return true;
    }
}
