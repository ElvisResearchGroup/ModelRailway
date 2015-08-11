package modelrailway.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelrailway.core.Event;
import modelrailway.core.Route;
import modelrailway.core.Section;
import modelrailway.core.Event.Listener;
import modelrailway.core.Event.SectionChanged;
import modelrailway.simulation.Locomotive;
import modelrailway.simulation.Movable;
import modelrailway.simulation.Simulator;
import modelrailway.simulation.Track;
import modelrailway.simulation.Train;
import modelrailway.simulation.Straight.StraightDblRing;
import modelrailway.util.ControlerCollision;
import modelrailway.util.Pair;
import modelrailway.util.SimulationTrack;

import org.junit.*;

public class SimTestRun {
	public Listener addSingleTrainListener( final ControlerCollision ctl,
            final List<Integer> outputArray,final  int trainZero, final int stopSec  ,final Route route){
		final Thread th = Thread.currentThread();
	    Listener lst = new Listener(){
	    	
 			public void notify(Event e){
 				System.out.println("event "+e.toString());
 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){

 					  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 					  outputArray.add(i);

 					  if(i == stopSec){

 						  ctl.stop(trainZero);
 						  th.interrupt();

 					  }
 					//  throw new RuntimeException("Experienced Notify Stop Statement");
 					}
 					else if (e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 						Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 						outputArray.add(route.nextSection(i));
 						if(route.nextSection(i) == stopSec){
 	 						  ctl.stop(trainZero);
 	 						  th.interrupt();
 	 					}

 					}

 					else if (e instanceof Event.EmergencyStop){
 						ctl.stop(trainZero);
 						th.interrupt();
 					}

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
			final Movable loco= new Locomotive(new Track[]{ring.getSectionNumberMap().get(routes[x].firstSection()).get(0)}, 40,40,10, false);
			final Train train = new Train(new Movable[]{loco});
			trainMap.put(trainIDs[x], train);
			train.setID(trainIDs[x]);
			orientationMap.put(trainIDs[x], new modelrailway.core.Train(routes[x].firstSection(), true));
			System.out.println("trains: "+trainIDs[x] + ", section: "+routes[x].firstSection().intValue());
		}
	    return new Pair<>(trainMap,orientationMap);
	}
	

	
	@Test public void testTrackRun0(){
		System.out.println("TestTrackRun0");
		System.out.println("--------------");
		SimulationTrack sim0 = new SimulationTrack();
		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();
		final Route route = new Route(true, 1,2,3,4,5,6,7,8);
		Pair<Map<Integer,modelrailway.simulation.Train>, Map<Integer,modelrailway.core.Train>> pair = this.makeDefaultTrains(new int[]{0}, new Route[]{route}, ring);
		final Simulator sim = new Simulator(ring.getHead(), pair.snd, pair.fst);
		final ControlerCollision ctl = new ControlerCollision(pair.snd,ring.getSectionNumberMap(),ring.getHead(),sim);
		sim.register(ctl);
		final List<Integer> outputArray = Collections.synchronizedList(new ArrayList<Integer>());
 		Listener lst = this.addSingleTrainListener(ctl,  outputArray, 0,1, route);
		ctl.register(lst);
		assertTrue(pair.fst.get(0).getFront().getSection().getNumber() == ring.getHead().getSection().getNumber());
 		ctl.start(0, route);
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
	@Test public void testTrackRun1(){
		System.out.println("TestTrackRun1");
		System.out.println("--------------");
		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();

		final Route route = new Route(true, 17,18,11,12,13,14,15,16,9,10,11);

		Pair<Map<Integer, Train>, Map<Integer, modelrailway.core.Train>> pair = this.makeDefaultTrains(new int[]{0}, new Route[]{route}, ring);
		final Simulator sim = new Simulator(sim0.getHead(), pair.snd, pair.fst);

		final ControlerCollision ctl = new ControlerCollision(pair.snd,ring.getSectionNumberMap(),sim0.getHead(),sim);
		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		this.addSingleTrainListener(ctl, outputArray, 0, 9, route);
		sim.register(ctl);
 		ctl.start(0, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("testTrackRun0");
			System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}
	}

	@Test public void testTrackRun2(){
		System.out.println("TestTrackRun2");
		System.out.println("--------------");
		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();

		final Route route = new Route(true,1,2,3,4,5,6,7,8,9,10,11,12);

		Pair<Map<Integer, Train>, Map<Integer, modelrailway.core.Train>> pair = this.makeDefaultTrains(new int[]{0}, new Route[]{route}, ring);
		final Simulator sim = new Simulator(sim0.getHead(), pair.snd, pair.fst);

		final ControlerCollision ctl = new ControlerCollision(pair.snd,ring.getSectionNumberMap(),sim0.getHead(),sim);
		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		this.addSingleTrainListener(ctl, outputArray, 0, 9, route);
		sim.register(ctl);
 		ctl.start(0, route);

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
		assertTrue(outputArray.get(7) == 9);
		assertTrue(outputArray.get(8) == 10);

		// check that section 1 is not reserved.

		assertFalse(ring.getSectionNumberMap().get(1).containsMovable(0));
		assertTrue(ring.getSectionNumberMap().get(1).getEntryRequests().size() == 0); // assert that the entry requests have been cleared. from section1

		//assert that the entry requests have been cleared from section 8
		assertTrue(ring.getSectionNumberMap().get(8).getEntryRequests().size() == 0);
		assertFalse(ring.getSectionNumberMap().get(8).containsMovable(0));

		//assert that the entry requests have been cleared from section 9
		assertTrue(ring.getSectionNumberMap().get(9).getEntryRequests().size() == 0);
		//assertFalse(ring.getSectionNumberMap().get(9).containsMovable(0)); // the train may or may not be still in section 9

		//assert that the entry requests have been cleared from section 2

		assertTrue(ring.getSectionNumberMap().get(2).getEntryRequests().size() == 0); // ensure that section 2 has not been reserved.
		assertFalse(ring.getSectionNumberMap().get(2).containsMovable(0));

		// check entry requests for section eleven.

		assertTrue(ring.getSectionNumberMap().get(11).getEntryRequests().size() == 1); // ensure that section 11 has been reserved
		assertFalse(ring.getSectionNumberMap().get(11).containsMovable(0)); // ensure that section 11 has not been reached.


	}
	/**
	 *  run the train around to section nine. Check whether section 1 is still locked on entry to nine.
	 */
	@Test public void testTrackRun3(){
		System.out.println("TestTrackRun3");
		System.out.println("--------------");
		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();

		Map<Integer,Section> numberMap = ring.getSectionNumberMap();

		Section startSec = numberMap.get(1);
		Track headPiece = startSec.get(0);

		final Route route = new Route(true,1,2,3,4,5,6,7,8,9,10,11,12);

		Pair<Map<Integer, Train>, Map<Integer, modelrailway.core.Train>> pair = this.makeDefaultTrains(new int[]{0}, new Route[]{route}, ring);

		final Simulator sim = new Simulator(headPiece, pair.snd,pair.fst);

		final ControlerCollision ctl = new ControlerCollision(pair.snd,ring.getSectionNumberMap(),headPiece,sim);
		sim.register(ctl);

		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
 		Listener lst = this.addSingleTrainListener(ctl, outputArray, 0, 9, route);
		ctl.register(lst);

 		ctl.start(0, route);

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
		assertTrue(outputArray.get(7) == 9);

		// check that section 1 is not reserved.

		assertFalse(ring.getSectionNumberMap().get(1).containsMovable(0));
		assertTrue(ring.getSectionNumberMap().get(1).getEntryRequests().size() == 0); // assert that the entry requests have been cleared. from section1

		//assert that the entry requests have been cleared from section 8
		assertTrue(ring.getSectionNumberMap().get(8).getEntryRequests().size() == 0);
		//assertFalse(ring.getSectionNumberMap().get(8).containsMovable(0));// the train may or may not be still in section 8

		//assert that the entry requests have been cleared from section 9
	//	assertTrue(ring.getSectionNumberMap().get(9).getEntryRequests().size() == 0);
		assertFalse(ring.getSectionNumberMap().get(9).containsMovable(1));

		//assert that the entry requests have been cleared from section 2

		assertTrue(ring.getSectionNumberMap().get(2).getEntryRequests().size() == 0); // ensure that section 2 has not been reserved.
		assertFalse(ring.getSectionNumberMap().get(2).containsMovable(0));

		// check entry requests for section eleven.

		assertTrue(ring.getSectionNumberMap().get(11).getEntryRequests().size() == 0); // ensure that section 11 has not been reserved
		assertFalse(ring.getSectionNumberMap().get(11).containsMovable(0)); // ensure that section 11 has not been reached.

	}

}
