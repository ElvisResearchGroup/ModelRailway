package modelrailway.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import modelrailway.core.Section;
import modelrailway.simulation.BackSwitch;
import modelrailway.simulation.Crossing;
import modelrailway.simulation.ForwardSwitch;
import modelrailway.simulation.Switch;
import modelrailway.simulation.Straight;
import modelrailway.simulation.Straight.StraightDblRing;
import modelrailway.simulation.Track;

/**
 * the test Track class generates a track for the train to run on which is identical to the track that is used in the hardware model.
 *
 *
 * @author powleybenj
 *
 */
public class SimulationTrack {

	private StraightDblRing track = null;
	public SimulationTrack(){
			track = null;
			Section strtSec = new Section(new ArrayList<Track>());
			Section strtInnerSec = new Section(new ArrayList<Track>());

			Straight strt = new Straight(null, null, strtSec, 100);
			Straight strtInner = new Straight(null,null,strtInnerSec,100);

			strtSec.add(strt);
			strtInnerSec.add(strtInner);

			track = new StraightDblRing(strt, strtInner);

			//System.out.println("simulation Track initial double ring:");
			//for(Map.Entry<Section,Integer> entry: track.getSectionList().entrySet()){
		    //	System.out.println(entry);
			//}

			//System.out.println("strtSection: "+ strt.getSection());
			//System.out.println("strtInnerSec: "+ strtInner.getSection());

			//System.out.println("double ring size: "+track.getTrackList().size());

			Pair<Track,Track> trackHeads = track.ringTrack(8, 8, 100);

			//System.out.println("Tracklist Size: "+track.getTrackList().size());

			//System.out.println("RingTrack : ");
			//for(Map.Entry<Section,Integer> entry: track.getSectionList().entrySet()){
			//	System.out.println(entry+" number: "+entry.getKey().getNumber());
			//}
			//System.out.println();
			Track outerHead = trackHeads.fst;

			Track innerHead = trackHeads.snd;


			Section sectionOne = outerHead.getSection();
			sectionOne.setSectionNumber(1);


		    Section sectionTwo = outerHead.getNext(false).getSection();
		    sectionTwo.setSectionNumber(2);


		    Section sectionThree = outerHead.getNext(false).getNext(false).getSection();
		    sectionThree.setSectionNumber(3);


		    Section sectionFour = outerHead.getNext(false).getNext(false).getNext(false).getSection();
		    sectionFour.setSectionNumber(4);


		    Section sectionFive = outerHead.getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionFive.setSectionNumber(5);


		    Section sectionSix = outerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionSix.setSectionNumber(6);


		    Section sectionSeven = outerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionSeven.setSectionNumber(new Integer(7));


		    Section sectionEight = outerHead.getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getNext(false).getSection();
		    sectionEight.setSectionNumber(8);




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



		    track.recalculateSections();


		    //System.out.println("Section Numbers for Track: ");
		    //for(Track tr: track.getTrackList()){
		    //	System.out.println("tr: "+tr.getSection().getNumber());
		    //}

		    //System.out.println("Section Numbers: ");
			//for(Map.Entry<Section,Integer> entry: track.getSectionList().entrySet()){
			//	System.out.println(entry +" number: "+entry.getKey().getNumber());
			//}



		    //insert switches and alternate track.

		    Track forwardSwitch8 = new ForwardSwitch(sectionSeven.get(0), outerHead, null, sectionEight, 100, 100, 50);
		    ((Switch) forwardSwitch8).setSwitchID(2);
		    sectionEight.add(forwardSwitch8);


		    track.replace(sectionEight.get(0), forwardSwitch8, true);
		    sectionEight.remove(0);

		   // sectionEight.add(forwardSwitch8);

		    assert(this.getTrack().getTrackList().size() == 16);
		    //System.out.println("tracklistSize : "+this.getTrack().getTrackList().size());

		    Track forwardSwitch16 = new ForwardSwitch(sectionFifteen.get(0), innerHead, null, sectionSixteen, 100, 100, 50);
		    ((Switch) forwardSwitch16).setSwitchID(1);
		    sectionSixteen.add(forwardSwitch16);
		    track.replace(sectionSixteen.get(0), forwardSwitch16, true);
		    sectionSixteen.remove(0);
		 //   sectionSixteen.add(forwardSwitch16);
		    assert(sectionSixteen.size() == 1);

		    Track backSwitch3 = new BackSwitch(outerHead.getNext(true), sectionFour.get(0),null , sectionThree, 100, 100, 50);
		    ((Switch)backSwitch3).setSwitchID(6);
		    sectionThree.add(backSwitch3);
		    track.replace(sectionThree.get(0),backSwitch3, true);
		    sectionThree.remove(0);
		    //sectionThree.add(backSwitch3);
		    assert(sectionThree.size() == 1);


		    Track backSwitch11 = new BackSwitch(innerHead.getNext(true), sectionTwelve.get(0), null, sectionEleven, 100, 100, 50);
		    ((Switch) backSwitch11).setSwitchID(5);
		    sectionEleven.add(backSwitch11);

		    
		    track.replace(sectionEleven.get(0), backSwitch11, true);
		    sectionEleven.remove(0);
		    //sectionEleven.add(backSwitch11);
		    assert(sectionEleven.size() == 1);


		//    System.out.println("next(false) for sectionSixteen");
		    track.join(sectionSixteen.get(0), true, sectionEleven.get(0), true);
		  //  System.out.println("next(true) for sectionSixteen: "+sectionSixteen.get(0).getNext(true).getSection().getNumber());
		  //  System.out.println("next(false) for sectionSixteen"+sectionSixteen.get(0).getNext(false).getSection().getNumber());

		    Section sectionNine = new Section(new ArrayList<Track>());

		    sectionNine.setSectionNumber(9);
		    track.recalculateSections();

		    Track straightNineSub = new Straight(null,null,sectionNine,100);
		    sectionNine.add(straightNineSub);

		    track.insertBetween(sectionSixteen.get(0), true, sectionEleven.get(0), true, straightNineSub, false);

		  //  System.out.println("next(true) for sectionSixteen: "+sectionSixteen.get(0).getNext(true).getSection().getNumber());
		  //  System.out.println("next(false) for sectionSixteen"+sectionSixteen.get(0).getNext(false).getSection().getNumber());

		    Section sectionTen = new Section(new ArrayList<Track>());
		    sectionTen.setSectionNumber(10);
		    track.recalculateSections();

		    Track straightTenSub = new Straight(null,null,sectionTen,100);
		    sectionTen.add(straightTenSub);

		    track.insertBetween(sectionNine.get(0), true, sectionEleven.get(0), true, straightTenSub, false);
		    track.insertBetween(sectionNine.get(0), false, sectionEleven.get(0), true, straightTenSub, false);
		 //   System.out.println("next(true) for sectionSixteen: "+sectionSixteen.get(0).getNext(true).getSection().getNumber());
		 //   System.out.println("next(false) for sectionSixteen"+sectionSixteen.get(0).getNext(false).getSection().getNumber());

		    Track backSwitch9 = new BackSwitch(sectionSixteen.get(0),
		    		sectionTen.get(0),
		    		sectionEight.get(0), sectionNine,100,100,50);
		    ((Switch) backSwitch9).setSwitchID(3);
		    sectionNine.add(backSwitch9);
		    track.replace(sectionNine.get(0), backSwitch9, false);
		    sectionNine.remove(0);



		   // System.out.println("next(true) for sectionSixteen: "+sectionSixteen.get(0).getNext(true).getSection().getNumber());
		   // System.out.println("next(false) for sectionSixteen"+sectionSixteen.get(0).getNext(false).getSection().getNumber());

		    Track forwardSwitch10 = new ForwardSwitch(sectionNine.get(0), sectionEleven.get(0), sectionThree.get(0), sectionTen, 100,100,50);
		    ((Switch) forwardSwitch10).setSwitchID(4);
		    sectionTen.add(forwardSwitch10);
		    track.replace(sectionTen.get(0),forwardSwitch10, false);
		    sectionTen.remove(0);

		    track.join(sectionEight.get(0), true, sectionNine.get(0), true);

		    track.join(sectionTen.get(0), true, sectionThree.get(0), true);
		    track.recalculateSections();

		   // System.out.println("sectionNine, false:"+sectionNine.get(0).getNext(false).getSection().getNumber()+", sectionNine true:"+sectionNine.get(0).getNext(true).getSection().getNumber());
		   // System.exit(0);
		    // insert diamond crossing and buffers.

		    Track forwardSwitch4 = new ForwardSwitch(sectionThree.get(0), sectionFive.get(0), null, sectionFour, 100,100,50);
		    ((Switch) forwardSwitch4).setSwitchID(7);
		    sectionFour.add(forwardSwitch4);

		    track.replace(sectionFour.get(0), forwardSwitch4, false);
		    sectionFour.remove(0);

		    Track forwardSwitch12 = new ForwardSwitch(sectionTwelve.get(0), sectionThirteen.get(0),null, sectionTwelve,100,100,50);
		    ((Switch) forwardSwitch12).setSwitchID(8);
		    sectionTwelve.add(forwardSwitch12);

		    track.replace(sectionTwelve.get(0),forwardSwitch12, false);
		    sectionTwelve.remove(0);
		    track.join(backSwitch11, true, sectionTwelve.get(0), true);
		    //track.join(backSwitch11, false, sectionTwelve.get(0), false);

		    Section sectionNineteen = new Section(new ArrayList<Track>());
		    sectionNineteen.setSectionNumber(19);


		    track.recalculateSections();
		    Track diamondCrossing13 = new Crossing(sectionTwelve.get(0), sectionFourteen.get(0), sectionFive.get(0), null, sectionThirteen, sectionNineteen,100,100);
		    sectionNineteen.add(diamondCrossing13);


		    track.replace(sectionThirteen.get(0), diamondCrossing13, false);
		    sectionThirteen.remove(0);
		    sectionThirteen.add(diamondCrossing13);


		    track.join(sectionFour.get(0), true,  sectionNineteen.get(0), true);
		    track.join(sectionTwelve.get(0), false, sectionThirteen.get(0), false);
		    
		    track.join(sectionThirteen.get(0), false, sectionFourteen.get(0), false);
		    track.recalculateSections();

		    track.bufferEnd(sectionTwelve.get(0),true,true);

		    Track section22Straight = sectionTwelve.get(0).getNext(true);
		    Section sectionTwentyTwo = section22Straight.getSection();
		    track.getSectionList().put(sectionTwentyTwo, 1);
		    sectionTwentyTwo.setSectionNumber(22);
		    track.recalculateSections();

		    Section sectionTwentyOne = new Section(new ArrayList<Track>());
		    Track section21Straight = new Straight(sectionTwelve.get(0), sectionTwentyTwo.get(0), sectionTwentyOne, 100);
		    sectionTwentyOne.setSectionNumber(21);
		    sectionTwentyOne.add(section21Straight);
		    track.getSectionList().put(sectionTwentyOne, 1);
		    track.recalculateSections();

		    //System.out.println("12 previous: "+sectionTwelve.get(0).getPrevious(false).getSection().getNumber());

		    //System.out.println("12 next(false): "+sectionTwelve.get(0).getNext(false).getSection().getNumber());

		    //System.out.println("12 previous: "+sectionTwelve.get(0).getPrevious(false).getSection().getNumber());

		    //System.out.println("12 next(true): "+sectionTwelve.get(0).getNext(true).getSection().getNumber());

		    //System.out.println("");
		    //System.out.println("-----------------------------------------");

		    track.insertBetween(sectionTwelve.get(0), true, section22Straight, false, section21Straight, false );

		    //System.out.println("21 previous: "+sectionTwentyOne.get(0).getPrevious(false).getSection().getNumber());

		    //System.out.println("21 next: "+sectionTwentyOne.get(0).getNext(false).getSection().getNumber());

		    //System.out.println("22 previous: "+sectionTwentyTwo.get(0).getPrevious(false).getSection().getNumber());

		    //System.out.println("22 next: "+sectionTwentyTwo.get(0).getNext(false).getSection().getNumber());

		    //System.out.println("12 previous: "+sectionTwelve.get(0).getPrevious(false).getSection().getNumber());

		    //System.out.println("12 next(false): "+sectionTwelve.get(0).getNext(false).getSection().getNumber());

		    //System.out.println("12 previous: "+sectionTwelve.get(0).getPrevious(false).getSection().getNumber());

		    //System.out.println("12 next(true): "+sectionTwelve.get(0).getNext(true).getSection().getNumber());


		    track.recalculateSections();



		    track.bufferEnd(sectionNineteen.get(0),true,true);

		    Track section20Straight  = sectionNineteen.get(0).getNext(true);
		    Section sectionTwenty = section20Straight.getSection();
		    sectionTwenty.setSectionNumber(20);
		    track.recalculateSections();


		    // add switching order.

		    sectionEight.putSwitchingOrder(new Pair<Integer,Integer>(7,9),Arrays.asList(new Boolean[]{true}));
		    sectionEight.putSwitchingOrder(new Pair<Integer,Integer>(7,1),Arrays.asList(new Boolean[]{false}));
		    sectionEight.putSwitchingOrder(new Pair<Integer,Integer>(1,7),Arrays.asList(new Boolean[]{false}));
		    sectionEight.putSwitchingOrder(new Pair<Integer,Integer>(9,7),Arrays.asList(new Boolean[]{true}));


		    // add switching order for s16

		    sectionSixteen.putSwitchingOrder(new Pair<Integer,Integer>(15,9),Arrays.asList(new Boolean[]{false}));
		    sectionSixteen.putSwitchingOrder(new Pair<Integer,Integer>(15,17),Arrays.asList(new Boolean[]{true}));
		    sectionSixteen.putSwitchingOrder(new Pair<Integer,Integer>(17,15),Arrays.asList(new Boolean[]{true}));
		    sectionSixteen.putSwitchingOrder(new Pair<Integer,Integer>(9,15),Arrays.asList(new Boolean[]{false}));


		    // add switching order for s11

		    sectionEleven.putSwitchingOrder(new Pair<Integer,Integer>(12,10),Arrays.asList(new Boolean[]{false}));
		    sectionEleven.putSwitchingOrder(new Pair<Integer,Integer>(12,18),Arrays.asList(new Boolean[]{true}));
		    sectionEleven.putSwitchingOrder(new Pair<Integer,Integer>(18,12),Arrays.asList(new Boolean[]{true}));
		    sectionEleven.putSwitchingOrder(new Pair<Integer,Integer>(10,12),Arrays.asList(new Boolean[]{false}));


		    // add switching order for s9

		    sectionNine.putSwitchingOrder(new Pair<Integer,Integer>(10,8),Arrays.asList(new Boolean[]{false}));
		    sectionNine.putSwitchingOrder(new Pair<Integer,Integer>(10,16),Arrays.asList(new Boolean[]{true}));
		    sectionNine.putSwitchingOrder(new Pair<Integer,Integer>(16,10),Arrays.asList(new Boolean[]{true}));
		    sectionNine.putSwitchingOrder(new Pair<Integer,Integer>(8,10),Arrays.asList(new Boolean[]{false}));

		    //add switching order for s10

		    sectionTen.putSwitchingOrder(new Pair<Integer,Integer>(9,3),Arrays.asList(new Boolean[]{false}));
		    sectionTen.putSwitchingOrder(new Pair<Integer,Integer>(9,11),Arrays.asList(new Boolean[]{true}));
		    sectionTen.putSwitchingOrder(new Pair<Integer,Integer>(11,9),Arrays.asList(new Boolean[]{true}));
		    sectionTen.putSwitchingOrder(new Pair<Integer,Integer>(3,9),Arrays.asList(new Boolean[]{false}));

		    //add switching order for s3

		    sectionThree.putSwitchingOrder(new Pair<Integer,Integer>(10,4),Arrays.asList(new Boolean[]{true}));
		    sectionThree.putSwitchingOrder(new Pair<Integer,Integer>(4,2),Arrays.asList(new Boolean[]{false}));
		    sectionThree.putSwitchingOrder(new Pair<Integer,Integer>(2,4),Arrays.asList(new Boolean[]{false}));
		    sectionThree.putSwitchingOrder(new Pair<Integer,Integer>(4,10),Arrays.asList(new Boolean[]{true}));

		    //add switching order for s4

		    sectionFour.putSwitchingOrder(new Pair<Integer,Integer>(19,3),Arrays.asList(new Boolean[]{false}));
		    sectionFour.putSwitchingOrder(new Pair<Integer,Integer>(5,3),Arrays.asList(new Boolean[]{true}));
		    sectionFour.putSwitchingOrder(new Pair<Integer,Integer>(3,5),Arrays.asList(new Boolean[]{true}));
		    sectionFour.putSwitchingOrder(new Pair<Integer,Integer>(3,19),Arrays.asList(new Boolean[]{false}));

		    // add switching order for s12

		    sectionTwelve.putSwitchingOrder(new Pair<Integer,Integer>(21,11),Arrays.asList(new Boolean[]{false}));
		    sectionTwelve.putSwitchingOrder(new Pair<Integer,Integer>(13,11),Arrays.asList(new Boolean[]{true}));
		    sectionTwelve.putSwitchingOrder(new Pair<Integer,Integer>(11,13),Arrays.asList(new Boolean[]{true}));
		    sectionTwelve.putSwitchingOrder(new Pair<Integer,Integer>(11,21),Arrays.asList(new Boolean[]{false}));




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
