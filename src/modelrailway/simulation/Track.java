package modelrailway.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract class to model pieces of track.
 * @author powleybenj
 *
 */
public abstract class Track {

	public static  class RingRoute{
		private Track head;
		private List<Track> trackList = new ArrayList<Track>();

		public RingRoute(Track head){
			this.head = head;
			trackList.add(this.head);
		}
		public Track getHead(){
			return head;
		}
		public List<Track> getTrackList(){
			return trackList;
		}
		/**
		 * insert a track between tr1 and the next track to tr1.
		 * @param tr1
		 * @param onAlt on alternate track for tr1
		 * @param insertedTrack
		 * @param onAlt2 on alternate track for inserted track
		 * @return
		 */

		public Track insertBetween(Track tr1, boolean onAlt, Track tr2, boolean onAlt2, Track insertedTrack, boolean onAlt3){
			if(insertedTrack instanceof Switch){
				head.addSwitchEntry(((Switch) insertedTrack).getSwitchID(), insertedTrack);
			}
			boolean tr1UnifyNext = false;
			boolean tr2UnifyPrevious = false;
			boolean insertedTrackUnifyNext = false;
			boolean insertedTrackUnifyPrevious = false;
			trackList.add(insertedTrack);
			if(tr1.next == tr1.alternateNext && tr1.next != null) tr1UnifyNext = true;
			if(tr2.previous == tr2.alternatePrevious && tr2.previous != null) tr2UnifyPrevious = true;
			if(insertedTrack.next == insertedTrack.alternateNext && insertedTrack.next != null) insertedTrackUnifyNext = true;
			if(insertedTrack.previous == insertedTrack.alternatePrevious && insertedTrack.previous != null) insertedTrackUnifyPrevious = true;


			if(onAlt2){
				tr2.alternatePrevious = insertedTrack;
				if(onAlt3 || insertedTrackUnifyNext) insertedTrack.alternateNext = tr2;
				if((!onAlt3)|| insertedTrackUnifyNext) insertedTrack.next = tr2;
				if(tr2UnifyPrevious) tr2.previous = insertedTrack;
			}
			else{
				tr2.previous = insertedTrack;
				if(onAlt3 || insertedTrackUnifyNext) insertedTrack.alternateNext =tr2;
				if((!onAlt3) || insertedTrackUnifyNext) insertedTrack.next = tr2;
				if(tr2UnifyPrevious) tr2.alternatePrevious = insertedTrack;

			}

			//fix tr1
			if(onAlt2){
				tr1.alternateNext = insertedTrack;
				if(onAlt3 || insertedTrackUnifyPrevious) insertedTrack.alternatePrevious = tr1;
				if((!onAlt3) || insertedTrackUnifyPrevious)  insertedTrack.previous = tr1;
				if(tr1UnifyNext) tr1.next = insertedTrack;

			}
			else{
				tr1.next = insertedTrack;
				if(onAlt3 || insertedTrackUnifyNext) insertedTrack.alternateNext = tr1;
				if((!onAlt3) || insertedTrackUnifyNext) insertedTrack.previous = tr1;
				if(tr1UnifyNext) tr1.alternateNext = insertedTrack;
			}

			return tr1; // return tr1


		}
		/**
		 * When onAlt2 is true we insert the crossing so that insertedTrack.alternatePrevious = tr1 and insertedTrack.next is the next piece of track.
		 * When onAlt2 is false we insert the crossing so that insertedTrack.previous = tr1 and insertedTrack.alternateNext is the next piece of track
		 * @param tr1
		 * @param onAlt
		 * @param insertedTrack
		 * @param onAlt2
		 * @return
		 */

