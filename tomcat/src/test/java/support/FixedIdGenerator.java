package support;

import org.apache.catalina.IdGenerator;

public class FixedIdGenerator implements IdGenerator {

    public static final String FIXED_ID = "fixed-id";

    @Override
    public String generate() {
        return FIXED_ID;
    }
}
