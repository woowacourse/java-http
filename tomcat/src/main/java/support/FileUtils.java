package support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public List<String> readFileLines(String fileName) {
        try {
            final Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
            final File file = path.toFile();

            final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            return bufferedReader.lines().collect(Collectors.toCollection(LinkedList::new));
        } catch (URISyntaxException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
