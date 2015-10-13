package modelrailway.testing;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Section;
import modelrailway.core.Event.Listener;
import modelrailway.core.Event.SectionChanged;
import modelrailway.core.Route;
import modelrailway.simulation.BackSwitch;
import modelrailway.simulation.ForwardSwitch;
import modelrailway.simulation.Straight;
import modelrailway.simulation.Straight.StraightRing;
import modelrailway.simulation.Switch;
import modelrailway.simulation.Track;
import modelrailway.simulation.Locomotive;
import modelrailway.simulation.Movable;
import modelrailway.simulation.Train;
import modelrailway.simulation.Simulator;
import modelrailway.simulation.Straight.StraightDblRing;
import modelrailway.util.Pair;
import modelrailway.util.ControlerCollision;
import modelrailway.util.MovementController;

import java.util.HashMap;

import org.junit.*;


public class CtlColTest {

	public Listener addSingleTrainStopAtSec(final Map<Integer,modelrailway.simulation.Train> trainMap, final Controller ctl, final StraightRing ring, final ArrayList<Integer> outputArray, final Route routePlan, final int stopSec, final int train, final Simulator sim){
		final Thread th = Thread.currentThread();
		return new Listener(){
 			public void notify(Event e){
 				//System.out.println("event "+e.toString());
 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){

 					  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 					  outputArray.add(i);

 					  if(i == stopSec){

 						  ctl.stop(train);
 						  sim.stop();
 						  th.interrupt();

 					  }
 					//  throw new RuntimeException("Experienced Notify Stop Statement");
 					}
 					else if (e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 						Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 						outputArray.add(routePlan.nextSection(i));

 					}


 			}

 		};

	}
	public Listener addMultiTrainListener(final Map<Integer, modelrailway.simulation.Train> trainMap, final ControlerCollision controller,final StraightRing ring , final ArrayList<String> outputArrayLoco0,
			final ArrayList<String> outputArrayLoco1,final  int trainZero ,final int trainOne, final Route rt0, final Route rt1, final Simulator sim){
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
						outputArrayLoco1.add("emergency stop at position by locomotive "+((Event.EmergencyStop) e).getLocomotive());
					}
					else{
						outputArrayLoco0.add("emergency stop at position by locomotive "+((Event.EmergencyStop) e).getLocomotive());

					}

					sim.stop();

					th.interrupt();
				}


				if(trainMap.get(trainZero).getCurrentSpeed() ==0 && trainMap.get(trainOne).getCurrentSpeed() == 0){
					//System.out.println("both have stopped");
					th.interrupt();

				}
			}



		};

		return lst;
	}
	public void setRoute(int id, Route rt, StraightRing ring, ControlerCollision ctl){
		//Integer fst = rt.firstSection();
		//Integer snd = rt.nextSection(rt.firstSection());
		ctl.trainOrientations().get(id).setSection(rt.firstSection());
		ring.getSectionNumberMap().get(rt.firstSection()).getEntryRequests().add(id);
		ring.getSectionNumberMap().get(rt.nextSection(rt.firstSection())).getEntryRequests().add(id);
	}

	public Pair<Map<Integer,modelrailway.simulation.Train>, Map<Integer,modelrailway.core.Train>> makeDefaultTrains(final int[] trainIDs,final  Route[] routes, final StraightRing ring){
		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();
		for(int x = 0; x< trainIDs.length; x++){
			final Movable loco= new Locomotive(new Track[]{ring.getSectionNumberMap().get(routes[x].firstSection()).get(0)}, 40,20,10, false);
			final Train train = new Train(new Movable[]{loco});
			trainMap.put(trainIDs[x], train);
			train.setID(trainIDs[x]);
			orientationMap.put(trainIDs[x], new modelrailway.core.Train(routes[x].firstSection(), true));
			System.out.println("trains: "+trainIDs[x] + ", section: "+routes[x].firstSection().intValue());
		}
		return new Pair<>(trainMap,orientationMap);
	}

  	@Test public void ctlColTest0(){
  		Section.resetCounter();
		Section sec = new Section(new ArrayList<Track>());
		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing ring = new Straight.StraightRing(st);
		final Track head = ring.ringTrack(4, 100);
		sec.add(head);

		ring.recalculateSections();
		ring.getSectionNumberMap();

		final Track tp_1 = head.getNext(false);
		final Track tp_2 = tp_1.getNext(false);
		final Track tp_3 = tp_2.getNext(false);
		final Track tp_4 = tp_3.getNext(false);

		head.getSection().setSectionNumber(1);
		tp_1.getSection().setSectionNumber(2);
		tp_2.getSection().setSectionNumber(3);
		tp_3.getSection().setSectionNumber(4);

		ring.recalculateSections();
		ring.getSectionNumberMap();

		assert(tp_4 == head);
		Integer headSection = head.getSection().getNumber();
		Integer sec1 = tp_1.getSection().getNumber();
		Integer sec2 = tp_2.getSection().getNumber();
		Integer sec3 = tp_3.getSection().getNumber();
		Route rt0 = new Route(true, headSection, sec1, sec2, sec3);
		Route rt1 = new Route(true, sec3,headSection,sec1,sec2);
		Pair<Map<Integer,modelrailway.simulation.Train>, Map<Integer,modelrailway.core.Train>>  pair = this.makeDefaultTrains(new int[]{0,1}, new Route[]{rt0, rt1}, ring);
		final Simulator sim = new Simulator(head, pair.snd, pair.fst);
		final MovementController ctl = new ControlerCollision(pair.snd,ring.getSectionNumberMap(),head,sim); //
		sim.register(ctl);
		//System.out.println("route: "+headSection+", "+switchSection+", "+swAlt+", "+sw2Section);
		final ArrayList<String> outputArrayLoco0 = new ArrayList<String>();
		final ArrayList <String> outputArrayLoco1 = new ArrayList<String>();
		int trainZero = 0;
		int trainOne = 1;
		Listener lst = this.addMultiTrainListener(pair.fst, (ControlerCollision) ctl, ring, outputArrayLoco0, outputArrayLoco1, trainZero, trainOne, rt0, rt1, sim);
		((ControlerCollision)ctl).register(lst);
		assertTrue(((Movable) pair.snd).isFowards() == true);
		ctl.start(0, rt0);

		try{
		   Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("outputArrayLoco0: "+outputArrayLoco0.toString());
			System.out.println("outputArrayLoco1: "+outputArrayLoco1.toString());

		}


  	}

  	public StraightRing createRingWithAlt(){
  		Section.resetCounter();
 		Section sec = new Section(new ArrayList<Track>());

 		Straight st = new Straight(null,null,sec,100);
 		Straight.StraightRing route = new Straight.StraightRing(st);
 		Track head = route.ringTrack(4, 100);
 		sec.add(head);
 		route.recalculateSections();
		route.getSectionNumberMap();


 		Track tp_1 = head.getNext(false);
 		Section s1 = tp_1.getSection();

 		Track tp_2 = tp_1.getNext(false);
 		Section s2 = tp_2.getSection();

 		Track tp_3 = tp_2.getNext(false);
 		Section s3 = tp_3.getSection();

 		Track tp_4 = tp_3.getNext(false);
 		Section s4 = tp_4.getSection();


 		route.recalculateSections();

 		Section sec2 = new Section(new ArrayList<Track>());
 		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

 		sec2.add(sw);

 		Section sec3 = new Section(new ArrayList<Track>());
 		Track sw2 = new BackSwitch(null, null, null, sec3, 100, 100, 50);

 		sec3.add(sw2);


 		route.replace(tp_1, sw, false);
 		route.replace(tp_3, sw2, false);

 		Section sec4 = new Section(new ArrayList<Track>());
 		Straight str = new Straight(null, null, sec4,100 );


 		sec4.add(str);

 		route.insertBetween(sw, true, sw2, true,  str, false);

 		head.getSection().setSectionNumber(1);
		sec2.setSectionNumber(2);
		tp_2.getSection().setSectionNumber(3);
		sec3.setSectionNumber(4);
		sec4.setSectionNumber(5);

 		route.recalculateSections();
		route.getSectionNumberMap();
 		Integer headSection = head.getSection().getNumber();
 		Integer switchSection = sw.getSection().getNumber();
 		Integer swAlt = str.getSection().getNumber();
 		Integer sw2Section = sw2.getSection().getNumber();
 		Integer mainRoute = tp_2.getSection().getNumber();

 		sec2.putSwitchingOrder(new Pair<Integer,Integer>(headSection,swAlt),Arrays.asList(new Boolean[]{true}));
		sec2.putSwitchingOrder(new Pair<Integer,Integer>(headSection,mainRoute),Arrays.asList(new Boolean[]{false}));
		sec2.putSwitchingOrder(new Pair<Integer,Integer>(swAlt,headSection),Arrays.asList(new Boolean[]{true}));
		sec2.putSwitchingOrder(new Pair<Integer,Integer>(mainRoute,headSection),Arrays.asList(new Boolean[]{false}));

		sec3.putSwitchingOrder(new Pair<Integer,Integer>(headSection,swAlt),Arrays.asList(new Boolean[]{true}));
		sec3.putSwitchingOrder(new Pair<Integer,Integer>(headSection,mainRoute),Arrays.asList(new Boolean[]{false}));
		sec3.putSwitchingOrder(new Pair<Integer,Integer>(swAlt,headSection),Arrays.asList(new Boolean[]{true}));
		sec3.putSwitchingOrder(new Pair<Integer,Integer>(mainRoute,headSection),Arrays.asList(new Boolean[]{false}));
		return route;

  	}
  	@Test public void ctlColTest1(){

  		StraightRing ring = this.createRingWithAlt();
  		Track head = ring.getSectionNumberMap().get(1).get(0);
  		Track sw = ring.getSectionNumberMap().get(2).get(0);
  		Track str = ring.getSectionNumberMap().get(5).get(0);
  		Track tp_2 = ring.getSectionNumberMap().get(3).get(0);
  		Section sec4 = ring.getSectionNumberMap().get(5);

 		assertTrue(sw.getNext(true) == str);
 		assertTrue(sw.getNext(false) == tp_2);
 		assertTrue(str.getSection() == sec4);

 		final Route routePlan1 = new Route(true, 5, 4, 1, 2);
 		final Route routePlan0 = new Route(true, 1, 2, 5, 4);
 		Pair<Map<Integer,modelrailway.simulation.Train>, Map<Integer,modelrailway.core.Train>>  pair  = this.makeDefaultTrains(new int[]{0,1}, new Route[]{routePlan0,routePlan1}, ring);



 		final Simulator sim = new Simulator(head, pair.snd, pair.fst);
 		final MovementController ctl = new MovementController(pair.snd,ring.getSectionNumberMap(),head, sim); //
 		sim.register(ctl);


 		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
 		Listener lst = addSingleTrainStopAtSec(pair.fst, ctl, ring, outputArray, routePlan0, 1, 0, sim);
 		ctl.register(lst);

 		ctl.start(0, routePlan0);

 		try{
 			//System.out.println("started: ");
 		   Thread.currentThread().join();
 		 //  System.out.println("stopped:");
 		}catch(InterruptedException e){
 			System.out.println("ctlColTest1");
 			System.out.println(routePlan0.toString());
 			System.out.println("output: "+outputArray.toString());
 		}
 		assertTrue(outputArray.get(0) == sw.getSection().getNumber());
 		assertTrue(outputArray.get(1) == str.getSection().getNumber());
 		assertTrue(outputArray.get(2) == 3);
 		assertTrue(outputArray.get(3) == head.getSection().getNumber());

  	}

  	@Test public void ctlColTest2(){
  		Section.resetCounter();
 		Section sec = new Section(new ArrayList<Track>());

 		Straight st = new Straight(null,null,sec,100);
 		Straight.StraightRing route = new Straight.StraightRing(st);
 		Track head = route.ringTrack(4, 100);
 		sec.add(head);
 		Track tp_1 = head.getNext(false);
 		Track tp_2 = tp_1.getNext(false);
 		Track tp_3 = tp_2.getNext(false);
 		Track tp_4 = tp_3.getNext(false);

 		Section sec2 = new Section(new ArrayList<Track>());
 		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50
 		sec2.add(sw);

 		Section sec3 = new Section(new ArrayList<Track>());
 		Track sw2 = new BackSwitch(null, null, null, sec3, 100, 100, 50);
 		sec3.add(sw2);


 		route.replace(tp_1, sw, false);
 		route.replace(tp_3, sw2, false);

 		Section sec4 = new Section(new ArrayList<Track>());
 		Straight str = new Straight(null, null, sec4,100 );
 		sec4.add(str);

 		route.insertBetween(sw, true, sw2, true,  str, false);

 		head.getSection().setSectionNumber(1);
		sec2.setSectionNumber(2);
		tp_2.getSection().setSectionNumber(3);
		sec3.setSectionNumber(4);
		sec4.setSectionNumber(5);

 		route.recalculateSections();
		route.getSectionNumberMap();

 		assertTrue(sw.getNext(true) == str);
 		assertTrue(sw.getNext(false) == tp_2);
 		assertTrue(str.getSection() == sec4);



 		//set up the train track.

 		// add a locomotive

 		Movable locomotive = new Locomotive(new Track[]{sw2,sw2},80,40,10,false);
 		Movable.GenerateID.generateID(locomotive);
 		Movable locomotive2 = new Locomotive(new Track[]{str,str},40,40,10,false);

 		str.getSection().getEntryRequests().add(1); // add the locomotive2 to the straight

 		Movable.GenerateID.generateID(locomotive2);
 		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
 		Train train = new Train(new Movable[]{locomotive});


 		sw.getSection().reserveSection(train.getID());

 		Train train2 = new Train(new Movable[]{locomotive2});

 		trainMap.put(0,train );
 		train.setID(0);
 		trainMap.put(1, train2);
 		train2.setID(1);
 		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

 		orientationMap.put(0, new modelrailway.core.Train(head.getSection().getNumber(), false));
 		orientationMap.put(1, new modelrailway.core.Train(sw.getSection().getNumber(),false));



 		final Simulator sim = new Simulator(head, orientationMap, trainMap);
 		final MovementController ctl = new ControlerCollision(orientationMap,route.getSectionNumberMap(),head,sim); //
 		sim.register(ctl);

 		final Integer headSection = head.getSection().getNumber();
 		final Integer switchSection = sw.getSection().getNumber();
 		final Integer swAlt = str.getSection().getNumber();
 		final Integer sw2Section = sw2.getSection().getNumber();
 		final Integer mainRoute = tp_2.getSection().getNumber();

 		sec2.putSwitchingOrder(new Pair<Integer,Integer>(headSection,swAlt),Arrays.asList(new Boolean[]{true}));
		sec2.putSwitchingOrder(new Pair<Integer,Integer>(headSection,mainRoute),Arrays.asList(new Boolean[]{false}));
		sec2.putSwitchingOrder(new Pair<Integer,Integer>(swAlt,headSection),Arrays.asList(new Boolean[]{true}));
		sec2.putSwitchingOrder(new Pair<Integer,Integer>(mainRoute,headSection),Arrays.asList(new Boolean[]{false}));

		sec3.putSwitchingOrder(new Pair<Integer,Integer>(headSection,swAlt),Arrays.asList(new Boolean[]{true}));
		sec3.putSwitchingOrder(new Pair<Integer,Integer>(headSection,mainRoute),Arrays.asList(new Boolean[]{false}));
		sec3.putSwitchingOrder(new Pair<Integer,Integer>(swAlt,headSection),Arrays.asList(new Boolean[]{true}));
		sec3.putSwitchingOrder(new Pair<Integer,Integer>(mainRoute,headSection),Arrays.asList(new Boolean[]{false}));


 		final Route routePlan = new Route(true, headSection, switchSection , swAlt , sw2Section  );
 		//Route routePlan = new Route(true, sw2Section, swAlt, switchSection, headSection);
 		//System.out.println("route: "+headSection +","+ switchSection +","+ swAlt + "," + sw2Section);

 		final ArrayList<String> outputArray = new ArrayList<String>();
 		final Thread th = Thread.currentThread();
 		ctl.register(new Listener(){
 			public void notify(Event e){
 				//System.out.println("event "+e.toString());
 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){

 					  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 					  outputArray.add(""+i);

 					  if(i == 1){

 						  ctl.stop(0);
 						  sim.stop();
 						 // th.interrupt();

 					  }
 					//  throw new RuntimeException("Experienced Notify Stop Statement");
 					}
 					else if (e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 						Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 						outputArray.add(""+routePlan.nextSection(i));

 					}



 				if(e instanceof Event.EmergencyStop){
 					sim.stop();
 					outputArray.add(("emergency stop by locomotive :"+((Event.EmergencyStop)e).getLocomotive()));
 					th.interrupt();
 				}
 			}

 		});

 		ctl.start(0, routePlan);

 		try{
 			//System.out.println("started: ");
 		   Thread.currentThread().join();
 		 //  System.out.println("stopped:");
 		}catch(InterruptedException e){
 			System.out.println("ctlColTest2");
 			System.out.println(routePlan.toString());
 			System.out.println("output: "+outputArray.toString());
 		}

 		assertTrue(outputArray.get(outputArray.size()-1).split(" ")[0].equals("emergency"));
  	}

  	/**
  	 * Test switches going along an alternate route.
  	 */
  	@Test public void testTrackRun1(){


  	}






}

