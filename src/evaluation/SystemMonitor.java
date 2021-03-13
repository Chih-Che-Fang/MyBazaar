package evaluation;

public class SystemMonitor {

	public static double latency = 0.0;
	public static int sampleNum = 0;
	
	public static double resTime = 0.0;
	
	public static void addLatencySample(int durantion) {
		if(++sampleNum > 1) SystemMonitor.latency = Math.max(SystemMonitor.latency, durantion);
		resTime += durantion;
		System.out.println(String.format("Max Latency from %s samples = %s", sampleNum, SystemMonitor.latency));
		System.out.println(String.format("Avg Response Time from %s samples = %s", sampleNum, resTime / sampleNum));
	}
}
