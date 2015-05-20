package modelrailway.testing;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import modelrailway.core.Section;
import modelrailway.simulation.BackSwitch;
import modelrailway.simulation.ForwardSwitch;
import modelrailway.simulation.Locomotive;
import modelrailway.simulation.Movable;
import modelrailway.simulation.RollingStock;
import modelrailway.simulation.Straight;
import modelrailway.simulation.Track;
import modelrailway.simulation.Train;

import org.junit.Test;

public class TrainTest {
	/**
	 * Test a single locomotive running around a ring track. The locomotive is smaller than a section.
	 */
	@Test public void trackTestLoco0(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);
		


		Movable locomotive = new Locomotive(new Track[]{head}, 50, 50, 50, false);
		locomotive.setID(0);
		sec.addMovable(locomotive.getID());
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
	@Test public void trackTestLoco1(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);

		Movable locomotive = new Locomotive(new Track[]{head.getNext(false),head}, 90, 110, 50, false);
		locomotive.setID(0);
		sec.addMovable(locomotive.getID());
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

	/**
	 * Test a locomotive that can navigate  a track with a switch without going through the switch.
	 */
	@Test public void trackTestLoco2(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);

		Section sec2 = new Section(new ArrayList<Track>());
		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new BackSwitch(null, null, sw, sec3, 100, 100, 50);

		route.replace(head.getNext(false), sw , false);
		route.replace(head.getNext(false).getNext(false), sw2, false);

		Movable locomotive = new Locomotive(new Track[]{head}, 50, 50, 50, false);
		locomotive.setID(0);
		sec.addMovable(locomotive.getID());
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
	 * test that the locomotive can take an alternate route on a switch.
	 */
	@Test public void trackTestLoco3(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);


		Section sec2 = new Section(new ArrayList<Track>());
		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		route.replace(head.getNext(false), sw , false);

		((ForwardSwitch) sw).toggle(); // set the switch so that we move along the alternate direction

