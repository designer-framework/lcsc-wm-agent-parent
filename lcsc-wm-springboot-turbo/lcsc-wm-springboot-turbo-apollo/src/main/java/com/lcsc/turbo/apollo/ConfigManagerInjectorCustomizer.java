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
import com.lcsc.turbo.common.utils.AsyncUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * 修改默认的ConfigManager
 */
public class ConfigManagerInjectorCustomizer implements ApolloInjectorCustomizer {

    final static Set<Future<?>> m_configFutures = new HashSet<>();

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
    @Slf4j
    private static class AsyncLoadConfigManager implements ConfigManager {
        private final ConfigFactoryManager m_factoryManager;
        private final Map<String, Config> m_configs = Maps.newConcurrentMap();
        private final Map<String, ConfigFile> m_configFiles = Maps.newConcurrentMap();

        public AsyncLoadConfigManager() {
            m_factoryManager = ApolloInjector.getInstance(ConfigFactoryManager.class);
        }

        @Override
        public Config getConfig(String namespace) {
            Future<Config> configFuture = AsyncUtils.submit(() -> getConfig0(namespace));
            m_configFutures.add(configFuture);

            return (Config) Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader()
                    , new Class[]{Config.class}
                    , (proxy, method, args) -> method.invoke(configFuture.get(), args)
            );
        }

        private Config getConfig0(String namespace) {

            Config config = m_configs.get(namespace);

            if (config == null) {
                // 由this改成namespace.intern()
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
