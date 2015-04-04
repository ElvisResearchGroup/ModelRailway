package modelrailway.simulation;
/**
 * Switch is a piece of track with one entrance and two exits. the switch can be toggled.
 * @author powleybenj
 *
 */
public class Switch extends Track {

	private enum Direction{
		exit,next
	}
	private Track exit;
	private Direction path;
	private int altlength;
	public Switch(Track previous, Track next,Track exit, Section section,int length,int altlength) {
		super(previous, next, section, length);
		this.exit = exit;
		this.altlength = altlength;
		this.path = Direction.next;
		// TODO Auto-generated constructor stub
	}
	/**
	 * toggle toggles which piece of track we are exiting from the signals.
	 * @return
	 */
    public Track toggle(){
    	path = Direction.values()[(path.ordinal()+1) %2];
    	return get(path);
    }


    public Track getNext(boolean onAlt){
    	return get(path);
    }

    /**
     * A private method that gets the piece of track at the supplied direction leaving the current piece of track.
     * @param d
     * @return
     */
    private Track get(Direction d){
		if(d.toString().equals("exit")){
			return exit;
		}
		return super.getNext(false);
	}

	public int getDistance(Track next){
		if(this.getNext(true).equals(next)) return getDistance(true);
		else if (super.getNext(false).equals(next)) return getDistance(false);
		throw new WrongTrackException();
	}
	/**
	 * return the Distance of either the alternate route or the primary route through the piece of track.
	 * @param onAlt
	 */
	public int getDistance(boolean onAlt){
		if(onAlt) return altlength;
		return super.getDistance(false);
	}
}
