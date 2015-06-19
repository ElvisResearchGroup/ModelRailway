package modelrailway.simulation;

public interface Switch {
	public int getSwitchID();
	public int setSwitchID(int i);
	public Track toggle();
}
