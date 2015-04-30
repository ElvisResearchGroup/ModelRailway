package modelrailway.simulation;

import java.util.ArrayList;

import modelrailway.core.Event;
import modelrailway.core.Section.RemovePair;


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
   private boolean backAlt = false;
   private Direction direction;
   private Integer id = -1;
   private Integer idCounter = -1;
   private modelrailway.core.Train trainOb = null;
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
	   id = idCounter;
	   idCounter--;
	   for(int x = 0; x < tr.length; x++){
		   if(!tr[x].getSection().containsMovable(this.getID())){
			   tr[x].getSection().addMovable(this.getID());
		   }
	   }
	   this.track = tr;
	   this.maxSpeed = maxSpeed;
	   this.onAlt = onAlt;
	   this.distance = distance;
	   this.length = length;
	   direction = Direction.forward;
	   if(track.length == 1){
		   this.track = new Track[]{tr[0],tr[0]};
	   }
   }

   public void setID(int id){
	   this.id = id;
   }

   public void setTrainOb(modelrailway.core.Train trainOb){
	   this.trainOb = trainOb;
   }
   public modelrailway.core.Train getTrainOb(){
	   return trainOb;
   }
   public Integer getID(){
	   return this.id;
   }

   /**
    * return the current speed of the train.
    * @return
    */
   public int getCurrentSpeed(){
	   return this.currentSpeed;
   }
   /**
    * return the maximum speed of the train.
    */
   public int getMaxSpeed(){
	   return this.maxSpeed;
   }

   /**
    * sets the maximum speed of the train.
    * @param int speed.
    */
   public void setMaxSpeed(int sp){
	   this.maxSpeed = sp;
   }
   /**
    * returns weather the track is on an alt segment.
    * @return
    */
   public boolean getOnAlt(){
	   return onAlt;
   }

   public boolean getBackAlt(){
	   return backAlt;
   }

   public int move(){
	   //System.out.println("old move");
	   return move(null);
   }

  /**
   * move causes the movable to move by its current speed. currently only moving forwards is supported.
   * @return
 * @throws SectionNotificationException
   */
   public int move(Event.Listener ls){ // returns the new distance
	   ///System.out.println("currentSpeed: "+currentSpeed);
	   ArrayList<Track> old = new ArrayList<Track>();
	   if(track[1] != null) old.add(track[1]);
	   old.add(track[0]);

	   if(isFowards()){
		  int distance2 = (distance + currentSpeed) % getFront().getDistance(onAlt);
		  if(distance2 < distance){ // we have moved onto a new segment
			   track[1] = track[0];
			   backAlt = onAlt;
			   track[0] = track[0].getNext(onAlt); // get next section of track based on weather we are on an alternate section.
			   onAlt = track[0].isAlt(track[1]);
		  }
		  distance = distance2;
		  if(distance >= getLength()){
			   track[1] = track[0]; // not on back segment
			   backAlt =onAlt;
		  }
	   } else{ // moving backwards
		  int newDistance = (getDistance() - currentSpeed);
		  if(newDistance >=0 && newDistance < length){ // move backwards. since we have moved backwards over a section we do not have track[1]
			   track[1] = track[0].getPrevious(track[0].getCurrentAlt(this));
			   backAlt = track[1].getCurrentAlt(this);
			   distance = newDistance;
		  }
		  else if(newDistance <0 && getDistance() < length) { // remove track[0]
			  //System.out.println("newDistance< 0 and getDistance < length");
			   track[0] = track[1];
			   onAlt = backAlt;
			   distance = track[0].getDistance(track[0].getCurrentAlt(this)) + newDistance;
			   if(distance < length){
				   track[1] = track[0].getPrevious(backAlt);
			   }
		  }
		  else if(newDistance < 0 && getDistance() >= length){ // A train cannot move further than one track length so
			 // System.out.println("newDistance < 0 and getDistance >= length");
			  Track temp = track[0];
			  track[0] = track[0].getPrevious(track[0].getCurrentAlt(this));
			  track[1] = track[0];
			  backAlt = track[0].getCurrentAlt(this);
			  distance = track[0].getDistance(track[0].getCurrentAlt(this))+newDistance;
		  }
		  else if(newDistance >=0 && newDistance >= length){
			 distance = newDistance;
		  }
	   }
	   onAlt = track[0].getCurrentAlt(this); // adjust for points.
	   if(track[1] != track[0]){
		   backAlt = track[1].getCurrentAlt(this);
	   } else{
		   backAlt = onAlt;
	   }
	   // update sectioning

	   for(Track t: old){
		   RemovePair pairs = t.getSection().removeMovable(this.getID());
		   if(t.getAltSection() != null){
			   RemovePair pair = t.getAltSection().removeMovable(this.getID());
		   }
	   }
	   if(onAlt && track[0].getAltSection() != null){
		   track[0].getAltSection().addMovable(this.getID());

	   } else{
	       track[0].getSection().addMovable(this.getID());
	   }
	   if(backAlt && track[1].getAltSection() != null){
		   track[1].getAltSection().addMovable(this.getID());

	   } else{
	       track[1].getSection().addMovable(this.getID());

	   }
	   return distance;
   }

   /**
    * Return the length.
    * @return
    */
   public int getLength() {
	// TODO Auto-generated method stub
	return length;
   }
   /**
    *  get the distance from the start of the front section to the front of the moving object.
    * @return
    */
   public int getDistance(){
	   return distance;
   }
   /**
    * get the distance from the start of the front section to the back of the moving object.
    * @return
    */
   public int getBackDistance(){
	   return Math.abs(distance-getLength());

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
	   //System.out.println("maxSpeed: "+maxSpeed);
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
	   if(track[1] == track[0]) return getFront();
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
