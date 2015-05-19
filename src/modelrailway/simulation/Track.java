package modelrailway.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelrailway.core.Section;
import modelrailway.util.Pair;

/**
 * An abstract class to model pieces of track.
 * @author powleybenj
 *
 */
public abstract class Track {

	public static  class RingRoute{
		private Track head;
		private List<Track> trackList = new ArrayList<Track>();
		private HashMap<Section,Integer> sectionMap = new HashMap<Section,Integer>();
		// sectionMap, a hashmap containing the number of times a section occurs in the Route

		public RingRoute(Track head){
			this.head = head;
			trackList.add(this.head);

			if(sectionMap.get(this.head.getSection()) == null)sectionMap.put(this.head.getSection(),0);
			sectionMap.put(this.head.getSection(), sectionMap.get(this.head.getSection())+1);
			if(this.head.getAltSection() != null){
				if(sectionMap.get(this.head.getAltSection()) == null)sectionMap.put(this.head.getAltSection(),0);
				sectionMap.put(this.head.getAltSection(), sectionMap.get(this.head.getAltSection())+1);
			}
		}
		public Track getHead(){
			return head;
		}
		public List<Track> getTrackList(){
			return trackList;
		}
		/**
		 * The sectionList() returns a hashmap from sections in the Route to the number of times the section occurs.
		 * @return
		 */
		public Map<Section,Integer> getSectionList(){

			return sectionMap;
		}
		/**
		 *  get a map from sections to section numbers.
		 */
		public Map<Integer, Section> getSectionNumberMap(){
			Map<Integer,Section > smap = new HashMap<Integer,Section>();
			System.out.println("sectionMap.keySet(): "+sectionMap.keySet().size());
			for(Section s : sectionMap.keySet()){
				if(s == null) System.out.println("A section is  null in sectionMap");

				smap.put(s.getNumber(), s);
			}
			return smap;
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

			if(sectionMap.get(insertedTrack.getSection()) == null) sectionMap.put(insertedTrack.getSection(),0);
			sectionMap.put(insertedTrack.getSection(),sectionMap.get(insertedTrack.getSection())+1);

			if(insertedTrack.getAltSection() != null){
				if(sectionMap.get(insertedTrack.getAltSection()) == null) sectionMap.put(insertedTrack.getAltSection(),0);
				sectionMap.put(insertedTrack.getAltSection(),sectionMap.get(insertedTrack.getAltSection())+1);
			}
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
			if(sectionMap.get(insertedTrack.getSection()) == null) sectionMap.put(insertedTrack.getSection(),0);
			sectionMap.put(insertedTrack.getSection(),sectionMap.get(insertedTrack.getSection())+1);
			if(insertedTrack.getAltSection() != null){
				if(sectionMap.get(insertedTrack.getAltSection()) == null) sectionMap.put(insertedTrack.getAltSection(),0);
				sectionMap.put(insertedTrack.getAltSection(),sectionMap.get(insertedTrack.getAltSection())+1);
			}
			return tr1; // return tr1
		}
		/**
		 * Insert a track between tr1 and the track next to tr1
		 * @param tr1
		 * @param onAlt
		 * @param insertedTrack
		 * @param onAlt2
		 * @return
		 */
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
			if(sectionMap.get(insertedTrack.getSection()) == null) sectionMap.put(insertedTrack.getSection(),0);
			sectionMap.put(insertedTrack.getSection(),sectionMap.get(insertedTrack.getSection())+1);
			if(insertedTrack.getAltSection() != null){
				if(sectionMap.get(insertedTrack.getAltSection()) == null) sectionMap.put(insertedTrack.getAltSection(),0);
				sectionMap.put(insertedTrack.getAltSection(),sectionMap.get(insertedTrack.getAltSection())+1);
			}
			return tr1; // return tr1
		}

