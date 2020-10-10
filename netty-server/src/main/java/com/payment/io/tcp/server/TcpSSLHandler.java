
package com.payment.io.tcp.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;

public class TcpSSLHandler extends SimpleBean {

    private static final Logger LOGGER = LogManager.getLogger(TcpSSLHandler.class);
    private String keyStoreFile;
    private String keyStorePassword;
    private String keyStoreType = "JKS";
    private String sslContextType = "SSL";
    private String keyManagerFactoryType = "SunX509";
    private boolean needClientAuth = Boolean.FALSE;
    private boolean useClientMode = Boolean.FALSE;
    private SSLContext sslContext;
    private String trustStoreFile;
    private String trustStorePassword;

    private void createServerContext() {
        try {
            char[] password = keyStorePassword.toCharArray();
            KeyStore ks = KeyStore.getInstance(keyStoreType);
            keyStoreFile = keyStoreFile.startsWith("/") ? keyStoreFile.substring(1) : keyStoreFile;
            try (FileInputStream fis = new FileInputStream(keyStoreFile)) {
                ks.load(fis, password);
            }
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(keyManagerFactoryType);
            kmf.init(ks, password);
            sslContext = SSLContext.getInstance(sslContextType);
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());
        } catch (Exception ex) {
            LOGGER.error("Exception in {} ", ex.getMessage());
            throw new RuntimeException("unable to initialize ssl context, exception occured", ex);
        }
    }

    private void createClientContext() {
        try {
            char[] password = keyStorePassword.toCharArray();
            KeyStore ks = KeyStore.getInstance(keyStoreType);
            FileInputStream fis = new FileInputStream(keyStoreFile);
            ks.load(fis, password);
            fis.close();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(keyManagerFactoryType);
            kmf.init(ks, password);
            TrustManager[] tma = null;
            if (trustStoreFile != null) {
                tma = initTrustManager();
            } else {
                tma = getTrustManagers(ks);
            }
            sslContext = SSLContext.getInstance(sslContextType);
            sslContext.init(kmf.getKeyManagers(), tma, new SecureRandom());
        } catch (Exception ex) {
            throw new RuntimeException("unable to initialize ssl context, exception occured", ex);
        }
    }

    private TrustManager[] initTrustManager()
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        char[] password = trustStorePassword.toCharArray();
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fileInputStream = new FileInputStream(new File(trustStoreFile));
        caKs.load(fileInputStream, password);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);
        fileInputStream.close();
        return tmf.getTrustManagers();
    }

    private TrustManager[] getTrustManagers(KeyStore ks) throws GeneralSecurityException { // NOSONAR
        return new TrustManager[] { new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                    throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                    throws java.security.cert.CertificateException {
            }
        } };
    }

    public SSLEngine createSSLEngine() {
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setNeedClientAuth(needClientAuth);
        sslEngine.setUseClientMode(useClientMode);
        return sslEngine;
    }

    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    public void setKeyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getSslContextType() {
        return sslContextType;
    }

    public void setSslContextType(String sslContextType) {
        this.sslContextType = sslContextType;
    }

    public String getKeyManagerFactoryType() {
        return keyManagerFactoryType;
    }

    public void setKeyManagerFactoryType(String keyManagerFactoryType) {
        this.keyManagerFactoryType = keyManagerFactoryType;
    }

    public boolean isNeedClientAuth() {
        return needClientAuth;
    }

    public void setNeedClientAuth(boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }

    public boolean isUseClientMode() {
        return useClientMode;
    }

    public void setUseClientMode(boolean useClientMode) {
        this.useClientMode = useClientMode;
    }

    public String getTrustStoreFile() {
        return trustStoreFile;
    }

    public void setTrustStoreFile(String trustStoreFile) {
        this.trustStoreFile = trustStoreFile;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    @Override
    public void onContextRefreshed(ContextRefreshedEvent event) {
        LOGGER.info("initializing global ssl context");
        if (!useClientMode) {
            createServerContext();
        } else {
            createClientContext();
        }
        LOGGER.info("global ssl context initialized");
    }

    @Override
    public void onContextStopped(ContextStoppedEvent event) {
        LOGGER.info("({}) onContextStopped", getName());
    }

    @Override
    public void onContextClosed(ContextClosedEvent event) {
        LOGGER.info("({}) onContextClosed", getName());
    }

    @Override
    public String toString() {
        return super.toString() + ", SSLServerSocketOptions [keyStoreFile=" + keyStoreFile + ", keyStorePassword="
                + keyStorePassword + ", keyStoreType=" + keyStoreType + ", sslContextType=" + sslContextType
                + ", keyManagerFactoryType=" + keyManagerFactoryType + ", needClientAuth=" + needClientAuth
                + ", useClientMode=" + useClientMode + "]";
    }

}