package modelrailway.core;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A route represents a potential path through the rail network that a train
 * might take. A route is simply a sequence of sections which are to be
 * followed. Routes can be "loops", in which case the last section must follow
 * into the first section.
 *
 * @author David J. Pearce
 *
 */
public class Route {

	/**
	 * The list of section numbers making up this route. These should be verfied
	 * as being sequential sections on the track.
	 */
	private int[] sections;

	/**
	 * Determine whether this route is a continous loop or not.
	 */
	private boolean isLoop;

	private int stopSection;


	/**
	 * Construct a give route.
	 *
	 * @param isLoop
	 * @param sections
	 */
	public Route(boolean isLoop, int... sections) {
		this.isLoop = isLoop;
		this.sections = sections;
	}

	public Integer firstSection() {
		return sections[0];
	}

	public int lastSection() {
		if (isLoop) {
			return -1;
		} else {
			return sections[sections.length - 1];
		}
	}

	public boolean isALoop(){
		return isLoop;

	}

	/**
	 * Determine the next section in this route after a given section. the
	 * <code>null</code> value is returned if this route is not a loop, the
	 * given section is the last section. An exception is raised if the given
	 * section is not in this route.
	 *
	 * @param section
	 * @return
	 */
	public Integer nextSection(int section) {
		// Go through each section looking for a match.
		for (int i = 0; i != sections.length; ++i) {
			if (sections[i] == section) {
				// Found matching section. Now, determine next section (if it
				// exists).
				i = (i + 1) % sections.length;
				// Check whether we reached the end of the route or not
				if (i == 0 && !isLoop) {
					// Yes, we did.
					return null;
				} else {
					// No, there is still more.
					return sections[i];
				}
			}
		}
		throw new IllegalArgumentException("Invalid section for route: "+section
				+" route: "+Arrays.toString(sections));
	}

	public Integer prevSection(int section){
		for (int i = sections.length-1; i >= 0; --i) {
			if (sections[i] == section) {
				// Found matching section. Now, determine next section (if it
				// exists).
				int x = (i - 1);
				// Check whether we reached the end of the route or not
				if(!isLoop){
					if(x < 0) return null;
					return sections[x];
					
				}else{
					if(x < 0) x = sections.length +x;
					return sections[x];
				}
				
			}
		}
		throw new IllegalArgumentException("Invalid section for route: "+section);

	}

	public void setStopSection(int section){
		this.stopSection = section;

	}

	public boolean isStopSection(int section){
		return section == this.stopSection;

	}

	public String toString(){
		return "route: "+Arrays.toString(sections);

	}
}
