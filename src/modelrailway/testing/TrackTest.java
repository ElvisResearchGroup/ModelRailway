package modelrailway.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;

import modelrailway.simulation.Locomotive;
import modelrailway.simulation.MergeSwitch;
import modelrailway.simulation.Movable;
import modelrailway.simulation.Straight;
import modelrailway.simulation.Switch;
import modelrailway.simulation.Track;
import modelrailway.simulation.Section;

import org.junit.*;

public class TrackTest {
	/**
	 * Assert that we can build a straight ring of 3 track pieces.
	 */
	@Test public void  testTrackBuild0(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null, null, sec, 100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3,100); // produce a ring track with 3 track pieces and each track piece has length 100
		sec.add(head);
		Track tp_1 = head.getNext(false);
		Track tp_2 = tp_1.getNext(false);
		Track tp_3 = tp_2.getNext(false);

		if(head != tp_3){
			fail("wrong length");
		}
		assertTrue("check that we have different pieces in the ring",head != tp_1 && tp_1 != tp_2 && tp_2 != tp_3);

		assertTrue(tp_3.getPrevious(false) == tp_2);
		assertTrue(tp_2.getPrevious(false) == tp_1);
		assertTrue(tp_1.getPrevious(false) == head);
	}

	/**
	 * Assert that we can build a straight ring of 2 track pieces
	 */
	@Test public void testTrackBuild1(){
     	Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null, null, sec, 100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(2,100); // produce a ring track with 3 track pieces and each track piece has length 100
		sec.add(head);
		Track tp_1 = head.getNext(false);
		Track tp_2 = tp_1.getNext(false);

		if(head != tp_2){
			fail("wrong length");
		}
		assertTrue("check that we have different pieces in the ring",head != tp_1 && tp_1 != tp_2);
		assertTrue(tp_2.getPrevious(false) == tp_1);
		assertTrue(tp_1.getPrevious(false) == head);
	}
	/**
	 * Assert that we can build a straight ring of 4 track pieces.
	 */
	@Test public void testTrackBuild2(){
        Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null, null, sec, 100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(4,100); // produce a ring track with 3 track pieces and each track piece has length 100
		sec.add(head);
		Track tp_1 = head.getNext(false);
		Track tp_2 = tp_1.getNext(false);
		Track tp_3 = tp_2.getNext(false);
		Track tp_4 = tp_3.getNext(false);
		if(head != tp_4){
			fail("wrong length");
		}
		assertTrue("check that we have different pieces in the ring",head != tp_1 && tp_1 != tp_2 && tp_2 != tp_3 && tp_3 != tp_4);

		assertTrue(tp_4.getPrevious(false) == tp_3);
		assertTrue(tp_3.getPrevious(false) == tp_2);
		assertTrue(tp_2.getPrevious(false) == tp_1);
		assertTrue(tp_1.getPrevious(false) == head);
	}

	@Test public void testTrackBuild3(){
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
		Track sw = new Switch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new MergeSwitch(null, null, sw, sec3, 100, 100, 50);

		route.replace(tp_1, sw, false);
		route.replace(tp_3, sw2, false);
		//System.out.println("sw.getNext(false) : "+sw.getNext(false));
		//System.out.println("sw.getNext(true) : " + sw.getNext(true));

		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );

		route.insertBetween(sw, true, sw2, true,  str, false);
		//System.out.println("sw.getNext(false) : "+sw.getNext(false));
		//System.out.println("sw.getNext(true) : " + sw.getNext(true));

		assertTrue (head.getNext(false) == sw);
		assertTrue (sw.getNext(false) == tp_2);
		assertTrue (tp_2.getNext(false) == sw2);
		assertTrue (sw2.getNext(false) == tp_4);
	    assertTrue (tp_4 == head);

	    assertTrue("check that we have different pieces in the ring",head != sw && sw != tp_2 && tp_2 != sw2 && sw2!= tp_4);

	    assertTrue (sw.getNext(true) == str);
	    assertTrue (str.getPrevious(false) == sw);
	    assertTrue (str.getNext(false) == sw2);
	    assertTrue (sw2.getPrevious(true) == str);
	}

	@Test public void testTrackBuild4(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(5, 100);
		sec.add(head);
		Track tp_1 = head.getNext(false);
		Track tp_2 = tp_1.getNext(false);
		Track tp_3 = tp_2.getNext(false);
		Track tp_4 = tp_3.getNext(false);
		Track tp_5 = tp_4.getNext(false);

		Section sec2 = new Section(new ArrayList<Track>());
		Track sw = new Switch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new MergeSwitch(null, null, sw, sec3, 100, 100, 50);

		route.replace(tp_1, sw, false);
		route.replace(tp_3, sw2, false);
		//System.out.println("sw.getNext(false) : "+sw.getNext(false));
		//System.out.println("sw.getNext(true) : " + sw.getNext(true));

		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );

		route.insertBetween(sw, true, sw2, true,  str, false);
	//	System.out.println("sw.getNext(false) : "+sw.getNext(false));
	//	System.out.println("sw.getNext(true) : " + sw.getNext(true));

		assertTrue (head.getNext(false) == sw);
		assertTrue (sw.getNext(false) == tp_2);
		assertTrue (tp_2.getNext(false) == sw2);
		assertTrue (sw2.getNext(false) == tp_4);
		assertTrue (tp_4.getNext(false) == tp_5);
	    assertTrue (tp_5 == head);

	    assertTrue("check that we have different pieces in the ring",head != sw && sw != tp_2 && tp_2 != sw2 && sw2!= tp_4);

	    assertTrue (sw.getNext(true) == str);
	    assertTrue (str.getPrevious(false) == sw);
	    assertTrue (str.getNext(false) == sw2);
	    assertTrue (sw2.getPrevious(true) == str);
	}
	/**
	 * Test a single locomotive running around a ring track. The locomotive is smaller than a section.
	 */
	@Test public void trackTestLocoRun0(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);

		Movable locomotive = new Locomotive(new Track[]{head}, 50, 50, 50, false);
		sec.addMovable(locomotive);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);

		locomotive.move(); // locomotive has not started yet.
		assertTrue(locomotive.getDistance() == 50);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);

		locomotive.start();

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 50);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false).getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));

		locomotive.move();
		assertTrue(locomotive.getDistance() == 50);
		assertTrue(locomotive.getFront() == head.getNext(false).getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false).getNext(false));

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head.getNext(false).getNext(false));
	}
	/**
	 * Test a locomotive that is longer than 1 piece of track.
	 */
	@Test public void trackTestLocoRun1(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);

		Movable locomotive = new Locomotive(new Track[]{head.getNext(false),head}, 90, 110, 50, false);
		sec.addMovable(locomotive);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);
		assertTrue(locomotive.getBackDistance() == 20);

		locomotive.start();

		locomotive.move();
		assertTrue(locomotive.getFront() == head.getNext(false).getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getBackDistance() == 70);

		locomotive.move();
		assertTrue(locomotive.getFront() == head.getNext(false).getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getDistance() == 90);
		assertTrue(locomotive.getBackDistance() == 20);

		locomotive.move();
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head.getNext(false).getNext(false));
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getBackDistance() == 70);

		locomotive.move();
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head.getNext(false).getNext(false));
		assertTrue(locomotive.getDistance() == 90);
		assertTrue(locomotive.getBackDistance() == 20);

		locomotive.move();
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getBackDistance() == 70);

		locomotive.move();
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);
		assertTrue(locomotive.getBackDistance() == 20);
		assertTrue(locomotive.getDistance() == 90);
	}


	@Test public void trackTestLocoRun2(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);

		Section sec2 = new Section(new ArrayList<Track>());
		Track sw = new Switch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new MergeSwitch(null, null, sw, sec3, 100, 100, 50);

		route.replace(head.getNext(false), sw , false);
		route.replace(head.getNext(false).getNext(false), sw2, false);

		Movable locomotive = new Locomotive(new Track[]{head}, 50, 50, 50, false);
		sec.addMovable(locomotive);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);

		locomotive.move(); // locomotive has not started yet.
		assertTrue(locomotive.getDistance() == 50);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);

		locomotive.start();

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 50);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false).getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));

		locomotive.move();
		assertTrue(locomotive.getDistance() == 50);
		assertTrue(locomotive.getFront() == head.getNext(false).getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false).getNext(false));

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head.getNext(false).getNext(false));
	}

	@Test public void trackTestLocoRun3(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);

		Section sec2 = new Section(new ArrayList<Track>());
		Track sw = new Switch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		route.replace(head.getNext(false), sw , false);

		((Switch) sw).toggle(); // set the switch so that we move along the alternate direction

		Movable locomotive = new Locomotive(new Track[]{head}, 40, 50, 60, false);
		sec.addMovable(locomotive);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move(); // locomotive has not started yet.
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);

		locomotive.start();

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == true);
		assertTrue(head.getNext(false).getNext(locomotive.getOnAlt()) == null);

	}

	@Test public void trackTestLoco4(){
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
		Track sw = new Switch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new MergeSwitch(null, null, sw, sec3, 100, 100, 50);

		route.replace(tp_1, sw, false);
		route.replace(tp_3, sw2, false);

		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );

		route.insertBetween(sw, true, sw2, true,  str, false);

		((Switch) sw).toggle(); // set the switch so that we move along the alternate direction
		((MergeSwitch) sw2).toggle();

		Movable locomotive = new Locomotive(new Track[]{head}, 40, 50, 60, false);
		sec.addMovable(locomotive);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move(); // locomotive has not started yet.
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);

		locomotive.start();

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == true);
		assertTrue(head.getNext(false).getNext(locomotive.getOnAlt()) == str);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 20);
		assertTrue(locomotive.getFront() == str);
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 80);
		assertTrue(locomotive.getFront() == str);
		assertTrue(locomotive.getBack() == str);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getFront() == sw2);
		assertTrue(locomotive.getBack() == str);
		assertTrue(locomotive.getOnAlt() == true);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == tp_4);
		assertTrue(locomotive.getBack() == sw2);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == tp_4);
		assertTrue(locomotive.getBack() == tp_4);
		assertTrue(locomotive.getOnAlt() == false);


		locomotive.toggleDirection();
		locomotive.move();
		//System.out.println("distance: "+ locomotive.getDistance());
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == tp_4);
		//System.out.println(locomotive.getBack());
		assertTrue(locomotive.getBack() == sw2);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 40);
		//System.out.println("Front: "+locomotive.getFront());
		assertTrue(locomotive.getFront() == sw2);
		//System.out.println("Back: "+ locomotive.getBack());
		assertTrue(locomotive.getBack() == str);
		assertTrue(locomotive.getOnAlt() == true);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 80);
		assertTrue(locomotive.getFront() == str);
		assertTrue(locomotive.getBack() == str);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 20);
		assertTrue(locomotive.getFront() == str);
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == true);
		assertTrue(head.getNext(false).getNext(locomotive.getOnAlt()) == str);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);
	}

	@Test public void trackTestLoco5(){
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
		Track sw = new Switch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new MergeSwitch(null, null, sw, sec3, 100, 100, 50);

		route.replace(tp_1, sw, false);
		route.replace(tp_3, sw2, false);

		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );

		route.insertBetween(sw, true, sw2, true,  str, false);

		((Switch) sw).toggle(); // set the switch so that we move along the alternate direction
		((MergeSwitch) sw2).toggle();

		Movable locomotive = new Locomotive(new Track[]{head}, 40, 50, 60, false);
		sec.addMovable(locomotive);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move(); // locomotive has not started yet.
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);

		locomotive.start();

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == true);
		assertTrue(head.getNext(false).getNext(locomotive.getOnAlt()) == str);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 20);
		assertTrue(locomotive.getFront() == str);
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 80);
		assertTrue(locomotive.getFront() == str);
		assertTrue(locomotive.getBack() == str);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getFront() == sw2);
		assertTrue(locomotive.getBack() == str);
		assertTrue(locomotive.getOnAlt() == true);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == tp_4);
		assertTrue(locomotive.getBack() == sw2);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == tp_4);
		assertTrue(locomotive.getBack() == tp_4);
		assertTrue(locomotive.getOnAlt() == false);


		locomotive.toggleDirection();
		locomotive.move();
		//System.out.println("distance: "+ locomotive.getDistance());
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == tp_4);
		//System.out.println(locomotive.getBack());
		assertTrue(locomotive.getBack() == sw2);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 40);
		//System.out.println("Front: "+locomotive.getFront());
		assertTrue(locomotive.getFront() == sw2);
		//System.out.println("Back: "+ locomotive.getBack());
		assertTrue(locomotive.getBack() == str);
		assertTrue(locomotive.getOnAlt() == true);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 80);
		assertTrue(locomotive.getFront() == str);
		assertTrue(locomotive.getBack() == str);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 20);
		assertTrue(locomotive.getFront() == str);
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == true);
		assertTrue(head.getNext(false).getNext(locomotive.getOnAlt()) == str);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);
	}

	@Test public void trackTestLoco6(){
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
		Track sw = new Switch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new MergeSwitch(null, null, sw, sec3, 100, 100, 50);

		route.replace(tp_1, sw, false);
		route.replace(tp_3, sw2, false);

		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );

		route.insertBetween(sw, true, sw2, true,  str, false);

		((Switch) sw).toggle(); // set the switch so that we move along the alternate direction
		((MergeSwitch) sw2).toggle();

		Movable locomotive = new Locomotive(new Track[]{head}, 40, 50, 60, false);
		sec.addMovable(locomotive);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move(); // locomotive has not started yet.
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);

		locomotive.start();

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == true);
		assertTrue(head.getNext(false).getNext(locomotive.getOnAlt()) == str);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 20);
		assertTrue(locomotive.getFront() == str);
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 80);
		assertTrue(locomotive.getFront() == str);
		assertTrue(locomotive.getBack() == str);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getFront() == sw2);
		assertTrue(locomotive.getBack() == str);
		assertTrue(locomotive.getOnAlt() == true);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == tp_4);
		assertTrue(locomotive.getBack() == sw2);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == tp_4);
		assertTrue(locomotive.getBack() == tp_4);
		assertTrue(locomotive.getOnAlt() == false);


		locomotive.toggleDirection();
		((Switch) sw).toggle();
		((MergeSwitch) sw2).toggle();

		locomotive.move();
		//System.out.println("distance: "+ locomotive.getDistance());
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == tp_4);
		//System.out.println(locomotive.getBack());
		assertTrue(locomotive.getBack() == sw2);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 40);
		//System.out.println("Front: "+locomotive.getFront());
		assertTrue(locomotive.getFront() == sw2);
		//System.out.println("Back: "+ locomotive.getBack());
		assertTrue(locomotive.getBack() == tp_2);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 80);
		assertTrue(locomotive.getFront() == tp_2);
		assertTrue(locomotive.getBack() == tp_2);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 20);
		assertTrue(locomotive.getFront() == tp_2);
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == false);
		assertTrue(head.getNext(false).getNext(locomotive.getOnAlt()) == tp_2);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 0);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);
	}
}