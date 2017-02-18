package com.github.novotnyr.aedile.filesystem;

import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.github.novotnyr.aedile.ImporterConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Imports multiple folders that corresponds to datacenter configurations.
 * <p>
 *     The top-level folder shall contain subfolders corresponding to datacenter names.
 *     Datacenter subfolders shall be mapped to datacenter-specific REST queries.
 * </p>
 * <p>
 *     The datacenter folder shall be directly mirrored to the Consul K/V structure,
 *     when properties file will contain the bottomost folders with keys and values.
 * </p>
 */
public class DatacenterFilesystemImporter {
    public final Logger logger = LoggerFactory.getLogger(getClass());

    private List<ExcludedFolder> excludedFolders = new LinkedList<>();

    private PathMatcher pathMatcher = new AntPathMatcher();

    public void importDatacenters(File datacenterConfigurationSubDirectory) {
        File[] datacenterDirectories = datacenterConfigurationSubDirectory.listFiles(File::isDirectory);
        for(File datacenterDir : datacenterDirectories) {
            importDatacenterDirectory(datacenterDir);
        }
    }

    public void importDatacenterDirectory(File datacenterDir) {
        String datacenter = datacenterDir.getName();

        Set<File> directories = getDirectories(datacenterDir);
        for (File directory : directories) {
            String configurationPrefix = removeCommonPathPrefix(datacenterDir, directory);
            if (!isAllowed(datacenter, configurationPrefix)) {
                logger.info("Skipping datacenter {} and directory {}", datacenter, configurationPrefix);
                continue;
            }
            logger.info("Importing from datacenter '{}', directory {}", datacenter, datacenterDir);

            ConsulConfiguration consulConfiguration = getConsulConfiguration();
            // override datacenter name from directory
            consulConfiguration.setDatacenter(datacenter);

            ConsulConfigurationRepository repository = getConsulConfigurationRepository(consulConfiguration);
            PropertyFilesDirectoryImporter directoryImporter = new PropertyFilesDirectoryImporter(repository);
            directoryImporter.setKeyPrefix(configurationPrefix);

            directoryImporter.run(directory);
        }
    }

    protected ConsulConfigurationRepository getConsulConfigurationRepository(ConsulConfiguration consulConfiguration) {
        return new ConsulConfigurationRepository(consulConfiguration);
    }

    /**
     * Return true if specified directory in the datacenter
     * file structure is allowed to be imported.
     *
     * @param datacenter name of the datacenter, such as 'dc1'
     * @param datacenterSubdirectory a directory relative to the datacenter subdirectory, such as 'config/unittest/application'
     */
    private boolean isAllowed(String datacenter, String datacenterSubdirectory) {
        for (ExcludedFolder excludedFolder : excludedFolders) {
            if(excludedFolder.getDatacenter().equals(datacenter)
                    && matches(excludedFolder, datacenterSubdirectory)) {

                return false;
            }
        }
        return true;
    }

    /**
     * Check if directory of datacenter configuration data matches the exclusion pattern
     * @param excludedFolder exclusion specification
     * @param directory a directory relative to the datacenter subdirectory, such as 'config/unittest/application'
     */
    private boolean matches(ExcludedFolder excludedFolder, String directory) {
        return pathMatcher.match(excludedFolder.getFolder(), directory);
    }

    /**
     * Removes common path prefix that is shared between parent file and child file.
     * <p>
     *     If the <i>parent</i> is <code>/var/log/data</code> and child is
     *     <code>/var/log/data/config/java</code>, then the removed prefix
     *     returned from method is <code>config/java</code>
     * </p>
     * <p>
     *     Note that the first character of the remaining string is removed as well.
     *     See the example, where the leading slash is removed.
     * </p>
     * @param parent parent file that is prefix of the child
     * @param child child file that shares a common prefix with parent
     * @return remains of path without prefix
     */
    public String removeCommonPathPrefix(File parent, File child) {
        StringBuilder parentFullName = new StringBuilder(parent.toString());
        StringBuilder childFullName = new StringBuilder(child.toString());

        StringBuilder suffix = childFullName.delete(0, parentFullName.length() + 1);
        return suffix.toString();
    }

    private Set<File> getDirectories(File datacenterDir) {
        try {
            Set<File> directories = new LinkedHashSet<>();
            FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Objects.requireNonNull(file);

                    File parentDirectory = file.getParent().toFile();
                    directories.add(parentDirectory);

                    return FileVisitResult.CONTINUE;
                }
            };

            Files.walkFileTree(datacenterDir.toPath(), visitor);

            return directories;
        } catch (IOException e) {
            throw new ImporterConfigurationException("Unable to gather directories", e);
        }
    }

    protected ConsulConfiguration getConsulConfiguration() {
        return ConsulConfiguration.fromEnvironment();
    }

    /**
     * Configure excluded folders from environment variable.
     * <p>
     *      The env variable {@value com.github.novotnyr.aedile.EnvironmentVariables#CONSUL_IMPORT_EXCLUDE}
     *      specification contains colon-delimited entries.
     * </p>
     * <p>
     *     Each entry contains a name of the datacenter, then a hash <code>#</code>
     *     then a directory structure that shall be ignored upon import.
     * </p>
     * <p>
     *     An example <code>dc1#config/unittest/application:dc1#config/unittest/application2</code>:
     *     <ul>
     *          <li>The datacenter <code>dc1</code> shall ignore the <code>config/unittest/application</code> folder</li>
     *          <li>The datacenter <code>dc1</code> shall ignore the <code>config/unittest2/application</code> folder</li>
     *     </ul>
     * @throws ImporterConfigurationException when excluded folders specification is malformed
     */
    public void configureExcludedFolders() throws ImporterConfigurationException {
        List<ExcludedFolder> excludedFolders = new LinkedList<>();
        String excludedSpecification = getExcludedFoldersSpecificationString();
        String[] excludedFoldersSpec = excludedSpecification.split(File.pathSeparator);
        for (String excludedFolderSpec : excludedFoldersSpec) {
            try {
                this.excludedFolders.add(ExcludedFolder.parse(excludedFolderSpec));
            } catch (ExcludedFolderSyntaxException e) {
                throw new ImporterConfigurationException("Illegal excluded folder specification", e);
            }
        }
        this.excludedFolders.addAll(excludedFolders);
    }

    protected String getExcludedFoldersSpecificationString() {
        return System.getenv("CONSUL_IMPORT_EXCLUDE");
    }

    public static void main(String[] args) {
        if(args.length < 1) {
            System.err.println("Missing folder");
            System.err.println();
            System.exit(1);
        }

        File datacenterConfigurationSubDirectory = new File(args[0]);
        if (! datacenterConfigurationSubDirectory.isDirectory()) {
            System.err.println("Not a folder " + datacenterConfigurationSubDirectory);
            System.err.println();
            System.exit(1);
        }

        DatacenterFilesystemImporter importer = new DatacenterFilesystemImporter();
        importer.configureExcludedFolders();

        importer.importDatacenters(datacenterConfigurationSubDirectory);
    }
}
