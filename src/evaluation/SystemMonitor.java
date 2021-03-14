package evaluation;
/*
 * A class used to store and calculate the latency / average respond time of client requests.  
 */
public class SystemMonitor {

	public static double latency = 0.0; // latency of client requests
	public static int sampleNum = 0; //number of request sampled
	public static double resTime = 0.0; //response time
	
	//Add latency/respond time sample to calculate the average latency/respond time
	public static void addLatencySample(int durantion) {
		if(++sampleNum > 1) SystemMonitor.latency = Math.max(SystemMonitor.latency, durantion);
		resTime += durantion;
		System.out.println(String.format("Max Latency from %s samples = %s", sampleNum, SystemMonitor.latency));
		System.out.println(String.format("Avg Response Time from %s samples = %s", sampleNum, resTime / sampleNum));
	}
}
