package nextstep.jwp.model;

import nextstep.jwp.exception.EmptyParameterException;
import java.util.Arrays;
import java.util.Objects;

public class User {

    private Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) {
        this(null, account, password, email);
        validateNullOrBlank(account, password, email);
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    private void validateNullOrBlank(final String ... params) {
        final long nullOrBlankCount = Arrays.stream(params)
                .filter(param -> Objects.isNull(param) || param.isBlank())
                .count();

        if (nullOrBlankCount > 0) {
            throw new EmptyParameterException();
        }
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
