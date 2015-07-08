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
import modelrailway.simulation.Switch;
import modelrailway.simulation.Track;
import modelrailway.simulation.Locomotive;
import modelrailway.simulation.Movable;
import modelrailway.simulation.Train;
import modelrailway.simulation.Simulator;
import modelrailway.util.Pair;
import modelrailway.util.ControlerCollision;
import modelrailway.util.MovementController;

import java.util.HashMap;

import org.junit.*;


public class CtlColTest {



  	@Test public void ctlColTest0(){
  		Section.resetCounter();
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(4, 100);
		sec.add(head);



		route.recalculateSections();
		route.getSectionNumberMap();

		Track tp_1 = head.getNext(false);
		Track tp_2 = tp_1.getNext(false);
		Track tp_3 = tp_2.getNext(false);
		Track tp_4 = tp_3.getNext(false);

		head.getSection().setSectionNumber(1);
		tp_1.getSection().setSectionNumber(2);
		tp_2.getSection().setSectionNumber(3);
		tp_3.getSection().setSectionNumber(4);

		route.recalculateSections();
		route.getSectionNumberMap();

		assert(tp_4 == head);

		//set up the train track.

		// add a locomotive

		final Movable locomotive = new Locomotive(new Track[]{head,head},40,20,10,false);




		final Movable loco2 = new Locomotive(new Track[]{tp_3,tp_3},40,20,10,false);
		//locomotive.toggleDirection();

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Train train = new Train(new Movable[]{locomotive});
		Movable.GenerateID.generateID(locomotive);

		Train train2 = new Train(new Movable[]{loco2});
		Movable.GenerateID.generateID(loco2);
		//train.toggleDirection();
		trainMap.put(0,train );
		train.setID(0);

		trainMap.put(1, train2);
		train2.setID(1);

		//System.out.println(sec.getNumber()+ ": head");
		//System.out.println(tp_1.getSection().getNumber()+ ": tp_1");
		//System.out.println(tp_2.getSection().getNumber()+ ": tp_2");
		//System.out.println(tp_3.getSection().getNumber()+ ": tp_3");

		boolean secReserve = tp_1.getSection().reserveSection(train.getID());
		assertTrue(secReserve);
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(head.getSection().getNumber(), true));
		orientationMap.put(1, new modelrailway.core.Train(tp_3.getSection().getNumber(),true));


		final Simulator sim = new Simulator(head, orientationMap, trainMap);
		final MovementController ctl = new ControlerCollision(orientationMap,route.getSectionNumberMap(),head,sim); //
		sim.register(ctl);

		Integer headSection = head.getSection().getNumber();
		Integer sec1 = tp_1.getSection().getNumber();
		Integer sec2 = tp_2.getSection().getNumber();
		Integer sec3 = tp_3.getSection().getNumber();



		Route routePlan = new Route(true, headSection, sec1, sec2, sec3);
		//System.out.println("route: "+headSection+", "+switchSection+", "+swAlt+", "+sw2Section);

		final Thread th = Thread.currentThread();
		final ArrayList<String> output = new ArrayList<String>();

		ctl.register(new Listener(){
			public void notify(Event e){
				//System.out.println("event "+e.toString());

				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){

					  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					  output.add(""+i);

					  if(((SectionChanged)e).getSection() == 1){

						  ctl.stop(0);
						  sim.stop();
						  th.interrupt();
						  output.add("failure to stop");

					  }
					//  throw new RuntimeException("Experienced Notify Stop Statement");
					}
					else if (e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
						Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
						output.add(""+i);
						//output.add(routePlan.nextSection(i));

					}


				if(e instanceof Event.EmergencyStop){
				  sim.stop();
				  output.add("emergency stop at position by locomotive "+((Event.EmergencyStop) e).getLocomotive());
				  th.interrupt();
				}
			}

		});

		assertTrue(train.isFowards() == true);
		ctl.start(0, routePlan);
		
		try{
		   Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("output: "+output);
			System.out.println("split: "+output.get(output.size()-1).split(" ")[0]); // stop on the last one
			assertTrue(output.get(output.size()-1).split(" ")[0].equals("emergency")); // stop on the last one.
			assertTrue(locomotive.getFront().getSection().getNumber() == sec2);
			System.out.println("ctlColTest0");
			System.out.println(routePlan.toString());
			System.out.println("routeTraveled: "+output.toString());
		}


  	}
  	@Test public void ctlColTest1(){
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


 		assertTrue(sw.getNext(true) == str);
 		assertTrue(sw.getNext(false) == tp_2);
 		assertTrue(str.getSection() == sec4);

 		Movable locomotive = new Locomotive(new Track[]{head,head},40,40,10,false);
 		Movable locomotive2 = new Locomotive(new Track[]{tp_2,tp_2},40,40,10,false);

 		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
 		Train train = new Train(new Movable[]{locomotive});
 		Movable.GenerateID.generateID(locomotive);

 		Train train2 = new Train(new Movable[]{locomotive2});
 		Movable.GenerateID.generateID(locomotive2);
 		trainMap.put(0,train );
 		train.setID(0);
 		trainMap.put(1, train2);
 		train2.setID(1);
 		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

 		orientationMap.put(0, new modelrailway.core.Train(head.getSection().getNumber(), true));
 		orientationMap.put(1, new modelrailway.core.Train(sw.getSection().getNumber(),true));


//		train.toggleDirection();

 		final Simulator sim = new Simulator(head, orientationMap, trainMap);
 		final MovementController ctl = new MovementController(orientationMap,route.getSectionNumberMap(),head, sim); //
 		sim.register(ctl);

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


 		final Route routePlan = new Route(true, switchSection, swAlt, sw2Section, headSection);


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
 						outputArray.add(routePlan.nextSection(i));

 					}


 			}

 		});

 		ctl.start(0, routePlan);

 		try{
 			//System.out.println("started: ");
 		   Thread.currentThread().join();
 		 //  System.out.println("stopped:");
 		}catch(InterruptedException e){
 			System.out.println("ctlColTest1");
 			System.out.println(routePlan.toString());
 			System.out.println("output: "+outputArray.toString());
 		}
 		assertTrue(outputArray.get(0) == switchSection);
 		assertTrue(outputArray.get(1) == swAlt);
 		assertTrue(outputArray.get(2) == sw2Section);
 		assertTrue(outputArray.get(3) == headSection);

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

 					  if(((SectionChanged)e).getSection() == 1){

 						  ctl.stop(0);
 						  sim.stop();
 						  th.interrupt();

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

