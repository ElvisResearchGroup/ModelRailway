package modelrailway.core;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import modelrailway.simulation.Track;
import modelrailway.util.Pair;
/**
 * Section is a list of track pieces with pointers to next and previous sections
 * Section also maintains both a set of movable object id's inside the section,
 * and a queue of movable object id's that want to obtain a lock to the section.
 * @author powleybenj
 *
 */
public class Section extends CopyOnWriteArrayList<Track>{ // a section is a list of tracks , sections can detect trains
	static{
		sectionNumberCounter = 0;
	}
	// The set of movable objects keeps track of the movable objects that are inside the section

	private Set<Integer> movableObjects = new ConcurrentSkipListSet<Integer>(); // the trains on the section.
	private Queue<Integer> entryRequests = new ConcurrentLinkedQueue<Integer>(); // entry requests on the section.
	private Map<Pair<Integer,Integer>,List<Boolean>> turnstyleTable = new ConcurrentHashMap<Pair<Integer,Integer>, List<Boolean>>();
	//A map from to and from sections. to turnstyle switching order in the order that the train is encountered.
	// true in the turnstyle table indicates altRoute false indicates standard Route.

	private static int sectionNumberCounter;
	private int sectionNumber;
    /**
     * Section takes a list of tracks that are being given to the section.
     * The section constructor automatically allocates an id number that is used as the ID of the section.
     * The ID number can be set later by hand also however the automatically generated ID numbers ensure that IDs are unique.
     * @param tr
     */
	public Section(List<Track> tr){
		sectionNumber = sectionNumberCounter;
		sectionNumberCounter++;
		this.addAll(tr);
	}
	public Queue<Integer> getEntryRequests(){
		return entryRequests;
	}

	public List<Boolean> retrieveSwitchingOrder(Pair<Integer,Integer> pair){
		return turnstyleTable.get(pair);
	}

	public void putSwitchingOrder(Pair<Integer,Integer> pair, List<Boolean> val){
		turnstyleTable.put(pair, val);
	}

	public void setSectionNumber(int sectionNumber){
		this.sectionNumber = sectionNumber;
	}

	/**
	 * The reserveSection method reserves the section for the supplied train. if there is nothing on the waiting list the section is reserved
	 * and true is returned. otherwise false is returned. The calling method should stop the train if false is returned.
	 * @param t
	 * @return
	 */
	public boolean reserveSection(Integer t){
		synchronized(movableObjects){
		synchronized(entryRequests){
		//System.out.println("Train: "+t +" reserving section for section "+this.getNumber() + "entryRequests :"+entryRequests+" movables: "+movableObjects);
		//if there are no requests for access to the section, and there are no objects inside the section we offer the train object to the list of requests
		// and return true. Returning true signifies that the train attempting to reserve the section does not need to stop.
		if((entryRequests.size() == 0 || entryRequests.peek() == t)&& movableObjects.size() == 0){

			if(entryRequests.peek() != t) entryRequests.offer(t);
			//System.out.println("return true");
			return true;
		}
		else { // else if either there were other entry requests or there were objects inside the section we add the supplied
			// train  to the queue of entry requests then return false. Returning false signifies that the train requesting access to
			// the section needs to stop.
			System.out.println("entryRequests: "+ entryRequests.size()+", movableObjects.size(): "+ movableObjects.size());
			System.out.println("entryRequests: "+entryRequests.toString());
			if(entryRequests.peek() != t) entryRequests.offer(t);
			return false;
		}
		}
		}

	}
	public boolean isQueueEmpty(){
		//System.out.println("entryRequests.size(): "+entryRequests.size()+" entryRequests: "+entryRequests.toString()+" number: "+this.sectionNumber);
		return (entryRequests.size() == 0);
	}

	/**
	 * remove from queue returns a pair, the first element is true if an element is removed, The second element is the head of the queue of entryRequests.
	 * @param t
	 * @return
	 */
	public Pair<Boolean,Integer> removeFromQueue(Integer t){
		if(entryRequests.size() == 0) return null;
		//System.out.println("entryRequests: "+entryRequests.toString()+" t: "+t+" number: "+this.sectionNumber+" thread: "+Thread.currentThread().getId());
		if(entryRequests.contains(t)){
			boolean removal = entryRequests.remove(t);
			if(entryRequests.size() == 0) return new Pair<Boolean, Integer>(removal,null); // if there are no elements left.
			return new Pair<Boolean,Integer>(entryRequests.remove(t), entryRequests.peek()); // the next element after removal.
		}
		else{
			return new Pair<Boolean,Integer>(false,entryRequests.peek());
		}
	}

	/**
	 * The onRequestList method checks if the ID for the train provided is on the list of trains that are queued for access to the section
	 * @param t
	 * @return
	 */
	public boolean onRequestList(Integer t){
		synchronized(entryRequests){
		if(entryRequests.contains(t)) return true;
		return false;
		}
	}
	/**
	 * The resetCounter method sets the sectionNumberCounter to zero.
	 * This is used in unit testing when we wish to create another track.
	 */
	public static void resetCounter(){
		sectionNumberCounter = 0;
	}
	/**
	 * getNumber returns an integer which is the section number.
	 * @return
	 */
	public int getNumber(){
		return sectionNumber;
	}
	/**
	 * Add movable adds a movable object to the section.
	 * When there are some entry requests and the supplied integer the next train id to be polled we remove the train from the entry requests.
	 * @param m
	 * @return
	 */
	public boolean addMovable(Integer m){
		//System.out.println(entryRequests);
		synchronized(entryRequests){
		synchronized(movableObjects){
		if(entryRequests.size() != 0){
		  if(m != null && entryRequests.peek() == m) entryRequests.poll();
		  }
		//throw new RuntimeException("adding a movable object");
		return movableObjects.add(m);
		}
		}
	}
	/**
	 * Check whether the supplied trainID is in the list of movableObjects.
	 * @param m
	 * @return
	 */
	public boolean containsMovable(Integer m){
		synchronized(movableObjects){
		return movableObjects.contains(m);
		}
	}

	/**
	 * The removeMovable method removes a movable object from the list of Movable objects in the Section,
	 * then the method returns a pair containing the return value of the removal and a train on the waiting queue if there is one.
	 * The calling method is responsible for notifying the train that it may start.
	 * @param m
	 * @return
	 */
	public Pair<Boolean,Integer>  performMovableExit(Integer m){
	   synchronized(movableObjects){
		boolean ret = movableObjects.remove(m);
		Integer tr = null;
		if(movableObjects.size() == 0){
		    tr = entryRequests.peek();
		}
		return new Pair<Boolean,Integer>(ret,tr);
	   }
	}

	/**
	 * emptyMovable returns wheather there are no trains or other movables in the section.
	 * @return
	 */
	public boolean emptyMovable(){
		synchronized(movableObjects){
		return (movableObjects.size() == 0);
		}
	}
	/**
	 * returns the set of movables in the section.
	 * @return
	 */
	public Set<Integer> getMovableSet(){
		return movableObjects;
	}

	/**
	 * Returns a string representation of the section.
	 * @return
	 */
	public String toString(){

		return "section: "+super.toString()+"  movables: "+movableObjects.toString() +" entryRequests: "+entryRequests.toString();
	}

	public boolean equals(Object o){
		if(o instanceof Section){
		   return (this.sectionNumber == ((Section)o).sectionNumber);
		}
		return false;
	}

	public int hashCode(){
		return this.sectionNumber;
	}
}
