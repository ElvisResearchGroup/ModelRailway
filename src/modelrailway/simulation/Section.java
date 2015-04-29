package modelrailway.simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import modelrailway.core.Controller;
/**
 * Section is a list of track pieces with pointers to next and previous sections
 * @author powleybenj
 *
 */
public class Section extends ArrayList<Track>{ // a section is a list of tracks , sections can detect trains
	public class RemovePair{
		public RemovePair(boolean retValue, Integer listedTrain){this.retValue = retValue; this.listedTrain = listedTrain;}
		public boolean retValue;
		public Integer listedTrain;
	}

	static{
		sectionNumberCounter = 0;
	}

	private Set<Movable> movableObjects = new HashSet<Movable>(); // the trains on the section.

	private Queue<Integer> entryRequests = new LinkedList<Integer>();

	/**
	 * Section takes a list of tracks in the section.
	 * @param tr
	 */
	private static int sectionNumberCounter;
	private int sectionNumber;
	public Section(List<Track> tr){
		sectionNumber = sectionNumberCounter;
		sectionNumberCounter++;
		this.addAll(tr);
	}
	/**
	 * The reserveSection method reserves the section for the supplied train. if there is nothing on the waiting list the section is reserved
	 * and true is returned. otherwise false is returned. The calling method should stop the train if false is returned.
	 * @param t
	 * @return
	 */
	public boolean reserveSection(Integer t){
		//System.out.println("Train: "+t.getID()+" reserving section for section "+this.getNumber() + "entryRequests :"+entryRequests+" movables: "+movableObjects);
		if(entryRequests.size() == 0 && movableObjects.size() == 0){
			entryRequests.offer(t);
			//System.out.println("return true");
			return true;
		}
		else {
			entryRequests.offer(t);
			return false;
		}

	}

	public boolean onRequestList(Integer t){
		if(entryRequests.contains(t)) return true;
		return false;
	}

	public static void resetCounter(){
		sectionNumberCounter = 0;
	}

	public int getNumber(){
		return sectionNumber;
	}

	public boolean addMovable(Movable m){
		if(entryRequests.peek() == m.getID()) entryRequests.poll();
		return movableObjects.add(m);
	}

	public boolean containsMovable(Movable m){
		return movableObjects.contains(m);
	}

	/**
	 * The removeMovable method removes a movable object from the list of Movable objects in the Section,
	 * then the method returns a pair containing the return value of the removal and a train on the waiting queue if there is one.
	 * The calling method is responsible for notifying the train that it may start.
	 * @param m
	 * @return
	 */
	public Section.RemovePair removeMovable(Movable m){

		boolean ret = movableObjects.remove(m);
		Integer tr = null;
		if(movableObjects.size() == 0){
		    tr = entryRequests.peek();
		}
		return new Section.RemovePair(ret,tr);
	}

	public boolean emptyMovable(){
		return (movableObjects.size() == 0);
	}

	public Set<Movable> getMovableSet(){
		return movableObjects;
	}
	public String toString(){

		return "section: "+super.toString();
	}
}
