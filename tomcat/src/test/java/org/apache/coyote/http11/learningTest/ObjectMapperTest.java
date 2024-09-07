package org.apache.coyote.http11.learningTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class ObjectMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Object -> Json 직렬화 테스트")
    void serializeTest() throws JsonProcessingException {
        User user = InMemoryUserRepository.findByAccount("gugu").get();

        System.out.println(objectMapper.writeValueAsString(user));
    }
}
