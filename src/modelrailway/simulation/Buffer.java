package modelrailway.simulation;

import modelrailway.core.Section;
import modelrailway.simulation.Track;

public class Buffer extends Track {
	/**
	 * A Buffer has one exit, the exit can either be next or previous, The buffer has one section, and length
	 * The boolean value into tells us which orientation to produce the buffer. when info is false there is a previous section and no next.
	 * When into is true there is a next and no previous secion.
	 * @param exit
	 * @param into
	 * @param section
	 * @param length
	 */
	public Buffer(Track exit, boolean into, Section section, int length) {
		super(frontFirst(exit,!into), frontFirst(exit,into), null , null , section, length, 0);

	}
	/**
	 * There is only one exit in a buffer. This method returns the exit when we are entering the Buffer.
	 * and nothing when into is false. We use it when creating the buffer in the constructor to toggle which parameter we give
	 * the exit to and which parameter is null.
	 * @param exit
	 * @param into
	 * @return
	 */
	private static Track frontFirst(Track exit, boolean into){
		if (into) return exit;
		return null;
	}
}
