package com.lcsc.turbo.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import com.google.common.base.Splitter;
import com.lcsc.turbo.common.thread.AsyncUtils;
import com.lcsc.turbo.common.thread.Callback;
import com.lcsc.turbo.common.thread.TCallable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link ApolloApplicationContextInitializer}
 */
@Slf4j
public class AsyncPreloadApolloConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String ApolloTurboPropertySource = "ApolloTurboPropertySource";

    private static final MapPropertySource enabledApolloBootstrapPropertySource;

    private static final Splitter NAMESPACE_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    static {
        Map<String, Object> turboPropertyMap = new HashMap<>();
        turboPropertyMap.put(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, "true");
        turboPropertyMap.put(PropertySourcesConstants.APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED, "true");
        enabledApolloBootstrapPropertySource = new MapPropertySource(ApolloTurboPropertySource, turboPropertyMap);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication springApplication) {
        MutablePropertySources propertySources = environment.getPropertySources();
        if (!propertySources.contains(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME)) {

            //
            addApolloTurboPropertySources(propertySources);

            //
            String namespaces = environment.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES, ConfigConsts.NAMESPACE_APPLICATION);
            log.debug("Apollo bootstrap namespaces: {}", namespaces);

            //
            concurrentInitializeConfig(NAMESPACE_SPLITTER.splitToList(namespaces));
        }
    }

    protected void concurrentInitializeConfig(List<String> namespaces) {
        //
        List<TCallable<Config>> callables = namespaces.stream()
                .map(this::wrapper2TCallable)
                .collect(Collectors.toList());
        //
        AsyncUtils.doInvokeAll(callables);
    }

    private TCallable<Config> wrapper2TCallable(String nameSpace) {
        //包装线程及回调
        return new TCallable<Config>(getLoadCallback(nameSpace)) {
            @Override
            public Config doCall() throws Exception {
                return ConfigService.getConfig(nameSpace);
            }
        };
    }

    private Callback<Config> getLoadCallback(String nameSpace) {
        return new Callback<Config>(nameSpace) {
            @Override
            public void onSuccess(Config result) {
                log.error("加载完成: {}", result);
            }
        };
    }

    private void addApolloTurboPropertySources(MutablePropertySources propertySources) {
        if (!propertySources.contains(ApolloTurboPropertySource)) {
            propertySources.addLast(enabledApolloBootstrapPropertySource);
        }
    }

    @Override
    public int getOrder() {
        return ApolloApplicationContextInitializer.DEFAULT_ORDER - 1;
    }

}
