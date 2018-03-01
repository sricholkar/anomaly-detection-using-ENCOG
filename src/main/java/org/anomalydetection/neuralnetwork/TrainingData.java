package org.anomalydetection.neuralnetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.anomalydetection.neuralnetwork.ADApp;
import org.anomalydetection.neuralnetwork.WeatherData;
import org.encog.app.analyst.csv.segregate.SegregateCSV;
import org.encog.app.analyst.csv.segregate.SegregateTargetPercent;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.ml.data.temporal.TemporalDataDescription.Type;
import org.encog.util.arrayutil.NormalizeArray;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;


public class TrainingData {

	public static final int INPUT_WINDOW_SIZE = 5;
	public static final int PREDICT_WINDOW_SIZE = 1;
	public static List<WeatherData> wdList = new ArrayList<WeatherData>();
	public static List<WeatherData> wdTestList = new ArrayList<WeatherData>();
	public static List<WeatherData> wdAnomalyTestList = new ArrayList<WeatherData>();

	public static File TRAIN_FILE = new File(ADApp.PATH + "trainFile.csv");
	public static File TEST_FILE = new File(ADApp.PATH + "testFile.csv");
	public static File ANOMALY_TEST_FILE = new File(ADApp.PATH + "anomalyTestFile.csv");
	public static final double NORMALIZE_HIGH = 1;
	public static final double NORMALIZE_LOW = -1;
	public static NormalizeArray normTr;
	public static NormalizeArray normTeAvg;
	public static NormalizeArray normATeAvg;


	ReadCSV csvreader;
	public void segregateData() {
		SegregateCSV seg = new SegregateCSV();
		seg.getTargets().add(new SegregateTargetPercent(TRAIN_FILE, 90));
		seg.getTargets().add(new SegregateTargetPercent(TEST_FILE, 10));
		seg.setProduceOutputHeaders(true);
		
		seg.analyze(ADApp.dataFile, true, CSVFormat.ENGLISH);
		seg.process();
	}
	
	public void prepareTrainingData() {
		csvreader = new ReadCSV(TRAIN_FILE, true, CSVFormat.ENGLISH);
		int i = 0;
		while (csvreader.next()) {
			i++;
			final Integer count = new Integer(i);
			wdList.add(new WeatherData() {{
				this.id = count;
//				this.date = Integer.parseInt(csvreader.get("DATE"));
				this.actual_avg_temperature = Double.parseDouble(csvreader.get("TOBS"));
				this.actual_tmax_temp = Double.parseDouble(csvreader.get("TMAX"));
				this.actual_tmin_temp = Double.parseDouble(csvreader.get("TMIN"));
			}});
		}	
	}
	
	public void prepareTestData() {
		csvreader = new ReadCSV(TEST_FILE, true, CSVFormat.ENGLISH);
		int i = 0;
		while (csvreader.next()) {
			i++;
			final Integer count = new Integer(i);
			wdTestList.add(new WeatherData() {{
				this.id = count;
//		this.date = Integer.parseInt(csvreader.get("DATE"));
				this.actual_avg_temperature = Double.parseDouble(csvreader.get("TOBS"));
				this.actual_tmax_temp = Double.parseDouble(csvreader.get("TMAX"));
				this.actual_tmin_temp = Double.parseDouble(csvreader.get("TMIN"));
			}});
		}	
	}
	
