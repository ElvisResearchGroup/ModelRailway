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


	static{
		sectionNumberCounter = 0;
	}

	private Set<Movable> movableObjects = new HashSet<Movable>(); // the trains on the section.

	private Queue<Train> entryRequests = new LinkedList<Train>();

	private Controller ctl = null;

	private Map<Integer,Train> trains;

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

	public Section(List<Track> tr, Controller ctl, Map<Integer,Train> trains){
		 this.ctl = ctl;
		 this.trains = trains;
		 sectionNumber = sectionNumberCounter;
		 sectionNumberCounter ++;
		 this.addAll(tr);
	}

	public boolean reserveSection(Train t){
		if(entryRequests.size() == 0 && entryRequests.size() == 0){
			return true;
		}
		else {
			entryRequests.offer(t);
			return false;
		}

	}

	public static void resetCounter(){
		sectionNumberCounter = 0;
	}

	public int getNumber(){
		return sectionNumber;
	}

	public boolean addMovable(Movable m){
		if(entryRequests.peek() == m) entryRequests.poll();
		return movableObjects.add(m);
	}

	public boolean containsMovable(Movable m){
		return movableObjects.contains(m);
	}

	public boolean removeMovable(Movable m){
		boolean ret = movableObjects.remove(m);
		if(ctl != null){ // else dont send the notification.
		if(movableObjects.size() == 0){
			Train tr = entryRequests.peek();
			Integer i = null;
			for(Map.Entry<Integer, Train> ent : trains.entrySet()){
				if(ent.getValue() == tr) i = ent.getKey(); // find the id of the train from the supplied list of trains.
			}
			tr.notifySectionFree(i,ctl);
		}
		}
		return ret;
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