		public Track insertAcross(Track tr1, boolean onAlt, Track insertedTrack, boolean onAlt2){
			Track tr2 = tr1.getNext(onAlt);
			if(insertedTrack instanceof Switch){
				head.addSwitchEntry(((Switch) insertedTrack).getSwitchID(), insertedTrack);
			}
			//first fix tr2
			boolean tr1UnifyNext = false;
			boolean tr2UnifyPrevious = false;
			boolean insertedTrackUnifyNext = false;
			boolean insertedTrackUnifyPrevious = false;

			if(tr1.next == tr1.alternateNext && tr1.next != null) tr1UnifyNext = true;
			if(tr2.previous == tr2.alternatePrevious && tr2.previous != null) tr2UnifyPrevious = true;
			if(insertedTrack.next == insertedTrack.alternateNext && insertedTrack.next != null) insertedTrackUnifyNext = true;
			if(insertedTrack.previous == insertedTrack.alternatePrevious && insertedTrack.previous != null) insertedTrackUnifyPrevious = true;


			if(tr2.isAlt(tr1)){
				tr2.alternatePrevious = insertedTrack;
				if(onAlt2 || insertedTrackUnifyNext) insertedTrack.next = tr2; // flip next and alternate next for inserted track
				if((!onAlt2)|| insertedTrackUnifyNext) insertedTrack.alternateNext = tr2;
				if(tr2UnifyPrevious) tr2.previous = insertedTrack;
			}
			else{
				tr2.previous = insertedTrack;
				if(onAlt2 || insertedTrackUnifyNext) insertedTrack.next =tr2;
				if((!onAlt2) || insertedTrackUnifyNext) insertedTrack.alternateNext = tr2;
				if(tr2UnifyPrevious) tr2.alternatePrevious = insertedTrack;

			}

			//fix tr1
			if(tr1.isAlt(tr2)){
				tr1.alternateNext = insertedTrack;
				if(onAlt2 || insertedTrackUnifyPrevious) insertedTrack.alternatePrevious = tr1;
				if((!onAlt2) || insertedTrackUnifyPrevious)  insertedTrack.previous = tr1;
				if(tr1UnifyNext) tr1.next = insertedTrack;

			}
			else{
				tr1.next = insertedTrack;
				if(onAlt2 || insertedTrackUnifyPrevious) insertedTrack.alternatePrevious = tr1;
				if((!onAlt2) || insertedTrackUnifyPrevious) insertedTrack.previous = tr1;
				if(tr1UnifyNext) tr1.alternateNext = tr1.next;
			}
			trackList.add(insertedTrack);
			return tr1; // return tr1
		}

		public Track insertBetween(Track tr1, boolean onAlt, Track insertedTrack, boolean onAlt2 ){
			Track tr2 = tr1.getNext(onAlt);
			if(insertedTrack instanceof Switch){
				head.addSwitchEntry(((Switch) insertedTrack).getSwitchID(), insertedTrack);
			}
			//first fix tr2
			boolean tr1UnifyNext = false;
			boolean tr2UnifyPrevious = false;
			boolean insertedTrackUnifyNext = false;
			boolean insertedTrackUnifyPrevious = false;

			if(tr1.next == tr1.alternateNext && tr1.next != null) tr1UnifyNext = true;
			if(tr2.previous == tr2.alternatePrevious && tr2.previous != null) tr2UnifyPrevious = true;
			if(insertedTrack.next == insertedTrack.alternateNext && insertedTrack.next != null) insertedTrackUnifyNext = true;
			if(insertedTrack.previous == insertedTrack.alternatePrevious && insertedTrack.previous != null) insertedTrackUnifyPrevious = true;
			if(tr2.isAlt(tr1)){
				tr2.alternatePrevious = insertedTrack;
				if(onAlt2 || insertedTrackUnifyNext) insertedTrack.alternateNext = tr2;
				if((!onAlt2)|| insertedTrackUnifyNext) insertedTrack.next = tr2;
				if(tr2UnifyPrevious) tr2.previous = insertedTrack;
			}
			else{
				tr2.previous = insertedTrack;
				if(onAlt2 || insertedTrackUnifyNext) insertedTrack.alternateNext =tr2;
				if((!onAlt2) || insertedTrackUnifyNext) insertedTrack.next = tr2;
				if(tr2UnifyPrevious) tr2.alternatePrevious = insertedTrack;

			}
			//fix tr1
			if(tr1.isAlt(tr2)){
				tr1.alternateNext = insertedTrack;
				if(onAlt2 || insertedTrackUnifyPrevious) insertedTrack.alternatePrevious = tr1;
				if((!onAlt2) || insertedTrackUnifyPrevious)  insertedTrack.previous = tr1;
				if(tr1UnifyNext) tr1.next = insertedTrack;

			}
			else{
				tr1.next = insertedTrack;
				if(onAlt2 || insertedTrackUnifyPrevious) insertedTrack.alternatePrevious = tr1;
				if((!onAlt2) || insertedTrackUnifyPrevious) insertedTrack.previous = tr1;
				if(tr1UnifyNext) tr1.alternateNext = tr1.next;
			}
			trackList.add(insertedTrack);
			return tr1; // return tr1
		}

