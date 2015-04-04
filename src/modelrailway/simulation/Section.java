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
public class Section extends ArrayList<Track>{
	private Section next;
	private Section previous;
	private Section altNext;
	private Section altPrev;

	/**
	 * Section takes a list of tracks in the section.
	 * @param tr
	 * @param next
	 * @param previous
	 */
	public Section(List<Track> tr,Section next, Section previous){
		this.addAll(tr);
		this.next = next;
		this.previous = previous;

	}
	/**
	 * Section takes a list of tracks in the section.
	 * @param tr
	 * @param next
	 * @param previous
	 * @param altNext
	 * @param altPrev
	 */
	public Section(List<Track> tr, Section next, Section previous, Section altNext, Section altPrev){
		this.addAll(tr);
		this.next = next;
		this.previous = previous;
		this.altNext = altNext;
		this.altPrev = altPrev;
	}

	/**
	 * Next sections returns a list of the next sections.
	 * @return
	 */
	public Set<Section> nextSections(){
		Set<Section> sectionSet = new HashSet<Section>();
		sectionSet.add(next);
		if(altNext != null) sectionSet.add(altNext);
		return sectionSet; // return the previous sections
	}
	/**
	 * Previous sections returns a list of the previous sections.
	 * @return
	 */
	public Set<Section> previousSection(){
		Set<Section> sectionSet = new HashSet<Section>();
		sectionSet.add(previous);
		if(altPrev != null) sectionSet.add(altPrev);
		return sectionSet; // return previous sections
	}

}
