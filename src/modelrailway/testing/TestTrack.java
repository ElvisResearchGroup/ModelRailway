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
			System.out.println("strtSection: "+ strt.getSection());
			System.out.println("strtInnerSec: "+ strtInner.getSection());

			System.out.println("double ring size: "+track.getTrackList().size());

			Pair<Track,Track> trackHeads = track.ringTrack(8, 8, 100);
			System.out.println();
			Track outerHead = trackHeads.fst;

			Track innerHead = trackHeads.snd;

			Integer outerStartSec = strtSec.getNumber();
			Integer innerStartSec = strtInnerSec.getNumber();

			Section sectionOne = outerHead.getSection();
			sectionOne.setSectionNumber(1);

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

		    Section sectionSeventeen = innerHead.getSection();
		    sectionSeventeen.setSectionNumber(17);

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
		    sectionSixteen.setSectionNumber(16);

		    assert(this.getTrack().getTrackList().size() == 16);
		    //insert switches and alternate track.

		    Track forwardSwitch8 = new ForwardSwitch(sectionSeven.get(0), outerHead, null, sectionEight, 100, 100, 50);
		    track.replace(sectionEight.get(0), forwardSwitch8, true);
		   // sectionEight.add(forwardSwitch8);
		    assert(sectionEight.size() == 1);
		    assert(this.getTrack().getTrackList().size() == 16);
		    System.out.println("tracklistSize : "+this.getTrack().getTrackList().size());

		    Track forwardSwitch16 = new ForwardSwitch(sectionFifteen.get(0), null, innerHead, sectionSixteen, 100, 100, 50);
		    track.replace(sectionSixteen.get(0), forwardSwitch16, true);
		 //   sectionSixteen.add(forwardSwitch16);
		    assert(sectionSixteen.size() == 1);

		    Track backSwitch3 = new BackSwitch(null, outerHead, sectionFour.get(0), sectionEight, 100, 100, 50);
		    track.replace(sectionThree.get(0),backSwitch3, true);
		    //sectionThree.add(backSwitch3);
		    assert(sectionThree.size() == 1);


		    Track backSwitch11 = new BackSwitch(null, null, sectionEleven.get(0), sectionSixteen, 100, 100, 50);
		    track.replace(sectionEleven.get(0), backSwitch11, true);
		    //sectionEleven.add(backSwitch11);
		    assert(sectionEleven.size() == 1);



		    track.join(sectionSixteen.get(0), true, sectionEleven.get(0), true);

		    Section sectionNine = new Section(new ArrayList<Track>());
		    sectionNine.setSectionNumber(9);
		    Track straightNineSub = new Straight(null,null,sectionNine,100);
		    sectionNine.add(straightNineSub);

		    track.insertBetween(sectionSixteen.get(0), true, sectionEleven.get(0), true, straightNineSub, false);

		    Section sectionTen = new Section(new ArrayList<Track>());
		    sectionTen.setSectionNumber(10);
		    Track straightTenSub = new Straight(null,null,sectionTen,100);
		    sectionTen.add(straightTenSub);

		    track.insertBetween(sectionNine.get(0), true, sectionEleven.get(0), true, straightTenSub, false);

		    Track backSwitch19 = new BackSwitch(sectionSixteen.get(0),
		    		sectionTen.get(0),
		    		sectionEight.get(0), sectionNine,100,100,50);

		    track.replace(sectionNine.get(0), backSwitch19, false);

		    Track forwardSwitch10 = new ForwardSwitch(sectionNine.get(0), sectionEleven.get(0), sectionThree.get(0), sectionTen, 100,100,50);

		    track.replace(sectionTen.get(0),forwardSwitch10, false);

		    track.join(sectionEight.get(0), true, sectionNine.get(0), true);

		    track.join(sectionTen.get(0), true, sectionThree.get(0), true);

		    // insert diamond crossing and buffers.

		    Track forwardSwitch4 = new ForwardSwitch(sectionThree.get(0), sectionFive.get(0), null, sectionFour, 100,100,50);
		    track.replace(sectionFour.get(0), forwardSwitch4, false);

		    Track forwardSwitch12 = new ForwardSwitch(sectionTwelve.get(0), sectionThirteen.get(0),null, sectionTwelve,100,100,50);
		    track.replace(sectionTwelve.get(0),forwardSwitch12, false);

		    Section sectionNineteen = new Section(new ArrayList<Track>());
		    sectionNineteen.setSectionNumber(19);
		    Track diamondCrossing13 = new Crossing(sectionTwelve.get(0), sectionFourteen.get(0), sectionFive.get(0), null, sectionThirteen, sectionNineteen,100,100);
		    sectionNineteen.add(diamondCrossing13);

		    track.join(sectionFour.get(0), true,  sectionNineteen.get(0), true);

		    track.bufferEnd(sectionTwelve.get(0),true,true);

		    Track section23Straight = sectionTwelve.get(0).getNext(true);
		    Section sectionTwentyThree = section23Straight.getSection();
		    sectionTwentyThree.setSectionNumber(23);

		    Section sectionTwentyTwo = new Section(new ArrayList<Track>());
		    Track section22Straight = new Straight(sectionTwelve.get(0), sectionTwentyThree.get(0), sectionTwentyTwo, 100);
		    sectionTwentyTwo.setSectionNumber(22);

		    track.insertBetween(sectionTwelve.get(0), true, section23Straight, false, section22Straight, false );


		    track.bufferEnd(sectionNineteen.get(0),true,true);

		    Track section21Straight  = sectionNineteen.get(0).getNext(true);
		    Section sectionTwentyOne = section21Straight.getSection();
		    sectionTwentyOne.setSectionNumber(21);

		    Section sectionTwenty = new Section(new ArrayList<Track>());
		    Track section20Straight = new Straight(sectionNineteen.get(0), sectionTwentyOne.get(0), sectionTwenty, 100);
		    sectionTwenty.setSectionNumber(20);

		    track.insertBetween(sectionNineteen.get(0), true, section21Straight, false, section20Straight, false);


		    // track should now be built., test with assert statements todo
	        // track has been built without diamond crossing. Now add diamond crossing.



	}

	public StraightDblRing getTrack(){
		return track;
	}

	public Track getHead(){
		return track.getHead();
	}

	public Map<Integer,Section> getSections(){
		return track.getSectionNumberMap();
	}

 }
