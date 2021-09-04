package nextstep.jwp.framework.message.response;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.common.HttpVersion;
import nextstep.jwp.framework.message.StartLine;
import nextstep.jwp.utils.StringUtils;

import java.util.Objects;

public class StatusLine implements StartLine {

    private final HttpVersion httpVersion;
    private final HttpStatusCode httpStatusCode;

    public StatusLine(HttpVersion httpVersion, HttpStatusCode httpStatusCode) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
    }

    public String asString() {
        String httpVersion = this.httpVersion.getValue();
        String code = httpStatusCode.getCodeAsString();
        String description = httpStatusCode.getDescription();
        return StringUtils.concatNewLine(
                StringUtils.joinWithBlank(httpVersion, code, description)
        );
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public byte[] toBytes() {
        return asString().getBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusLine that = (StatusLine) o;
        return getHttpVersion() == that.getHttpVersion() && getHttpStatusCode() == that.getHttpStatusCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHttpVersion(), getHttpStatusCode());
    }
}
