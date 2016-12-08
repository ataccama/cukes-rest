package lv.ctco.cukescore.internal.resources;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import lv.ctco.cukescore.CukesRuntimeException;
import lv.ctco.cukescore.internal.helpers.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ResourceFileReader {

    @Inject
    FilePathService pathService;

    public String read(String path) {
        return Joiner.on("").join(readLines(path));
    }

    public List<String> readLines(String path) {
        try {
            String normalizedPath = pathService.normalize(path);
            File file = new File(normalizedPath);
            return Files.readLines(file);
        } catch (IOException e) {
            throw new CukesRuntimeException(e);
        }
    }
}
