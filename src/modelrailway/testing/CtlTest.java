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


public class CtlTest {

	/**
	 * Use the TestController to test movement through a switch where the direction of the switch must be changed.
	 */
  @Test public void ctlTest0(){
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
		
		route.recalculateSections();

		assertTrue(sw.getNext(true) == str);
		assertTrue(sw.getNext(false) == tp_2);
		assertTrue(str.getSection() == sec4);

		System.out.println("routeSectionSize: "+route.getSectionNumberMap().size());
		//set up the train track.

		// add a locomotive

		Movable locomotive = new Locomotive(new Track[]{head,head},40,40,10,false);
		Movable.GenerateID.generateID(locomotive);

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Train train = new Train(new Movable[]{locomotive});

		trainMap.put(0,train );
		train.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(0, true));


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

		Route routePlan = new Route(true,  switchSection, swAlt, sw2Section,headSection);
		//System.out.println("route: "+headSection+", "+switchSection+", "+swAlt+", "+sw2Section);

		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();
		ctl.register(new Listener(){
			public void notify(Event e){
				//System.out.println("event "+e.toString());
				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
				  outputArray.add(((Event.SectionChanged) e).getSection());

				  if(((Event.SectionChanged)e).getSection() == 0){

					  ctl.stop(0);
					  sim.stop();
					  th.interrupt();

				  }
				}
			}

		});

		ctl.start(0, routePlan);

		try{
			//System.out.println("started: ");
		   Thread.currentThread().join();
		 //  System.out.println("stopped:");
		}catch(InterruptedException e){
			System.out.println("ctlTest0:");
			System.out.println(routePlan.toString());
			System.out.println("output: "+outputArray.toString());
		}
		assertTrue(outputArray.get(0) == 4);
		assertTrue(outputArray.get(1) == 6);
		assertTrue(outputArray.get(2) == 5);
		assertTrue(outputArray.get(3) == 0);


	}

  	@Test public void ctlTest1(){
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

		assertTrue(sw.getNext(true) == str);
		assertTrue(sw.getNext(false) == tp_2);
		assertTrue(str.getSection() == sec4);
		assertTrue(head.getPrevious(false) == sw2);


		route.recalculateSections();
		route.getSectionNumberMap();
		//set up the train track.

		// add a locomotive

		Movable locomotive = new Locomotive(new Track[]{head,head},40,20,10,false);
		Movable.GenerateID.generateID(locomotive);

		//locomotive.toggleDirection();

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Train train = new Train(new Movable[]{locomotive});
		train.toggleDirection();
		trainMap.put(0,train );
		train.setID(0);
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(0, false));


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

		Route routePlan = new Route(true, sw2Section, swAlt, switchSection, headSection);
		//System.out.println("route: "+headSection+", "+switchSection+", "+swAlt+", "+sw2Section);

		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();

		ctl.register(new Listener(){
			public void notify(Event e){
				//System.out.println("event "+e.toString());
				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){

				  //System.out.println(e);
				  outputArray.add(((Event.SectionChanged) e).getSection());

				  if(((Event.SectionChanged)e).getSection() == 0){

					  ctl.stop(0);
					  sim.stop();
					  th.interrupt();

				  }
				}
			}

		});

		assertTrue(train.isFowards() == false);
		ctl.start(0, routePlan);



		try{
			//System.out.println("started: ");
		   Thread.currentThread().join();
		 //  System.out.println("stopped:");
		}catch(InterruptedException e){
			System.out.println("ctlTest1");
			System.out.println(routePlan.toString());
			System.out.println("routeTraveled: "+outputArray.toString());
		}
		assertTrue(outputArray.get(0) == 5);
		assertTrue(outputArray.get(1) == 6);
		assertTrue(outputArray.get(2) == 4);
		assertTrue(outputArray.get(3) == 0);

  	}

  	@Test public void ctlTest2(){
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
		
		route.recalculateSections();
		route.getSectionNumberMap();

		assertTrue(sw.getNext(true) == str);
		assertTrue(sw.getNext(false) == tp_2);
		assertTrue(sw2.getPrevious(false) == tp_2);
		assertTrue(str.getSection() == sec4);
		assertTrue(head.getPrevious(false) == sw2);



		//set up the train track.

		// add a locomotive

		Movable locomotive = new Locomotive(new Track[]{head,head},40,20,10,false);
		Movable.GenerateID.generateID(locomotive);
		//locomotive.toggleDirection();

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Train train = new Train(new Movable[]{locomotive});
		train.toggleDirection();
		trainMap.put(0,train );
		train.setID(0);
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(0, false));

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

		Route routePlan = new Route(true, sw2Section, mainRoute, switchSection, headSection);
		//System.out.println("route: "+headSection+", "+switchSection+", "+swAlt+", "+sw2Section);

		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();

		ctl.register(new Listener(){
			public void notify(Event e){
				//System.out.println("event "+e.toString());
				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){

				  //System.out.println(e);
				  outputArray.add(((Event.SectionChanged) e).getSection());

				  if(((Event.SectionChanged)e).getSection() == 0){

					  ctl.stop(0);
					  sim.stop();
					  th.interrupt();

				  }
				}
			}

		});

		assertTrue(train.isFowards() == false);
		ctl.start(0, routePlan);



		try{
			//System.out.println("started: ");
		   Thread.currentThread().join();
		 //  System.out.println("stopped:");
		}catch(InterruptedException e){
			System.out.println("ctlTest2");
			System.out.println(routePlan.toString());
			System.out.println("routeTraveled: "+outputArray.toString());
		}
		assertTrue(outputArray.get(0) == 5);
		assertTrue(outputArray.get(1) == mainRoute);
		assertTrue(outputArray.get(2) == 4);
		assertTrue(outputArray.get(3) == 0);

  	}

  	@Test public void ctlTest3(){
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

		route.recalculateSections();
		route.getSectionNumberMap();
		
		assertTrue(sw.getNext(true) == str);
		assertTrue(sw.getNext(false) == tp_2);
		assertTrue(sw2.getPrevious(false) == tp_2);
		assertTrue(str.getSection() == sec4);
		assertTrue(head.getPrevious(false) == sw2);

		

		//set up the train track.

		// add a locomotive

		Movable locomotive = new Locomotive(new Track[]{head,head},40,20,10,false);
		Movable.GenerateID.generateID(locomotive);
		//locomotive.toggleDirection();

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Train train = new Train(new Movable[]{locomotive});
		//train.toggleDirection();
		trainMap.put(0,train );
		train.setID(0);
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(0, true));

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
		//System.out.println("headSection: "+headSection);
		//System.out.println("switchSection: "+switchSection);
		//System.out.println("swAlt: "+swAlt);
		//System.out.println("sw2Section: "+sw2Section);
		//System.out.println("mainRoute: "+mainRoute);

		//System.out.println("");

		//Route routePlan = new Route(true, sw2Section, mainRoute, switchSection, headSection);
		Route routePlan = new Route(true, switchSection, mainRoute, sw2Section, headSection);
		///System.out.println("route: "+switchSection+", "+swAlt+", "+sw2Section);

		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();

		ctl.register(new Listener(){
			public void notify(Event e){
				//System.out.println("event "+e.toString());
				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){

				  //System.out.println(e);
				  outputArray.add(((Event.SectionChanged) e).getSection());

				  if(((Event.SectionChanged)e).getSection() == 0){

					  ctl.stop(0);
					  sim.stop();
					  th.interrupt();

				  }
				}
			}

		});

		assertTrue(train.isFowards() == true);
		ctl.start(0, routePlan);



		try{
			//System.out.println("started: ");
		   Thread.currentThread().join();
		 //  System.out.println("stopped:");
		}catch(InterruptedException e){
			System.out.println("ctlTest3");
			System.out.println(routePlan.toString());
			System.out.println("routeTraveled: "+outputArray.toString());
		}
		assertTrue(outputArray.get(0) == 4);
		assertTrue(outputArray.get(1) == 2);
		assertTrue(outputArray.get(2) == 5);
		assertTrue(outputArray.get(3) == 0);

  	}

  	@Test public void ctlColTest0(){
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

		orientationMap.put(0, new modelrailway.core.Train(0, true));
		orientationMap.put(1, new modelrailway.core.Train(1,true));


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

				  //System.out.println(e);
				  if(((Event.SectionChanged)e).getSection() == 0){

					  ctl.stop(0);
					  sim.stop();
					  th.interrupt();
					  output.add("failure to stop");

				  }

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
			//System.out.println("started: ");
		   Thread.currentThread().join();
		 //  System.out.println("stopped:");
		}catch(InterruptedException e){
			//System.out.println(output);
			//System.out.println(routePlan);
			//System.out.println(output);
			assertTrue(output.get(0).split(" ")[0].equals("emergency"));
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

 		route.recalculateSections();



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

 		orientationMap.put(0, new modelrailway.core.Train(0, true));
 		orientationMap.put(1, new modelrailway.core.Train(1,true));


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


 		Route routePlan = new Route(true, switchSection, swAlt, sw2Section, headSection);


 		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
 		final Thread th = Thread.currentThread();
 		ctl.register(new Listener(){
 			public void notify(Event e){
 				//System.out.println("event "+e.toString());
 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  outputArray.add(((Event.SectionChanged) e).getSection());
 				  System.out.println("section changed: "+ ((Event.SectionChanged)e).getSection());
 				  if(((Event.SectionChanged)e).getSection() == 0){

 					  ctl.stop(0);
 					  sim.stop();
 					  th.interrupt();

 				  }
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
 		assertTrue(outputArray.get(0) == 4);
 		assertTrue(outputArray.get(1) == 6);
 		assertTrue(outputArray.get(2) == 5);
 		assertTrue(outputArray.get(3) == 0);

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

 		orientationMap.put(0, new modelrailway.core.Train(0, false));
 		orientationMap.put(1, new modelrailway.core.Train(1,false));

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


 		Route routePlan = new Route(true, headSection, switchSection , swAlt , sw2Section  );
 		//Route routePlan = new Route(true, sw2Section, swAlt, switchSection, headSection);
 		//System.out.println("route: "+headSection +","+ switchSection +","+ swAlt + "," + sw2Section);

 		final ArrayList<String> outputArray = new ArrayList<String>();
 		final Thread th = Thread.currentThread();
 		ctl.register(new Listener(){
 			public void notify(Event e){
 				//System.out.println("event "+e.toString());
 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  outputArray.add(""+((Event.SectionChanged) e).getSection());

 				  if(((Event.SectionChanged)e).getSection() == sw2Section){

 					  ctl.stop(0);
 					  sim.stop();
 					  th.interrupt();

 				  }
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

 		assertTrue(outputArray.get(0).split(" ")[0].equals("emergency"));
  	}




}

