package org.anomalydetection.neuralnetwork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.anomalydetection.neuralnetwork.TrainingData;
import org.encog.ConsoleStatusReportable;
import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.error.ErrorCalculationMode;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.mathutil.randomize.Randomizer;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.ml.data.temporal.TemporalDataDescription.Type;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.propagation.resilient.RPROPType;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.JordanPattern;
import org.encog.neural.prune.PruneIncremental;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizeArray;
import org.encog.util.simple.EncogUtility;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

public class Elman {

//	public static final double MAX_ERROR = 0.0325;
	private static BasicNetwork network;

	public static void run(double max_error) {
		TemporalMLDataSet trainData = prepareTemporalData();
		createAndTrainNetwork(trainData, max_error);
		Map<String, List<Double>> mappedData1 = predictTempData(TrainingData.wdAnomalyTestList, TrainingData.normATeAvg);
//		Map<String, List<Double>> mappedData2 = predictTempData(TrainingData.wdTestList, TrainingData.normTeAvg);
		computeRMSD(mappedData1, "withAnomalies");
//		computeRMSD(mappedData2, "withoutAnomalies");


	}
	
	public static TemporalMLDataSet prepareTemporalData() {
		TemporalMLDataSet trainingset = new TemporalMLDataSet(TrainingData.INPUT_WINDOW_SIZE, TrainingData.PREDICT_WINDOW_SIZE);
		TemporalDataDescription tdd_avgtemp = new TemporalDataDescription(Type.RAW, true, true);
		TemporalDataDescription tdd_tmaxtemp = new TemporalDataDescription(Type.RAW, true, false);
		TemporalDataDescription tdd_tmintemp = new TemporalDataDescription(Type.RAW, true, false);
		trainingset.addDescription(tdd_avgtemp);
		trainingset.addDescription(tdd_tmaxtemp);
		trainingset.addDescription(tdd_tmintemp);
		
		for (WeatherData item: TrainingData.wdList) {
			TemporalPoint point = new TemporalPoint(trainingset.getDescriptions().size());
			point.setSequence(item.id);
			point.setData(0, item.normalized_avg_temperature);
			point.setData(1, item.normalized_tmax_temp);
			point.setData(2, item.normalized_tmin_temp);
			trainingset.getPoints().add(point);
		}
		trainingset.generate();
		return trainingset;
	}
	
	
	public static void createAndTrainNetwork(TemporalMLDataSet trainData, double max_error) {
		ElmanPattern pattern = new ElmanPattern();
//		JordanPattern pattern = new JordanPattern();
//		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(TrainingData.INPUT_WINDOW_SIZE);
		pattern.setOutputNeurons(TrainingData.PREDICT_WINDOW_SIZE);
//		pattern.addHiddenLayer(1);
		pattern.setActivationFunction(new ActivationTANH());
		PruneIncremental prune = new PruneIncremental(trainData, pattern, 100, 10, 5, new ConsoleStatusReportable());
		prune.addHiddenLayer(1, 5);
//		prune.addHiddenLayer(0, 8);
		prune.process();
		network = (BasicNetwork) prune.getBestNetwork();
//		network = (BasicNetwork) pattern.generate();
		Randomizer randomizer = new ConsistentRandomizer(-1, 1, 123);
		randomizer.randomize(network);
//		ResilientPropagation train = new ResilientPropagation(network, trainData);
//		train.setDroupoutRate(0.01);
//		train.setRPROPType(RPROPType.RPROPp);
//		train.setBatchSize(1);
		
		CalculateScore score = new TrainingSetScore(trainData);
		final MLTrain trainAlt = new NeuralSimulatedAnnealing(network, score, 10, 2, 100);
		final ResilientPropagation trainMain = new ResilientPropagation(network, trainData,0.00001, 0.0001 ); //0.01, 0.00001
		trainMain.setBatchSize(16);
		trainMain.setRPROPType(RPROPType.RPROPp);
//		trainMain.setDroupoutRate(0.001);
		final StopTrainingStrategy stop = new StopTrainingStrategy();
		trainMain.addStrategy(new Greedy());
		trainMain.addStrategy(new HybridStrategy(trainAlt));
		trainMain.addStrategy(stop);
		int epoch = 1; 
		List<Double> netScore = new ArrayList<>();

		do { 
		   trainMain.iteration(); 
		   System.out.println("Epoch #" + epoch + " Error:" + trainMain.getError()); 
		   epoch++;
		   netScore.add(score.calculateScore(network));
		} while(trainMain.getError() > max_error);
		 
		System.out.println("Trained network prepared");
		EncogDirectoryPersistence.saveObject(new File("ff.eg"), network);
		
			XYSeriesCollection sc = new XYSeriesCollection();
			createSeriesForScore(sc, 0, netScore, "Score");
			JFreeChart chart = createScoreChart(sc);
			plotDataset(sc, chart, "Training Analysis");
			System.out.println("MSE " + calculateError(network, trainData, ErrorCalculationMode.MSE) + " " + "RMSE " + calculateError(network, trainData, ErrorCalculationMode.RMS));
		
	} 
	

	private static JFreeChart createScoreChart(XYSeriesCollection sc) {
//		String title = networkType;
        String xAxisLabel = "Iterations";
        String yAxisLabel = "Score";
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        boolean legend = true;
        boolean tooltips = false;
        boolean urls = false;
        JFreeChart chart = ChartFactory.createXYLineChart("Fee", xAxisLabel, yAxisLabel, sc, orientation, legend, tooltips, urls);

		return chart;
	}

	private static void createSeriesForScore(XYSeriesCollection sc, int offset, List<Double> netScore, String name) {
		XYSeries series = new XYSeries(name);
		int iterations = 1;
		for (Double scoreValues: netScore) {
			series.add(iterations + offset, scoreValues);
			iterations++;
		}
		sc.addSeries(series);
	}

