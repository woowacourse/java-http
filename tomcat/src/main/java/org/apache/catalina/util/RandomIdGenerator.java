package org.apache.catalina.util;

import java.util.UUID;
import org.apache.catalina.IdGenerator;

public class RandomIdGenerator implements IdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
