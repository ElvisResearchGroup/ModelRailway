package modelrailway.simulation;

import modelrailway.core.Section;
import modelrailway.simulation.Track;

public class Buffer extends Track {
	/**
	 * A Buffer has one exit, the exit can either be next or previous, The buffer has one section, and length
	 * @param exit
	 * @param into
	 * @param section
	 * @param length
	 */
	public Buffer(Track exit, boolean into, Section section, int length) {
		super(frontFirst(exit,!into), frontFirst(exit,into), null , null , section, length, 0);

	}
	private static Track frontFirst(Track exit, boolean into){
		if (into) return exit;
		return null;
	}
}
