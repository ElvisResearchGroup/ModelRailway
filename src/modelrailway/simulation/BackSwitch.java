package modelrailway.simulation;

import modelrailway.core.Section;


public class BackSwitch extends Track implements Switch{

	private enum Direction{
		enter,prev
	}
	private Direction path;
	private int pointPos; // point position from the back
	private static int switchCounter = -1;
	private int switchCount = switchCounter;
	/**
	 * A back section has a previous section of track, an entrance and a next section of track.
	 * @param previous
	 * @param next
	 * @param entrance
	 * @param section
	 * @param length
	 * @param altlength
	 * @param pointPos
	 */
	public BackSwitch(Track previous, Track next,Track entrance, Section section,int length,int altlength, int pointPos) {
		super(previous, next, entrance, null, section, length, altlength);
		switchCounter--;
		this.pointPos = pointPos;

		this.path = Direction.prev;
		// TODO Auto-generated constructor stub
	}
	/**
	 * isPrev() returns true when the path is set to direct trains to the previous section rather than to the alternate prevous section.
	 * @return
	 */
	public boolean isPrev(){
		return (path == Direction.prev);
	}

	public Track getNext(boolean onAlt){
		return super.getNext(false);
	}

	public int setSwitchID(int i){
		switchCount = i;
		return i;
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
    	//when this is the back section
    	if(m.getBack() == this ){
    		if(m.isFowards()){
    		    if (m.getBackDistance() < pointPos){ // point position from the back.
    		        return super.getCurrentAlt(m);
    		    } else {
    		     	return this.path == Direction.enter;
    		    }
    		}
    		else{
    			if(m.getBackDistance() < pointPos){
    				return this.path == Direction.enter;
    			} else {
    				return super.getCurrentAlt(m);
    			}
    		}
    		//when this is the front section
    	} else if (m.getFront() == this){ // back is not the current piece of track.
    		if(m.getBack() == this.getPrevious(false)){ // are we on the alternate path
    			return false;
    		}
    		else if (m.getBack() == this.getPrevious(true)){ // are we on the current path.
    			return true;
    		}
    	}
    	throw new WrongTrackException(); // this segment of track is neither on the front or the back section.

    	//return false; // deadcode

	}

	@Override
	public int getSwitchID() {
		// TODO Auto-generated method stub
		return switchCount;
	}
}
