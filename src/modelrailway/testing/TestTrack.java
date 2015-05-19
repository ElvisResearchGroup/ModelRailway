package modelrailway.testing;

import java.util.ArrayList;
import java.util.Map;

import modelrailway.core.Section;
import modelrailway.simulation.BackSwitch;
import modelrailway.simulation.Crossing;
import modelrailway.simulation.ForwardSwitch;
import modelrailway.simulation.Straight;
import modelrailway.simulation.Straight.StraightDblRing;
import modelrailway.simulation.Track;
import modelrailway.util.Pair;

/**
 * the test Track class generates a track for the train to run on which is identical to the track that is used in the hardware model.
 *
 *
 * @author powleybenj
 *
 */
public class TestTrack {

	private StraightDblRing track = null;
	public TestTrack(){
			Section strtSec = new Section(new ArrayList<Track>());
			Section strtInnerSec = new Section(new ArrayList<Track>());

			Straight strt = new Straight(null, null, strtSec, 100);
			Straight strtInner = new Straight(null,null,strtInnerSec,100);

			strtSec.add(strt);
			strtInnerSec.add(strtInner);
			strtSec.setSectionNumber(1);
			strtInnerSec.setSectionNumber(17);

			track = new StraightDblRing(strt, strtInner);
			Pair<Track,Track> trackHeads = track.ringTrack(8, 8, 100);
			Track outerHead = trackHeads.fst;

			Track innerHead = trackHeads.snd;

			Integer outerStartSec = strtSec.getNumber();
			Integer innerStartSec = strtInnerSec.getNumber();

		    Section sectionTwo = outerHead.getNext(false).getSection();
		    sectionTwo.setSectionNumber(2);
		    
		    Section sectionThree = outerHead.getNext(false).getNext(false).getSection();

		    Section sectionFour = outerHead.getNext(false).getNext(false).getNext(false).getSection();
		    sectionFour.setSectionNumber(4);

		    Section sectionFive = outerHead.getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionFive.setSectionNumber(5);;

		    Section sectionSix = outerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionSix.setSectionNumber(6);

		    Section sectionSeven = outerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionSeven.setSectionNumber(7);
		    
		    Section sectionEight = outerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionSeven.setSectionNumber(8);
		    
		    

		    Section sectionEighteen = innerHead.getNext(false).getSection();
		    sectionEighteen.setSectionNumber(18);
		    
		    Section sectionEleven = innerHead.getNext(false).getNext(false).getSection();
		    sectionEleven.setSectionNumber(11);

		    Section sectionTwelve = innerHead.getNext(false).getNext(false).getNext(false).getSection();
		    sectionTwelve.setSectionNumber(12);

		    Section sectionThirteen = innerHead.getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionThirteen.setSectionNumber(13);

		    Section sectionFourteen = innerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionFourteen.setSectionNumber(14);

		    Section sectionFifteen = innerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionFifteen.setSectionNumber(15);

		    Section sectionSixteen = innerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionFifteen.setSectionNumber(16);
		    
		    Track forwardSwitch8 = new ForwardSwitch(sectionSeven.get(0), outerHead, null, sectionEight, 100, 100, 50);
		    track.replace(sectionEight.get(0), forwardSwitch8, true);
		    
		    
		    Track forwardSwitch16 = new ForwardSwitch(sectionFifteen.get(0), null, innerHead, sectionSixteen, 100, 100, 50);
		    track.replace(sectionEight.get(0), forwardSwitch16, true);
		    
		    Track backSwitch3 = new BackSwitch(null, outerHead, sectionFour.get(0), sectionEight, 100, 100, 50);
		    track.replace(sectionThree.get(0),backSwitch3, true);
		    
		    
		    Track backSwitch11 = new BackSwitch(null, null, sectionEleven.get(0), sectionSixteen, 100, 100, 50);
		    track.replace(sectionEleven.get(0), backSwitch11, true);
		    
		    track.join(sectionSixteen.get(0), true, sectionEleven.get(0), true);
		    
		    Section sectionNineteen = new Section(new ArrayList<Track>());
		    Track straightNineteenSub = new Straight(null,null,sectionNineteen,100);
		    sectionNineteen.add(straightNineteenSub);
		   
		    track.insertBetween(sectionSixteen.get(0), true, sectionEleven.get(0), true, straightNineteenSub, false);
		    
		    Section sectionTen = new Section(new ArrayList<Track>());
		    Track straightTenSub = new Straight(null,null,sectionTen,100);
		    sectionNineteen.add(straightNineteenSub);
		   
		    track.insertBetween(sectionNineteen.get(0), true, sectionEleven.get(0), true, straightTenSub, false);
		   
		    Track backSwitch19 = new BackSwitch(sectionSixteen.get(0), sectionTen.get(0),sectionEight.get(0), sectionNineteen,100,100,50);
		    
		    track.replace(sectionNineteen.get(0), backSwitch19, false);
		    
		    Track forwardSwitch10 = new ForwardSwitch(sectionNineteen.get(0), sectionEleven.get(0), sectionThree.get(0), sectionTen, 100,100,50);
		    
		    track.replace(sectionTen.get(0),forwardSwitch10, false);
		    
		    track.join(sectionEight.get(0), true, sectionNineteen.get(0), true);
		    
		    track.join(sectionTen.get(0), true, sectionThree.get(0), true);
		    
		    // track should now be built., test with assert statements todo
	    // track should now be built., test with assert statements todo
	}

	public StraightDblRing getTrack(){
		return track;
	}

	public Track getHead(){
		return track.getHead();
	}

	public Map<Integer,Section> getSections(){
		return track.getSectionMap();
	}

 }