		/**
		 * trivalRing produces a ring of two elements.
		 * @param tr
		 * @return
		 */
		protected Track trivialRing(Track tr){
			trackList.add(tr);
			tr.alternateNext = null;
			tr.alternatePrevious = null;
			tr.altlength = 0;
			tr.next = head;
			tr.previous = head;
			head.next = tr;
			head.previous = tr;
			return head;
		}

		/**
		 * Replaces t with T2, This is only valid if the track t has only one or no paths connected
		 * @param t
		 * @param onAlt
		 * @param t2
		 * @param onAlt2
		 * @return
		 */
		public  Track replace(Track t, Track t2, boolean onAlt2){ // replace a section of track with another section of track;
			Track tr = getHead();
			if(t instanceof Switch){
				tr.removeSwitchEntry(((Switch) t).getSwitchID());
			}
			List<Track> tlist = getTrackList();
		    int index = tlist.lastIndexOf(t);
		 //   System.out.println("t index: "+index);
		    Track tn;
		    Track tp;
		    if(t.getNext(false) != null){
		        tn = t.getNext(false);
		        tp = t.getPrevious(false);
		        tn.previous=  t2;
		        tp.next =  t2;
		        if(onAlt2 ){
		           if(t2.alternatePrevious == t2.previous && t2.alternatePrevious != null) t2.previous = tp;
		           t2.alternatePrevious = tp;
		           if(t2.alternateNext == t2.next && t2.next != null) t2.next = tn;
		           t2.alternateNext = tn;
		        }else{
		           if(t2.alternatePrevious == t2.previous && t2.previous != null) t2.alternatePrevious = tp;
		           t2.previous = tp;
		           if(t2.alternateNext == t2.next && t2.next != null) t2.alternateNext = tn;
		           t2.next = tn;
		        }
		    }
		    else{
		    	tn = t.getNext(true);
		        tp = t.getPrevious(true);
		        tn.alternatePrevious = t2;
		        tp.alternateNext = t2;
		        if(onAlt2 ){
			       if(t2.alternatePrevious == t2.previous && t2.previous != null) t2.previous = tp;
			       t2.alternatePrevious = tp;
			       if(t2.alternateNext == t2.next && t2.next != null) t2.next = tn;
			       t2.alternateNext = tn;
			    }else{
			       if(t2.alternatePrevious == t2.previous && t2.previous != null) t2.alternatePrevious = tp;
			       t2.previous = tp;
			       if(t2.alternateNext == t2.next && t2.next != null) t2.alternateNext = tn;
			       t2.next = tn;
			    }
		    }
		    if(tlist.get(index) == head){
		    	head = t2; // replace the head.
		    }
		    tlist.set(index, t2);
			return tr;

		}

		public Track bufferEnd(Track t1, boolean enterForward, boolean onAlt){
			Section sec = new Section(new ArrayList<Track>());
			Track buffer = new Buffer(t1,enterForward,sec,100);
			sec.add(buffer);
			if(onAlt){
				if(enterForward){
					t1.alternateNext = buffer;
				}else{
					t1.alternatePrevious = buffer;

				}
			}else{
				if(enterForward){
					t1.next = buffer;
				}else{
					t1.previous = buffer;
				}
			}
			return getHead();
		}
	}

