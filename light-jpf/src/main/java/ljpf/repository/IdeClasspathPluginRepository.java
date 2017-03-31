package ljpf.repository;

import com.google.common.collect.Lists;
import ljpf.PluginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by souzen on 25.03.2017.
 */
public class IdeClasspathPluginRepository extends BasePluginRepository implements PluginRepository {

    private static final Logger LOG = LoggerFactory.getLogger(IdeClasspathPluginRepository.class.getSimpleName());

    private static final String CLASSPATH_ATTR_NAME = "Class-Path";

    public IdeClasspathPluginRepository() {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        if ((systemClassLoader instanceof URLClassLoader)) {

            URLClassLoader cl = (URLClassLoader) systemClassLoader;

            List<File> classpathUris = Lists.newArrayList();
            for (URL url : cl.getURLs()) {
                try {
                    final URI uri = url.toURI();
                    classpathUris.add(new File(uri));
                } catch (URISyntaxException e) {
                    LOG.warn("Could not parse classpath element", e);
                }
            }

            // extract classpath dirs if it was packed to jar file
            classpathUris.addAll(loadPackedClasspathDirs(classpathUris).collect(toList()));
        }
    }

    private Stream<File> loadPackedClasspathDirs(List<File> dirs) {
        try {
            // This is performed in order to load compressed intellij classpath

            for (int i = 0; i < dirs.size(); i++) {
                File file = dirs.get(i);
                final Path path = file.toPath();

                if (!Files.isReadable(path))
                    continue;

                JarInputStream jarStream = new JarInputStream(Files.newInputStream(path));
                Manifest mf = jarStream.getManifest();

                if (mf == null || mf.getMainAttributes().getValue(CLASSPATH_ATTR_NAME) == null) {
                    continue;
                }

                String classpath = mf.getMainAttributes().getValue(CLASSPATH_ATTR_NAME);

                return Stream.of(classpath.split("file:")).map(dir -> new File(dir.trim()));
            }

        } catch (IOException e) {
            // NO-OP
            LOG.debug("Couldn't load packed classpath dirs {}", e.getMessage());
        }

        return Stream.empty();
    }

}