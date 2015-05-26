package modelrailway.testing;

import static org.junit.Assert.*;

import java.util.Map;

import modelrailway.core.Section;
import modelrailway.simulation.Straight.StraightDblRing;
import modelrailway.simulation.Track.RingRoute;
import modelrailway.simulation.Track;
import modelrailway.util.SimulationTrack;

import org.junit.Test;


public class SimulationTrackTest {
	/**
	 * Test the switch connections at section 8
	 */
	@Test public void testTrackBuild0(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		//first check that all the section numbers are in the numberMap.

		Section sectionEight = numberMap.get(8);
		Section sectionSeven = numberMap.get(7);
		Section sectionOne = numberMap.get(1);
		Section sectionNine = numberMap.get(9);

		//System.out.println("Section counts:");
		//for(Map.Entry<Section, Integer> entry: ring.getSectionList().entrySet()){
		//	System.out.println(entry+" section: "+entry.getKey().getNumber());
		//}
		//System.out.println("Section Numbering:");
		//for(Map.Entry<Integer,Section> entry : ring.getSectionNumberMap().entrySet()){
		//	System.out.println(entry);
		//}


		assertTrue(sectionEight != null);
		assertTrue(sectionSeven != null);
		assertTrue(sectionOne != null);
		assertTrue(sectionNine != null);

		assertTrue (sectionEight.getNumber() == 8);
		assertTrue(sectionSeven.getNumber() == 7);
		assertTrue(sectionOne.getNumber() == 1);
		assertTrue(sectionNine.getNumber() == 9);

		// check t make sure that all the sections only have exactly one piece in them

		assertTrue(sectionEight.size() == 1);
		assertTrue(sectionSeven.size() == 1);
		assertTrue(sectionOne.size() == 1);
		assertTrue(sectionNine.size() == 1);

		//now check the connections for section seven to section eight.

		//System.out.println(sectionSeven.get(0).getNext(false).getSection().getNumber());
		//System.out.println(sectionSeven.get(0).getNext(false) +"   "+sectionEight.get(0));

		assertTrue(sectionSeven.get(0).getNext(false)== sectionEight.get(0));
		assertTrue(sectionEight.get(0).getPrevious(false) == sectionSeven.get(0));

		//now check the connections from section eight to section nine
		//System.out.println("number for sec 8: "+sectionEight.get(0).getSection().getNumber());
		//System.out.println("number for sec 9: "+sectionNine.get(0).getSection().getNumber());
		//System.out.println("next alt sec 8: "+sectionEight.get(0).getNext(false).getSection().getNumber());

		assertTrue(sectionEight.get(0).getNext(true) == sectionNine.get(0));
		assertTrue(sectionNine.get(0).getPrevious(true) == sectionEight.get(0));

		//now check the connections from section eight to section one

		assertTrue(sectionEight.get(0).getNext(false)== sectionOne.get(0));
		assertTrue(sectionOne.get(0).getPrevious(false) == sectionEight.get(0));

	}
	/**
	 * Test switch connections at section 16
	 */
	@Test public void testTrackBuild1(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		//first check that all the section numbers are in the numberMap.

		Section sectionSixteen = numberMap.get(16);
		Section sectionFifteen = numberMap.get(15);
		Section sectionSeventeen = numberMap.get(17);
		Section sectionNine = numberMap.get(9);

		//System.out.println("Section counts:");
		//for(Map.Entry<Section, Integer> entry: ring.getSectionList().entrySet()){
		//	System.out.println(entry+" section: "+entry.getKey().getNumber());
		//}
		//System.out.println("Section Numbering:");
		//for(Map.Entry<Integer,Section> entry : ring.getSectionNumberMap().entrySet()){
		//	System.out.println(entry);
		//}


		assertTrue(sectionSixteen != null);
		assertTrue(sectionFifteen != null);
		assertTrue(sectionSeventeen != null);
		assertTrue(sectionNine != null);

		assertTrue (sectionSixteen.getNumber() == 16);
		assertTrue(sectionFifteen.getNumber() == 15);
		assertTrue(sectionSeventeen.getNumber() == 17);
		assertTrue(sectionNine.getNumber() == 9);

		// check t make sure that all the sections only have exactly one piece in them

		assertTrue(sectionSixteen.size() == 1);
		assertTrue(sectionFifteen.size() == 1);
		assertTrue(sectionSeventeen.size() == 1);
		assertTrue(sectionNine.size() == 1);

		//now check the connections for section seven to section eight.

		//System.out.println(sectionSeven.get(0).getNext(false).getSection().getNumber());
		//System.out.println(sectionSeven.get(0).getNext(false) +"   "+sectionEight.get(0));

		assertTrue(sectionFifteen.get(0).getNext(false)== sectionSixteen.get(0));
		assertTrue(sectionSixteen.get(0).getPrevious(false) == sectionFifteen.get(0));

		//now check the connections from section eight to section nine
		//System.out.println("number for sec 8: "+sectionEight.get(0).getSection().getNumber());
		//System.out.println("number for sec 9: "+sectionNine.get(0).getSection().getNumber());
		//System.out.println("next alt sec 8: "+sectionEight.get(0).getNext(false).getSection().getNumber());

		//System.out.println(sectionSixteen.get(0).getNext(false).getSection().getNumber());
		//System.out.println(sectionSixteen.get(0).getNext(true).getSection().getNumber());

		assertTrue(sectionSixteen.get(0).getNext(false) == sectionSeventeen.get(0));
		assertTrue(sectionSeventeen.get(0).getPrevious(false) == sectionSixteen.get(0));

		//now check the connections from section eight to section one

		assertTrue(sectionSixteen.get(0).getNext(true)== sectionNine.get(0));
		assertTrue(sectionNine.get(0).getPrevious(false) == sectionSixteen.get(0));
	}
	/**
	 * Test switch connections at section 3
	 */
	@Test public void testTrackBuild2(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		Section sectionTen = numberMap.get(10);
		Section sectionTwo = numberMap.get(2);
		Section sectionFour = numberMap.get(4);
		Section sectionThree = numberMap.get(3);

		assertTrue(sectionFour.getNumber() == 4);
		assertTrue(sectionThree.getNumber() == 3);
		assertTrue(sectionTwo.getNumber() == 2);
		assertTrue(sectionTen.getNumber() == 10);

		assertTrue(sectionTen.size() == 1);
		assertTrue(sectionTwo.size() == 1);
		assertTrue(sectionFour.size() == 1);
		assertTrue(sectionThree.size() == 1);

		assertTrue(sectionTwo.get(0).getNext(false).equals(sectionThree.get(0)));
		assertTrue(sectionThree.get(0).getPrevious(false).equals(sectionTwo.get(0)));

		assertTrue(sectionTen.get(0).getNext(true).equals(sectionThree.get(0)));
		assertTrue(sectionThree.get(0).getPrevious(true).equals(sectionTen.get(0)));

		assertTrue(sectionThree.get(0).getNext(false).equals(sectionFour.get(0)));
		assertTrue(sectionFour.get(0).getPrevious(false).equals(sectionThree.get(0)));


	}
	/**
	 * Test switch connections at section 11
	 */
	@Test public void testTrackBuild3(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		Section sectionTen = numberMap.get(10);
		Section sectionEighteen = numberMap.get(18);
		Section sectionEleven = numberMap.get(11);
		Section sectionTwelve = numberMap.get(12);

		assertTrue(sectionTen.getNumber() == 10);
		assertTrue(sectionEighteen.getNumber() == 18);
		assertTrue(sectionEleven.getNumber() == 11);
		assertTrue(sectionTwelve.getNumber() == 12);


		assertTrue(sectionTen.size() == 1);
		assertTrue(sectionEighteen.size() == 1);
		assertTrue(sectionEleven.size() == 1);
		assertTrue(sectionTwelve.size() == 1);

		assertTrue(sectionEighteen.get(0).getNext(false).equals(sectionEleven.get(0)));
		assertTrue(sectionEleven.get(0).getPrevious(false).equals(sectionEighteen.get(0)));

		System.out.println("sectionTen.getNext(true): "+sectionTen.get(0).getNext(true).getSection().getNumber());
		System.out.println("sectionTen.getNext(false): "+sectionTen.get(0).getNext(false).getSection().getNumber());


		System.out.println("sectionEleven.get(0):"+sectionEleven.get(0).getSection().getNumber());
		assertTrue(sectionTen.get(0).getNext(false).equals(sectionEleven.get(0)));
		assertTrue(sectionEleven.get(0).getPrevious(true).equals(sectionTen.get(0)));

		assertTrue(sectionEleven.get(0).getNext(false).equals(sectionTwelve.get(0)));
		assertTrue(sectionTwelve.get(0).getPrevious(false).equals(sectionEleven.get(0)));

	}