	private static final int PREVIOUS=0;
	private static final int NEXT=1;
	private Track previous;
	private Track next;
	private Track alternatePrevious;
	private Track alternateNext;
	private int altlength;

	private int length;
	private Section section;
	private Section altSection;
	private Map<Integer,Track> knownSwitches = new HashMap<Integer,Track>(); // used for the head section.


	/**
	 * produces a section of track.
	 * @param previous
	 * @param next
	 * @param alternatePrevious
	 * @param alternateNext
	 * @param section
	 * @param altSection
	 * @param length
	 * @param altLength
	 */
	public Track(Track previous, Track next, Track alternatePrevious, Track alternateNext, Section section, Section altSection, int length, int altlength){
		this.previous = previous;
		this.next = next;
		this.section = section;
		this.length = length;
		this.alternatePrevious = alternatePrevious;
		this.alternateNext = alternateNext;
		this.altlength = altlength;
		this.altSection = altSection;
	}

	public Track removeSwitchEntry(int switchID) {
		return knownSwitches.remove(switchID);
	}

	/**
	 * produces a section of track.
	 * @param previous
	 * @param next
	 * @param alternatePrevious
	 * @param alternateNext
	 * @param section
	 * @param length
	 * @param altLength
	 */
	public Track(Track previous, Track next, Track alternatePrevious, Track alternateNext, Section section, int length ,int altlength) {
		this.previous = previous;
		this.next = next;
		this.section = section;
		this.length = length;
		this.alternatePrevious = alternatePrevious;
		this.alternateNext = alternateNext;
		this.altlength = altlength;
	}
	/**
	 * returns the previous piece of track that connected to this section of track for the primary route through the track
	 * @return
	 */
	public Track getPrevious(boolean onAlt){
		if(onAlt) return this.alternatePrevious;
		return this.previous;
	}

	/**
	 * get the next piece of track.
	 * @param onAlt
	 * @return
	 */
	public Track getNext(boolean onAlt){
		if(onAlt) return this.alternateNext;
		return this.next;
	}
	/**
	 * get the alternate section if one exists. Otherwise return null.
	 * @return
	 */
	public Section getAltSection(){
		return altSection;
	}
	/**
	 * get the section that the piece of track is part of.
	 * @return
	 */

	public Section getSection(){
	    return section;
	}
	/**
	 * get the distance along the piece of track using the next piece of track to work out which route we are taking along the track.
	 * @param next
	 * @return
	 */
	public int getDistance(Track next){
		if(this.getNext(true).equals(next)) return getDistance(true);
		else if (this.getNext(false).equals(next)) return getDistance(false);
		throw new WrongTrackException();
	}
	/**
	 * get the distance along the piece of track using a boolean variable to instruct whether we are on the primary route or a secondary route.
	 * @param onAlt
	 * @return
	 */
	public int getDistance(boolean onAlt){
		if(onAlt) return altlength;
		return  length;
	}

	/**
	 * given the previous section of track return weather we are currently traveling along the alternate route or the primary route.
	 * @param track
	 * @return
	 */
	public boolean getCurrentAlt(Movable m){
		if(m.getFront() == this){
			return m.getOnAlt();
		}else if(m.getBack() == this){
			return m.getOnAlt();
		}
		throw new WrongTrackException();
	}

	/**
	 *  returns weather a piece of track is connecting to the primary route or to a secondary route.
	 *  @return
	 */
	public boolean isAlt(Track track) {
		if(track == next || track == previous){
			return false;
		}
		else if(track == alternateNext || track == alternatePrevious){
			return true;
		}
		throw new WrongTrackException();
	}

	/**
	 * Add a switch with the supplied id to the list of known switches.
	 * @param turnoutID
	 * @param sw
	 * @return
	 */
	public Track addSwitchEntry(Integer turnoutID, Track sw){
		return knownSwitches.put(turnoutID, sw);
	}
	/**
	 * get the switch with the supplied turnout ID from the hash map.
	 * @param turnoutID
	 * @param sw
	 * @return
	 */
	public Track getSwitchEntry(Integer turnoutID){
		return knownSwitches.get(turnoutID);
	}

}
