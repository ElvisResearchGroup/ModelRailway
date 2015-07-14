package modelrailway.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
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
import modelrailway.simulation.Straight.StraightDblRing;
import modelrailway.simulation.Track.RingRoute;
import modelrailway.simulation.Track;
import modelrailway.simulation.Train;
import modelrailway.util.ControlerCollision;
import modelrailway.util.MovementController;
import modelrailway.util.Pair;
import modelrailway.util.SimulationTrack;

import org.junit.Test;


public class SimulationTrackTest {
	/**
	 * Test the switch connections at section 8
	 */
	@Test public void testTrackBuild0(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		ring.recalculateSections();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		//first check that all the section numbers are in the numberMap.

		Section sectionEight = numberMap.get(8);
		Section sectionSeven = numberMap.get(7);
		Section sectionOne = numberMap.get(1);
		Section sectionNine = numberMap.get(9);

		//System.out.println("Section counts:");
		//for(Map.Entry<Section, Integer> entry: ring.getSectionList().entrySet()){
		//	System.out.println(entry+" section: "+entry.getKey().getNumber());
		//}
		//System.out.println("Section Numbering:");
		//for(Map.Entry<Integer,Section> entry : ring.getSectionNumberMap().entrySet()){
		//	System.out.println(entry);
		//}


		assertTrue(sectionEight != null);
		assertTrue(sectionSeven != null);
		assertTrue(sectionOne != null);
		assertTrue(sectionNine != null);

		assertTrue (sectionEight.getNumber() == 8);
		assertTrue(sectionSeven.getNumber() == 7);
		assertTrue(sectionOne.getNumber() == 1);
		assertTrue(sectionNine.getNumber() == 9);

		// check t make sure that all the sections only have exactly one piece in them

		assertTrue(sectionEight.size() == 1);
		assertTrue(sectionSeven.size() == 1);
		assertTrue(sectionOne.size() == 1);
		assertTrue(sectionNine.size() == 1);

		//now check the connections for section seven to section eight.

		//System.out.println(sectionSeven.get(0).getNext(false).getSection().getNumber());
		//System.out.println(sectionSeven.get(0).getNext(false) +"   "+sectionEight.get(0));

		assertTrue(sectionSeven.get(0).getNext(false)== sectionEight.get(0));
		assertTrue(sectionEight.get(0).getPrevious(false) == sectionSeven.get(0));

		//now check the connections from section eight to section nine
		//System.out.println("number for sec 8: "+sectionEight.get(0).getSection().getNumber());
		//System.out.println("number for sec 9: "+sectionNine.get(0).getSection().getNumber());
		//System.out.println("next alt sec 8: "+sectionEight.get(0).getNext(false).getSection().getNumber());

		assertTrue(sectionEight.get(0).getNext(true) == sectionNine.get(0));
		assertTrue(sectionNine.get(0).getPrevious(true) == sectionEight.get(0));

		//now check the connections from section eight to section one

		assertTrue(sectionEight.get(0).getNext(false)== sectionOne.get(0));
		assertTrue(sectionOne.get(0).getPrevious(false) == sectionEight.get(0));

	}
	/**
	 * Test switch connections at section 16
	 */
	@Test public void testTrackBuild1(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		ring.recalculateSections();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		//first check that all the section numbers are in the numberMap.

		Section sectionSixteen = numberMap.get(16);
		Section sectionFifteen = numberMap.get(15);
		Section sectionSeventeen = numberMap.get(17);
		Section sectionNine = numberMap.get(9);

		//System.out.println("Section counts:");
		//for(Map.Entry<Section, Integer> entry: ring.getSectionList().entrySet()){
		//	System.out.println(entry+" section: "+entry.getKey().getNumber());
		//}
		//System.out.println("Section Numbering:");
		//for(Map.Entry<Integer,Section> entry : ring.getSectionNumberMap().entrySet()){
		//	System.out.println(entry);
		//}


		assertTrue(sectionSixteen != null);
		assertTrue(sectionFifteen != null);
		assertTrue(sectionSeventeen != null);
		assertTrue(sectionNine != null);

		assertTrue (sectionSixteen.getNumber() == 16);
		assertTrue(sectionFifteen.getNumber() == 15);
		assertTrue(sectionSeventeen.getNumber() == 17);
		assertTrue(sectionNine.getNumber() == 9);

		// check t make sure that all the sections only have exactly one piece in them

		assertTrue(sectionSixteen.size() == 1);
		assertTrue(sectionFifteen.size() == 1);
		assertTrue(sectionSeventeen.size() == 1);
		assertTrue(sectionNine.size() == 1);

		//now check the connections for section seven to section eight.

		//System.out.println(sectionSeven.get(0).getNext(false).getSection().getNumber());
		//System.out.println(sectionSeven.get(0).getNext(false) +"   "+sectionEight.get(0));

		assertTrue(sectionFifteen.get(0).getNext(false)== sectionSixteen.get(0));
		assertTrue(sectionSixteen.get(0).getPrevious(false) == sectionFifteen.get(0));

		//now check the connections from section eight to section nine
		//System.out.println("number for sec 8: "+sectionEight.get(0).getSection().getNumber());
		//System.out.println("number for sec 9: "+sectionNine.get(0).getSection().getNumber());
		//System.out.println("next alt sec 8: "+sectionEight.get(0).getNext(false).getSection().getNumber());

		//System.out.println(sectionSixteen.get(0).getNext(false).getSection().getNumber());
		//System.out.println(sectionSixteen.get(0).getNext(true).getSection().getNumber());

		assertTrue(sectionSixteen.get(0).getNext(false) == sectionSeventeen.get(0));
		assertTrue(sectionSeventeen.get(0).getPrevious(false) == sectionSixteen.get(0));

		//now check the connections from section eight to section one

		assertTrue(sectionSixteen.get(0).getNext(true)== sectionNine.get(0));
		assertTrue(sectionNine.get(0).getPrevious(false) == sectionSixteen.get(0));
	}
	/**
	 * Test switch connections at section 3
	 */
	@Test public void testTrackBuild2(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		ring.recalculateSections();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		Section sectionTen = numberMap.get(10);
		Section sectionTwo = numberMap.get(2);
		Section sectionFour = numberMap.get(4);
		Section sectionThree = numberMap.get(3);

		assertTrue(sectionFour.getNumber() == 4);
		assertTrue(sectionThree.getNumber() == 3);
		assertTrue(sectionTwo.getNumber() == 2);
		assertTrue(sectionTen.getNumber() == 10);

		assertTrue(sectionTen.size() == 1);
		assertTrue(sectionTwo.size() == 1);
		assertTrue(sectionFour.size() == 1);
		assertTrue(sectionThree.size() == 1);

		assertTrue(sectionTwo.get(0).getNext(false).equals(sectionThree.get(0)));
		assertTrue(sectionThree.get(0).getPrevious(false).equals(sectionTwo.get(0)));

		assertTrue(sectionTen.get(0).getNext(true).equals(sectionThree.get(0)));
		assertTrue(sectionThree.get(0).getPrevious(true).equals(sectionTen.get(0)));

		assertTrue(sectionThree.get(0).getNext(false).equals(sectionFour.get(0)));
		assertTrue(sectionFour.get(0).getPrevious(false).equals(sectionThree.get(0)));


	}
	/**
	 * Test switch connections at section 11
	 */
	@Test public void testTrackBuild3(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		ring.recalculateSections();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		Section sectionTen = numberMap.get(10);
		Section sectionEighteen = numberMap.get(18);
		Section sectionEleven = numberMap.get(11);
		Section sectionTwelve = numberMap.get(12);

		assertTrue(sectionTen.getNumber() == 10);
		assertTrue(sectionEighteen.getNumber() == 18);
		assertTrue(sectionEleven.getNumber() == 11);
		assertTrue(sectionTwelve.getNumber() == 12);


		assertTrue(sectionTen.size() == 1);
		assertTrue(sectionEighteen.size() == 1);
		assertTrue(sectionEleven.size() == 1);
		assertTrue(sectionTwelve.size() == 1);

		assertTrue(sectionEighteen.get(0).getNext(false).equals(sectionEleven.get(0)));
		assertTrue(sectionEleven.get(0).getPrevious(false).equals(sectionEighteen.get(0)));

		System.out.println("sectionTen.getNext(true): "+sectionTen.get(0).getNext(true).getSection().getNumber());
		System.out.println("sectionTen.getNext(false): "+sectionTen.get(0).getNext(false).getSection().getNumber());


		System.out.println("sectionEleven.get(0):"+sectionEleven.get(0).getSection().getNumber());
		assertTrue(sectionTen.get(0).getNext(false).equals(sectionEleven.get(0)));
		assertTrue(sectionEleven.get(0).getPrevious(true).equals(sectionTen.get(0)));

		assertTrue(sectionEleven.get(0).getNext(false).equals(sectionTwelve.get(0)));
		assertTrue(sectionTwelve.get(0).getPrevious(false).equals(sectionEleven.get(0)));

	}

