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

		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		final Route route = new Route(false, 8,1,2,3,4,5,6,7);
		final Movable locomotive = new Locomotive(new Track[]{outerPiece}, 40,40,10, false);
		Train train = new Train(new Movable[]{locomotive});
		final Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		trainMap.put(0,train );
		train.setID(0);
		final Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();
		orientationMap.put(0, new modelrailway.core.Train(8, true));



		final Route route2 = new Route(false, 16,9,10,3,4,5,6,7,8);
		final Movable loco2 = new Locomotive(new Track[]{innerStart},40,40,10,true);
		Train train2 = new Train(new Movable[]{loco2});
		trainMap.put(1, train2);
		train2.setID(1);
		orientationMap.put(1,  new modelrailway.core.Train(16, true));
		Track headPiece = numberMap.get(1).get(0);

		ring.getSectionNumberMap().get(16).getEntryRequests().offer(1);
		ring.getSectionNumberMap().get(9).getEntryRequests().offer(1);

		ring.getSectionNumberMap().get(8).getEntryRequests().offer(0);
		ring.getSectionNumberMap().get(1).getEntryRequests().offer(0);

		////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
 					Integer oldI = i;
 					if(!((SectionChanged) e).getInto()){
 						Integer t1 = orientationMap.get(0).currentSection();
 						Integer t2 = orientationMap.get(0).currentSection();
 						Train t1t = trainMap.get(0);
 						Train t2t = trainMap.get(1);
 						Integer train0Sec = t1t.getFront().getSection().getNumber();
 						Integer train1Sec = t2t.getFront().getSection().getNumber();

 						System.out.println("i:"+ i);
 						System.out.println("train1Sec: "+train0Sec);
 						System.out.println("train2Sec: "+train1Sec);


 						try{
 							System.out.println("route.nextSection(i): "+route.nextSection(i));
 						    if(route.nextSection(i) == train0Sec) {

 						       i = route.nextSection(i);
 						    }
 						}catch(IllegalArgumentException ex){}

 						try{
 							System.out.println("route2.nextSection(i):"+route2.nextSection(i));
 					    	if (route2.nextSection(i) == train1Sec){
 							   i = route2.nextSection(i);
 					 	    }
 						}catch(IllegalArgumentException ex){}

 						if(i == oldI){
 							System.out.println("Mistake in test");
 							th.interrupt();

 						}
 					}
 					if(loco2.getFront().getSection().getNumber() == i){
 						outputArrayLoco2.add(""+i);
 					} else{
 						outputArrayLoco1.add(""+i);
 					}
 				}
 				if(e instanceof Event.EmergencyStop){
 					Integer loco = ((Event.EmergencyStop) e).getLocomotive(); // get the locomotive
 					System.out.println("Emergency stop detected in unit test.");
 					if(loco == 1){
 						outputArrayLoco1.add("Stop");
 					}
 					else{
 						outputArrayLoco2.add("Stop");
 					}
 				}
 				if(e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
   				  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 				  System.out.println("sectionchanged in unit test into section: "+ i);
 				  if(i == 7){// if any loco is changing into section 8. i.e out of section 7
 					  System.out.println("stop triggered by unit test");
 					  controller.stop(0);
 					  controller.stop(1);
 					  System.out.flush();
 					  th.interrupt();
 				  }
 				}
 				else if(e instanceof Event.SpeedChanged){
 				   System.out.println("speed changed in test: "+((Event.SpeedChanged) e).getLocomotive());

 				}
 				if(locomotive.getCurrentSpeed()==0 && loco2.getCurrentSpeed()== 0){
 					System.out.println("both have stopped");
 					System.out.flush();
 					th.interrupt();
 				}
 				System.out.println("loco0: "+locomotive.getCurrentSpeed());
 				System.out.println("loco1: "+loco2.getCurrentSpeed());
 			}
		};

		controller.register(lst);
		controller.start(1, route2);
 		controller.start(0, route);



 		System.out.println("The end of the hardware test.");
		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest0");
			System.out.println("output: "+outputArrayLoco1.toString());
			System.out.println("output: "+outputArrayLoco2.toString());
			System.out.println("1:"+ring.getSectionNumberMap().get(1).getEntryRequests().toString());
			System.out.println("2:"+ring.getSectionNumberMap().get(2).getEntryRequests().toString());
			System.out.println("3:"+ring.getSectionNumberMap().get(3).getEntryRequests().toString());
			System.out.println("4:"+ring.getSectionNumberMap().get(4).getEntryRequests().toString());
			System.out.println("5:"+ring.getSectionNumberMap().get(5).getEntryRequests().toString());
			System.out.println("6:"+ring.getSectionNumberMap().get(6).getEntryRequests().toString());
			System.out.println("7:"+ring.getSectionNumberMap().get(7).getEntryRequests().toString());
			System.out.println("8:"+ring.getSectionNumberMap().get(8).getEntryRequests().toString());
			System.out.println("9:"+ring.getSectionNumberMap().get(9).getEntryRequests().toString());
			System.out.println("10:"+ring.getSectionNumberMap().get(10).getEntryRequests().toString());
		    System.out.println("16:"+ring.getSectionNumberMap().get(16).getEntryRequests().toString());

			System.out.flush();
		}
	}

}