	/**
	 * test Track 12
	 */
	@Test public void testTrackBuild4(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		Section sectionTwentyOne = numberMap.get(21);
		Section sectionThirteen = numberMap.get(13);
		Section sectionEleven = numberMap.get(11);
		Section sectionTwelve = numberMap.get(12);

		assertTrue(sectionTwentyOne.getNumber() == 21);
		assertTrue(sectionThirteen.getNumber() == 13);
		assertTrue(sectionEleven.getNumber() == 11);
		assertTrue(sectionTwelve.getNumber() == 12);



		assertTrue(sectionTwentyOne.size() == 1);
		assertTrue(sectionThirteen.size() == 1);
		assertTrue(sectionEleven.size() == 1);
		assertTrue(sectionTwelve.size() == 1);

		assertTrue(sectionEleven.get(0).getNext(false).equals(sectionTwelve.get(0)));
		assertTrue(sectionTwelve.get(0).getPrevious(false).equals(sectionEleven.get(0)));

		//System.out.println(sectionTwelve.get(0).getNext(true).getSection().getNumber());


		//System.out.println(sectionTwentyOne.get(0).getSection().getNumber());


		assertTrue(sectionTwelve.get(0).getNext(true).equals(sectionTwentyOne.get(0)));
		assertTrue(sectionTwentyOne.get(0).getPrevious(false).equals(sectionTwelve.get(0)));

		assertTrue(sectionTwelve.get(0).getNext(false).equals(sectionThirteen.get(0)));
		assertTrue(sectionThirteen.get(0).getPrevious(false).equals(sectionTwelve.get(0)));
	}

