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
		
		final ArrayList<Integer> loco0Movement = new ArrayList<Integer>();
		final ArrayList<Integer> loco1Movement = new ArrayList<Integer>();
		loco0Movement.add(8);
		loco1Movement.add(16);
		final ArrayList<String> outputArrayLoco0 = new ArrayList<String>();
		final ArrayList<String> outputArrayLoco1 = new ArrayList<String>();
		final Thread th = Thread.currentThread();


		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());
 				if(e instanceof Event.SectionChanged){
					boolean stop = false;
					int loco0Sec = controller.trainOrientations().get(0).currentSection();
					int loco1Sec = controller.trainOrientations().get(1).currentSection();
					if(loco0Sec != loco0Movement.get(loco0Movement.size()-1)){
						if(loco0Movement.contains(loco0Sec)){
							stop = true;
						}else{
						    loco0Movement.add(loco0Sec);
						    outputArrayLoco0.add(""+loco0Movement.get(loco0Movement.size()-1));
						}
						
					}
					if(loco1Sec != loco1Movement.get(loco1Movement.size()-1)){
						if(loco1Movement.contains(loco1Sec)) {
							stop = true;
							
						}else{
						    loco1Movement.add(loco1Sec);
						    outputArrayLoco1.add(""+loco1Movement.get(loco1Movement.size()-1));
						}
						
					}
					if(stop){
						System.out.flush();
						th.interrupt();
						
					}
				}
 				
 				if(e instanceof Event.EmergencyStop){
 					Integer loco = ((Event.EmergencyStop) e).getLocomotive(); // get the locomotive
 					System.out.println("Emergency stop detected in unit test.");
 					if(loco == 1){
 						outputArrayLoco0.add("Stop");
 					}
 					else{
 						outputArrayLoco1.add("Stop");
 					}
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
			System.out.println("output 0: "+outputArrayLoco0.toString());
			System.out.println("output 1: "+outputArrayLoco1.toString());
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
	/**
	 * Two trains go around the track. one arround an outer loop, the other around and inner loop, they do not collide. 
	 */
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

		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		final Route route0 = new Route(true, 8,1,2,3,4,5,6,7);
		final Movable loco0 = new Locomotive(new Track[]{outerPiece}, 40,40,10, false);
		Train train0 = new Train(new Movable[]{loco0});
		final Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		trainMap.put(0,train0 );
		train0.setID(0);
		final Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();
		orientationMap.put(0, new modelrailway.core.Train(8, true));



		final Route route1 = new Route(true, 16,9,10,11,12,13,14,15);
		final Movable loco1 = new Locomotive(new Track[]{innerStart},40,40,10,true);
		Train train1 = new Train(new Movable[]{loco1});
		trainMap.put(1, train1);
		train1.setID(1);
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
		final ArrayList<Integer> loco0Movement = new ArrayList<Integer>();
		final ArrayList<Integer> loco1Movement = new ArrayList<Integer>();
		final ArrayList<String> outputArrayLoco0 = new ArrayList<String>();
		final ArrayList<String> outputArrayLoco1 = new ArrayList<String>();
		final Thread th = Thread.currentThread();
		loco0Movement.add(orientationMap.get(0).currentSection());
		loco1Movement.add(orientationMap.get(1).currentSection());
		final Listener lst = new Listener(){
			public void notify(Event e){
				if(e instanceof Event.SectionChanged){
					boolean stop = false;
					int loco0Sec = controller.trainOrientations().get(0).currentSection();
					int loco1Sec = controller.trainOrientations().get(1).currentSection();
					if(loco0Sec != loco0Movement.get(loco0Movement.size()-1)){
						if(loco0Movement.contains(loco0Sec)){
							stop = true;
						}else{
						    loco0Movement.add(loco0Sec);
						    outputArrayLoco0.add(""+loco0Movement.get(loco0Movement.size()-1));
						}
						
					}
					if(loco1Sec != loco1Movement.get(loco1Movement.size()-1)){
						if(loco1Movement.contains(loco1Sec)) {
							stop = true;
							
						}else{
						    loco1Movement.add(loco1Sec);
						    outputArrayLoco1.add(""+loco1Movement.get(loco1Movement.size()-1));
						}
						
					}
					if(stop){
						System.out.flush();
						th.interrupt();
						
					}
				}
				if(e instanceof Event.EmergencyStop){
					if(((Event.EmergencyStop) e).getLocomotive() == 0){
						outputArrayLoco0.add("STOP");
					}else{
						outputArrayLoco1.add("STOP");
					}
				}
				
			}
		};
		
		controller.register(lst);
		controller.start(0,route0);
		controller.start(1,route1);
		
		try{
			Thread.currentThread().join();
		} catch(InterruptedException e){
			System.out.println("output 0: "+outputArrayLoco0.toString());
			System.out.println("output 1: "+outputArrayLoco1.toString());
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
			System.out.println("11:"+ring.getSectionNumberMap().get(11).getEntryRequests().toString());
			System.out.println("12:"+ring.getSectionNumberMap().get(12).getEntryRequests().toString());
			System.out.println("13:"+ring.getSectionNumberMap().get(13).getEntryRequests().toString());
			System.out.println("14:"+ring.getSectionNumberMap().get(14).getEntryRequests().toString());
			System.out.println("15:"+ring.getSectionNumberMap().get(15).getEntryRequests().toString());
		    System.out.println("16:"+ring.getSectionNumberMap().get(16).getEntryRequests().toString());

			System.out.flush();
		}
	}

}
