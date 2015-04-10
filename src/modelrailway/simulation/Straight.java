package modelrailway.simulation;

import java.util.ArrayList;

/**
 * Straight is a piece of track with one entrance and one exit.
 * @author powleybenj
 *
 */
public class Straight extends Track {

	public static class StraightRing extends RingRoute{
		public StraightRing(Track head) {
			super( head);
			// TODO Auto-generated constructor stub
		}
		public Track ringTrack(int numTracks, int trackSegmentLength){ // produce a ring.
			
		    Section section = new Section(new ArrayList<Track>() );
		    Track tr = new Straight(getHead(),getHead(),section,trackSegmentLength); 
		    section.add(tr); // add a track
			this.trivialRing(tr);
			for(int x = 2; x < numTracks; x++){ 
				Track head = getHead(); // get the head
				Section sinsert = new Section(new ArrayList<Track>()); // the section to insert
				Track insert = new Straight(null,null,section,trackSegmentLength); // the track to insert
				sinsert.add(insert); // add a track
				insertBetween(head,false,insert,false); // insert the insert piece between head and head.getNext(false);
			}
			return getHead();
		}
		
	}
	public Straight(Track previous, Track next, Section section, int length) {
		super(previous, next,null, null, section, length, 0);
		// TODO Auto-generated constructor stub
	}
	
	public Track getPrevious(boolean onAlt){
		return super.getPrevious(false);
	}

	/**
	 * get the next piece of track.
	 * @param onAlt
	 * @return
	 */
	public Track getNext(boolean onAlt){
		return super.getNext(false);
	}
	
	
	/**
	 * get the section that the piece of track is part of.
	 * @return
	 */
	public Section getSection(){
	    return super.getSection();
	}
	/**
	 * get the distance along the piece of track using the next piece of track to work out which route we are taking along the track.
	 * @param next
	 * @return
	 */
	public int getDistance(Track next){
		if (this.getNext(false) != null && this.getNext(false).equals(next)) return getDistance(false);
		throw new WrongTrackException();
	}
	/**
	 * get the distance along the piece of track using a boolean variable to instruct whether we are on the primary route or a secondary route.
	 * @param onAlt
	 * @return
	 */
	public int getDistance(boolean onAlt){
		return super.getDistance(false);
	}
	
	/**
	 *  returns weather a piece of track is connecting to the primary route or to a secondary route.
	 *  @return
	 */
	public boolean isAlt(Track track) {
		if(track == getNext(false) || track == getPrevious(false)){
			return false;
		}
		throw new WrongTrackException();
	}

}
