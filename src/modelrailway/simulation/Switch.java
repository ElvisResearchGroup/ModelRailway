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
	private Direction path;
	public Switch(Track previous, Track next,Track exit, Section section,int length,int altlength) {
		super(previous, next, null, exit, section, length, altlength);

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

    /**
     * A private method that gets the piece of track at the supplied direction leaving the current piece of track.
     * @param d
     * @return
     */
    private Track get(Direction d){
		if(d.toString().equals("exit")){
			return super.getNext(true);
		}
		return super.getNext(false);
	}

}