	private static double calculateError(BasicNetwork method, TemporalMLDataSet data, ErrorCalculationMode errorMode) {
		ErrorCalculation.setMode(errorMode);
		ErrorCalculation ec = new ErrorCalculation();
		try {
            for (final MLDataPair pair : data) {
                final MLData actual = method.compute(pair.getInput());
                ec.updateError(actual.getData(), pair.getIdeal()
                        .getData(), pair.getSignificance());
            }
        } catch(EncogError e) {
            return Double.NaN;
        }
		
		return ec.calculate();
	}

	public static Map<String, List<Double>> predictTempData(List<WeatherData> testList, NormalizeArray normAvgData) {
		
		Map<String, List<Double>> map = new HashMap<String, List<Double>>();
		List<Double> predList = new ArrayList<Double>();
		List<Double> actList = new ArrayList<Double>();
		int evaluation_start = testList.get(0).id + TrainingData.INPUT_WINDOW_SIZE; 
		ArrayList<Integer> arraylist = new ArrayList<Integer>(testList.size());
		for (int i = 0; i < testList.size(); i++)
			arraylist.add(testList.get(i).id); 
		int evaluation_end = Collections.max(arraylist); 
		for (int currentId = evaluation_start; currentId < evaluation_end; currentId++) { 
			
			final Integer itr = new Integer(currentId); 
			BasicMLData input = new BasicMLData(TrainingData.INPUT_WINDOW_SIZE);
			for (int i= 0; i<input.size(); i++) {
				final Integer itri = new Integer(i);
				Double val = testList.stream()
													.filter(t-> t.id == ((itr - TrainingData.INPUT_WINDOW_SIZE) + itri))
													.map(t-> t.normalized_avg_temperature)
													.findFirst()
													.get();
				input.add(itri, val);
			}
			MLData output = network.compute(input);

			double normalizedPredicted = output.getData(0);
			double normalizedAct = testList.stream()
															.filter(t->t.id == itr) //itr = currentId = evaluationStart = 20
															.map(t->t.normalized_avg_temperature)
															.findFirst()
															.get();
			predList.add(normalizedPredicted);
			actList.add(normalizedAct);

			double actual = normAvgData.getStats().deNormalize(normalizedAct);

			double predicted = normAvgData.getStats().deNormalize(normalizedPredicted);
			
			int date = testList.stream().filter(t->t.id == itr).map(t->t.date).findFirst().get();
			actual = Math.round(actual);
	
			predicted = Math.round(predicted);
			 
			
			BufferedWriter out = null;
			try {
				FileWriter fstream = new FileWriter("temperaturePrediction.csv", true );
				out = new BufferedWriter(fstream);
				out.write(date + ", " + actual + ", "+predicted + "\n");
			} catch (IOException e) {
				
				System.err.println("Error: " + e.getMessage());
			}finally
			{
		        try {
					out.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			    
			}
			System.out.println("Id : " + currentId + "        Actual " + actual +"       Predicted : " + predicted);
			
			
		}
		XYSeriesCollection seriesCollection = new XYSeriesCollection();
		createSeries(seriesCollection, 0, actList, "ActualTestData");
		createSeries(seriesCollection, 0, predList, "PredictedData");
		JFreeChart chart = createAvsPChart(seriesCollection);
		plotDataset(seriesCollection, chart, "Test Results");
		map.put("actual", actList);
		map.put("predicted", predList);
		return map;
	}
	
	private static void computeRMSD(Map<String, List<Double>> mappedData, String name) {
		ErrorCalculation.setMode(ErrorCalculationMode.MSE);
		ErrorCalculation ec = new ErrorCalculation();
		int n = mappedData.get("actual").size();
		
		for(int i = 0; i < n; i++) {
			Double actual = new Double(mappedData.get("actual").get(i));
			Double predicted = new Double(mappedData.get("predicted").get(i));
			
			ec.updateError(actual, predicted);	
			}
		System.out.println(name + " " + ec.calculate());

//		int n = mappedData.get("actual").size();
//		double globalError = 0;
//		for(int i = 1; i < n; i++) {
//			Double actual = new Double(mappedData.get("actual").get(i));
//			Double predicted = new Double(mappedData.get("predicted").get(i));
//			double delta = actual-predicted;
//			globalError = globalError + delta * delta;
//			double rootMeanSquareError = Math.sqrt(globalError/i);
//			System.out.println("RMSE "+ rootMeanSquareError);
//			}
		
		

	}

	

	private static JFreeChart createAvsPChart(XYSeriesCollection seriesCollection) {
		String title = "Feed forward's Anomaly Detection example";
        String xAxisLabel = "Timestep";
        String yAxisLabel = "Temperature";
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        boolean legend = true;
        boolean tooltips = false;
        boolean urls = false;
        JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, seriesCollection, orientation, legend, tooltips, urls);
		return chart;

		
	}

	private static void createSeries(XYSeriesCollection seriesCollection, int offset, List<Double> data,
			String name) {
		XYSeries series = new XYSeries(name);
		int size = data.size();
		int count = 0;
		for (Double d: data) {
			count++;
			
			series.add(count + offset, d);
		}
		System.out.println(series.getItemCount());
		seriesCollection.addSeries(series);
	}
	
	private static void plotDataset(XYSeriesCollection seriesCollection, JFreeChart chart, String title) {
		
		
        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();

        // Auto zoom to fit time series in initial window
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(true);

        JPanel panel = new ChartPanel(chart);

        JFrame f = new JFrame();
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.pack();
        f.setTitle(title);

        RefineryUtilities.centerFrameOnScreen(f);
        f.setVisible(true);
	}
}
