package nextstep.jwp.application.dto;

import nextstep.jwp.domain.model.User;

public class UserDto {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public UserDto(final Long id, final String account, final String password, final String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public static UserDto from(final User user) {
        return new UserDto(
                user.getId(),
                user.getAccount(),
                user.getPassword(),
                user.getEmail()
        );
    }

    public Long getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
