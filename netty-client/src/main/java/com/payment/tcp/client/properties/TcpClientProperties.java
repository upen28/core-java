package com.payment.tcp.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tcpclient")
public class TcpClientProperties {
	private String ip;
	private int port;
	private boolean keepAlive;
	private int rcvBuf;
	private int sndBuf;
	private int connectTimeoutMillis;
	private int maxConnection;
	private int poolTimeout;
	private ConnectionPoolProperties connectionPool = new ConnectionPoolProperties();

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public int getRcvBuf() {
		return rcvBuf;
	}

	public void setRcvBuf(int rcvBuf) {
		this.rcvBuf = rcvBuf;
	}

	public int getSndBuf() {
		return sndBuf;
	}

	public void setSndBuf(int sndBuf) {
		this.sndBuf = sndBuf;
	}

	public int getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}

	public void setConnectTimeoutMillis(int connectTimeoutMillis) {
		this.connectTimeoutMillis = connectTimeoutMillis;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}

	public int getPoolTimeout() {
		return poolTimeout;
	}

	public void setPoolTimeout(int poolTimeout) {
		this.poolTimeout = poolTimeout;
	}

	public ConnectionPoolProperties getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(ConnectionPoolProperties connectionPool) {
		this.connectionPool = connectionPool;
	}

}
