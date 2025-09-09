package org.apache.catalina;

import com.techcourse.model.User;
import java.util.UUID;

public record HttpSession(
        UUID uuid,
        User user
) {
}
