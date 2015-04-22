package modelrailway.testing;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Event.Listener;
import modelrailway.core.Event.SectionChanged;
import modelrailway.core.Route;
import modelrailway.simulation.BackSwitch;
import modelrailway.simulation.ForwardSwitch;
import modelrailway.simulation.Section;
import modelrailway.simulation.Straight;
import modelrailway.simulation.Track;
import modelrailway.simulation.Locomotive;
import modelrailway.simulation.Movable;
import modelrailway.simulation.Train;
import modelrailway.simulation.Simulator;

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
		Track sw2 = new BackSwitch(null, null, tp_2, sec3, 100, 100, 50);
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

		Movable locomotive = new Locomotive(new Track[]{head,head},40,40,10,false);

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Train train = new Train(new Movable[]{locomotive});

		trainMap.put(0,train );
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(0, true));

		final Simulator sim = new Simulator(head, orientationMap, trainMap);
		final TestController ctl = new TestController( trainMap,orientationMap,head, sim); //

		Integer headSection = head.getSection().getNumber();
		Integer switchSection = sw.getSection().getNumber();
		Integer swAlt = str.getSection().getNumber();
		Integer sw2Section = sw2.getSection().getNumber();
		Integer mainRoute = tp_2.getSection().getNumber();


		Route routePlan = new Route(true, headSection, switchSection, swAlt, sw2Section);
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
			//System.out.println(outputArray.toString());
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
		Track sw2 = new BackSwitch(null, null, tp_2, sec3, 100, 100, 50);
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
		
		

		//set up the train track.

		// add a locomotive

		Movable locomotive = new Locomotive(new Track[]{head,head},40,40,10,false);
		//locomotive.toggleDirection();

		Map<Integer,modelrailway.simulation.Train> trainMap = new HashMap<Integer,modelrailway.simulation.Train>();
		Train train = new Train(new Movable[]{locomotive});
		train.toggleDirection();
		trainMap.put(0,train );
		Map<Integer,modelrailway.core.Train> orientationMap = new HashMap<Integer,modelrailway.core.Train>();

		orientationMap.put(0, new modelrailway.core.Train(0, false));

		final Simulator sim = new Simulator(head, orientationMap, trainMap);
		final TestController ctl = new TestController( trainMap,orientationMap,head, sim); //

		Integer headSection = head.getSection().getNumber();
		Integer switchSection = sw.getSection().getNumber();
		Integer swAlt = str.getSection().getNumber();
		Integer sw2Section = sw2.getSection().getNumber();
		Integer mainRoute = tp_2.getSection().getNumber();


		Route routePlan = new Route(true, sw2Section, swAlt, switchSection, headSection);
		//System.out.println("route: "+headSection+", "+switchSection+", "+swAlt+", "+sw2Section);

		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();
		
		ctl.register(new Listener(){
			public void notify(Event e){
				System.out.println("event "+e.toString());
				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
					
				  System.out.println(e);
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
			
			System.out.println("routePlan: " +Arrays.asList(new Integer[]{ sw2Section, swAlt, switchSection, headSection}).toString());
			System.out.println("routeTraveled: "+outputArray.toString());
		}
		assertTrue(outputArray.get(0) == 5);
		assertTrue(outputArray.get(1) == 6);
		assertTrue(outputArray.get(2) == 4);
		assertTrue(outputArray.get(3) == 0);
  	}
}
