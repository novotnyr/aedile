package com.github.novotnyr.aedile.git;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.github.novotnyr.aedile.io.CollectingFileVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsulConfigurationRepositoryTest {

    private ConsulConfigurationRepository repository;

    private ConsulClient consulClient;

    @Before
    public void setUp() throws Exception {
        ConsulConfiguration configuration = new ConsulConfiguration();

        consulClient = mock(ConsulClient.class);
        when(consulClient.getKVValues(eq("/"), any(), any()))
                .thenReturn(getMockKVValueForRoot());

        repository = new ConsulConfigurationRepository(configuration) {
            @Override
            protected ConsulClient getConsulClient() {
                return consulClient;
            }
        };
    }

    @Test
    public void testStore() throws Exception {
        String versionKey = "versionKey";
        String versionValue = "1.0";

        String implKey = "impl";
        String implValue = "unittest";

        Map<String, String> values = new LinkedHashMap<>();
        values.put(versionKey, versionValue);
        values.put(implKey, implValue);

        repository.store("imported-config", "unit-test", values);

        verify(consulClient, times(1)).setKVValue(eq("imported-config/unit-test/" + versionKey), eq(versionValue), any(), any(), any());
        verify(consulClient, times(1)).setKVValue(eq("imported-config/unit-test/" + implKey), eq(implValue), any(), any(), any());
    }

    @Test
    public void testExportToDirectory() throws Exception {
        File targetDirectory = Files.createTempDirectory("consul-test").toFile();

        repository.export(targetDirectory);

        CollectingFileVisitor fileVisitor = new CollectingFileVisitor();
        Files.walkFileTree(targetDirectory.toPath(), fileVisitor);

        List<File> files = fileVisitor.getFiles();
        Assert.assertEquals(2, files.size());
        Assert.assertTrue(files.stream().anyMatch(f -> f.toString().endsWith("/config/default/api.properties")));
        Assert.assertTrue(files.stream().anyMatch(f -> f.toString().endsWith("/config/default/application.properties")));

        for (File file : files) {
            if (file.getName().endsWith("application.properties")) {
                Properties properties = new Properties();
                properties.load(new FileReader(file));

                Assert.assertEquals("1.0", properties.getProperty("version"));
                Assert.assertEquals("Unit", properties.getProperty("platform"));
            }
            if (file.getName().endsWith("api.properties")) {
                Properties properties = new Properties();
                properties.load(new FileReader(file));

                Assert.assertEquals("2.0", properties.getProperty("version"));
                Assert.assertEquals("Test", properties.getProperty("impl"));
            }
        }
    }

    public Response<List<GetValue>> getMockKVValueForRoot() {
        List<GetValue> values = new ArrayList<>();
        values.add(buildValue("config/default/application/version", "1.0"));
        values.add(buildValue("config/default/application/platform", "Unit"));
        values.add(buildValue("config/default/api/version", "2.0"));
        values.add(buildValue("config/default/api/impl", "Test"));

        return new Response<>(values, 1L, true, 0L);
    }

    public GetValue buildValue(String key, String value) {
        GetValue valueObject = new GetValue();
        valueObject.setKey(key);
        valueObject.setValue(Base64.getEncoder().encodeToString(value.getBytes(Charset.defaultCharset())));

        return valueObject;
    }
}