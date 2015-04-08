package modelrailway.simulation;


/**
 * Movable object abstract class used to model trains,locomotives, rolling stock.
 * @author powleybenj
 *
 */
public abstract class Movable {
   private enum Direction{
	   back,forward
   }
   private Track[] track; // track segments the item spans; may span up to two segments of track.
   private int distance; // distance along the track segment
   private int length; // length of the object
   private int currentSpeed = 0;
   private int maxSpeed = 0;
   private boolean onAlt = false; // are we on alt segment.
   private Direction direction;
   /**
    * Create a movable item at the specified distance along a track segment with the specified length.
    * The track array must contain two connected segments and must be valid before being supplied to the constructor.
    *
    * @param distance
    * @param length
    * @param tr
    * @param maxSpeed The maxSpeed is the max distance traveled per clock tick.
    */
   public Movable(Track[] tr, int distance,int length, int maxSpeed,boolean onAlt){
	   this.track = tr;
	   this.maxSpeed = maxSpeed;
	   this.onAlt = onAlt;
	   direction = Direction.forward;

   }
/**
 * move causes the movable to move by its current speed. currently only moving forwards is supported.
 * @return
 */
   public int move(){ // returns the new distance
	   if(isFowards()){
		   int distance2 = (distance + currentSpeed) % getFront().getDistance(onAlt);
		   if(distance2 < distance){ // we have moved onto a new segment
			   track[1] = track[0];
			   track[0] = track[0].getNext(onAlt); // get next section of track based on wheather we are on an alternate section.
			   onAlt = track[0].isAlt(track[1]);
		   }
	
		   distance = distance2;
		   if(distance > getLength()) track[1] = null; // not on back segment
	   } else{ // moving backwards
		   int distance2= (distance - currentSpeed); // adjust distance2
		   if(distance2 < 0){ // we need to move backwards by a track piece
			   Track temp = track[0]; // keep hold of old track piece
			   track[0] = track[0].getPrevious(onAlt); // get the previous section of track. 
			   boolean newalt = track[0].isAlt(temp); // are we on the alternate section of the new section of track ?
			   if(distance2+track[0].getDistance(newalt) < length){ // check that we are all on the piece
				   track[1]=track[0].getPrevious(newalt); // if not then set the track[1] array index to the piece our rear end is on
			   }
			   else{
				   track[1]=null; // otherwise the entire object is on the first piece, track[0]
			   }
			   onAlt = newalt; // set whether we are on an alternate section or not
			   distance = distance2+track[0].getDistance(newalt); // reset the distance
		   }
		   else{
		      distance = distance2; // the distance was not negative so we are still on the same track piece
		   }
	   }
	   return distance;
   }

   public int getLength() {
	// TODO Auto-generated method stub
	return length;
   }
   public int getDistance(){
	   return distance;
   }
   /**
    * get the distance from the start of the piece of track to the back of the movable object
    * @return
    */
   public int getBackDistance(){
	   int backDist = Math.abs(distance-getLength());
	   if(getBack() == getFront()) return backDist;
	   return getBack().getDistance(getFront())-backDist;
   }
/**
 * stops the movable object. currently there is no acceleration.
 * @return
 */
   public int stop(){
	   currentSpeed = 0;
	   return distance;
   }
   /**
    * starts the movable object. Currently there is no acceleration
    * @return
    */
   public int start(){
	   currentSpeed = maxSpeed;
	   return distance;
   }
   /**
    * isFowards checks whether the movable object is moving forwards or not.
    * @return
    */
   public boolean isFowards(){
	   return direction == Direction.forward;
   }

   /**
    * return the front piece of track that the object is on.
    * @return
    */
   public Track getFront(){
	   return track[0];
   }
   /**
    * return the back piece of track the object is on.
    * @return
    */
   public Track getBack(){
	   if(track[1] == null) return getFront();
	   return track[1];
   }
   
   /**
    * toggles whether the object moves forwards or backwards
    */
   public void toggleDirection(){
	   if(isFowards()) direction = Direction.back;
	   else direction = Direction.forward;
   }

}
