package modelrailway;

import modelrailway.core.Controller;
import modelrailway.core.Train;
import modelrailway.util.SimpleController;
import modelrailway.util.ControlerCollision;
import modelrailway.util.TrainController;

public class Main2 extends Main {
	public Main2(ModelRailway railway, Controller controller) {
		super(railway, controller);
		// TODO Auto-generated constructor stub
	}

	public ModelRailway getRailway(){
		return rails;
	}
	public Controller getCtl(){
		return ctl;
	}
	private static ModelRailway rails;
	private static Controller ctl;

	public static void main(String args[]) throws Exception {
		String port = args[0];

		// Needed for connection on lab machines
		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		// Construct the model railway assuming the interface (i.e. USB Cable)
		// is on a given port. Likewise, we initialise it with three locomotives
		// whose addresses are 1,2 + 3. If more locomotives are to be used, this
		// needs to be updated accordingly.
		final ModelRailway railway = new ModelRailway(port,
				new int[] { 1, 2, 3 });
		rails = railway;
		// Add shutdown hook to make sure resources are released when quiting
		// the application, even if the application is quit in a non-standard
		// fashion.
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Disconnecting from railway...");
				railway.destroy();
			}
		}));

		// Enter Read, Evaluate, Print loop.
		Train[] trains = {
				new Train(0,true), // default config for train 0
				new Train(0,true), // default config for train 1
				new Train(0,true)  // default config for train 2
		};
		//Controller controller = new TrainController(trains,railway);
		//railway.register(controller);
		//controller.register(railway);
		//ctl = controller;
		//new Main2(railway,controller).readEvaluatePrintLoop();
	}
}
