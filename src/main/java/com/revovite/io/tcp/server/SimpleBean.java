/*******************************************************************************
 * Copyright (c) 2015, 2016 RenovITe Technologies Inc.
 * All rights reserved.
 *
 *******************************************************************************/

package com.revovite.io.tcp.server;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;

public abstract class SimpleBean
        implements BeanNameAware, ApplicationListener<ApplicationEvent>, ApplicationContextAware {

    private String name;

    private ApplicationContext applicationContext;

    public String getName() {
        return name;
    }

    @Override
    public void setBeanName(String name) {
        this.name = BeanFactoryUtils.originalBeanName(name);
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            onContextRefreshed((ContextRefreshedEvent) event);
        } else if (event instanceof ContextStoppedEvent) {
            onContextStopped((ContextStoppedEvent) event);
        } else if (event instanceof ContextClosedEvent) {
            onContextClosed((ContextClosedEvent) event);
        }
    }

    public abstract void onContextRefreshed(ContextRefreshedEvent event);

    public abstract void onContextStopped(ContextStoppedEvent event);

    public abstract void onContextClosed(ContextClosedEvent event);
}