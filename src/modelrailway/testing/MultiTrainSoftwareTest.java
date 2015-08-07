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

	public Listener addMultiTrainListener(final Map<Integer, modelrailway.simulation.Train> trainMap, final TrainController controller,final StraightDblRing ring , final ArrayList<String> outputArrayLoco0,
			                              final ArrayList<String> outputArrayLoco1,final  int trainZero ,final int trainOne, final Route rt0, final Route rt1){
		final ArrayList<Integer> outputLoco0 = new ArrayList<Integer>();
		final ArrayList<Integer> outputLoco1 = new ArrayList<Integer>();
		outputLoco0.add(rt0.firstSection());
		outputLoco1.add(rt1.firstSection());

		final Thread th = Thread.currentThread();
		final Listener lst = new Listener(){
			public void notify(Event e){


 				if(e instanceof Event.SectionChanged){
 					Integer sec0 = controller.trainOrientations().get(trainZero).currentSection();
 					Integer sec1 = controller.trainOrientations().get(trainOne).currentSection();
 				    boolean stop = false;
 					if(outputLoco0.get(outputLoco0.size()-1) != sec0){
 						if(outputLoco0.contains(sec0)){
 							stop = true;

 							th.interrupt();
 						}
 						else{
 						//	System.out.println("Locomotive 0 is the moving train");
 							//System.out.println("sec0: "+sec0);
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
 							//System.out.println("Locomotive 1 is the moving train");
 							//System.out.println("sec1: "+sec1);
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


 				if(trainMap.get(trainZero).getCurrentSpeed() ==0 && trainMap.get(trainOne).getCurrentSpeed() == 0){
 					//System.out.println("both have stopped");
 					th.interrupt();

 				}
 				//System.out.println("event in unit test: "+e.);
				//System.out.println();
				///System.out.println();
				///System.out.println();
 				System.out.println(" 1: "+ring.getSectionNumberMap().get(1).getEntryRequests().toString());
 				System.out.println(" 2: "+ring.getSectionNumberMap().get(2).getEntryRequests().toString());
 				System.out.println(" 3: "+ring.getSectionNumberMap().get(3).getEntryRequests().toString());
 				System.out.println(" 4: "+ring.getSectionNumberMap().get(4).getEntryRequests().toString());
 				System.out.println(" 5: "+ring.getSectionNumberMap().get(5).getEntryRequests().toString());
 				System.out.println(" 6: "+ring.getSectionNumberMap().get(6).getEntryRequests().toString());
 				System.out.println(" 7: "+ring.getSectionNumberMap().get(7).getEntryRequests().toString());
 				System.out.println(" 8: "+ring.getSectionNumberMap().get(8).getEntryRequests().toString());
 				System.out.println(" 9: "+ring.getSectionNumberMap().get(9).getEntryRequests().toString());
 				System.out.println(" 10: "+ring.getSectionNumberMap().get(10).getEntryRequests().toString());
 				System.out.println(" 11: "+ring.getSectionNumberMap().get(11).getEntryRequests().toString());
 				System.out.println(" 12: "+ring.getSectionNumberMap().get(12).getEntryRequests().toString());
 				System.out.println(" 13: "+ring.getSectionNumberMap().get(13).getEntryRequests().toString());
 				System.out.println(" 14: "+ring.getSectionNumberMap().get(14).getEntryRequests().toString());
 				System.out.println(" 15: "+ring.getSectionNumberMap().get(15).getEntryRequests().toString());
 				System.out.println(" 16: "+ring.getSectionNumberMap().get(16).getEntryRequests().toString());
 			}



		};

		return lst;
	}
	public void setRoute(int id, Route rt, StraightDblRing ring, ControlerCollision ctl){
		//Integer fst = rt.firstSection();
		//Integer snd = rt.nextSection(rt.firstSection());
		ctl.trainOrientations().get(id).setSection(rt.firstSection());
		ring.getSectionNumberMap().get(rt.firstSection()).getEntryRequests().add(id);
		ring.getSectionNumberMap().get(rt.nextSection(rt.firstSection())).getEntryRequests().add(id);
	}

	public Pair<Map<Integer,modelrailway.simulation.Train>, Map<Integer,modelrailway.core.Train>> makeDefaultTrains(final int[] trainIDs,final  Route[] routes, final StraightDblRing ring){
		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();
		for(int x = 0; x< trainIDs.length; x++){
			final Movable loco0= new Locomotive(new Track[]{ring.getSectionNumberMap().get(routes[x].firstSection()).get(0)}, 40,40,10, false);
			final Train train0 = new Train(new Movable[]{loco0});
			trainMap.put(trainIDs[x], train0);
			train0.setID(trainIDs[x]);
			orientationMap.put(trainIDs[x], new modelrailway.core.Train(routes[x].firstSection(), true));
		}
	    return new Pair<>(trainMap,orientationMap);
	}

	@Test public void softwareTest0(){
		SimulationTrack sim0 = new SimulationTrack();
		final StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();
		Map<Integer,Section> numberMap = ring.getSectionNumberMap();


		final Route route0 = new Route(true, 8,1,2,3,4,5,6,7);
		final Route route1 = new Route(false, 16,9,10,3,4,5,6,7,8);
		Pair<Map<Integer,modelrailway.simulation.Train>, Map<Integer,modelrailway.core.Train>>
		trainPair = this.makeDefaultTrains(new int[]{0,1},new Route[]{route0,route1}, ring);
		Map<Integer,modelrailway.simulation.Train> trainMap = trainPair.fst;
		Map<Integer,modelrailway.core.Train> orientationMap = trainPair.snd;
		Track headPiece = numberMap.get(1).get(0);
		final Simulator sim = new Simulator(headPiece, orientationMap, trainMap);
		final ControlerCollision controller = new ControlerCollision(orientationMap,ring.getSectionNumberMap(),headPiece,sim);
		setRoute(0, route0, ring, (ControlerCollision )controller);
		setRoute(1, route0, ring, (ControlerCollision )controller);
		final ArrayList<String> outputArrayLoco0 = new ArrayList<String>();
		final ArrayList<String> outputArrayLoco1 = new ArrayList<String>();
		Listener lst = addMultiTrainListener(trainMap, (TrainController) controller, ring, outputArrayLoco0, outputArrayLoco1, 0, 1, route0, route1);

		controller.set(8, numberMap.get(8).retrieveSwitchingOrder(new Pair<Integer,Integer>(7,1)).get(0));
		controller.set(16, numberMap.get(16).retrieveSwitchingOrder(new Pair<Integer,Integer>(15,9)).get(0));
		sim.register(controller);
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
	}

	@Test public void softwareTest1(){
		SimulationTrack sim0 = new SimulationTrack();
		final StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();
		Map<Integer,Section> numberMap = ring.getSectionNumberMap();
		final Route route0 = new Route(true, 8,1,2,3,4,5,6,7);
		final Route route1 = new Route(true, 16,9,10,11,12,13,14,15);
		Pair<Map<Integer,modelrailway.simulation.Train>, Map<Integer,modelrailway.core.Train>>
		trainPair = this.makeDefaultTrains(new int[]{0,1},new Route[]{route0,route1}, ring);
		Map<Integer,modelrailway.simulation.Train> trainMap = trainPair.fst;
		Map<Integer,modelrailway.core.Train> orientationMap = trainPair.snd;
		Track headPiece = numberMap.get(1).get(0);
		final Simulator sim = new Simulator(headPiece, orientationMap, trainMap);
		final ControlerCollision controller = new ControlerCollision(orientationMap,ring.getSectionNumberMap(),headPiece,sim);
		setRoute(0, route0, ring, (ControlerCollision )controller);
		setRoute(1, route0, ring, (ControlerCollision )controller);
		final ArrayList<String> outputArrayLoco0 = new ArrayList<String>();
		final ArrayList<String> outputArrayLoco1 = new ArrayList<String>();
		Listener lst = addMultiTrainListener(trainMap, (TrainController) controller, ring, outputArrayLoco0, outputArrayLoco1, 0, 1, route0, route1);

		controller.set(8, numberMap.get(8).retrieveSwitchingOrder(new Pair<Integer,Integer>(7,1)).get(0));
		controller.set(16, numberMap.get(16).retrieveSwitchingOrder(new Pair<Integer,Integer>(15,9)).get(0));

		sim.register(controller);
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


	}


	@Test public void softwareTest2(){
		SimulationTrack sim0 = new SimulationTrack();
		final StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();
		Map<Integer,Section> numberMap = ring.getSectionNumberMap();
		final Route route0 = new Route(true, 8,1,2,3,4,5,6,7);
		final Route route1 = new Route(true, 16,9,10,11,12,13,14,15);
		Pair<Map<Integer,modelrailway.simulation.Train>, Map<Integer,modelrailway.core.Train>>
		trainPair = this.makeDefaultTrains(new int[]{0,1},new Route[]{route0,route1}, ring);
		Map<Integer,modelrailway.simulation.Train> trainMap = trainPair.fst;
		Map<Integer,modelrailway.core.Train> orientationMap = trainPair.snd;
		Track headPiece = numberMap.get(1).get(0);
		final Simulator sim = new Simulator(headPiece, orientationMap, trainMap);
		final ControlerCollision controller = new ControlerCollision(orientationMap,ring.getSectionNumberMap(),headPiece,sim);
		setRoute(0, route0, ring, (ControlerCollision )controller);
		setRoute(1, route0, ring, (ControlerCollision )controller);
		final ArrayList<String> outputArrayLoco0 = new ArrayList<String>();
		final ArrayList<String> outputArrayLoco1 = new ArrayList<String>();
		Listener lst = addMultiTrainListener(trainMap, (TrainController) controller, ring, outputArrayLoco0, outputArrayLoco1, 0, 1, route0, route1);

		controller.set(8, numberMap.get(8).retrieveSwitchingOrder(new Pair<Integer,Integer>(7,1)).get(0));
		controller.set(16, numberMap.get(16).retrieveSwitchingOrder(new Pair<Integer,Integer>(15,9)).get(0));

		sim.register(controller);
		controller.register(lst);
		controller.zerostart(1, route1);
 		controller.zerostart(0, route0);

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