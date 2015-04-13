package modelrailway.simulation;


public class MergeSwitch extends Track{

	private enum Direction{
		enter,prev
	}
	private Direction path;
	private int pointPos; // point position from the back

	public MergeSwitch(Track previous, Track next,Track entrance, Section section,int length,int altlength, int pointPos) {
		super(previous, next, entrance, null, section, length, altlength);
		this.pointPos = pointPos;

		this.path = Direction.prev;
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
		if(d.toString().equals("enter")){
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
    	if(m.getBack() == this ){
    		if(m.getBackDistance() < pointPos){ // point position from the back.
    		   
    		   return super.getCurrentAlt(m);
    		} else {
    			return this.path == Direction.enter;
    		}
    	} else if (m.getFront() == this){ // back is not the current piece of track.
    		if(m.getBack() == this.getPrevious(false)){ // are we on the alternate path
    			return false;
    		}
    		else if (m.getBack() == this.getPrevious(true)){ // are we on the current path.
    			return true;
    		}
    	} 
    	throw new WrongTrackException();
    	
    	//return false; // deadcode

	}
}
