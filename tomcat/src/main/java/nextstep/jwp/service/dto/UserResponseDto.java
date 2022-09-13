package nextstep.jwp.service.dto;

import nextstep.jwp.domain.User;

public class UserResponseDto {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;


    public UserResponseDto(final Long id, final String account, final String password, final String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public static UserResponseDto from(final User user) {
        return new UserResponseDto(user.getId(), user.getAccount(), user.getPassword(), user.getEmail());
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
}
