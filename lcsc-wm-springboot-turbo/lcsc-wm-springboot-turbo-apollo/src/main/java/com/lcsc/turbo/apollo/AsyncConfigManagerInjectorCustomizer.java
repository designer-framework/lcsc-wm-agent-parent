package com.lcsc.turbo.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.internals.ConfigManager;
import com.ctrip.framework.apollo.spi.ApolloInjectorCustomizer;
import com.ctrip.framework.apollo.spi.ConfigFactory;
import com.ctrip.framework.apollo.spi.ConfigFactoryManager;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 修改默认的ConfigManager
 */
@Slf4j
public class AsyncConfigManagerInjectorCustomizer implements ApolloInjectorCustomizer {

    final static Map<String, Future<Config>> m_configFutureMap = new ConcurrentHashMap<>();

    @Override
    public <T> T getInstance(Class<T> clazz) {
        if (ConfigManager.class.equals(clazz)) {
            return (T) new AsyncLoadConfigManager();
        } else {
            return null;
        }
    }

    @Override
    public <T> T getInstance(Class<T> clazz, String name) {
        return null;
    }

    /**
     * @see com.ctrip.framework.apollo.internals.DefaultConfigManager
     */
    private static class AsyncLoadConfigManager implements ConfigManager {
        private final ConfigFactoryManager m_factoryManager;
        private final Map<String, Config> m_configs = Maps.newConcurrentMap();
        private final Map<String, ConfigFile> m_configFiles = Maps.newConcurrentMap();

        public AsyncLoadConfigManager() {
            m_factoryManager = ApolloInjector.getInstance(ConfigFactoryManager.class);
        }

        @SneakyThrows
        @Override
        public Config getConfig(String namespace) {
            Config config = m_configs.get(namespace);

            if (config == null) {
                synchronized (namespace.intern()) {
                    config = m_configs.get(namespace);

                    if (config == null) {
                        ConfigFactory factory = m_factoryManager.getFactory(namespace);

                        config = factory.create(namespace);
                        m_configs.put(namespace, config);
                    }
                }
            }

            return config;
        }

        @Override
        public ConfigFile getConfigFile(String namespace, ConfigFileFormat configFileFormat) {
            String namespaceFileName = String.format("%s.%s", namespace, configFileFormat.getValue());
            ConfigFile configFile = m_configFiles.get(namespaceFileName);

            if (configFile == null) {
                synchronized (namespace.intern()) {
                    configFile = m_configFiles.get(namespaceFileName);

                    if (configFile == null) {
                        ConfigFactory factory = m_factoryManager.getFactory(namespaceFileName);

                        configFile = factory.createConfigFile(namespaceFileName, configFileFormat);
                        m_configFiles.put(namespaceFileName, configFile);
                    }
                }
            }

            return configFile;
        }

    }


}
