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
	 * The current route the train is taking. This is essentially a sequence of
	 * sections linked together. This can be null if the train is stopped and
	 * not currently following a route.
	 */
	private Route route;
}
