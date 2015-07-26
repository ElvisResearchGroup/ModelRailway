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



		final Route route0 = new Route(true, 8,1,2,3,4,5,6,7);

		final Movable loco0= new Locomotive(new Track[]{outerPiece}, 40,40,10, false);

		Train train0 = new Train(new Movable[]{loco0});

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();

		trainMap.put(0,train0 );
		train0.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(8, true));



		final Route route1 = new Route(true, 16,9,10,3,4,5,6,7,8);

		final Movable loco1 = new Locomotive(new Track[]{innerStart},40,40,10,true);
		
		Train train1 = new Train(new Movable[]{loco1});
		trainMap.put(1, train1);
		train1.setID(1);
		orientationMap.put(1,  new modelrailway.core.Train(16, true));

		Track headPiece = numberMap.get(1).get(0);
		final Simulator sim = new Simulator(headPiece, orientationMap, trainMap);

		final ControlerCollision controller = new ControlerCollision(orientationMap,ring.getSectionNumberMap(),headPiece,sim);

		controller.sections().get(8).getEntryRequests().add(0);
		controller.sections().get(1).getEntryRequests().add(0);
		
		controller.sections().get(16).getEntryRequests().add(1);
		controller.sections().get(9).getEntryRequests().add(1);
		
		sim.register(controller);

		controller.set(headID, startSec.retrieveSwitchingOrder(new Pair<Integer,Integer>(7,1)).get(0));
		controller.set(innerID, numberMap.get(16).retrieveSwitchingOrder(new Pair<Integer,Integer>(15,9)).get(0));
		
		
		final ArrayList<Integer> outputLoco0 = new ArrayList<Integer>();
		final ArrayList<Integer> outputLoco1 = new ArrayList<Integer>();
		outputLoco0.add(8);
		outputLoco1.add(16);
		
		final ArrayList<String> outputArrayLoco0 = new ArrayList<String>();
		final ArrayList<String> outputArrayLoco1 = new ArrayList<String>();
		final Thread th = Thread.currentThread();
		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());
 				
 				if(e instanceof Event.SectionChanged){
 					Integer sec0 = controller.trainOrientations().get(0).currentSection();
 					Integer sec1 = controller.trainOrientations().get(1).currentSection();
 				    boolean stop = false;
 					if(outputLoco0.get(outputLoco0.size()-1) != sec0){
 						if(outputLoco0.contains(sec0)){
 							stop = true;
 							th.interrupt();
 						}
 						else{
 							outputLoco0.add(sec0);
 							outputArrayLoco0.add(""+sec0);
 						}
 					}
 					
 					if(outputLoco1.get(outputLoco1.size()-1) != sec1){
 						if(outputLoco1.contains(sec1)){
 							stop = true;
 							th.interrupt();
 						}
 						else{
 							outputLoco1.add(sec1);
 							outputArrayLoco1.add(""+sec1);
 						}
 					}
 				}
 					
 				
 				if(e instanceof Event.EmergencyStop){
 					Integer loco = ((Event.EmergencyStop) e).getLocomotive();
 					if(loco == 1){
 						outputArrayLoco1.add("Stop");
 					}
 					else{
 						outputArrayLoco0.add("Stop");
 						
 					}
 				}
 				
 			
 				if(loco0.getCurrentSpeed() ==0 && loco1.getCurrentSpeed() == 0){
 					System.out.println("both have stopped");
 					th.interrupt();
 					
 				}
 			}

		};

		controller.register(lst);
		controller.start(1, route1);
 		controller.start(0, route0);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest0");
			//System.out.println("route: "+route.toString());
			System.out.println("output 0: "+outputArrayLoco0.toString());
			System.out.println("output 1: "+outputArrayLoco1.toString());
		}
		//assert(outputArray.get(0) == 2);
		//assert(outputArray.get(1) == 3);
		//assert(outputArray.get(2) == 4);
		//assert(outputArray.get(3) == 5);
		//assert(outputArray.get(4) == 6);
		//assert(outputArray.get(5) == 7);
		//assert(outputArray.get(6) == 8);
		//assert(outputArray.get(7) == 1);

		//((TrainController) controller).deregister(lst);

	}
	
	@Test public void softwareTest1(){
		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();

		Map<Integer,Section> numberMap = ring.getSectionNumberMap();

		Section startSec = numberMap.get(8);
		Track outerPiece = startSec.get(0);
		Integer headID = ((Switch)outerPiece).getSwitchID();

		Track innerStart = numberMap.get(16).get(0);

		Integer innerID = ((Switch) innerStart).getSwitchID();



		final Route route0 = new Route(true, 8,1,2,3,4,5,6,7);

		final Movable loco0= new Locomotive(new Track[]{outerPiece}, 40,40,10, false);

		Train train0 = new Train(new Movable[]{loco0});

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();

		trainMap.put(0,train0 );
		train0.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(8, true));



		final Route route1 = new Route(true, 16,9,10,11,12,13,14,15);

		final Movable loco1 = new Locomotive(new Track[]{innerStart},40,40,10,true);
		
		Train train1 = new Train(new Movable[]{loco1});
		trainMap.put(1, train1);
		train1.setID(1);
		orientationMap.put(1,  new modelrailway.core.Train(16, true));

		Track headPiece = numberMap.get(1).get(0);
		final Simulator sim = new Simulator(headPiece, orientationMap, trainMap);

		final ControlerCollision controller = new ControlerCollision(orientationMap,ring.getSectionNumberMap(),headPiece,sim);

		controller.sections().get(8).getEntryRequests().add(0);
		controller.sections().get(1).getEntryRequests().add(0);
		
		controller.sections().get(16).getEntryRequests().add(1);
		controller.sections().get(9).getEntryRequests().add(1);
		
		sim.register(controller);

		controller.set(headID, startSec.retrieveSwitchingOrder(new Pair<Integer,Integer>(7,1)).get(0));
		controller.set(innerID, numberMap.get(16).retrieveSwitchingOrder(new Pair<Integer,Integer>(15,9)).get(0));
		
		
		final ArrayList<Integer> outputLoco0 = new ArrayList<Integer>();
		final ArrayList<Integer> outputLoco1 = new ArrayList<Integer>();
		outputLoco0.add(8);
		outputLoco1.add(16);
		
		final ArrayList<String> outputArrayLoco0 = new ArrayList<String>();
		final ArrayList<String> outputArrayLoco1 = new ArrayList<String>();
		final Thread th = Thread.currentThread();
		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());
 				
 				if(e instanceof Event.SectionChanged){
 					Integer sec0 = controller.trainOrientations().get(0).currentSection();
 					Integer sec1 = controller.trainOrientations().get(1).currentSection();
 				    boolean stop = false;
 					if(outputLoco0.get(outputLoco0.size()-1) != sec0){
 						if(outputLoco0.contains(sec0)){
 							stop = true;
 							th.interrupt();
 						}
 						else{
 							outputLoco0.add(sec0);
 							outputArrayLoco0.add(""+sec0);
 						}
 					}
 					
 					if(outputLoco1.get(outputLoco1.size()-1) != sec1){
 						if(outputLoco1.contains(sec1)){
 							stop = true;
 							th.interrupt();
 						}
 						else{
 							outputLoco1.add(sec1);
 							outputArrayLoco1.add(""+sec1);
 						}
 					}
 				}
 					
 				
 				if(e instanceof Event.EmergencyStop){
 					Integer loco = ((Event.EmergencyStop) e).getLocomotive();
 					if(loco == 1){
 						outputArrayLoco1.add("Stop");
 					}
 					else{
 						outputArrayLoco0.add("Stop");
 						
 					}
 				}
 				
 			
 				if(loco0.getCurrentSpeed() ==0 && loco1.getCurrentSpeed() == 0){
 					System.out.println("both have stopped");
 					th.interrupt();
 					
 				}
 			}

		};

		controller.register(lst);
		controller.start(1, route1);
 		controller.start(0, route0);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest0");
			//System.out.println("route: "+route.toString());
			System.out.println("output 0: "+outputArrayLoco0.toString());
			System.out.println("output 1: "+outputArrayLoco1.toString());
		}
		//assert(outputArray.get(0) == 2);
		//assert(outputArray.get(1) == 3);
		//assert(outputArray.get(2) == 4);
		//assert(outputArray.get(3) == 5);
		//assert(outputArray.get(4) == 6);
		//assert(outputArray.get(5) == 7);
		//assert(outputArray.get(6) == 8);
		//assert(outputArray.get(7) == 1);

		//((TrainController) controller).deregister(lst);

	}

}