	/**
	 * test Track 12
	 */
	@Test public void testTrackBuild4(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		ring.recalculateSections();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		Section sectionTwentyOne = numberMap.get(21);
		Section sectionThirteen = numberMap.get(13);
		Section sectionEleven = numberMap.get(11);
		Section sectionTwelve = numberMap.get(12);

		assertTrue(sectionTwentyOne.getNumber() == 21);
		assertTrue(sectionThirteen.getNumber() == 13);
		assertTrue(sectionEleven.getNumber() == 11);
		assertTrue(sectionTwelve.getNumber() == 12);



		assertTrue(sectionTwentyOne.size() == 1);
		assertTrue(sectionThirteen.size() == 1);
		assertTrue(sectionEleven.size() == 1);
		assertTrue(sectionTwelve.size() == 1);

		assertTrue(sectionEleven.get(0).getNext(false).equals(sectionTwelve.get(0)));
		assertTrue(sectionTwelve.get(0).getPrevious(false).equals(sectionEleven.get(0)));

		//System.out.println(sectionTwelve.get(0).getNext(true).getSection().getNumber());


		//System.out.println(sectionTwentyOne.get(0).getSection().getNumber());


		assertTrue(sectionTwelve.get(0).getNext(true).equals(sectionTwentyOne.get(0)));
		assertTrue(sectionTwentyOne.get(0).getPrevious(false).equals(sectionTwelve.get(0)));

		assertTrue(sectionTwelve.get(0).getNext(false).equals(sectionThirteen.get(0)));
		assertTrue(sectionThirteen.get(0).getPrevious(false).equals(sectionTwelve.get(0)));
	}