	/**
	 * test Track 4
	 */
	@Test public void testTrackBuild5(){
		SimulationTrack simTrack0 = new SimulationTrack();
		StraightDblRing ring = simTrack0.getTrack();
		Map<Integer, Section> numberMap = ring.getSectionNumberMap();

		Section sectionNineteen = numberMap.get(19);
		Section sectionFive = numberMap.get(5);
		Section sectionThree = numberMap.get(3);
		Section sectionFour = numberMap.get(4);
		Section sectionTwenty = numberMap.get(20);

		if(sectionNineteen == null) fail("section nineteen expected to exist");

		assertTrue(sectionNineteen.getNumber() == 19);
		assertTrue(sectionFive.getNumber() == 5);
		assertTrue(sectionThree.getNumber() == 3);
		assertTrue(sectionFour.getNumber() == 4);



		assertTrue(sectionNineteen.size() == 1);
		assertTrue(sectionFive.size() == 1);
		assertTrue(sectionThree.size() == 1);
		assertTrue(sectionFour.size() == 1);

		assertTrue(sectionThree.get(0).getNext(false).equals(sectionFour.get(0)));
		assertTrue(sectionFour.get(0).getPrevious(false).equals(sectionThree.get(0)));

		//System.out.println(sectionTwelve.get(0).getNext(true).getSection().getNumber());


		//System.out.println(sectionTwentyOne.get(0).getSection().getNumber());


		assertTrue(sectionFour.get(0).getNext(true).equals(sectionNineteen.get(0)));



		assertTrue(sectionNineteen.get(0).getPrevious(true).equals(sectionFour.get(0)));

		assertTrue(sectionFour.get(0).getNext(false).equals(sectionFive.get(0)));
		assertTrue(sectionFive.get(0).getPrevious(false).equals(sectionFour.get(0)));
		assertTrue(sectionNineteen.get(0).getNext(true).equals(sectionTwenty.get(0)));


	}
}
