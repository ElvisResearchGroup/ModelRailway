package modelrailway.simulation;
/**
 * Switch is a piece of track with one entrance and two exits. the switch can be toggled.
 * @author powleybenj
 *
 */
public class ForwardSwitch extends Track {

	private enum Direction{
		exit,next
	}
	private Direction path;
	private int pointPos;
	public ForwardSwitch(Track previous, Track next,Track exit, Section section,int length,int altlength, int pointPos) {
		super(previous, next, null, exit, section, length, altlength);
		this.pointPos = pointPos;

		this.path = Direction.next;
		// TODO Auto-generated constructor stub
	}

	public Track getPrevious(boolean onAlt){
		return super.getPrevious(false);
	}
	
	public boolean isNext(){
		return (path == Direction.next);
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
    /**
     * returns true when the movable object is on the alternate path and false when it is on the primary path.
     * @param m
     * @return
     */
    public boolean getCurrentAlt(Movable m){
    	if(m.getFront() == this ){
    		if(m.isFowards()){
    		   if(m.getDistance() >= pointPos){
    		      return super.getCurrentAlt(m);
    		   } else {
    			  return this.path == Direction.exit;
    		   }
    		} else {
    		   if(m.getDistance() >= pointPos){
    			  return this.path == Direction.exit;
    		   }
    		   else{
    			  return super.getCurrentAlt(m);
    		   }

    		}
    	} else if (m.getBack() == this){
    		if(m.getFront() == this.getNext(false)) return false;
    		else if (m.getFront() == this.getNext(true)) return true;
    	}
    	throw new WrongTrackException();
	}

}
