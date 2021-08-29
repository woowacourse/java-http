package nextstep.jwp.application.domain;

import java.util.Objects;

public class Account {

    private final String value;

    public Account(String value) {
        if (value.isBlank()) {
            throw new RuntimeException("계정명은 비어있을 수 없습니다.");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "Account{" +
            "value='" + value + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return Objects.equals(value, account.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