		/**
		 * trivalRing produces a ring of two elements.
		 * @param tr
		 * @return
		 */
		protected Track trivialRing(Track tr){
			trackList.add(tr);
			if(sectionMap.get(tr.getSection()) == null) sectionMap.put(tr.getSection(),0);
			sectionMap.put(tr.getSection(),sectionMap.get(tr.getSection())+1);
			if(tr.getAltSection() != null){
				if(sectionMap.get(tr.getAltSection()) == null) sectionMap.put(tr.getAltSection(),0);
				sectionMap.put(tr.getAltSection(),sectionMap.get(tr.getAltSection())+1);
			}
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
			if(t2 instanceof Switch){
				tr.addSwitchEntry(((Switch) t2).getSwitchID(),t2);
			}
			List<Track> tlist = getTrackList();
			System.out.println("trackListSize: "+tlist.size());
		    int index = tlist.lastIndexOf(t);
		    System.out.println(tlist.size());
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
		    System.out.println("index: "+index);
		    if(tlist.get(index) == head){
		    	head = t2; // replace the head.
		    }

		    Section sec = tlist.get(index).getSection();
		    sectionMap.put(tlist.get(index).getSection(),sectionMap.get(sec) -1);

		    if (sectionMap.get(tlist.get(index).getSection()) == 0) sectionMap.remove(sec);
		    if(tlist.get(index).getAltSection() != null ){
		    	sectionMap.put(tlist.get(index).getAltSection(),sectionMap.get(sec) -1);

				if (sectionMap.get(tlist.get(index).getAltSection()) == 0) sectionMap.remove(sec);
		    	sectionMap.remove(tlist.get(index).getAltSection());

		    }

		    if(sectionMap.get(t2.getSection()) == null) sectionMap.put(t2.getSection(), 0);
		    sectionMap.put(t2.getSection(), sectionMap.get(t2.getSection())+1);
		    if(t2.getAltSection() != null){
		       if(sectionMap.get(t2.getAltSection()) == null) sectionMap.put(t2.getAltSection(), 0);
		       sectionMap.put(t2.getAltSection(), sectionMap.get(t2.getAltSection())+1);
		    }
		    tlist.set(index, t2);

			return tr;

		}


		public Track bufferEnd(Track t1, boolean enterForward, boolean onAlt){
			Section sec = new Section(new ArrayList<Track>());
			Track buffer = new Buffer(t1,enterForward,sec,100);
			sec.add(buffer);
			if(sectionMap.get(sec) == null) sectionMap.put(sec, 0);
		    sectionMap.put(sec, sectionMap.get(sec)+1);

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

		public Track join(Track t1, boolean onAlt, Track t2, boolean onAlt2){
			if(onAlt){
				t1.next = t2;

				if(onAlt2){
					t2.alternatePrevious = t1;
				}else{
					t2.previous = t1;
				}
			} else{
				t1.alternateNext = t2;
				if(onAlt2){
					t2.alternatePrevious = t1;
				}else{
					t2.previous = t1;
				}
			}
			return getHead();
		}
	}

	public static class DblRing extends RingRoute{
		private Track secondRingHead = null;
		public DblRing(Track head, Track secHead) {
			super(head);
			secondRingHead = secHead;
			this.getTrackList().add(secHead);

			if(this.getSectionList().get(secondRingHead.getSection()) == null)this.getSectionList().put(secondRingHead.getSection(),0);
			this.getSectionList().put(secondRingHead.getSection(), this.getSectionList().get(secondRingHead.getSection())+1);
			if(secondRingHead.getAltSection() != null){
				if(this.getSectionList().get(secondRingHead.getAltSection()) == null)this.getSectionList().put(secondRingHead.getAltSection(),0);
				this.getSectionList().put(secondRingHead.getAltSection(), this.getSectionList().get(secondRingHead.getAltSection())+1);
			}
		}

		public Track getSecondHead(){
			return secondRingHead;
		}

