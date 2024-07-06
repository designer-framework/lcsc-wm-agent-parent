package com.lcsc.turbo.feign;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.function.SingletonSupplier;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-30 16:22
 * 有两种实现方式
 * 1. 基于AnnotationInjectedBeanPostProcessor, 在注入Feign字段时, 使用懒加载进行代理
 * @see org.springframework.cloud.openfeign.EnableFeignClients
 */
public class LazyLoadFeignClientPostProcessor implements BeanDefinitionRegistryPostProcessor, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    /**
     * 官方实现懒加载的方式相对复杂, 因此采用JDK动态代理简单实现懒加载
     * <p>
     * {@link  CommonAnnotationBeanPostProcessor#buildLazyResourceProxy(CommonAnnotationBeanPostProcessor.LookupElement, String)}
     *
     * @param beanDefinition the merged bean definition for the bean
     * @param beanType       the actual type of the managed bean instance
     * @param beanName       the name of the bean
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        for (String beanDefinitionName : beanDefinitionRegistry.getBeanDefinitionNames()) {

            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);
            Object feignClientsRegistrarFactoryBean = beanDefinition.getAttribute("feignClientsRegistrarFactoryBean");
            if (feignClientsRegistrarFactoryBean instanceof FeignClientFactoryBean) {
                AbstractBeanDefinition abstractBeanDefinition = (AbstractBeanDefinition) beanDefinition;
                abstractBeanDefinition.setInstanceSupplier(wrapper2AsyncInstanceSupplier((FeignClientFactoryBean) feignClientsRegistrarFactoryBean, (AbstractBeanDefinition) beanDefinition));
            }

        }

    }

    /**
     * @param feignClientFactoryBean
     * @return
     */
    private Supplier<?> wrapper2AsyncInstanceSupplier(FeignClientFactoryBean feignClientFactoryBean, AbstractBeanDefinition beanDefinition) {
        SingletonSupplier<?> singletonSupplier = new SingletonSupplier<>(() -> null, Objects.requireNonNull(beanDefinition.getInstanceSupplier()));
        //代理Feign接口实现懒加载
        return () -> Proxy.newProxyInstance(resourceLoader.getClassLoader(), new Class[]{feignClientFactoryBean.getType()}, (proxy, method, args) -> {
            return method.invoke(singletonSupplier.get(), args);
        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
