package modelrailway.testing;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import modelrailway.Main2;
import modelrailway.ModelRailway;
import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Route;
import modelrailway.core.Section;
import modelrailway.core.Train;
import modelrailway.core.Event.Listener;
import modelrailway.core.Event.SectionChanged;
import modelrailway.simulation.Locomotive;
import modelrailway.simulation.Movable;
import modelrailway.simulation.Simulator;
import modelrailway.simulation.Track;
import modelrailway.simulation.Straight.StraightDblRing;
import modelrailway.util.ControlerCollision;
import modelrailway.util.SimulationTrack;
import modelrailway.util.TrainController;

public class HardwareTrackTest {


	final String port="/dev/ttyACM0";
	@Test public void hardwareTest0() throws Exception{

		// Needed for connection on lab machines
		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		// Construct the model railway assuming the interface (i.e. USB Cable)
		// is on a given port. Likewise, we initialise it with three locomotives
		// whose addresses are 1,2 + 3. If more locomotives are to be used, this
		// needs to be updated accordingly.
		final ModelRailway railway = new ModelRailway(port,
				new int[] {1});

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
				new Train(1,true), // default config for train 0
		};
		final Controller controller = new TrainController(trains,railway);
		railway.register(controller);
		controller.register(railway);
		final Thread evalThread = new Thread(){
			public void run(){
				new Main2(railway,controller).readEvaluatePrintLoop();
			}
		};
		evalThread.start();

		Thread.currentThread().sleep(10000); // wait for 5 seconds for turn on.

		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();

		Map<Integer,Section> numberMap = ring.getSectionNumberMap();

		Section startSec = numberMap.get(1);
		Track headPiece = startSec.get(0);

		Route route = new Route(true, 1,2,3,4,5,6,7,8);

		Movable locomotive = new Locomotive(new Track[]{headPiece}, 40,40,10, false);

		modelrailway.simulation.Train train = new modelrailway.simulation.Train(new Movable[]{locomotive});

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();

		trainMap.put(0,train );
		train.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, trains[0]);


		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();


		controller.register(new Listener(){
			public void notify(Event e){
 				//System.out.println("event "+e.toString());
 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  outputArray.add(((Event.SectionChanged) e).getSection());

 				  if(((Event.SectionChanged)e).getSection() == 1){

 					  controller.stop(0);
 					  th.interrupt();

 				  }
 				}

 				if(e instanceof Event.EmergencyStop){
 					controller.stop(0);
 					th.interrupt();
 				}
 			}

		});

 		controller.start(0, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("testTrackRun0");
			System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}
		assertTrue(outputArray.get(0) == 2);
		assertTrue(outputArray.get(1) == 3);
		assertTrue(outputArray.get(2) == 4);
		assertTrue(outputArray.get(3) == 5);
		assertTrue(outputArray.get(4) == 6);
		assertTrue(outputArray.get(5) == 7);
		assertTrue(outputArray.get(6) == 8);
		assertTrue(outputArray.get(7) == 1);


	}
}
