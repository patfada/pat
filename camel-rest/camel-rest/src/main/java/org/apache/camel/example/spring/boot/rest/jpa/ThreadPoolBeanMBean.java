package org.apache.camel.example.spring.boot.rest.jpa;

import java.io.Serializable;

public interface ThreadPoolBeanMBean extends Serializable {
	// Returns the current number of threads in the pool.
	public int getPoolSize();

	// Returns the approximate number of threads that are actively executing
	// tasks.
	public int getActiveCount();

	public int getQueueSize();

	public int getQueueRemainingCapacity();

	// Returns the maximum allowed number of threads.
	public int getMaximumPoolSize();
	
	public void setMaximumPoolSize(int maximumPoolSize) ;

}
