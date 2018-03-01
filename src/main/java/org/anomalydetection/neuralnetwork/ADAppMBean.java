package org.anomalydetection.neuralnetwork;

public interface ADAppMBean {

	public void setError(double max_error);
	public double getError();
	
	public void anomalyDetection();
	
}
