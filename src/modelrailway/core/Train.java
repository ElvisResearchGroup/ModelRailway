package modelrailway.core;

/**
 * Represents a complete train on the railway, including any carriages or
 * rolling stock.
 * 
 * @author djp
 * 
 */
public class Train {
	/**
	 * Identifies if the train is facing in the clockwise direction (true) or
	 * the anti-clockwise direction (false). This is important as otherwise you
	 * can tell whether to move it forwards or backwards.
	 */
	private boolean orientation;

	/**
	 * Identifies which section the train is in. This information is updated as
	 * the train moves around the track from the sectioning sensors.
	 */
	private int section;

	/**
	 * Create the train with a given section and orientation.
	 * 
	 * @param section
	 * @param orientation
	 */
	public Train(int section, boolean orientation) {
		this.setSection(section);
		this.setOrientation(orientation);
	}

	/**
	 * Get the current orientation of the train. On networks which have a
	 * "reversing loop", it is possible for the orientation to change.
	 * 
	 * @return
	 */
	public boolean currentOrientation() {
		return orientation;
	}

	/**
	 * Set the current orientation of the train.
	 * 
	 * @param orientation
	 */
	public void setOrientation(boolean orientation) {
		this.orientation = orientation;
	}

	/**
	 * Get the current section of the train.  
	 * 
	 * @return
	 */
	public int currentSection() {
		return section;
	}

	/**
	 * Set the current section of the train.
	 * 
	 * @param section
	 */
	public void setSection(int section) {
		this.section = section;
	}
}