	public void prepareAnomalyTestData() {
		csvreader = new ReadCSV(TEST_FILE, true, CSVFormat.ENGLISH);
		int i = 0;
		while (csvreader.next()) {
			i++;
			final Integer count = new Integer(i);
			wdAnomalyTestList.add(new WeatherData() {{
//				if (count == 450) {
//					this.id = count;
////					this.date = Integer.parseInt(csvreader.get("DATE"));
//					this.actual_avg_temperature = (Double.parseDouble(csvreader.get("TOBS")) - 32)*Double.valueOf((double)5/(double)9);
//					this.actual_tmax_temp = (Double.parseDouble(csvreader.get("TMAX")) - 32)*Double.valueOf((double)5/(double)9);
//					this.actual_tmin_temp = (Double.parseDouble(csvreader.get("TMIN")) - 32)*Double.valueOf((double)5/(double)9);
//				}
//				else if (count == 300) {
//					this.id = count;
////					this.date = Integer.parseInt(csvreader.get("DATE"));
//					this.actual_avg_temperature = (Double.parseDouble(csvreader.get("TOBS")) - 32)*Double.valueOf((double)5/(double)9);
//					this.actual_tmax_temp = (Double.parseDouble(csvreader.get("TMAX")) - 32)*Double.valueOf((double)5/(double)9);
//					this.actual_tmin_temp = (Double.parseDouble(csvreader.get("TMIN")) - 32)*Double.valueOf((double)5/(double)9);
//				}
//				else if (count >= 800 && count <= 820) {
//					this.id = count;
////					this.date = Integer.parseInt(csvreader.get("DATE"));
//					this.actual_avg_temperature = (Double.parseDouble(csvreader.get("TOBS")) - 32)*Double.valueOf((double)5/(double)9);
//					this.actual_tmax_temp = (Double.parseDouble(csvreader.get("TMAX")) - 32)*Double.valueOf((double)5/(double)9);
//					this.actual_tmin_temp = (Double.parseDouble(csvreader.get("TMIN")) - 32)*Double.valueOf((double)5/(double)9);
//				} 
//				else if (count >= 1500 && count <= 1540) {
//					this.id = count;
////					this.date = Integer.parseInt(csvreader.get("DATE"));
//					this.actual_avg_temperature = (Double.parseDouble(csvreader.get("TOBS")) - 32)*Double.valueOf((double)5/(double)9);
//					this.actual_tmax_temp = (Double.parseDouble(csvreader.get("TMAX")) - 32)*Double.valueOf((double)5/(double)9);
//					this.actual_tmin_temp = (Double.parseDouble(csvreader.get("TMIN")) - 32)*Double.valueOf((double)5/(double)9);
//				}else {
//					this.id = count;
////					this.date = Integer.parseInt(csvreader.get("DATE"));
//					this.actual_avg_temperature = Double.parseDouble(csvreader.get("TOBS"));
//					this.actual_tmax_temp = Double.parseDouble(csvreader.get("TMAX"));
//					this.actual_tmin_temp = Double.parseDouble(csvreader.get("TMIN"));
//				}
				if (count >=0 && count <= 100) {
					if (count == 50) {
						this.id = count;
	//					this.date = Integer.parseInt(csvreader.get("DATE"));
						this.actual_avg_temperature = (Double.parseDouble(csvreader.get("TOBS")) - 32)*Double.valueOf((double)5/(double)9);
						this.actual_tmax_temp = (Double.parseDouble(csvreader.get("TMAX")) - 32)*Double.valueOf((double)5/(double)9);
						this.actual_tmin_temp = (Double.parseDouble(csvreader.get("TMIN")) - 32)*Double.valueOf((double)5/(double)9);
					} 
					else 
					{
						this.id = count;
//						this.date = Integer.parseInt(csvreader.get("DATE"));
						this.actual_avg_temperature = Double.parseDouble(csvreader.get("TOBS"));
						this.actual_tmax_temp = Double.parseDouble(csvreader.get("TMAX"));
						this.actual_tmin_temp = Double.parseDouble(csvreader.get("TMIN"));
					}
				} 
			}});
		}	
	}
	
	public void normalizeTrainingData() {
		
		normTr = new NormalizeArray();
		normTr.setNormalizedHigh(1.0);
		normTr.setNormalizedLow(-1.0);
		
		
		double[] actual_avgtemp_data = new double[wdList.size()];
		double[] actual_tmaxtemp_data = new double[wdList.size()];
		double[] actual_tmintemp_data = new double[wdList.size()];
		
		for (WeatherData item: wdList) {
			int index = wdList.indexOf(item);
			actual_avgtemp_data[index] = item.actual_avg_temperature;
			actual_tmaxtemp_data[index] = item.actual_tmax_temp;
			actual_tmintemp_data[index] = item.actual_tmin_temp;
		}
		double[] normalizedArray_avgtemp = normTr.process(actual_avgtemp_data);
		double[] normalizedArray_tmaxtemp = normTr.process(actual_tmaxtemp_data);
		double[] normalizedArray_tmintemp = normTr.process(actual_tmintemp_data);
		
		for (WeatherData item: wdList) {
			item.setNormalized_avg_temperature(normalizedArray_avgtemp[wdList.indexOf(item)]);
			item.setNormalized_tmax_temp(normalizedArray_tmaxtemp[wdList.indexOf(item)]);
			item.setNormalized_tmin_temp(normalizedArray_tmintemp[wdList.indexOf(item)]);

		}
		
//		for (WeatherData item: wdList) {
//			System.out.println(item.date + " AVG " +item.actual_avg_temperature + " " + item.normalized_avg_temperature);
//			System.out.println(item.date + " TMAX " + item.actual_tmax_temp + " " + item.normalized_tmax_temp);
//			System.out.println(item.date + " TMIN " + item.actual_tmin_temp + " " + item.normalized_tmin_temp);
//		}
		
	}
	
