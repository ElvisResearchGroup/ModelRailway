package modelrailway.testing;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import modelrailway.core.Event;
import modelrailway.core.Section;
import modelrailway.core.Event.Listener;
import modelrailway.core.Event.SectionChanged;
import modelrailway.simulation.Crossing;
import modelrailway.simulation.Locomotive;
import modelrailway.simulation.BackSwitch;
import modelrailway.simulation.Movable;
import modelrailway.simulation.Movable.GenerateID;
import modelrailway.simulation.RollingStock;
import modelrailway.simulation.Simulator;
import modelrailway.simulation.Straight;
import modelrailway.simulation.ForwardSwitch;
import modelrailway.simulation.Track;
import modelrailway.simulation.Train;

import org.junit.*;

public class TrackTest {
	/**
	 * Assert that we can build a straight ring of 3 track pieces.
	 */
	@Test public void  testTrackBuild0(){
		Section.resetCounter();
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
		Section.resetCounter();
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
		Section.resetCounter();
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
	/**
	 * Create a ring track with an alternate branch to the main ring which leaves and rejoins the main ring.
	 *
	 */
	@Test public void testTrackBuild3(){
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

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new BackSwitch(null, null, sw, sec3, 100, 100, 50);

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
		Section.resetCounter();
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
		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new BackSwitch(null, null, sw, sec3, 100, 100, 50);

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
	 * Test adding a diamond crossing;
	 */
	@Test public void testLocoDiamond0(){
		Section.resetCounter();
		Section sec = new Section (new ArrayList<Track>());
		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);

		//create a ring track with 3 track sections.
		Track head1 = route.ringTrack(3,100);
		Track firstr1 = head1.getNext(false);
		Track firstr2 = head1.getNext(false).getNext(false);

		Section sec2 = new Section(new ArrayList<Track>());
		Straight st2 = new Straight(null,null,sec2,100);
		Straight.StraightRing route2 = new Straight.StraightRing(st2);

		// create another ring track with 3 track sections.
		Track head2 = route2.ringTrack(3, 100);
		Track secr1 = head2.getNext(false);
		Track secr2 = head2.getNext(false).getNext(false);


		Section crMain = new Section(new ArrayList<Track>());
		Section crAlt = new Section(new ArrayList<Track>());
		Crossing cr = new Crossing(null,null,null,null,crMain,crAlt,100,100);


		// insert the diamond crossing.
		route.insertBetween(head1, false, cr, false);
		route2.insertBetween(head2, false, cr, true);

		// create a locomotive

		Movable loco = new Locomotive(new Track[]{head1,head1}, 50, 50, 40, false); // tracks, dist, length, maxspeed, onAlt

		loco.setID(0);
		loco.start();

		loco.move();
		assertTrue(loco.getDistance() == 90);
		assertTrue(loco.getFront() == head1);
		assertTrue(loco.getBack() == head1);

		loco.move();
		assertTrue(loco.getDistance() == 30);
		assertTrue(loco.getFront() == cr);
		assertTrue(loco.getBack() == head1);

		loco.move();
		assertTrue(loco.getDistance() == 70);
		assertTrue(loco.getFront() == cr);
		assertTrue(loco.getBack() == cr);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 10);
		assertTrue(loco.getFront() == firstr1);
		assertTrue(loco.getBack() == cr);
		assertTrue(loco.getOnAlt() ==  false);

		loco.move();
		assertTrue(loco.getDistance() == 50);
		assertTrue(loco.getFront() == firstr1);
		assertTrue(loco.getBack() == firstr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 90);
		assertTrue(loco.getFront() == firstr1);
		assertTrue(loco.getBack() == firstr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 30);
		//System.out.println("getFront(): "+loco.getFront()+" firstr2: "+firstr2+" firstr1: "+firstr1);
		assertTrue(loco.getFront() == firstr2);
		assertTrue(loco.getBack() == firstr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 70);
		assertTrue(loco.getFront() == firstr2);
		assertTrue(loco.getBack() == firstr2);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 10);
		assertTrue(loco.getFront() == head1);
		assertTrue(loco.getBack() == firstr2);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 50);
		assertTrue(loco.getFront() == head1);
		assertTrue(loco.getBack() == head1);
		assertTrue(loco.getOnAlt() == false);

	}

	@Test public void testLocoDiamond1(){
		Section.resetCounter();
		Section sec = new Section (new ArrayList<Track>());
		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);

		//create a ring track with 3 track sections.
		Track head1 = route.ringTrack(3,100);
		Track firstr1 = head1.getNext(false);
		Track firstr2 = head1.getNext(false).getNext(false);

		Section sec2 = new Section(new ArrayList<Track>());
		Straight st2 = new Straight(null,null,sec2,100);
		Straight.StraightRing route2 = new Straight.StraightRing(st2);

		// create another ring track with 3 track sections.
		Track head2 = route2.ringTrack(3, 100);
		Track secr1 = head2.getNext(false);
		Track secr2 = head2.getNext(false).getNext(false);


		Section crMain = new Section(new ArrayList<Track>());
		Section crAlt = new Section(new ArrayList<Track>());
		Crossing cr = new Crossing(null,null,null,null,crMain,crAlt,100,100);


		// insert the diamond crossing.
		route.insertBetween(head1, false, cr, false);
		route2.insertBetween(head2, false, cr, true);

		//System.out.println("head2: "+head2 +" cr: "+cr+" cr.getNext(true): "+cr.getNext(true) );

		// create a locomotive

		Movable loco = new Locomotive(new Track[]{head2,head2}, 50, 50, 40, false); // tracks, dist, length, maxspeed, onAlt
		loco.setID(0);
		loco.start();

		loco.move();
		assertTrue(loco.getDistance() == 90);
		assertTrue(loco.getFront() == head2);
		assertTrue(loco.getBack() == head2);

		loco.move();
		assertTrue(loco.getDistance() == 30);
		assertTrue(loco.getFront() == cr);
		assertTrue(loco.getBack() == head2);

		loco.move();
		assertTrue(loco.getDistance() == 70);
		assertTrue(loco.getFront() == cr);
		assertTrue(loco.getBack() == cr);
		assertTrue(loco.getOnAlt() == true);

		loco.move();
		assertTrue(loco.getDistance() == 10);
	//	System.out.println("loco.getFront():" + loco.getFront());
		assertTrue(loco.getFront() == secr1);
		assertTrue(loco.getBack() == cr);
		assertTrue(loco.getOnAlt() ==  false);

		loco.move();
		assertTrue(loco.getDistance() == 50);
		assertTrue(loco.getFront() == secr1);
		assertTrue(loco.getBack() == secr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 90);
		assertTrue(loco.getFront() == secr1);
		assertTrue(loco.getBack() == secr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 30);
		//System.out.println("getFront(): "+loco.getFront()+" firstr2: "+firstr2+" firstr1: "+firstr1);
		assertTrue(loco.getFront() == secr2);
		assertTrue(loco.getBack() == secr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 70);
		assertTrue(loco.getFront() == secr2);
		assertTrue(loco.getBack() == secr2);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 10);
		assertTrue(loco.getFront() == head2);
		assertTrue(loco.getBack() == secr2);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 50);
		assertTrue(loco.getFront() == head2);
		assertTrue(loco.getBack() == head2);
		assertTrue(loco.getOnAlt() == false);

	}

	@Test public void testLocoDiamond2(){
		Section.resetCounter();
		Section sec = new Section (new ArrayList<Track>());
		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);

		//create a ring track with 3 track sections.
		Track head1 = route.ringTrack(3,100);
		Track firstr1 = head1.getNext(false);
		Track firstr2 = head1.getNext(false).getNext(false);

		Section sec2 = new Section(new ArrayList<Track>());
		Straight st2 = new Straight(null,null,sec2,100);
		Straight.StraightRing route2 = new Straight.StraightRing(st2);

		// create another ring track with 3 track sections.
		Track head2 = route2.ringTrack(3, 100);
		Track secr1 = head2.getNext(false);
		Track secr2 = head2.getNext(false).getNext(false);


		Section crMain = new Section(new ArrayList<Track>());
		Section crAlt = new Section(new ArrayList<Track>());
		Crossing cr = new Crossing(null,null,null,null,crMain,crAlt,100,100);


		// insert the diamond crossing.
		route.insertAcross(head1, false, cr, false);
		route2.insertAcross(head2, false, cr, true);

		//System.out.println("head2: "+head2 +" cr: "+cr+" cr.getNext(true): "+cr.getNext(true) );

		// create a locomotive

		Movable loco = new Locomotive(new Track[]{head2,head2}, 50, 50, 40, false); // tracks, dist, length, maxspeed, onAlt
		loco.setID(0);
		loco.start();

		loco.move();
		assertTrue(loco.getDistance() == 90);
		assertTrue(loco.getFront() == head2);
		assertTrue(loco.getBack() == head2);

		loco.move();
		assertTrue(loco.getDistance() == 30);
		assertTrue(loco.getFront() == cr);
		assertTrue(loco.getBack() == head2);

		loco.move();
		assertTrue(loco.getDistance() == 70);
		assertTrue(loco.getFront() == cr);
		assertTrue(loco.getBack() == cr);
		assertTrue(loco.getOnAlt() == true);

		loco.move();
		assertTrue(loco.getDistance() == 10);
	//	System.out.println("loco.getFront():" + loco.getFront());
		assertTrue(loco.getFront() == firstr1);
		assertTrue(loco.getBack() == cr);
		assertTrue(loco.getOnAlt() ==  false);

		loco.move();
		assertTrue(loco.getDistance() == 50);
		assertTrue(loco.getFront() == firstr1);
		assertTrue(loco.getBack() == firstr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 90);
		assertTrue(loco.getFront() == firstr1);
		assertTrue(loco.getBack() == firstr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 30);
		//System.out.println("getFront(): "+loco.getFront()+" firstr2: "+firstr2+" firstr1: "+firstr1);
		assertTrue(loco.getFront() == firstr2);
		assertTrue(loco.getBack() == firstr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 70);
		assertTrue(loco.getFront() == firstr2);
		assertTrue(loco.getBack() == firstr2);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 10);
		assertTrue(loco.getFront() == head1);
		assertTrue(loco.getBack() == firstr2);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 50);
		assertTrue(loco.getFront() == head1);
		assertTrue(loco.getBack() == head1);
		assertTrue(loco.getOnAlt() == false);
	}

	@Test public void testLocoDiamond3(){
		Section.resetCounter();
		Section sec = new Section (new ArrayList<Track>());
		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);

		//create a ring track with 3 track sections.
		Track head1 = route.ringTrack(3,100);
		Track firstr1 = head1.getNext(false);
		Track firstr2 = head1.getNext(false).getNext(false);

		Section sec2 = new Section(new ArrayList<Track>());
		Straight st2 = new Straight(null,null,sec2,100);
		Straight.StraightRing route2 = new Straight.StraightRing(st2);

		// create another ring track with 3 track sections.
		Track head2 = route2.ringTrack(3, 100);
		Track secr1 = head2.getNext(false);
		Track secr2 = head2.getNext(false).getNext(false);


		Section crMain = new Section(new ArrayList<Track>());
		Section crAlt = new Section(new ArrayList<Track>());
		Crossing cr = new Crossing(null,null,null,null,crMain,crAlt,100,100);


		// insert the diamond crossing.
		route.insertAcross(head1, false, cr, false);
		route2.insertAcross(head2, false, cr, true);

		//System.out.println("head2: "+head2 +" cr: "+cr+" cr.getNext(true): "+cr.getNext(true) );

		// create a locomotive

		Movable loco = new Locomotive(new Track[]{head1,head1}, 50, 50, 40, false); // tracks, dist, length, maxspeed, onAlt
		loco.setID(0);
		loco.start();

		loco.move();
		assertTrue(loco.getDistance() == 90);
		assertTrue(loco.getFront() == head1);
		assertTrue(loco.getBack() == head1);

		loco.move();
		assertTrue(loco.getDistance() == 30);
		assertTrue(loco.getFront() == cr);
		assertTrue(loco.getBack() == head1);

		loco.move();
		assertTrue(loco.getDistance() == 70);
		assertTrue(loco.getFront() == cr);
		assertTrue(loco.getBack() == cr);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 10);
	//	System.out.println("loco.getFront():" + loco.getFront());
		assertTrue(loco.getFront() == secr1);
		assertTrue(loco.getBack() == cr);
		assertTrue(loco.getOnAlt() ==  false);

		loco.move();
		assertTrue(loco.getDistance() == 50);
		assertTrue(loco.getFront() == secr1);
		assertTrue(loco.getBack() == secr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 90);
		assertTrue(loco.getFront() == secr1);
		assertTrue(loco.getBack() == secr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 30);
		//System.out.println("getFront(): "+loco.getFront()+" firstr2: "+firstr2+" firstr1: "+firstr1);
		assertTrue(loco.getFront() == secr2);
		assertTrue(loco.getBack() == secr1);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 70);
		assertTrue(loco.getFront() == secr2);
		assertTrue(loco.getBack() == secr2);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 10);
		assertTrue(loco.getFront() == head2);
		assertTrue(loco.getBack() == secr2);
		assertTrue(loco.getOnAlt() == false);

		loco.move();
		assertTrue(loco.getDistance() == 50);
		assertTrue(loco.getFront() == head2);
		assertTrue(loco.getBack() == head2);
		assertTrue(loco.getOnAlt() == false);

	}
	/**
	 * Test sectioning on a basic ring track
	 */
	@Test public void testLocoSectioning0(){
		Section.resetCounter();
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

		Movable loco = new Locomotive(new Track[]{head}, 50, 40, 40, false);
		//loco.setID(0);
		assertTrue(head.getSection().containsMovable(loco.getID()));
		assertFalse(tp_1.getSection().containsMovable(loco.getID()));
		assertFalse(tp_2.getSection().containsMovable(loco.getID()));

		loco.start();
		loco.move(); // 90
		loco.move(); // 30
		assertTrue(loco.getDistance() == 30);
		assertTrue(loco.getFront() == tp_1);
		assertTrue(loco.getBack() == head);
		assertTrue(head.getSection().containsMovable(loco.getID()));
		assertTrue(tp_1.getSection().containsMovable(loco.getID()));
		//System.out.println("tp_2: "+tp_2);
		assertFalse(tp_2.getSection().containsMovable(loco.getID()));

		loco.move();// 70
		assertFalse(head.getSection().containsMovable(loco.getID()));
		assertTrue(tp_1.getSection().containsMovable(loco.getID()));
		assertFalse(tp_2.getSection().containsMovable(loco.getID()));

		loco.move(); // 10
		assertFalse(head.getSection().containsMovable(loco.getID()));
		assertTrue(tp_1.getSection().containsMovable(loco.getID()));
		assertTrue(tp_2.getSection().containsMovable(loco.getID()));

		loco.move();//50
		assertFalse(head.getSection().containsMovable(loco.getID()));
		assertFalse(tp_1.getSection().containsMovable(loco.getID()));
		assertTrue(tp_2.getSection().containsMovable(loco.getID()));
	}

    /**
    * The the test puts one train on the track with the simulator and no controller.
    */
    @Test public void testSimulator0(){
    	Section.resetCounter();
		Section section = new Section(new ArrayList<Track>());
		Track track = new Straight(null, null, section, 100);
		Straight.StraightRing ring  = new Straight.StraightRing(track);
		track = ring.ringTrack(6, 100); // produce a ring.

		track.getSection().setSectionNumber(1);
		Track t1 = track.getNext(false);
		t1.getSection().setSectionNumber(2);
		Track t2 = t1.getNext(false);
		t2.getSection().setSectionNumber(3);
		Track t3 = t2.getNext(false);
		t3.getSection().setSectionNumber(4);
		Track t4 = t3.getNext(false);
		t4.getSection().setSectionNumber(5);
		Track t5 = t4.getNext(false);
		t5.getSection().setSectionNumber(6);

		ring.recalculateSections();
		ring.getSectionNumberMap();


		Locomotive loco = new Locomotive(new Track[]{track,track} ,40,40,10, false);

		Movable.GenerateID.generateID(loco);
		Train tr = new Train(new Movable[]{loco});
		tr.toggleDirection(); // make simulation train start by going backwards.
		Map<Integer,Train> map = new HashMap<Integer,Train>();
		map.put(0, tr);
		tr.setID(0);
		Map<Integer,modelrailway.core.Train>  map2 = new HashMap<Integer,modelrailway.core.Train>();
		map2.put(0, new modelrailway.core.Train(1,false));

		final class Pair<X,Y> {
			public Pair(X one , Y two){
				fst = one;
				snd = two;
			}
			final X fst;
			final Y snd;
			public String toString(){
				return "\n<"+fst.toString() + "," + snd.toString()+">";
			}
		}
		final Simulator sim = new Simulator(track, map2 , map);
		final ArrayList<Pair<Integer,Boolean>> sectionsList = new ArrayList<Pair<Integer,Boolean>>();
		//sim.start(0, null);
		final Thread th = Thread.currentThread();
		Listener lis = new Listener(){

			public void notify(Event e){
				if(e instanceof Event.SectionChanged){
					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					System.out.println("i: "+i);
					if (i  == 1 &&
					   ((Event.SectionChanged) e).getInto() == true ){

						sim.notify(new Event.EmergencyStop(0));
						sim.stop();
						Pair<Integer,Boolean> pair1 = new Pair<Integer,Boolean>(i,
								                                                ((Event.SectionChanged) e).getInto());
						sectionsList.add(pair1);
						th.interrupt();
					}
					else {
						Integer sec = i;
						if(((Event.SectionChanged) e).getInto() == false){
						  sec = i-1;
						  if(sec == 0) sec = 6;
						}
						Pair<Integer,Boolean> pair1 = new Pair<Integer,Boolean>( sec, true);
					     sectionsList.add(pair1);
					}

				}

			}
		};

		sim.register(lis);
		sim.notify(new Event.SpeedChanged(0, Integer.MAX_VALUE));
		try{
		   Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println(sectionsList.toString());
			assertTrue(sectionsList.size() == 6);
			assertTrue(sectionsList.get(0).fst == 6);
			assertTrue(sectionsList.get(1).fst == 5);
			assertTrue(sectionsList.get(2).fst == 4);
			assertTrue(sectionsList.get(3).fst == 3);
			assertTrue(sectionsList.get(4).fst == 2);
			assertTrue(sectionsList.get(5).fst == 1);
		}
	}

    /**
     * The test puts two trains on the track with the simulator and no controller.
     */
     @Test public void testSimulator1(){
    	Section.resetCounter(); // reset the counter for labeling track segments.
 		Section section = new Section(new ArrayList<Track>());
 		Track track = new Straight(null, null, section, 100);
 		Straight.StraightRing ring  = new Straight.StraightRing(track);
 		track = ring.ringTrack(6, 100); // produce a ring.
 		Locomotive loco1 = new Locomotive(new Track[]{track,track} ,40,40,10, false);
 		Movable.GenerateID.generateID(loco1);
 		//create two trains
 		final Train tr = new Train(new Movable[]{loco1});
 		Locomotive loco2 =new Locomotive(new Track[]{track.getNext(false),track.getNext(false)},40,40,10,false );
 		Movable.GenerateID.generateID(loco2);
 		final Train tr2 = new Train (new Movable[]{loco2});

 		//put trains into both maps.
 		Map<Integer,Train> map = new HashMap<Integer,Train>();
 		map.put(0, tr);
 		tr.setID(0);
 		map.put(1, tr2);
 		tr2.setID(1);
 		Map<Integer,modelrailway.core.Train>  map2 = new HashMap<Integer,modelrailway.core.Train>();
 		map2.put(0, new modelrailway.core.Train(0,true));
 		map2.put(1, new modelrailway.core.Train(1, true));

 		final class Pair<X,Y> {
 			public Pair(X one , Y two){
 				fst = one;
 				snd = two;
 			}
 			final X fst;
 			final Y snd;
 			public String toString(){
 				return "\n<"+fst.toString() + "," + snd.toString()+">";
 			}
 		}
 		final Simulator sim = new Simulator(track, map2 , map);
 		final ArrayList<Pair<Integer,Boolean>> trSectionsList = new ArrayList<Pair<Integer,Boolean>>();
 		final ArrayList<Pair<Integer,Boolean>> tr2SectionsList = new ArrayList<Pair<Integer,Boolean>>();
 		final Thread th = Thread.currentThread();

 		Listener lis = new Listener(){
 			public void notify(Event e){
 				if(e instanceof Event.SectionChanged){
 					if (((Event.SectionChanged) e).getSection() == 0 &&
 					   ((Event.SectionChanged) e).getInto() == true ){

 						sim.notify(new Event.EmergencyStop(0));
 						sim.notify(new Event.EmergencyStop(1));
 						sim.stop();
 						Pair<Integer,Boolean> pair1 = new Pair<Integer,Boolean>(((Event.SectionChanged) e).getSection(),
 								                                                ((Event.SectionChanged) e).getInto());
 						if(tr.getFront().getSection().getNumber() == ((SectionChanged) e).getSection()){
 						    trSectionsList.add(pair1);
 						}
 						else{
 							tr2SectionsList.add(pair1);
 						}
 						th.interrupt();
 					}
 					else if (((SectionChanged) e).getInto() == true){
 						Pair<Integer,Boolean> pair1 = new Pair<Integer,Boolean>(((Event.SectionChanged) e).getSection(),
 					                                                            ((Event.SectionChanged) e).getInto());
 						if(((Event.SectionChanged)e).getInto() == true){
 							if(tr.getFront().getSection().getNumber() == ((SectionChanged) e).getSection()){
 	 						    trSectionsList.add(pair1);
 	 						}
 	 						else{
 	 							tr2SectionsList.add(pair1);
 	 						}
 						}
 					}
 				}
 			}
 		};

 		sim.register(lis);
 		sim.notify(new Event.SpeedChanged(1, Integer.MAX_VALUE)); // start front train.
 		sim.notify(new Event.SpeedChanged(0, Integer.MAX_VALUE)); // start back train.

 		try{
 		   Thread.currentThread().join();
 		}catch(InterruptedException e){
 			//System.out.println(trSectionsList);
 			//System.out.println(tr2SectionsList);
 		    assertTrue(tr2SectionsList.get(0).fst == 3);
 		    assertTrue(tr2SectionsList.get(1).fst == 2);
 		    assertTrue(tr2SectionsList.get(2).fst == 1);
 		    assertTrue(tr2SectionsList.get(3).fst == 0);

 		    if(trSectionsList.size() >= 1){
 		    	assertTrue(trSectionsList.get(0).fst == 4);
 		    }
 		    if(trSectionsList.size() >= 2){
 		    	assertTrue(trSectionsList.get(1).fst == 3);

 		    }
 		    if(trSectionsList.size() >= 3){
 		    	assertTrue(trSectionsList.get(2).fst == 2);
 		    }
 		    if(trSectionsList.size() >= 4){
 		    	assertTrue(trSectionsList.get(3).fst == 1);
 		    }
 		}
 	}
    @Test public void testSimulator2(){
    	Section.resetCounter(); // reset the counter for labeling track segments.
 		Section section = new Section(new ArrayList<Track>());
 		Track track = new Straight(null, null, section, 100);
 		Straight.StraightRing ring  = new Straight.StraightRing(track);
 		track = ring.ringTrack(5, 100); // produce a ring.




    }
}