	/**
	 * test Track 4
	 */
	@Test public void testTrackBuild5(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		ring.recalculateSections();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		Section sectionNineteen = numberMap.get(19);
		Section sectionFive = numberMap.get(5);
		Section sectionThree = numberMap.get(3);
		Section sectionFour = numberMap.get(4);
		Section sectionTwenty = numberMap.get(20);

		if(sectionNineteen == null) fail("section nineteen expected to exist");

		assertTrue(sectionNineteen.getNumber() == 19);
		assertTrue(sectionFive.getNumber() == 5);
		assertTrue(sectionThree.getNumber() == 3);
		assertTrue(sectionFour.getNumber() == 4);



		assertTrue(sectionNineteen.size() == 1);
		assertTrue(sectionFive.size() == 1);
		assertTrue(sectionThree.size() == 1);
		assertTrue(sectionFour.size() == 1);

		assertTrue(sectionThree.get(0).getNext(false).equals(sectionFour.get(0)));
		assertTrue(sectionFour.get(0).getPrevious(false).equals(sectionThree.get(0)));

		//System.out.println(sectionTwelve.get(0).getNext(true).getSection().getNumber());


		//System.out.println(sectionTwentyOne.get(0).getSection().getNumber());


		assertTrue(sectionFour.get(0).getNext(true).equals(sectionNineteen.get(0)));



		assertTrue(sectionNineteen.get(0).getPrevious(true).equals(sectionFour.get(0)));

		assertTrue(sectionFour.get(0).getNext(false).equals(sectionFive.get(0)));
		assertTrue(sectionFive.get(0).getPrevious(false).equals(sectionFour.get(0)));
		assertTrue(sectionNineteen.get(0).getNext(true).equals(sectionTwenty.get(0)));


	}


