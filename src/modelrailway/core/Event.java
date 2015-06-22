package modelrailway.core;

/**
 * Provides the basic mechanism by which a system controller can responds to
 * events, updates the model and take actions. This provides a separation
 * between the model and the actual hardware interface.
 *
 * @author djp
 *
 */
public interface Event {

	/**
	 * An even listener is used to handle events of different kinds on the
	 * railway.
	 *
	 * @author djp
	 *
	 */
	public interface Listener {

		/**
		 * Notify the listener that an event has occurred.
		 *
		 * @param e
		 */
		public void notify(Event e);
	}

	/**
	 * Indicates that the railway was powered on or off.
	 *
	 * @author djp
	 *
	 */
	public static final class PowerChanged implements Event {
		/**
		 * True if the railway was powered on, false otherwise.
		 */
		private final boolean powerOn;

		public PowerChanged(boolean powerOn) {
			this.powerOn = powerOn;
		}

		public boolean isPowerOn() {
			return powerOn;
		}

		public String toString() {
			if(powerOn) {
				return "Railway was powered on.";
			} else {
				return "Railway was powered off.";
			}
		}
	}

	/**
	 * Indicates that a locomotive moved into or out of a given section in the
	 * railway.
	 *
	 * @author djp
	 *
	 */
	public static final class SectionChanged implements Event {
		/**
		 * The section in which the event took place.
		 */
		private final int section;

		/**
		 * True indicates a locomotive moved into the section (rising edge), and
		 * false indicates it moved out (falling edge).
		 */
		private final boolean into;

		public SectionChanged(int section, boolean into) {
			this.section = section;
			this.into = into;
		}

		public boolean getInto() {
			return into;
		}

		public int getSection() {
			return section;
		}

		public String toString() {
			if(into) {
				return "Locomotive moved into Section " + section + ".";
			} else {
				return "Locomotive moved out of Section " + section + ".";
			}
		}
	}

	/**
	 * Indicates a change to a given locomotives throttle control.
	 *
	 * @author djp
	 *
	 */
	public static final class SpeedChanged implements Event {
		/**
		 * The locomotive whose throttle was changed.
		 */
		private final int locomotive;

		/**
		 * The speed setting of the locomotive.
		 */
		private final float speed;

		public SpeedChanged(int locomotive, float speed) {
			this.locomotive = locomotive;
			this.speed = speed;
		}

		public int getLocomotive() {
			return locomotive;
		}

		public float getSpeed() {
			return speed;
		}

		public String toString() {
			int percent = (int) (speed * 100f);
			return "Locomotive " + locomotive + " now moving at speed "
					+ percent + "%.";
		}
	}

	/**
	 * Indicates a change to a given locomotive's direction.
	 *
	 * @author djp
	 *
	 */
	public static final class DirectionChanged implements Event {
		/**
		 * The locomotive whose throttle was changed.
		 */
		private final int locomotive;

		/**
		 * The direction for the locomotive (true = forwards, false = backwards).
		 */
		private final boolean direction;

		public DirectionChanged(int locomotive, boolean direction) {
			this.locomotive = locomotive;
			this.direction = direction;
		}

		public int getLocomotive() {
			return locomotive;
		}

		public boolean getDirection() {
			return direction;
		}

		public String toString() {
			if (direction) {
				return "Locomotive " + locomotive + " now going forwards.";
			} else {
				return "Locomotive " + locomotive
						+ " going backwards at speed now going backwards.";
			}
		}
	}

	/**
	 * Instruct a train to perform an emergency stop.
	 *
	 * @author David J. Pearce
	 *
	 */
	public class EmergencyStop implements Event {
		/**
		 * The locomotive whose throttle was changed.
		 */
		private final int locomotive;

		public EmergencyStop(int locomotive) {
			this.locomotive = locomotive;
		}

		public int getLocomotive() {
			return locomotive;
		}

		public String toString() {
			return "Locomotive " + locomotive + " performing emergency stop.";
		}
	}

	/**
	 * Indicates a change to a given turnout's status
	 *
	 * @author djp
	 *
	 */
	public static final class TurnoutChanged implements Event {
		/**
		 * The turnout being changed
		 */
		private final int turnout;

		/**
		 * Indicates whether or not the turnout is thrown
		 */
		private final boolean thrown;

		public TurnoutChanged(int turnout, boolean thrown) {
			this.turnout = turnout;
			this.thrown = thrown;
			//if(turnout == 6 && thrown == false) throw new RuntimeException("Wrong TurnoutChanged Event Created");
		}

		public int getTurnout() {
			return turnout;
		}

		public boolean getThrown() {
			return thrown;
		}

		public String toString() {
			if (thrown) {
				return "Turnout " + turnout + " thrown.";
			} else {
				return "Turnout " + turnout + " closed.";
			}
		}
	}

}
