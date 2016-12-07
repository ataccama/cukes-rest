package lv.ctco.cukescore.internal.resources;

import com.google.inject.*;
import lv.ctco.cukescore.*;
import lv.ctco.cukescore.internal.context.*;
import lv.ctco.cukescore.internal.helpers.*;

import java.io.*;

public class FilePathService {

    @Inject
    GlobalWorldFacade world;

    public String normalize(String path) {
        if (Files.isRelative(path)) {
            // TODO: Put correct RESOURCE_ROOT
            String resourceRoot = world.get(CukesOptions.RESOURCES_ROOT, "resources");
            return new File(resourceRoot, path).getAbsolutePath();
        }
        return new File(path).getAbsolutePath();
    }
}
