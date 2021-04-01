package com.github.novotnyr.aedile.resolver;

import java.io.File;

public interface ConfigurationNameResolver {
    String getConfigurationName(File configurationFile);
}
