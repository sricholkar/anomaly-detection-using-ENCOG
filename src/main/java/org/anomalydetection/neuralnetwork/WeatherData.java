package org.anomalydetection.neuralnetwork;

import java.util.Date;

public class WeatherData {

	public int id;
	public int date;
	public double actual_avg_temperature;
	public double actual_tmax_temp;
	public double actual_tmin_temp;
	public double normalized_avg_temperature;
	public double normalized_tmax_temp;
	public double normalized_tmin_temp;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getActual_avg_temperature() {
		return actual_avg_temperature;
	}

	public void setActual_avg_temperature(double actual_avg_temperature) {
		this.actual_avg_temperature = actual_avg_temperature;
	}

	public double getNormalized_avg_temperature() {
		return normalized_avg_temperature;
	}

	public void setNormalized_avg_temperature(double normalized_avg_temperature) {
		this.normalized_avg_temperature = normalized_avg_temperature;
	}

	public double getActual_tmax_temp() {
		return actual_tmax_temp;
	}

	public void setActual_tmax_temp(double actual_tmax_temp) {
		this.actual_tmax_temp = actual_tmax_temp;
	}

	public double getActual_tmin_temp() {
		return actual_tmin_temp;
	}

	public void setActual_tmin_temp(double actual_tmin_temp) {
		this.actual_tmin_temp = actual_tmin_temp;
	}

	public double getNormalized_tmax_temp() {
		return normalized_tmax_temp;
	}

	public void setNormalized_tmax_temp(double normalized_tmax_temp) {
		this.normalized_tmax_temp = normalized_tmax_temp;
	}

	public double getNormalized_tmin_temp() {
		return normalized_tmin_temp;
	}

	public void setNormalized_tmin_temp(double normalized_tmin_temp) {
		this.normalized_tmin_temp = normalized_tmin_temp;
	}
	
	
	
}
