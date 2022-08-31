import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

public class Note {

    @Test
    void test() throws IOException {
        File file = new File("./src/main/resources/static/index.html");
        System.out.println(Files.readString(file.toPath()));
    }
}