		public Pair<Track,Track> trivialDRing(Track tr, Track tr2){
			System.out.println("running trivial DRing");
			System.out.println("tr: "+tr.getSection()+" tr2: "+tr2.getSection());
			if(tr.getSection() == null || tr2.getSection() == null){
				System.out.println("A section was null");;
			}
			this.getTrackList().add(tr);
			Map<Section,Integer> sectionMap = this.getSectionList();
			if(sectionMap.get(tr.getSection()) == null) sectionMap.put(tr.getSection(), 0);
		    sectionMap.put(tr.getSection(), sectionMap.get(tr.getSection())+1);

		    if(tr.getAltSection() != null){
		    if(sectionMap.get(tr.getAltSection()) == null) sectionMap.put(tr.getAltSection(), 0);
		    sectionMap.put(tr.getAltSection(), sectionMap.get(tr.getAltSection())+1);
		    }
			tr.alternateNext = null;
			tr.alternatePrevious = null;
			tr.altlength = 0;
			tr.next = this.getHead();
			tr.previous = this.getHead();
			this.getHead().next = tr;
			this.getHead().previous = tr;

			this.getTrackList().add(tr2);
			if(sectionMap.get(tr2.getSection()) == null) sectionMap.put(tr2.getSection(), 0);
		    sectionMap.put(tr2.getSection(), sectionMap.get(tr2.getSection())+1);

		    if(tr2.getAltSection() != null){
		    if(sectionMap.get(tr2.getAltSection()) == null) sectionMap.put(tr2.getAltSection(), 0);
		    sectionMap.put(tr2.getAltSection(), sectionMap.get(tr2.getAltSection())+1);
		    }
			tr2.alternateNext = null;
			tr2.alternatePrevious = null;
			tr2.altlength = 0;
			tr2.next = secondRingHead;
			tr2.previous = secondRingHead;
			secondRingHead.next = tr2;
			secondRingHead.previous = tr2;

			return new Pair<Track,Track>(tr,tr2);
		}

		public Pair<Track,Track> insertMultiSwitch(Straight inner1S, Straight outer1S, Straight inner2S, Straight outer2S){
			Track inner1 =  inner1S;
			Track outer1 = outer1S;
			Track inner2 = inner2S;
			Track outer2 = outer2S;
			Section sec = this.createSwitchesSectionforward();
			Track innerSW = sec.get(0);
			Track outerSW = sec.get(1);
			Track mergeSW = sec.get(2);

			innerSW.previous = inner1;
			inner1.next = innerSW;

		    outerSW.previous = outer1;
		    outer1.next = outerSW;

		    inner2.previous = mergeSW;
		    mergeSW.next = inner2;

		    outer2.previous = outerSW;
		    outerSW.next = outer2;

		    return new Pair<Track,Track> (getHead(),getSecondHead());
		}
		/**
		 * Insert a section with three switches in it where there are 3 entrance points and two exit points.
		 * @param inner1S
		 * @param outer1S
		 * @param inner2S
		 * @param outer2S
		 * @return
		 */
		public Pair<Track,Track> insertMultiSwitchBack(Straight inner1S, Straight outer1S, Straight inner2S, Straight outer2S){
			Track inner1 = inner1S;
			Track outer1 = outer1S;
			Track inner2 = inner2S;
			Track outer2 = outer2S;

			Section sec = this.createSwitchesSectionBackward();

			Track innerSW = sec.get(0); //section s11
			Track outerSW = sec.get(1);
			Track mergeSW = sec.get(2);

			outerSW.previous = outer1S;
			outer1.next = outerSW;

			mergeSW.previous = inner1;
			inner1.next = mergeSW;

			inner2.previous = innerSW;
			innerSW.next = inner2;

			outer2.previous = outerSW;
			outerSW.next = outer2;

			return new Pair<Track,Track>(getHead(), getSecondHead());

		}


		/**
		 * The first and second pieces tracks in the returned section have the entry to the setion.
		 * All three pieces have exits from the section.
		 *
		 * @return
		 */
		public Section createSwitchesSectionforward(){
			Section sec = new Section(new ArrayList<Track>());

			this.getSectionList().put(sec, 1);

			Track s8Piece = new ForwardSwitch(null, null, null, sec, 300, 175, 150);
			Track s16Piece = new ForwardSwitch(null,null,null,sec,300,175,150);
			Track mergePiece = new BackSwitch(s16Piece, null, s8Piece, sec, 100, 100, 50);

			s8Piece.alternateNext = mergePiece;
			s16Piece.alternateNext = mergePiece;

			sec.add(s8Piece);
			sec.add(s16Piece);
			sec.add(mergePiece);

			return sec;
		}

		public Section createSwitchesSectionBackward(){

			Section sec = new Section(new ArrayList<Track>());
			this.getSectionList().put(sec, 1);

			Track s11Piece = new BackSwitch(null,null,null,sec,300,175,150);
			Track s3Piece = new BackSwitch(null,null,null,sec,300,175,150);
			Track mergePiece = new ForwardSwitch(null, s11Piece, s3Piece,sec, 100,100,50);

			s11Piece.alternatePrevious = mergePiece;
			s3Piece.alternatePrevious = mergePiece;

			sec.add(s11Piece);
			sec.add(s3Piece);
			sec.add(mergePiece);

			return sec;
		}
	}

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
