package org.anomalydetection.neuralnetwork;

import java.io.File;

import org.anomalydetection.neuralnetwork.Elman;
import org.anomalydetection.neuralnetwork.TrainingData;

public class ADApp implements ADAppMBean {

	private double max_error;
	private static File classpath = new File(ADApp.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	public static final String PATH = classpath.getParent() + "/resources/";
//	public static final String PATH = "E:\\Thesis\\Datasets\\TemporalDatasets\\";
	public static final File dataFile = new File(PATH + "california.csv");
	
	public ADApp(double max_error) {
		this.max_error = max_error;
	}
	
	public void anomalyDetection() {
		TrainingData data = new TrainingData();
		data.segregateData();
		data.prepareTrainingData();
		data.prepareTestData();
		data.prepareAnomalyTestData();
		data.normalizeTrainingData();
		data.normalizeTestingData();
		data.normalizeAnomalyTestingData();
		Elman.run(this.max_error);
	}

	@Override
	public void setError(double max_error) {
			
		this.max_error = max_error;
		
	}

	@Override
	public double getError() {
		
		return this.max_error;
		
	}
}