	public void normalizeTestingData() {
		
		normTeAvg = new NormalizeArray();
		normTeAvg.setNormalizedHigh(NORMALIZE_HIGH);
		normTeAvg.setNormalizedLow(NORMALIZE_LOW);
		NormalizeArray normTe = new NormalizeArray();
		normTe.setNormalizedHigh(NORMALIZE_HIGH);
		normTe.setNormalizedLow(NORMALIZE_LOW);
		
		double[] actual_avgtemp_data = new double[wdTestList.size()];
		double[] actual_tmaxtemp_data = new double[wdTestList.size()];
		double[] actual_tmintemp_data = new double[wdTestList.size()];
		
		for (WeatherData item: wdTestList) {
			int index = wdTestList.indexOf(item);
			actual_avgtemp_data[index] = item.actual_avg_temperature;
			actual_tmaxtemp_data[index] = item.actual_tmax_temp;
			actual_tmintemp_data[index] = item.actual_tmin_temp;
		}
		double[] normalizedArray_avgtemp = normTeAvg.process(actual_avgtemp_data);
		double[] normalizedArray_tmaxtemp = normTe.process(actual_tmaxtemp_data);
		double[] normalizedArray_tmintemp = normTe.process(actual_tmintemp_data);
		
		
		for (WeatherData item: wdTestList) {
			item.setNormalized_avg_temperature(normalizedArray_avgtemp[wdTestList.indexOf(item)]);
			item.setNormalized_tmax_temp(normalizedArray_tmaxtemp[wdTestList.indexOf(item)]);
			item.setNormalized_tmin_temp(normalizedArray_tmintemp[wdTestList.indexOf(item)]);
		}
//		for (WeatherData item: wdTestList) {
//			System.out.println(item.date + " lolAVG " +item.actual_avg_temperature + " " + item.normalized_avg_temperature);
////			System.out.println(item.date + " TMAX " + item.actual_tmax_temp + " " + item.normalized_tmax_temp);
////			System.out.println(item.date + " TMIN " + item.actual_tmin_temp + " " + item.normalized_tmin_temp);
//		}

	}
	
	public void normalizeAnomalyTestingData() {
		
		normATeAvg = new NormalizeArray();
		normATeAvg.setNormalizedHigh(NORMALIZE_HIGH);
		normATeAvg.setNormalizedLow(NORMALIZE_LOW);
		NormalizeArray normATe = new NormalizeArray();
		normATe.setNormalizedHigh(NORMALIZE_HIGH);
		normATe.setNormalizedLow(NORMALIZE_LOW);
		
		double[] actual_avgtemp_data = new double[wdAnomalyTestList.size()];
		double[] actual_tmaxtemp_data = new double[wdAnomalyTestList.size()];
		double[] actual_tmintemp_data = new double[wdAnomalyTestList.size()];
		
		for (WeatherData item: wdAnomalyTestList) {
			int index = wdAnomalyTestList.indexOf(item);
			actual_avgtemp_data[index] = item.actual_avg_temperature;
			actual_tmaxtemp_data[index] = item.actual_tmax_temp;
			actual_tmintemp_data[index] = item.actual_tmin_temp;
		}
		double[] normalizedArray_avgtemp = normATeAvg.process(actual_avgtemp_data);
		double[] normalizedArray_tmaxtemp = normATe.process(actual_tmaxtemp_data);
		double[] normalizedArray_tmintemp = normATe.process(actual_tmintemp_data);
		
		
		for (WeatherData item: wdAnomalyTestList) {
			item.setNormalized_avg_temperature(normalizedArray_avgtemp[wdAnomalyTestList.indexOf(item)]);
			item.setNormalized_tmax_temp(normalizedArray_tmaxtemp[wdAnomalyTestList.indexOf(item)]);
			item.setNormalized_tmin_temp(normalizedArray_tmintemp[wdAnomalyTestList.indexOf(item)]);
		}
	}
	
	
}
