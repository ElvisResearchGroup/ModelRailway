package modelrailway.simulation;
/**
 * An abstract class to model pieces of track.
 * @author powleybenj
 *
 */
public abstract class Track {
	private static final int PREVIOUS=0;
	private static final int NEXT=1;
	private Track previous;
	private Track next;
	private int length;
	private Section section;
	/**
	 * produces a section of track.
	 * @param previous
	 * @param next
	 * @param section
	 * @param length
	 */
	public Track(Track previous, Track next,Section section, int length){
		this.previous = previous;
		this.next = next;
		this.section = section;
		this.length = length;
	}
	/**
	 * returns the previous piece of track that connected to this section of track for the primary route through the track
	 * @return
	 */
	public Track getPrevious(boolean onAlt){
		return previous;
	}
	/**
	 * get the next piece of track.
	 * @param onAlt
	 * @return
	 */
	public Track getNext(boolean onAlt){
		return next;
	}
	/**
	 * get the section that the piece of track is part of.
	 * @return
	 */
	public Section getSection(){
	    return section;
	}
	/**
	 * get the distance along the piece of track using the next piece of track to work out which route we are taking along the track.
	 * @param next
	 * @return
	 */
	public int getDistance(Track next){
		 return length;
	}
	/**
	 * get the distance along the piece of track using a boolean variable to instruct whether we are on the primary route or a secondary route.
	 * @param onAlt
	 * @return
	 */
	public int getDistance(boolean onAlt){
		return length;
	}
	/**
	 *  returns weather a piece of track is connecting to the primary route or to a secondary route.
	 *  @return
	 */
	public boolean isAlt(Track track) {
		return false;
	}

}
