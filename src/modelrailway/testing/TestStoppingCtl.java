package modelrailway.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
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
import modelrailway.util.SimulationTrack;

import org.junit.Test;

public class TestStoppingCtl {


	@Test public void test0(){
		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();

		Map<Integer,Section> numberMap = ring.getSectionNumberMap();

		Section startSec = numberMap.get(1);
		Track headPiece = startSec.get(0);

		final Route route = new Route(false,1,2,3,4,5,6,7,8,9,10,11,12);
		route.setStopSection(9);

		final Movable locomotive = new Locomotive(new Track[]{headPiece}, 40,40,10, false);

		Train train = new Train(new Movable[]{locomotive});

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();

		trainMap.put(0,train );
		train.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(1, true)); // put model train in section 1

		final Simulator sim = new Simulator(headPiece, orientationMap, trainMap);

		final ControlerCollision ctl = new ControlerCollision(orientationMap,ring.getSectionNumberMap(),headPiece,sim);
		sim.register(ctl);

		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
 		final Thread th = Thread.currentThread();

		ctl.register(new Listener(){
 			public void notify(Event e){
 				System.out.println("event "+e.toString());
 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){

 					  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 					  outputArray.add(i);
 					  System.out.println("ADDInG: "+i);

 				}
 				else if (e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 					outputArray.add(route.nextSection(i));
 					System.out.println("ADDING : "+route.nextSection(i));
 				}
 				if(outputArray.size() > 0 && locomotive.getCurrentSpeed() == 0){
 					// then the train has been moving and has been stopped.
 					sim.stop();
 					th.interrupt();
 				}
 			}});

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
		assertTrue(ring.getSectionNumberMap().get(9).getEntryRequests().size() == 1);
		assertFalse(ring.getSectionNumberMap().get(9).containsMovable(1));

		//assert that the entry requests have been cleared from section 2

		assertTrue(ring.getSectionNumberMap().get(2).getEntryRequests().size() == 0); // ensure that section 2 has not been reserved.
		assertFalse(ring.getSectionNumberMap().get(2).containsMovable(0));

		// check entry requests for section eleven.

		assertTrue(ring.getSectionNumberMap().get(11).getEntryRequests().size() == 0); // ensure that section 11 has not been reserved
		assertFalse(ring.getSectionNumberMap().get(11).containsMovable(0)); // ensure that section 11 has not been reached.

	}
	@Test public void test1(){
		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();

		Map<Integer,Section> numberMap = ring.getSectionNumberMap();

		Section startSec = numberMap.get(1);
		Track headPiece = startSec.get(0);
		
		Track eightPiece = numberMap.get(8).get(0);

		final Route route = new Route(false,8,7,6,5,4,3,2,1);
		route.setStopSection(1);

		final Movable locomotive = new Locomotive(new Track[]{eightPiece}, 40,40,10, false);

		Train train = new Train(new Movable[]{locomotive});

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();

		trainMap.put(0,train );
		train.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(8, false));

		final Simulator sim = new Simulator(headPiece, orientationMap, trainMap);

		final ControlerCollision ctl = new ControlerCollision(orientationMap,ring.getSectionNumberMap(),headPiece,sim);
		sim.register(ctl);
		train.toggleDirection();
		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
 		final Thread th = Thread.currentThread();

		ctl.register(new Listener(){
 			public void notify(Event e){
 				System.out.println("event "+e.toString());
 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){

 					  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 					  outputArray.add(i);
 					  System.out.println("ADDInG: "+i);

 				}
 				else if (e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 					outputArray.add(route.nextSection(i));
 					System.out.println("ADDING : "+route.nextSection(i));
 				}
 				if(outputArray.size() > 0 && locomotive.getCurrentSpeed() == 0){
 					// then the train has been moving and has been stopped.
 					sim.stop();
 					th.interrupt();
 				}
 			}});

 		ctl.start(0, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("testTrackRun0");
			System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}
		assertTrue(outputArray.get(0) == 7);
		assertTrue(outputArray.get(1) == 6);
		assertTrue(outputArray.get(2) == 5);
		assertTrue(outputArray.get(3) == 4);
		assertTrue(outputArray.get(4) == 3);
		assertTrue(outputArray.get(5) == 2);
		assertTrue(outputArray.get(6) == 1);


	}
}
