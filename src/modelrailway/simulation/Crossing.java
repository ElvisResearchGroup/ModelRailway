package modelrailway.simulation;

import modelrailway.core.Section;

/**
 *
 * The Crossing class is a piece of track with a diamond crossing.
 * @author powleybenj
 *
 */

public class Crossing extends Track {

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
	public Crossing(Track previous, Track next, Track alternatePrevious, Track alternateNext, Section section, Section altSection, int length ,int altlength) {
		super(previous, next, alternatePrevious, alternateNext, section, altSection, length, altlength);
		// TODO Auto-generated constructor stub
	}

}
