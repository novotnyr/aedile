package com.github.novotnyr.aedile.git;

import com.github.novotnyr.aedile.ConsulConfiguration;
import com.github.novotnyr.aedile.ConsulConfigurationRepository;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsulConfigurationRepositoryTest {
    @Test
    public void testImport() throws Exception {
        Map<String, String> configuration = new HashMap<>();
        configuration.put("version", "1.0");

        ConsulConfigurationRepository repository = new ConsulConfigurationRepository(new ConsulConfiguration());
        repository.store("config", "test", configuration);
        // ---
        ConsulClient consulClient = new ConsulClient("localhost");
        Response<List<String>> response = consulClient.getKVKeysOnly("config/test");
        List<String> values = response.getValue();
        Assert.assertTrue(values.contains("config/test/version"));
    }
}