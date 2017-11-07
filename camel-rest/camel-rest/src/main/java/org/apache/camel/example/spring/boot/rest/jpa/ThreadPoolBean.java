package org.apache.camel.example.spring.boot.rest.jpa;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolBean implements ThreadPoolBeanMBean {
	private ThreadPoolExecutor threadPool;

	public ThreadPoolBean(ThreadPoolExecutor myPool) {
		threadPool = myPool;
	}

	public int getQueueRemainingCapacity() {
		return threadPool.getQueue().remainingCapacity();

	}

	public int getQueueSize() {
		return threadPool.getQueue().size();
	}

	public int getActiveCount() {
		return threadPool.getActiveCount();
	}

	public int getPoolSize() {
		return threadPool.getPoolSize();
	}

	public int getMaximumPoolSize() {
		return threadPool.getMaximumPoolSize();
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		threadPool.setMaximumPoolSize(maximumPoolSize);
	}

}
