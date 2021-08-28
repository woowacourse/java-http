package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpResponse;

public class EndLineFormatter extends AbstractLineFormatter {

    public EndLineFormatter(HttpResponse httpResponse) {
        super(httpResponse);
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
    public LineFormatter convertNextFormatter() {
        throw new UnsupportedOperationException("이미 종료된 HTTP 응답 변환 작업입니다.");
    }
}