	@Test public void testTrackSwitches0(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		ring.recalculateSections();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		Section sectionEight = numberMap.get(8);
		Section sectionSixteen = numberMap.get(16);
		Section sectionThree = numberMap.get(3);
		Section sectionTen = numberMap.get(10);
		Section sectionNine = numberMap.get(9);
		Section sectionEleven = numberMap.get(11);

		Section sectionTwelve = numberMap.get(12);
		Section sectionFour = numberMap.get(4);


		//test track switch 8

	 //   sectionEight.putSwitchingOrder(new Pair<Integer,Integer>(7,9),Arrays.asList(new Boolean[]{true}));
	 //   sectionEight.putSwitchingOrder(new Pair<Integer,Integer>(7,1),Arrays.asList(new Boolean[]{false}));
	 //   sectionEight.putSwitchingOrder(new Pair<Integer,Integer>(1,7),Arrays.asList(new Boolean[]{false}));
	 //   sectionEight.putSwitchingOrder(new Pair<Integer,Integer>(9,7),Arrays.asList(new Boolean[]{true}));

		assertTrue(sectionEight.get(0).getNext(false).getSection().getNumber() == 1);
	    assertTrue(sectionEight.get(0).getNext(true).getSection().getNumber()==9);
	    assertTrue(sectionEight.get(0).getPrevious(true).getSection().getNumber() == 7);
	    assertTrue(sectionEight.get(0).getPrevious(false).getSection().getNumber() == 7);

	    //test track switch 9;

	    assertTrue(sectionNine.get(0).getNext(false).getSection().getNumber()==10);
	    assertTrue(sectionNine.get(0).getNext(true).getSection().getNumber()==10);
	    assertTrue(sectionNine.get(0).getPrevious(true).getSection().getNumber() == 8);
	    assertTrue(sectionNine.get(0).getPrevious(false).getSection().getNumber() == 16);

	    //test track switch 10

	    assertTrue(sectionTen.get(0).getNext(true).getSection().getNumber() == 3);
	    assertTrue(sectionTen.get(0).getNext(false).getSection().getNumber() == 11);
	    assertTrue(sectionTen.get(0).getPrevious(true).getSection().getNumber() == 9);
	    assertTrue(sectionTen.get(0).getPrevious(false).getSection().getNumber() == 9);

	    // test track switch 11

	    assertTrue(sectionEleven.get(0).getNext(false).getSection().getNumber() == 12);
	    assertTrue(sectionEleven.get(0).getNext(true).getSection().getNumber() == 12);
	    assertTrue(sectionEleven.get(0).getPrevious(false).getSection().getNumber() == 18);
	    assertTrue(sectionEleven.get(0).getPrevious(true).getSection().getNumber() == 10);

	    // test track switch 12

	   // System.out.println(sectionTwelve.get(0).getNext(true).getSection().getNumber());
	   // System.out.println(sectionTwelve.get(0).getNext(false).getSection().getNumber() );
	    assertTrue(sectionTwelve.get(0).getNext(false).getSection().getNumber() == 13);
	    assertTrue(sectionTwelve.get(0).getNext(true).getSection().getNumber() == 21);
	    assertTrue(sectionTwelve.get(0).getPrevious(true).getSection().getNumber() == 11);
	    assertTrue(sectionTwelve.get(0).getPrevious(false).getSection().getNumber() == 11);

	    // test track switch 4

	    assertTrue(sectionFour.get(0).getNext(true).getSection().getNumber() == 5); // 
	    assertTrue(sectionFour.get(0).getNext(false).getAltSection().getNumber() == 19);
	    assertTrue(sectionFour.get(0).getPrevious(false).getSection().getNumber() == 3);
	    assertTrue(sectionFour.get(0).getPrevious(true).getSection().getNumber() == 3);

	    // test track switch 3

	    assertTrue(sectionThree.get(0).getNext(false).getSection().getNumber() == 4);
	    assertTrue(sectionThree.get(0).getNext(true).getSection().getNumber() == 4);
	    assertTrue(sectionThree.get(0).getPrevious(true).getSection().getNumber() == 10);
	    assertTrue(sectionThree.get(0).getPrevious(false).getSection().getNumber() == 2);

	    // test track switch 16

	    assertTrue(sectionSixteen.get(0).getNext(false).getSection().getNumber() == 17);
	    assertTrue(sectionSixteen.get(0).getNext(true).getSection().getNumber() == 9);
	    assertTrue(sectionSixteen.get(0).getPrevious(true).getSection().getNumber() == 15);
	    assertTrue(sectionSixteen.get(0).getPrevious(false).getSection().getNumber() == 15);

	}
	@Test public void testTrackRunNoCtl(){
		SimulationTrack sim0 = new SimulationTrack();
		sim0.getTrack().recalculateSections();
		Map<Integer,Section> map = sim0.getTrack().getSectionNumberMap();
		Movable loco = new Locomotive(new Track[]{map.get(1).get(0)}, 40,40,10,false);
		Train train = new Train(new Movable[]{loco});
		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		trainMap.put(0, train);
		train.setID(0);
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();
		orientationMap.put(0,  new modelrailway.core.Train(1,true));
		final Route route = new Route(true,1,2,3,4,5,6,7,8);
		final Simulator sim = new Simulator(sim0.getHead(),orientationMap,trainMap);

		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();
		sim.register(new Listener(){
			@Override
			public void notify(Event e) {
				System.out.println("in notify" + e.getClass());
				if(e instanceof Event.SectionChanged){
					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					if( ((SectionChanged) e).getInto() == false){
						outputArray.add(route.nextSection(i));
					}else {
					   outputArray.add(i);
					}
					System.out.println(i+" added");
					if(i > 8){
						th.interrupt();
						sim.notify(new Event.SpeedChanged(0, 0));
					}
					if(i == 7){
						th.interrupt();
					}
				}
			}
		});
		sim.notify( new Event.SpeedChanged(0, 6));
		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println(outputArray.toString());
		}
	}

	@Test public void testTrackRunMoveCtl(){
		SimulationTrack sim0 = new SimulationTrack();
		sim0.getTrack().recalculateSections();
		Map<Integer,Section> map = sim0.getTrack().getSectionNumberMap();
		Movable loco = new Locomotive(new Track[]{map.get(1).get(0)}, 40,40,10,false);
		Train train = new Train(new Movable[]{loco});
		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		trainMap.put(0, train);
		train.setID(0);
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();
		orientationMap.put(0,  new modelrailway.core.Train(1,true));
		final Route route = new Route(true,1,2,3,4,5,6,7,8);
		final Simulator sim = new Simulator(sim0.getHead(),orientationMap,trainMap);
		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();
		final MovementController controller = new MovementController(orientationMap,map,sim0.getHead(), sim);

		sim.register(controller);
		controller.register(new Listener(){
			@Override
			public void notify(Event e) {
				System.out.println("in notify" + e.getClass());
				if(e instanceof Event.SectionChanged){
					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					if( ((SectionChanged) e).getInto() == false){
						outputArray.add(route.nextSection(i));
					}else {
					   outputArray.add(i);
					}
					System.out.println(i+" added");
					if(i > 8){
						th.interrupt();
						sim.notify(new Event.SpeedChanged(0, 0));
					}
					if(i == 7){
						th.interrupt();
					}
				}
			}
		});
		controller.start(0, route);
		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println(outputArray.toString());
		}

	}


	@Test public void testTrackRun0(){
		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();

		Map<Integer,Section> numberMap = ring.getSectionNumberMap();

		Section startSec = numberMap.get(1);
		Track headPiece = startSec.get(0);

		final Route route = new Route(true, 1,2,3,4,5,6,7,8);

		Movable locomotive = new Locomotive(new Track[]{headPiece}, 40,40,10, false);

		Train train = new Train(new Movable[]{locomotive});

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();

		trainMap.put(0,train );
		train.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(1, true));

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

 					  if(((SectionChanged)e).getSection() == 1){

 						  ctl.stop(0);
 						  sim.stop();
 						  th.interrupt();

 					  }
 					//  throw new RuntimeException("Experienced Notify Stop Statement");
 					}
 					else if (e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 						Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 						outputArray.add(route.nextSection(i));

 					}

 					else if (e instanceof Event.EmergencyStop){
 						ctl.stop(0);
 						sim.stop();
 						th.interrupt();
 					}

 			}

 		});

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
		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();
		Map<Integer,Section> numberMap = ring.getSectionNumberMap();

		Section startSec = numberMap.get(17);
		Track startPiece = startSec.get(0);

		Section headSec = numberMap.get(1);
		Track headPiece = headSec.get(0);
		final Route route = new Route(true, 17,18,11,12,13,14,15,16,9,10);

		Movable locomotive = new Locomotive(new Track[]{startPiece}, 40,40,10, false);

		Train train = new Train(new Movable[]{locomotive});

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();

		trainMap.put(0,train );
		train.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(17, true));

		final Simulator sim = new Simulator(headPiece, orientationMap, trainMap);

		final ControlerCollision ctl = new ControlerCollision(orientationMap,ring.getSectionNumberMap(),headPiece,sim);
		sim.register(ctl);

		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
 		final Thread th = Thread.currentThread();

		ctl.register(new Listener(){
 			public void notify(Event e){
 				//System.out.println("event "+e.toString());
 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){

 					  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 					  outputArray.add(i);

 					  if(((SectionChanged)e).getSection() == 1){

 						  ctl.stop(0);
 						  sim.stop();
 						  th.interrupt();

 					  }
 					//  throw new RuntimeException("Experienced Notify Stop Statement");
 				}
 				else if (e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 						Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 						outputArray.add(route.nextSection(i));

 			    }
 				else if (e instanceof Event.EmergencyStop){
 					  ctl.stop(0);
 					  sim.stop();
 					  th.interrupt();
 				}

 			}

 		});

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
		SimulationTrack sim0 = new SimulationTrack();

		StraightDblRing ring = sim0.getTrack();
		ring.recalculateSections();

		Map<Integer,Section> numberMap = ring.getSectionNumberMap();

		Section startSec = numberMap.get(1);
		Track headPiece = startSec.get(0);

		final Route route = new Route(true,1,2,3,4,5,6,7,8,9,10);

		Movable locomotive = new Locomotive(new Track[]{headPiece}, 40,40,10, false);

		Train train = new Train(new Movable[]{locomotive});

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();

		trainMap.put(0,train );
		train.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(1, true));

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

 					  if(((SectionChanged)e).getSection() == 10){

 						  ctl.stop(0);
 						  sim.stop();
 						  th.interrupt();

 					  }
 					//  throw new RuntimeException("Experienced Notify Stop Statement");
 					}
 					else if (e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 						Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 						outputArray.add(route.nextSection(i));

 					}

 					else if (e instanceof Event.EmergencyStop){
 						ctl.stop(0);
 						sim.stop();
 						th.interrupt();
 					}

 			}

 		});

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



	}
}
