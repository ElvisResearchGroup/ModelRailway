package modelrailway.simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

	public int getNumber(){
		return sectionNumber;
	}

	public boolean addMovable(Movable m){
		return movableObjects.add(m);
	}

	public boolean containsMovable(Movable m){
		return movableObjects.contains(m);
	}

	public boolean removeMovable(Movable m){
		return movableObjects.remove(m);
	}

	public boolean emptyMovable(){
		return (movableObjects.size() == 0);
	}
}
