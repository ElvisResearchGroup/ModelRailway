package modelrailway.testing;

import static org.junit.Assert.*;

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
import modelrailway.util.MovementController;

import java.util.HashMap;

import org.junit.*;

public class ModelTrackTest {
	/*
	 * Test the model railway with a ring of straight track segments.
	 */
	@Test public void modelTrackTest0(){
		Section.resetCounter();
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(8, 100);
		sec.add(head);
		Track tp_1 = head.getNext(false);
		tp_1.getSection().setSectionNumber(1);

		Track tp_2 = tp_1.getNext(false);
		tp_2.getSection().setSectionNumber(2);

		Track tp_3 = tp_2.getNext(false);
		tp_3.getSection().setSectionNumber(3);

		Track tp_4 = tp_3.getNext(false);
		tp_4.getSection().setSectionNumber(4);

		Track tp_5 = tp_4.getNext(false);
		tp_5.getSection().setSectionNumber(5);

		Track tp_6 = tp_5.getNext(false);
		tp_6.getSection().setSectionNumber(6);

		Track tp_7 = tp_6.getNext(false);
		tp_7.getSection().setSectionNumber(7);

		Track tp_8 = tp_7.getNext(false);
		tp_8.getSection().setSectionNumber(8);

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

		String portname = "port";
		Event.Listener sim = null;
		try {
			sim = new  modelrailway.ModelRailway(portname, 0);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail("initialization error: ");
		}
		final MovementController ctl = new MovementController(orientationMap,route.getSectionNumberMap(),head, sim); //


		Integer start = tp_1.getSection().getNumber();
		Integer s2 = tp_2.getSection().getNumber();
		Integer s3 = tp_3.getSection().getNumber();
		Integer s4 = tp_4.getSection().getNumber();
		Integer s5 = tp_5.getSection().getNumber();
		Integer s6 = tp_6.getSection().getNumber();
		Integer s7 = tp_7.getSection().getNumber();
		Integer s8 = tp_8.getSection().getNumber();
		

		Route routePlan = new Route(true,  start, s2,s3,s4,s5,s6,s7,s8);
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

	}
}
