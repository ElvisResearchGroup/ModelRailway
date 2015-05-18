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
		Pair<Track,Track> trackHeads = track.ringTrack(6, 6, 100);
		Track outerHead = trackHeads.fst;

		Track innerHead = trackHeads.snd;

		Integer outerStartSec = strtSec.getNumber();
		Integer innerStartSec = strtInnerSec.getNumber();

	    Section sectionTwo = outerHead.getNext(false).getSection();
	    sectionTwo.setSectionNumber(2);

	    Section sectionFour = outerHead.getNext(false).getNext(false).getSection();
	    sectionFour.setSectionNumber(4);

	    Section sectionFive = outerHead.getNext(false).getNext(false).getNext(false).getSection();
	    sectionFive.setSectionNumber(5);;

	    Section sectionSix = outerHead.getNext(false).getNext(false).getNext(false).getNext(false).getSection();
	    sectionSix.setSectionNumber(6);



	    Section sectionSeven = outerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
	    sectionSeven.setSectionNumber(7);

	    Section sectionEighteen = innerHead.getNext(false).getSection();
	    sectionEighteen.setSectionNumber(18);

	    Section sectionTwelve = innerHead.getNext(false).getNext(false).getSection();
	    sectionTwelve.setSectionNumber(12);

	    Section sectionThirteen = innerHead.getNext(false).getNext(false).getNext(false).getSection();
	    sectionThirteen.setSectionNumber(13);

	    Section sectionFourteen = innerHead.getNext(false).getNext(false).getNext(false).getNext(false).getSection();
	    sectionFourteen.setSectionNumber(14);

	    Section sectionFifteen = innerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
	    sectionFifteen.setSectionNumber(15);

	    track.insertMultiSwitch((Straight)sectionFifteen.get(0), (Straight)sectionSeven.get(0), (Straight)innerHead, (Straight)outerHead);

	    Section sectionEightAndSixteen = sectionFifteen.get(0).getNext(false).getSection();

	    track.insertMultiSwitchBack((Straight) sectionEighteen.get(0), (Straight) sectionTwo.get(0), (Straight) sectionTwelve.get(0), (Straight) sectionFour.get(0));

	    Section sectionThreeAndEleven = sectionTwo.get(0).getNext(false).getSection();

	    track.replace(sectionTwelve.get(0), new ForwardSwitch(null,null,null,sectionTwelve, 100, 100, 50), false);

	    track.bufferEnd(sectionTwelve.get(0),true, true);
	    Section section22 = sectionTwelve.get(0).getNext(true).getSection();

	    Section section21 = new Section(new ArrayList<Track>());
	    Straight section21Piece = new Straight(sectionTwelve.get(0) , section22.get(0) , section21, 100);
	    section21.add(section21Piece);
	    track.insertBetween(sectionTwelve.get(0), true, section22.get(0), false, section21Piece, false);

	    track.replace(sectionFour.get(0), new ForwardSwitch(null,null,null,sectionFour, 100, 100, 50), false);

	    Section sectionNineteen = new Section(new ArrayList<Track>());
	    Track diamondCrossing = new Crossing(sectionTwelve.get(0), sectionFourteen.get(0), sectionFour.get(0), null, sectionThirteen, sectionNineteen, 100, 100);
	    sectionNineteen.add(diamondCrossing);

	    track.bufferEnd(sectionNineteen.get(0), true, true);


	    track.join(sectionFour.get(0), true, sectionNineteen.get(0), true);


	    track.join(sectionEightAndSixteen.get(0), true, sectionThreeAndEleven.get(0), true);

	    Section sectionNine  = new Section(new ArrayList<Track>());
	    Straight nineStraight = new Straight(null,null,sectionNine,100);
	    sectionNine.add(nineStraight);

	    track.insertBetween(sectionEightAndSixteen.get(3), false, sectionThreeAndEleven.get(3), false, nineStraight, false);

	    Section sectionTen = new Section(new ArrayList<Track>());
	    Straight tenStraight = new Straight(null,null,sectionTen, 100);
	    sectionEighteen.add(tenStraight);

	    track.insertBetween(sectionNine.get(0), false, sectionThreeAndEleven.get(0), false, tenStraight, false);

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
