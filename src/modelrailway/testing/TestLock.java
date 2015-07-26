package modelrailway.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import modelrailway.core.Event;
import modelrailway.core.Event.Listener;
import modelrailway.core.Route;
import modelrailway.core.Section;
import modelrailway.core.Train;
import modelrailway.simulation.Crossing;
import modelrailway.simulation.Straight;
import modelrailway.simulation.Track;
import modelrailway.util.ControlerCollision;

import org.junit.Test;

public class TestLock {
	
	public class MockController extends ControlerCollision{

		public MockController(Map<Integer, Train> trains,
				Map<Integer, Section> sections, Track head,
				Listener trackControler) {
			super(trains, sections, head, trackControler);
			// TODO Auto-generated constructor stub
		}
		
		public void moveSwitches(Integer trainID, Integer section, Train trainObj, Track  thisTrack){
			return; // do not adjust the switches. 
		}
		
		public Train adjustSection(Event e, boolean b){ // override the section adjustment
			//System.out.println("in adjust");
			
			Integer section = this.calculateSectionNumber((Event.SectionChanged)e);
			//System.out.println(section);
			
			if(!((Event.SectionChanged)e).getInto()){
				section = this.routes().get(0).nextSection(section);
			}
			//System.out.println(section);
			trainOrientations().get(0).setSection(section);
			return this.trainOrientations().get(0); // always return train 0
			
		}
	}
	
	public class MockListener implements Listener{
		public void notify(Event e){
			
		}
	}
	@Test public void testLock0(){
		Section straightSec = new Section(new ArrayList<Track>());
		Track straight0 = new Straight(null, null, straightSec, 100);
		straightSec.add(straight0);
		
		Section straightSec0 = new Section(new ArrayList<Track>());
		Track straight1 = new Straight(null,null,straightSec0, 100);
		straightSec0.add(straight1);
	
		Section diamondSec = new Section(new ArrayList<Track>());
		Section diamondSec2 = new Section(new ArrayList<Track>());
		Track diamondCrossing = new Crossing(null, straight0, null, straight1, diamondSec, diamondSec2, 100, 100);
		diamondSec.add(diamondCrossing);
		diamondSec2.add(diamondCrossing);
		
		diamondSec.setSectionNumber(1);
		diamondSec2.setSectionNumber(4);
		straightSec.setSectionNumber(2);
		straightSec0.setSectionNumber(3);
		
		Train tr = new Train(1, true);
		Map<Integer,Train> trains = new HashMap<Integer,Train>();
		trains.put(0,tr);
		
		Map<Integer,Section> sections = new HashMap<Integer,Section>();
		sections.put(1,diamondSec);
		sections.put(2, straightSec);
		sections.put(3, straightSec0);
		
	
		
		MockController ctl = new MockController(trains, sections, diamondCrossing, new MockListener());
		Integer num = ctl.calculateEventNumber(1);
		sections.get(1).getEntryRequests().offer(0);
		Route rt = new Route(true,1,2);
		ctl.start(0, rt);
		
		ctl.tryLocking(new Event.SectionChanged(num, true), true); // try locking
		
		System.out.println("EntryRequest 0: "+sections.get(1).getEntryRequests().toString());
		System.out.println("EntryRequest 1: "+sections.get(2).getEntryRequests().toString());
		System.out.println("EntryRequest 2: "+sections.get(3).getEntryRequests().toString());
		
		assertTrue(sections.get(1).getEntryRequests().contains(0));
		assertTrue(sections.get(2).getEntryRequests().contains(0));
		assertTrue(!sections.get(3).getEntryRequests().contains(0));	
		
	}
	
	@Test public void testLock1(){
		Section straightSec = new Section(new ArrayList<Track>());
		Track straight0 = new Straight(null, null, straightSec, 100);
		straightSec.add(straight0);
		
		Section straightSec0 = new Section(new ArrayList<Track>());
		Track straight1 = new Straight(null,null,straightSec0, 100);
		straightSec0.add(straight1);
	
		Section diamondSec = new Section(new ArrayList<Track>());
		Section diamondSec2 = new Section(new ArrayList<Track>());
		Track diamondCrossing = new Crossing(null, straight0, null, straight1, diamondSec, diamondSec2, 100, 100);
		diamondSec.add(diamondCrossing);
		diamondSec2.add(diamondCrossing);
		
		diamondSec.setSectionNumber(1);
		diamondSec2.setSectionNumber(4);
		straightSec.setSectionNumber(2);
		straightSec0.setSectionNumber(3);
		
		Train tr = new Train(4, true);
		Map<Integer,Train> trains = new HashMap<Integer,Train>();
		trains.put(0,tr);
		
		Map<Integer,Section> sections = new HashMap<Integer,Section>();
		sections.put(1,diamondSec);
		sections.put(4,diamondSec2);
		sections.put(2, straightSec);
		sections.put(3, straightSec0);
		
	
		
		MockController ctl = new MockController(trains, sections, diamondCrossing, new MockListener());
		Integer num = ctl.calculateEventNumber(4);
		sections.get(4).getEntryRequests().offer(0);
		Route rt = new Route(true,4,3);
		ctl.start(0, rt);
		
		ctl.tryLocking(new Event.SectionChanged(num,false), true); // move out of a detection section into num. 
		
		System.out.println("EntryRequest 1: "+sections.get(1).getEntryRequests().toString());
		System.out.println("EntryRequest 2: "+sections.get(2).getEntryRequests().toString());
		System.out.println("EntryRequest 3: "+sections.get(3).getEntryRequests().toString());
		System.out.println("EntryRequest 4: "+sections.get(4).getEntryRequests().toString());
		
		assertTrue(!sections.get(1).getEntryRequests().contains(0));
		assertTrue(!sections.get(2).getEntryRequests().contains(0));
		assertTrue(sections.get(4).getEntryRequests().contains(0));
		assertTrue(sections.get(3).getEntryRequests().contains(0));	
	
	}
	
	@Test public void testLock2(){
		
	}
}
