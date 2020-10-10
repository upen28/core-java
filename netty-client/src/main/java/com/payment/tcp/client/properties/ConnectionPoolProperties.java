package com.payment.tcp.client.properties;

import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.FixedChannelPool.AcquireTimeoutAction;

public class ConnectionPoolProperties {
	private ChannelHealthChecker healthCheck = ChannelHealthChecker.ACTIVE;
	private AcquireTimeoutAction action = AcquireTimeoutAction.FAIL;
	private long acquireTimeoutMillis;
	private int maxConnections;
	private int maxPendingAcquires;
	private boolean releaseHealthCheck;
	private boolean lastRecentUsed;

	public ChannelHealthChecker getHealthCheck() {
		return healthCheck;
	}

	public void setHealthCheck(ChannelHealthChecker healthCheck) {
		this.healthCheck = healthCheck;
	}

	public AcquireTimeoutAction getAction() {
		return action;
	}

	public void setAction(AcquireTimeoutAction action) {
		this.action = action;
	}

	public long getAcquireTimeoutMillis() {
		return acquireTimeoutMillis;
	}

	public void setAcquireTimeoutMillis(long acquireTimeoutMillis) {
		this.acquireTimeoutMillis = acquireTimeoutMillis;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public int getMaxPendingAcquires() {
		return maxPendingAcquires;
	}

	public void setMaxPendingAcquires(int maxPendingAcquires) {
		this.maxPendingAcquires = maxPendingAcquires;
	}

	public boolean isReleaseHealthCheck() {
		return releaseHealthCheck;
	}

	public void setReleaseHealthCheck(boolean releaseHealthCheck) {
		this.releaseHealthCheck = releaseHealthCheck;
	}

	public boolean isLastRecentUsed() {
		return lastRecentUsed;
	}

	public void setLastRecentUsed(boolean lastRecentUsed) {
		this.lastRecentUsed = lastRecentUsed;
	}
}
