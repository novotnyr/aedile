package com.github.novotnyr.aedile;

import com.github.novotnyr.aedile.util.NamedMap;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.ecwid.consul.v1.kv.model.PutParams;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * High-level accessor to the Consul K/V repository.
 */
public class ConsulConfigurationRepository {

    public final Logger logger = LoggerFactory.getLogger(getClass());

    public static final PutParams DEFAULT_PUT_PARAMS = null;

    public static final String DEFAULT_PROPERTY_FILE_EXTENSION = "properties";

    private Base64.Decoder base64Decoder = Base64.getMimeDecoder();

    private String propertyFileExtension = DEFAULT_PROPERTY_FILE_EXTENSION;

    private ConsulClientProvider consulClientProvider;

    private ConsulConfiguration configuration;

    /**
     * Create a repository with necessary configuration.
     * @param configuration Consul configuration object
     */
    public ConsulConfigurationRepository(ConsulConfiguration configuration) {
        this.configuration = configuration;
        this.consulClientProvider = new ConsulClientProvider(configuration);
    }

    /**
     * Exports the whole contents of the K/V database to a specific directory.
     * <p>
     *     The export method will automatically create subfolders of the specified <i>directory</i>
     *     in the filesystem. The last directory will correspond to a properties file that will
     *     contain keys and values in the directory.
     * </p>
     * @param directory
     */
    public void export(File directory) {
        ConsulClient consul = getConsulClient();
        Response<List<GetValue>> response = consul.getKVValues("/", this.configuration.getAclToken(), getQueryParams());
        List<GetValue> responseValues = response.getValue();
        for (GetValue responseValue : responseValues) {
            String base64Value = responseValue.getValue();
            String key = responseValue.getKey();
            String value = decodeBase64(base64Value);
            export(key, value, directory);
        }
    }

    private void export(String key, String value, File directory) {
        String[] components = key.split("/");
        String[] directoryHierarchy
                = Arrays.copyOf(components, components.length - 1 /* exclude filename*/ - 1 /* exclude property */);
        String fileName = components[components.length - 2];
        fileName = fileName + "." + this.propertyFileExtension;
        String path = Arrays.stream(directoryHierarchy).collect(Collectors.joining(File.separator));
        String propertyName = components[components.length - 1];

        File directories = new File(directory, path);
        if (! directories.exists()) {
            if (!directories.mkdirs()) {
                logger.error("Cannot create directory structure for Consul key prefix [{}], file [{}]", path, directories);
            }
        }
        File file = new File(directories, fileName);
        try(PrintWriter writer = new PrintWriter(new FileOutputStream(file, /*append*/ true))) {
            writer.println(propertyName + "=" + value);
        } catch (FileNotFoundException e) {
            logger.error("Cannot create file for Consul key prefix {}, file {}", path, file);
        }
    }

    public <K, V> void store(String prefix, String name, Map<K, V> configuration) {
        store(new NamedMap<K, V>(prefix, name, configuration));
    }

    private <K, V> void store(NamedMap<K, V> map) {
        logger.info("Creating Consul client {}:{}", this.configuration.getHost(), this.configuration.getPort());
        store(getConsulClient(), map);
    }

    private <K, V> void store(ConsulClient consul, NamedMap<K, V> map) {
        String aclToken = this.configuration.getAclToken();
        Map<K, V> configuration = map.getDelegate();
        for(Map.Entry<K, V> entry : configuration.entrySet()) {
            String key = prepareKey(map, entry);
            String value = prepareValue(map, entry);
            consul.setKVValue(key, value, aclToken, DEFAULT_PUT_PARAMS, getQueryParams());
        }
    }

    private QueryParams getQueryParams() {
        return new QueryParams(this.configuration.getDatacenter());
    }

    private <K, V> String prepareValue(NamedMap<K, V> map, Map.Entry<K, V> entry) {
        return entry.getValue().toString();
    }

    private <K, V> String prepareKey(NamedMap<K, V> map, Map.Entry<K, V> entry) {
        return map.getPrefix() + "/" + map.getName() + "/" + entry.getKey();
    }

    private String decodeBase64(String base64Value) {
        if(base64Value == null) {
            return null;
        }
        byte[] bytes = base64Decoder.decode(base64Value);
        return new String(bytes, Charsets.UTF_8);
    }

    protected ConsulClient getConsulClient() {
        return this.consulClientProvider.getConsulClient();
    }

    public void setPropertyFileExtension(String propertyFileExtension) {
        this.propertyFileExtension = propertyFileExtension;
    }


}
