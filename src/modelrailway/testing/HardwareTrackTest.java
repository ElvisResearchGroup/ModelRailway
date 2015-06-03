package modelrailway.testing;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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

public class HardwareTrackTest extends Main2{

	
	public HardwareTrackTest(ModelRailway railway, Controller controller) {
		super(railway, controller);
		Command[] cmd = this.getCommands();
		Command htest0 = this.new Command("hardwareTest0", super.getMethod("hardwareTest0"));
		Command[] cmd2 = new Command[cmd.length+1];
		System.arraycopy(cmd, 0, cmd2, 0, cmd.length);
		cmd2[cmd2.length-1] = htest0;
		this.setCommands(cmd2);
		
		// TODO Auto-generated constructor stub
	}
	
	
	
	final String port="/dev/ttyACM0";
	public void hardwareTest0() throws Exception{

		
		final Controller controller = getCtl();
		// Enter Read, Evaluate, Print loop.
		Train[] trains = {
				new Train(1,true), // default config for train 0
		};

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
		assert(outputArray.get(0) == 2);
		assert(outputArray.get(1) == 3);
		assert(outputArray.get(2) == 4);
		assert(outputArray.get(3) == 5);
		assert(outputArray.get(4) == 6);
		assert(outputArray.get(5) == 7);
		assert(outputArray.get(6) == 8);
		assert(outputArray.get(7) == 1);


	}
}
