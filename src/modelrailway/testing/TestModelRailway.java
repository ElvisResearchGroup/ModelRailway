package modelrailway.testing;

import org.junit.Test;

import modelrailway.Main;
import modelrailway.ModelRailway;
import modelrailway.core.Controller;
import modelrailway.core.Train;
import modelrailway.util.SimpleController;

public class TestModelRailway {
	    final static String port="/dev/ttyACM0";

		@Test public void testRailway0(){


		}

		public static void main(String[] args){

			try {
				// Needed for connection on lab machines
				System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

				// Construct the model railway assuming the interface (i.e. USB Cable)
				// is on a given port. Likewise, we initialise it with three locomotives
				// whose addresses are 1,2 + 3. If more locomotives are to be used, this
				// needs to be updated accordingly.
				System.out.println("Port: "+port);
				final ModelRailway railway = new ModelRailway(port,
						new int[] { 1});

				// Add shutdown hook to make sure resources are released when quiting
				// the application, even if the application is quit in a non-standard
				// fashion.
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					@Override
					public void run() {
						System.out.println("Disconnecting from railway...");
						railway.destroy();
					}
				}) {
				});

				// Enter Read, Evaluate, Print loop.
				Train[] trains = {
						new Train(1,true), // default config for train 0  // default config for train 2
				};
				Controller controller = new SimpleController(trains);
				railway.register(controller);
				controller.register(railway);
				new Main(railway,controller).readEvaluatePrintLoop();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
