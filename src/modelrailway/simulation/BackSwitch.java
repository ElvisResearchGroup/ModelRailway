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

	public BackSwitch(Track previous, Track next,Track entrance, Section section,int length,int altlength, int pointPos) {
		super(previous, next, entrance, null, section, length, altlength);
		switchCounter--;
		this.pointPos = pointPos;

		this.path = Direction.prev;
		// TODO Auto-generated constructor stub
	}

	public boolean isPrev(){
		return (path == Direction.prev);
	}
	public Track getNext(boolean onAlt){
		return super.getNext(false);
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

	@Override
	public int getSwitchID() {
		// TODO Auto-generated method stub
		return switchCount;
	}
}
