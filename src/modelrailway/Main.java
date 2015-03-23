package modelrailway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import modelrailway.core.Event;

public class Main {
		
	// ===============================================================
	// Main entry point
	// ===============================================================
	public static void main(String args[]) throws Exception {
		final ModelRailway railway = new ModelRailway(args[0],new int[]{1,2,3});
		
		// Add shutdown hook to make sure resources are released when quiting
		// the application, even if the application is quit in a non-standard
		// fashion.
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    @Override
		    public void run() {
		        System.out.println("Disconnecting from railway...");
		        railway.destroy();
		    }
		}){});
		
		// Enter Read, Evaluate, Print loop.
		
		final BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		
		try {
			System.out.println("Welcome to the Model Railway!");
			while (true) {
				System.out.print("> ");
				String text = input.readLine();

				// commands goes here
				if (text.equals("help")) {
					printHelp();
				} else if (text.startsWith("go")) {
					startTrain(text,railway);
				} else if (text.equals("exit")) {
					System.exit(0);
				} 
			}
		} catch (IOException e) {
			System.err.println("I/O Error - " + e.getMessage());
		}
	}
	
	private static void startTrain(String command, ModelRailway railway) {
		int locomotive = Integer.parseInt(command.substring(3));
		//railway.notify(new Event.DirectionChanged(locomotive, true));
		//railway.notify(new Event.SpeedChanged(locomotive, 50, 50));
		railway.setTrainSpeed(locomotive, 0.5f);
	}
	
	private static void printHelp() {
		System.out.println("Model rail commands:");
		System.out.println("\thelp --- access this help page");
		System.out.println("\texit --- quit the application");
	}
}
