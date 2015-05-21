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
	 * Test switch connections at section three
	 */
	@Test public void testTrackBuild2(){
		
	}
}
