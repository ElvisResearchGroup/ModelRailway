package modelrailway;


public class Main {
	// ===============================================================
	// Main entry point
	// ===============================================================
	public static void main(String args[]) throws Exception {
		ModelRailway railway = new ModelRailway(args[0], 3);
		float speed = 0.1f;
		boolean direction = true;
		//			
		//			while (true) {
		//				Thread.currentThread().sleep(1000);
		//				railway.setSpeedAndDirection(1, true, speed);
		//				if (speed > 0.9f) {
		//					direction = false;
		//				} else if (speed < 0.0f) {
		//					direction = true;
		//				}
		//				if (direction) {
		//					speed = Math.min(1.0f, speed + 0.25f);
		//				} else {
		//					speed = Math.max(0.0f, speed - 0.1f);
		//				}
		//			}
		while(true) {
			Thread.currentThread().sleep(100);
			railway.sendMessage(0x83);
		}
	}
}
