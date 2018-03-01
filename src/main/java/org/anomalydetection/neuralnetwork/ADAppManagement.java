package org.anomalydetection.neuralnetwork;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class ADAppManagement {

	private static final double MAX_ERROR = 0.04072;// 0.04072
	private static final double Mb = 1024 * 1024;
	private static final double Gb = 1024 * 1024 * 1024;
	public static void main(String[] args) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		
        double startTime = System.currentTimeMillis();

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		ADApp ad_mBean = new ADApp(MAX_ERROR);
		ObjectName name = new ObjectName("org.anomalydetection.neuralnetwork:type=AnomalyDetection");
		mbs.registerMBean(ad_mBean, name);
		
		ad_mBean.anomalyDetection();
		
		double stopTime = System.currentTimeMillis();
        double elapsedTime = stopTime - startTime;
        System.out.println("Runtime of the Program in Milliseconds: " + elapsedTime);
	}
}
