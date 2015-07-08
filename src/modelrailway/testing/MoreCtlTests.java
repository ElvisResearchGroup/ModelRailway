package modelrailway.testing;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import modelrailway.core.Event;
import modelrailway.core.Route;
import modelrailway.core.Section;
import modelrailway.core.Event.Listener;
import modelrailway.core.Event.SectionChanged;
import modelrailway.simulation.BackSwitch;
import modelrailway.simulation.ForwardSwitch;
import modelrailway.simulation.Locomotive;
import modelrailway.simulation.Movable;
import modelrailway.simulation.Simulator;
import modelrailway.simulation.Straight;
import modelrailway.simulation.Switch;
import modelrailway.simulation.Track;
import modelrailway.simulation.Train;
import modelrailway.util.ControlerCollision;
import modelrailway.util.MovementController;
import modelrailway.util.Pair;

import org.junit.Test;

public class MoreCtlTests {

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


		head.getSection().setSectionNumber(1);
		sec2.setSectionNumber(2);
		tp_2.getSection().setSectionNumber(3);
		sec3.setSectionNumber(4);
		sec4.setSectionNumber(5);


		route.recalculateSections();



		assertTrue(sw.getNext(true) == str);
		assertTrue(sw.getNext(false) == tp_2);
		assertTrue(str.getSection() == sec4);

		System.out.println("routeSectionSize: "+route.getSectionNumberMap().size());
		//set up the train track.

		// add a locomotive

		Movable locomotive = new Locomotive(new Track[]{head,head},40,40,10,false);
		System.out.println("head of locomotive 0 is: "+ head.getSection().getNumber());
		Movable.GenerateID.generateID(locomotive);

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Train train = new Train(new Movable[]{locomotive});

		trainMap.put(0,train );
		train.setID(0);

		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(head.getSection().getNumber(), true));


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

		final Route routePlan = new Route(true,  switchSection, swAlt, sw2Section,headSection);
		//System.out.println("route: "+headSection+", "+switchSection+", "+swAlt+", "+sw2Section);

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
			System.out.println("ctlTest0:");
			System.out.println(routePlan.toString());
			System.out.println("output: "+outputArray.toString());
		}
		//assertTrue(outputArray.get(0) == 4);
		//assertTrue(outputArray.get(1) == 6);
		//assertTrue(outputArray.get(2) == 5);
		//assertTrue(outputArray.get(3) == 0);


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

		head.getSection().setSectionNumber(1);
		sec2.setSectionNumber(2);
		tp_2.getSection().setSectionNumber(3);
		sec3.setSectionNumber(4);
		sec4.setSectionNumber(5);

		route.recalculateSections();
		route.getSectionNumberMap();
		//set up the train track.

		// add a locomotive

		Movable locomotive = new Locomotive(new Track[]{head,head},40,20,10,false); // head
		Movable.GenerateID.generateID(locomotive);

		//locomotive.toggleDirection();

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Train train = new Train(new Movable[]{locomotive});
		train.toggleDirection();
		trainMap.put(0,train );
		train.setID(0);
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(head.getSection().getNumber(), false)); // go backwards. 


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
		
		System.out.println("Section 2: "+((Switch)sec2.get(0)).getSwitchID());

		sec3.putSwitchingOrder(new Pair<Integer,Integer>(headSection,swAlt),Arrays.asList(new Boolean[]{true}));
		sec3.putSwitchingOrder(new Pair<Integer,Integer>(headSection,mainRoute),Arrays.asList(new Boolean[]{false}));
		sec3.putSwitchingOrder(new Pair<Integer,Integer>(swAlt,headSection),Arrays.asList(new Boolean[]{true}));
		sec3.putSwitchingOrder(new Pair<Integer,Integer>(mainRoute,headSection),Arrays.asList(new Boolean[]{false}));
		
		System.out.println("Section 3: "+((Switch)sec3.get(0)).getSwitchID());

		
	
		final Route routePlan = new Route(true, sw2Section, swAlt, switchSection, headSection);
		//System.out.println("route: "+headSection+", "+switchSection+", "+swAlt+", "+sw2Section);

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
						System.out.println("Try getting next Section for i: "+i);
						outputArray.add(routePlan.nextSection(i));

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
		assertTrue(outputArray.get(0) == sw2Section);
		assertTrue(outputArray.get(1) == swAlt);
		assertTrue(outputArray.get(2) == switchSection);
		assertTrue(outputArray.get(3) == headSection);

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


		head.getSection().setSectionNumber(1);
		sec2.setSectionNumber(2);
		tp_2.getSection().setSectionNumber(3);
		sec3.setSectionNumber(4);
		sec4.setSectionNumber(5);

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

		orientationMap.put(0, new modelrailway.core.Train(head.getSection().getNumber(), false));

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

		final Route routePlan = new Route(true, sw2Section, mainRoute, switchSection, headSection);
		//System.out.println("route: "+headSection+", "+switchSection+", "+swAlt+", "+sw2Section);

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
		assertTrue(outputArray.get(0) == sw2Section);
		assertTrue(outputArray.get(1) == mainRoute);
		assertTrue(outputArray.get(2) == switchSection);
		assertTrue(outputArray.get(3) == headSection);

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


		head.getSection().setSectionNumber(1);
		sec2.setSectionNumber(2);
		tp_2.getSection().setSectionNumber(3);
		sec3.setSectionNumber(4);
		sec4.setSectionNumber(5);

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

		orientationMap.put(0, new modelrailway.core.Train(head.getSection().getNumber(), true));

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
		final Route routePlan = new Route(true, switchSection, mainRoute, sw2Section, headSection);
		///System.out.println("route: "+switchSection+", "+swAlt+", "+sw2Section);

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
		assertTrue(outputArray.get(0) == switchSection);
		assertTrue(outputArray.get(1) == mainRoute);
		assertTrue(outputArray.get(2) == sw2Section);
		assertTrue(outputArray.get(3) == headSection);

  	}

}
