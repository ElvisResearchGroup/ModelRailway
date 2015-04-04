package modelrailway.simulation;
/**
 *
 * The Crossing class is a piece of track with a diamond crossing.
 * @author powleybenj
 *
 */

public class Crossing extends Track {
	private Track alternatePrevious;
	private Track alternateNext;
	private int altlength;
	/**
	 * create a new crossing here are previous and next paths for two different tracks. also there is the length of both sections of track.
	 * @param previous
	 * @param next
	 * @param alternatePrevious
	 * @param alternateNext
	 * @param section
	 * @param length
	 * @param altlength
	 */
	public Crossing(Track previous, Track next, Track alternatePrevious, Track alternateNext, Section section, int length ,int altlength) {
		super(previous, next, section, length);
		this.alternatePrevious = alternatePrevious;
		this.alternateNext = alternateNext;
		this.altlength = altlength;
		// TODO Auto-generated constructor stub
	}
	public Track getNext(boolean onAlt){
		if(onAlt) return this.alternateNext;
		return super.getNext(false);
	}

	public Track getPrevious(boolean onAlt){
		if(onAlt) return this.alternatePrevious;
		return super.getPrevious(false);
	}

	public int getDistance(Track next){
		if(this.getNext(true).equals(next)) return getDistance(true);
		else if (super.getNext(false).equals(next)) return getDistance(false);
		throw new WrongTrackException();
	}
	public int getDistance(boolean onAlt){
		if(onAlt) return altlength;
		return  super.getDistance(false);
	}
}
