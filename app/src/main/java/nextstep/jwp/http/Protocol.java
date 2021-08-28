package nextstep.jwp.http;

import java.util.Objects;

public class Protocol {

    String value;

    public Protocol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Protocol protocol = (Protocol) o;
        return value.equals(protocol.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
