package modelrailway.testing;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import modelrailway.Main;
import modelrailway.ModelRailway;
import modelrailway.Main.Command;
import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Route;
import modelrailway.core.Section;
import modelrailway.core.Event.Listener;
import modelrailway.core.Event.SectionChanged;
import modelrailway.simulation.Locomotive;
import modelrailway.simulation.Movable;
import modelrailway.simulation.Simulator;
import modelrailway.simulation.Track;
import modelrailway.simulation.Straight.StraightDblRing;
import modelrailway.simulation.Train;
import modelrailway.util.ControlerCollision;
import modelrailway.util.MovementController;
import modelrailway.util.Pair;
import modelrailway.util.SimulationTrack;
import modelrailway.util.TrainController;
import modelrailway.simulation.Switch;
import static org.junit.Assert.assertTrue;

public class MultiTrainSoftwareTest {


	@Test public void softwareTest0(){
		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();

		Map<Integer,Section> numberMap = ring.getSectionNumberMap();

		Section startSec = numberMap.get(8);
		Track outerPiece = startSec.get(0);
		Integer headID = ((Switch)outerPiece).getSwitchID();

		Track innerStart = numberMap.get(16).get(0);

		Integer innerID = ((Switch) innerStart).getSwitchID();



		final Route route = new Route(true, 8,1,2,3,4,5,6,7);

		final Movable locomotive = new Locomotive(new Track[]{outerPiece}, 40,40,10, false);

		Train train = new Train(new Movable[]{locomotive});

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();

		trainMap.put(0,train );
		train.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(8, true));



		final Route route2 = new Route(true, 16,9,10,3,4,5,6,7,8);

		final Movable loco2 = new Locomotive(new Track[]{innerStart},40,40,10,true);
		
		Train train2 = new Train(new Movable[]{loco2});
		trainMap.put(1, train2);
		train2.setID(1);
		orientationMap.put(1,  new modelrailway.core.Train(16, true));

		Track headPiece = numberMap.get(1).get(0);
		final Simulator sim = new Simulator(headPiece, orientationMap, trainMap);

		final ControlerCollision controller = new ControlerCollision(orientationMap,ring.getSectionNumberMap(),headPiece,sim);

		sim.register(controller);

		controller.set(headID, startSec.retrieveSwitchingOrder(new Pair<Integer,Integer>(7,1)).get(0));
		controller.set(innerID, numberMap.get(16).retrieveSwitchingOrder(new Pair<Integer,Integer>(15,9)).get(0));

		
		
		final ArrayList<String> outputArrayLoco1 = new ArrayList<String>();
		final ArrayList<String> outputArrayLoco2 = new ArrayList<String>();
		final Thread th = Thread.currentThread();
		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());
 				
 				if(e instanceof Event.SectionChanged){
 					
 					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;	
 					if(loco2.getFront().getSection().getNumber() == i){
 						outputArrayLoco2.add(""+i);
 					} else{
 						outputArrayLoco1.add(""+i);
 						
 					}
 				
 				}
 				
 				if(e instanceof Event.EmergencyStop){
 					Integer loco = ((Event.EmergencyStop) e).getLocomotive();
 					if(loco == 1){
 						outputArrayLoco1.add("Stop");
 					}
 					else{
 						outputArrayLoco2.add("Stop");
 						
 					}
 					
 				}

 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 					
 				  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 				  
 				  System.out.println("sectionchanged in unit test into section: "+ i);
 				  if(i == 8){
 					  System.out.println("stop triggered by unit test");
 					  controller.stop(0);
 					  controller.stop(1);
 					  th.interrupt();

 				  }
 				  
 				  
 				}
 				else if(e instanceof Event.SpeedChanged){

 				   System.out.println("speed changed in test: "+((Event.SpeedChanged) e).getLocomotive());
 				}
 				//else if(e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 					//Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					//outputArray.add(route.nextSection(i));


 				//}

 			//	if(e instanceof Event.EmergencyStop){
 			//		System.out.println("an emergency stop has been triggered by an event in the unit test");
 					//controller.stop(0);
 					//th.interrupt();
 				//}
 				
 				if(locomotive.getCurrentSpeed() ==0 && loco2.getCurrentSpeed() == 0){
 					System.out.println("both have stopped");
 					th.interrupt();
 					
 				}
 			}

		};

		controller.register(lst);
		controller.start(1, route2);
 		controller.start(0, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest0");
			//System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArrayLoco1.toString());
			System.out.println("output: "+outputArrayLoco2.toString());
		}
		//assert(outputArray.get(0) == 2);
		//assert(outputArray.get(1) == 3);
		//assert(outputArray.get(2) == 4);
		//assert(outputArray.get(3) == 5);
		//assert(outputArray.get(4) == 6);
		//assert(outputArray.get(5) == 7);
		//assert(outputArray.get(6) == 8);
		//assert(outputArray.get(7) == 1);

		((TrainController) controller).deregister(lst);

	}

}