		Movable locomotive = new Locomotive(new Track[]{head}, 40, 40, 40, false);
		locomotive.setID(0);
		sec.addMovable(locomotive.getID());
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);
		assertTrue(locomotive.getOnAlt() == false);

		locomotive.move(); // locomotive has not started yet.
		assertTrue(locomotive.getDistance() == 40);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);

		locomotive.start();

		locomotive.move();
		assertTrue(locomotive.getDistance() == 80);
		assertTrue(locomotive.getFront() == head);
		assertTrue(locomotive.getBack() == head);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 20);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head);

		locomotive.move();
		assertTrue(locomotive.getDistance() == 60);
		assertTrue(locomotive.getFront() == head.getNext(false));
		assertTrue(locomotive.getBack() == head.getNext(false));
		assertTrue(locomotive.getOnAlt() == true);
		assertTrue(head.getNext(false).getNext(locomotive.getOnAlt()) == null);

	}
	/**
	 * test that a locomotive can take the alternate route on a switch an rejoin to the main route through a merge switch.
	 * Also check that the locomotive can change direction and navigate the same route in reverse.
	 */
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
		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50


		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new BackSwitch(null, null, sw, sec3, 100, 100, 50);


		route.replace(tp_1, sw, false);
		route.replace(tp_3, sw2, false);

		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );


		route.insertBetween(sw, true, sw2, true,  str, false);

		((ForwardSwitch) sw).toggle(); // set the switch so that we move along the alternate direction
		((BackSwitch) sw2).toggle();

		Movable locomotive = new Locomotive(new Track[]{head}, 40, 40, 40, false);
		locomotive.setID(0);
		sec.addMovable(locomotive.getID());
		Movable train = locomotive;
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);
		assertTrue(train.getOnAlt() == false);

		train.start(); // train has not started yet.
		train.move(); // 80 head
		train.move(); // 20 head.next
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == head);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);
		train.move(); // 0
		assertTrue(train.getFront() == str);
		assertTrue(train.getBack() == sw);
		train.move(); // 40
		train.move(); // 80
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == str);
		train.move();//60
		train.move(); // 0
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == sw2);
		train.toggleDirection();


		train.move(); // 60
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == str);
		train.move(); // 80
		train.move(); // 40
		train.move(); // 0
		assertTrue(train.getFront() == str);
		assertTrue(train.getBack() == sw);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);
	}

	/**
	 * check that a locomotive can take the alternate route of a switch, rejoin the main route, reverse after a switch change,
	 * and take the main route back to its start position.
	 */
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
		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new BackSwitch(null, null, sw, sec3, 100, 100, 50);

		route.replace(tp_1, sw, false);
		route.replace(tp_3, sw2, false);

		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );

		route.insertBetween(sw, true, sw2, true,  str, false);
		

		
		((ForwardSwitch) sw).toggle(); // set the switch so that we move along the alternate direction
		((BackSwitch) sw2).toggle();

		Movable locomotive = new Locomotive(new Track[]{head}, 40, 40, 40, false);
		locomotive.setID(0);
		sec.addMovable(locomotive.getID());


		Movable train = locomotive;

		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);
		assertTrue(train.getOnAlt() == false);


		train.start(); // train has not started yet.
		train.move(); // 80 head
		train.move(); // 20 head.next
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == head);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);
		train.move(); // 0
		assertTrue(train.getFront() == str);
		assertTrue(train.getBack() == sw);
		train.move(); // 40
		train.move(); // 80
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == str);
		train.move();//60
		train.move(); // 0
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == sw2);

		locomotive.toggleDirection();
		((ForwardSwitch) sw).toggle();
		((BackSwitch) sw2).toggle();

		train.move(); // 60
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == tp_2);
		train.move(); // 80
		train.move(); // 40
		train.move(); // 0
		assertTrue(train.getFront() == tp_2);
		assertTrue(train.getBack() == sw);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);
	}
	/**
	 * Same as trackTestLoco0 except we put a train on the track, with rolling stock.
	 *
	 */
	@Test public void trackTestStock0(){ // put a train on.
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);
		

		

		Movable locomotive = new Locomotive(new Track[]{head}, 50, 30, 50, false); // distance 50, length 30, max speed 50

		Movable stock = new RollingStock(new Track[]{head},20,20,50,false); // distance 80 length 20, max speed 50
		Movable train = new Train(new Movable[]{locomotive});
		train.setID(0);
		sec.addMovable(train.getID());

		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.move(); // locomotive has not started yet.
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.start();

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);

		train.move();
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head.getNext(false));

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false));

		train.move();
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false).getNext(false));

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head.getNext(false).getNext(false));

	}

	@Test public void trackTestStock1(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);
		
		

		Movable locomotive = new Locomotive(new Track[]{head.getNext(false)}, 90, 30, 50, false); // dist, length, speed
		Movable stock = new RollingStock(new Track[]{head.getNext(false),head}, 60, 80, 50, false);
		Train train = new Train(new Movable[]{locomotive,stock});
		train.setID(0);
		sec.addMovable(train.getID());



		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);
		assertTrue(train.getBackDistance() == 20);

		train.start();

		train.move();
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false));
		assertTrue(train.getDistance() == 40);
		assertTrue(train.getBackDistance() == 70);

		train.move();
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false));
		assertTrue(train.getDistance() == 90);
		assertTrue(train.getBackDistance() == 20);

		train.move();
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head.getNext(false).getNext(false));
		assertTrue(train.getDistance() == 40);
		assertTrue(train.getBackDistance() == 70);

		train.move();
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head.getNext(false).getNext(false));
		assertTrue(train.getDistance() == 90);
		assertTrue(train.getBackDistance() == 20);

		train.move();
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);
		assertTrue(train.getDistance() == 40);
		assertTrue(train.getBackDistance() == 70);

		train.move();
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);
		assertTrue(train.getBackDistance() == 20);
		assertTrue(train.getDistance() == 90);
	}

	@Test public void trackTestStock2(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);

		Section sec2 = new Section(new ArrayList<Track>());
		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new BackSwitch(null, null, sw, sec3, 100, 100, 50);

		route.replace(head.getNext(false), sw , false);
		route.replace(head.getNext(false).getNext(false), sw2, false);
		


		Movable locomotive = new Locomotive(new Track[]{head}, 50, 30, 50, false); // dist length speed
		Movable stock = new RollingStock(new Track[]{head},20,20,50,false);
		Train train = new Train(new Movable[]{locomotive,stock});
		train.setID(0);
		sec.addMovable(train.getID());
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.move(); // locomotive has not started yet.
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.start();

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);

		train.move();
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head.getNext(false));

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false));

		train.move();
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false).getNext(false));

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head.getNext(false).getNext(false));
	}
	@Test public void trackTestStock3(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);

		Section sec2 = new Section(new ArrayList<Track>());
		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		route.replace(head.getNext(false), sw , false);
		

		

		((ForwardSwitch) sw).toggle(); // set the switch so that we move along the alternate direction
		Locomotive loco = new Locomotive(new Track[]{head}, 40, 20, 40, false); // dist length speed.
		Movable stock = new RollingStock(new Track[]{head}, 20, 20, 40, false);
		Movable train = new Train(new Movable[]{loco,stock});
		train.setID(0);
		sec.addMovable(train.getID());

		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);
		assertTrue(train.getOnAlt() == false);

		train.move(); // train has not started yet.
		assertTrue(train.getDistance() == 40);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.start();

		train.move();
		assertTrue(train.getDistance() == 80);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.move();
		assertTrue(train.getDistance() == 20);
		assertTrue(train.getFront() == head.getNext(false));
	//	System.out.println(train.getBack());
	//	System.out.println(train.getLength());
		assertTrue(train.getBack() == head);

		train.move();
		assertTrue(train.getDistance() == 60);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head.getNext(false));
		assertTrue(train.getOnAlt() == true);
		assertTrue(head.getNext(false).getNext(train.getOnAlt()) == null);


	}

	@Test public void trackTestStock4(){
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

		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );

		route.insertBetween(sw, true, sw2, true,  str, false);
		

		((ForwardSwitch) sw).toggle(); // set the switch so that we move along the alternate direction
		((BackSwitch) sw2).toggle();

		Locomotive loco = new Locomotive(new Track[]{head}, 40, 20, 40, false); // speed should not be more than 50 due to positioning of points.
		Movable stock = new RollingStock(new Track[]{head}, 20, 20, 40, false);
		Movable train = new Train(new Movable[]{loco,stock});
		train.setID(0);
		sec.addMovable(train.getID());
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);
		assertTrue(train.getOnAlt() == false);

		train.start(); // train has not started yet.
		train.move(); // 80 head
		train.move(); // 20 head.next
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == head);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);
		train.move(); // 0
		assertTrue(train.getFront() == str);
		assertTrue(train.getBack() == sw);
		train.move(); // 40
		train.move(); // 80
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == str);
		train.move();//60
		train.move(); // 0
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == sw2);
		train.toggleDirection();


		train.move(); // 60
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == str);
		train.move(); // 80
		train.move(); // 40
		train.move(); // 0
		assertTrue(train.getFront() == str);
		assertTrue(train.getBack() == sw);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);

	}

	@Test public void trackTestStock5(){
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

		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );

		route.insertBetween(sw, true, sw2, true,  str, false);
		


		((ForwardSwitch) sw).toggle(); // set the switch so that we move along the alternate direction
		((BackSwitch) sw2).toggle();

		Movable loco = new Locomotive(new Track[]{head}, 40, 20, 40, false);
		Movable stock = new RollingStock(new Track[]{head}, 20, 20, 40, false);
		Train train = new Train(new Movable[]{loco, stock});
		train.setID(0);
		sec.addMovable(train.getID());
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);
		assertTrue(train.getOnAlt() == false);

		train.start(); // train has not started yet.
		train.move(); // 80 head
		train.move(); // 20 head.next
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == head);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);
		train.move(); // 0
		assertTrue(train.getFront() == str);
		assertTrue(train.getBack() == sw);
		train.move(); // 40
		train.move(); // 80
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == str);
		train.move();//60
		train.move(); // 0
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == sw2);


		train.toggleDirection();
		((ForwardSwitch) sw).toggle();
		((BackSwitch) sw2).toggle();

		train.move(); // 60
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == tp_2);
		train.move(); // 80
		train.move(); // 40
		train.move(); // 0
		assertTrue(train.getFront() == tp_2);
		assertTrue(train.getBack() == sw);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);


	}
	@Test public void trackTestTrain0(){ // put a train on.
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);
		

		Movable locomotive = new Locomotive(new Track[]{head}, 50, 50, 50, false);
		Movable train = new Train(new Movable[]{locomotive});
		train.setID(0);

		sec.addMovable(train.getID());
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.move(); // locomotive has not started yet.
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.start();

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);

		train.move();
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head.getNext(false));

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false));

		train.move();
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false).getNext(false));

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head.getNext(false).getNext(false));

	}

	@Test public void trackTestTrain1(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);
		


		Movable locomotive = new Locomotive(new Track[]{head.getNext(false),head}, 90, 110, 50, false);
		Train train = new Train(new Movable[]{locomotive});
		train.setID(0);
		sec.addMovable(train.getID());
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);
		assertTrue(train.getBackDistance() == 20);

		train.start();

		train.move();
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false));
		assertTrue(train.getDistance() == 40);
		assertTrue(train.getBackDistance() == 70);

		train.move();
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false));
		assertTrue(train.getDistance() == 90);
		assertTrue(train.getBackDistance() == 20);

		train.move();
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head.getNext(false).getNext(false));
		assertTrue(train.getDistance() == 40);
		assertTrue(train.getBackDistance() == 70);

		train.move();
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head.getNext(false).getNext(false));
		assertTrue(train.getDistance() == 90);
		assertTrue(train.getBackDistance() == 20);

		train.move();
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);
		assertTrue(train.getDistance() == 40);
		assertTrue(train.getBackDistance() == 70);

		train.move();
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);
		assertTrue(train.getBackDistance() == 20);
		assertTrue(train.getDistance() == 90);
	}

	@Test public void trackTestTrain2(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);

		Section sec2 = new Section(new ArrayList<Track>());
		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		Section sec3 = new Section(new ArrayList<Track>());
		Track sw2 = new BackSwitch(null, null, sw, sec3, 100, 100, 50);

		route.replace(head.getNext(false), sw , false);
		route.replace(head.getNext(false).getNext(false), sw2, false);
		



		Movable locomotive = new Locomotive(new Track[]{head}, 50, 50, 50, false);
		Train train = new Train(new Movable[]{locomotive});
		train.setID(0);
		sec.addMovable(train.getID());
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.move(); // locomotive has not started yet.
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.start();

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);

		train.move();
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head.getNext(false));

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false));

		train.move();
		assertTrue(train.getDistance() == 50);
		assertTrue(train.getFront() == head.getNext(false).getNext(false));
		assertTrue(train.getBack() == head.getNext(false).getNext(false));

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head.getNext(false).getNext(false));
	}
	@Test public void trackTestTrain3(){
		Section sec = new Section(new ArrayList<Track>());

		Straight st = new Straight(null,null,sec,100);
		Straight.StraightRing route = new Straight.StraightRing(st);
		Track head = route.ringTrack(3, 100);
		sec.add(head);

		Section sec2 = new Section(new ArrayList<Track>());
		Track sw = new ForwardSwitch(null, null, null, sec2, 100 , 100 , 50 ) ; // points are crossed at 50

		route.replace(head.getNext(false), sw , false);
		


		((ForwardSwitch) sw).toggle(); // set the switch so that we move along the alternate direction
		Locomotive loco = new Locomotive(new Track[]{head}, 40, 50, 60, false);
		Movable train = new Train(new Movable[]{loco});
		train.setID(0);
		sec.addMovable(train.getID());
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);
		assertTrue(train.getOnAlt() == false);

		train.move(); // train has not started yet.
		assertTrue(train.getDistance() == 40);
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);

		train.start();

		train.move();
		assertTrue(train.getDistance() == 0);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head);

		train.move();
		assertTrue(train.getDistance() == 60);
		assertTrue(train.getFront() == head.getNext(false));
		assertTrue(train.getBack() == head.getNext(false));
		assertTrue(train.getOnAlt() == true);
		assertTrue(head.getNext(false).getNext(train.getOnAlt()) == null);

	}

	@Test public void trackTestTrain4(){
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
		



		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );

		route.insertBetween(sw, true, sw2, true,  str, false);

		((ForwardSwitch) sw).toggle(); // set the switch so that we move along the alternate direction
		((BackSwitch) sw2).toggle();

		Locomotive loco = new Locomotive(new Track[]{head}, 40, 40, 40, false);
		Movable train = new Train(new Movable[]{loco});
		train.setID(0);
		sec.addMovable(train.getID());
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);
		assertTrue(train.getOnAlt() == false);

		train.start(); // train has not started yet.
		train.move(); // 80 head
		train.move(); // 20 head.next
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == head);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);
		train.move(); // 0
		assertTrue(train.getFront() == str);
		assertTrue(train.getBack() == sw);
		train.move(); // 40
		train.move(); // 80
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == str);
		train.move();//60
		train.move(); // 0
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == sw2);
		train.toggleDirection();


		train.move(); // 60
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == str);
		train.move(); // 80
		train.move(); // 40
		train.move(); // 0
		assertTrue(train.getFront() == str);
		assertTrue(train.getBack() == sw);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);
	}



	@Test public void trackTestTrain5(){
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

		Section sec4 = new Section(new ArrayList<Track>());
		Straight str = new Straight(null, null, sec4,100 );
		


		route.insertBetween(sw, true, sw2, true,  str, false);

		((ForwardSwitch) sw).toggle(); // set the switch so that we move along the alternate direction
		((BackSwitch) sw2).toggle();

		Movable loco = new Locomotive(new Track[]{head}, 40, 40, 40, false);
		Train train = new Train(new Movable[]{loco});
		train.setID(0);
		sec.addMovable(train.getID());
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == head);
		assertTrue(train.getOnAlt() == false);

		train.start(); // train has not started yet.
		train.move(); // 80 head
		train.move(); // 20 head.next
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == head);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);
		train.move(); // 0
		assertTrue(train.getFront() == str);
		assertTrue(train.getBack() == sw);
		train.move(); // 40
		train.move(); // 80
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == str);
		train.move();//60
		train.move(); // 0
		assertTrue(train.getFront() == head);
		assertTrue(train.getBack() == sw2);


		train.toggleDirection();
		((ForwardSwitch) sw).toggle();
		((BackSwitch) sw2).toggle();

		train.move(); // 60
		train.move(); // 20
		assertTrue(train.getFront() == sw2);
		assertTrue(train.getBack() == tp_2);
		train.move(); // 80
		train.move(); // 40
		train.move(); // 0
		assertTrue(train.getFront() == tp_2);
		assertTrue(train.getBack() == sw);
		train.move(); // 60
		assertTrue(train.getFront() == sw);
		assertTrue(train.getBack() == sw);
	}
}
