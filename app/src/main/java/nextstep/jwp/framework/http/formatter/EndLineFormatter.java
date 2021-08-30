package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpMessage;

public class EndLineFormatter extends AbstractHttpFormatter {

    public EndLineFormatter(HttpMessage httpMessage) {
        super(httpMessage);
    }

    @Override
    public boolean canRead() {
        return false;
    }

    @Override
    public String transform() {
        throw new UnsupportedOperationException("이미 종료된 HTTP 응답 변환 작업입니다.");
    }

    @Override
    public HttpFormatter convertNextFormatter() {
        throw new UnsupportedOperationException("이미 종료된 HTTP 응답 변환 작업입니다.");
    }
}
