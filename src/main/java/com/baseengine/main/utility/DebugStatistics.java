package main.java.com.baseengine.main.utility;

public class DebugStatistics
{
	private static final int MEGABYTE = 1024 * 1024;
	private static float memoryTotalMiB = 0;
	private static float memoryUsedMiB = 0;
	private static float memoryFreeMiB = 0;
	
	private static int numDrawCallsLastFrame = 0;
	private static int numDrawCallsThisFrame = 0;
	
	private static int numModels = 0;
	private static int numModelBatches = 0;
	
	
	private static float oneSecondTimer = 0;
	private static int frameCounter = 0;
	private static int framesPerSecond = 0;
	
	private static float tenSecondTimer = 0;
	private static float slowestFrame = -Float.MAX_VALUE;
	private static float fastestFrame = Float.MAX_VALUE;
	private static String slowestFrameOutput = "";
	private static String fastestFrameOutput = "";
	
	
	public static void countDrawCall() {
		numDrawCallsThisFrame++;
	}
	
	public static void countModel() {
		numModels ++;
	}
	
	public static void removeRenderable() {
		numModels --;
	}
	
	public static void countModelBatch() {
		numModelBatches++;
	}
	
	public static void removeModelBatch() {
		numModelBatches--;
	}
	
	public static void endFrame(float deltaTime) {
		oneSecondTimer += deltaTime;
		frameCounter++;
		if (oneSecondTimer >= 1.0) {
			oneSecondTimer -= 1.0;
			framesPerSecond = frameCounter;
			frameCounter = 0;
		}
		
		tenSecondTimer += deltaTime;
		if (tenSecondTimer >= 10) {
			tenSecondTimer -= 10;
			fastestFrame = Float.MAX_VALUE;
			slowestFrame = -Float.MAX_VALUE;
		}
		
		if (deltaTime > slowestFrame) {
			slowestFrame = deltaTime;
			slowestFrameOutput = String.format("%.2f", slowestFrame * 1000);
		}
		if (deltaTime < fastestFrame) {
			fastestFrame = deltaTime;
			fastestFrameOutput = String.format("%.2f", fastestFrame * 1000);
		}
		
		
		numDrawCallsLastFrame = numDrawCallsThisFrame;
		numDrawCallsThisFrame = 0;
		
		Runtime instance = Runtime.getRuntime();
		memoryTotalMiB = instance.totalMemory() / MEGABYTE;
		memoryFreeMiB = instance.freeMemory() / MEGABYTE;
		memoryUsedMiB = memoryTotalMiB - memoryFreeMiB;
		
		
	}
	
	public static float getTotalMemoryMiB() {
		return memoryTotalMiB;
	}
	
	public static float getUsedMemoryMiB() {
		return memoryUsedMiB;
	}
	
	public static float getFreeMemoryMiB() {
		return memoryFreeMiB;
	}
	
	public static int getFramesPerSecond() {
		return framesPerSecond;
	}
	
	public static String getFastestFrame() {
		return fastestFrameOutput;
	}
	
	public static String getSlowestFrame() {
		return slowestFrameOutput;
	};
	
	public static int getNumDrawCallsPerFrame() {
		return numDrawCallsLastFrame;
	}
	
	public static int getNumModels() {
		return numModels;
	}
	
	public static int getNumModelBatches() {
		return numModelBatches;
	}
}
