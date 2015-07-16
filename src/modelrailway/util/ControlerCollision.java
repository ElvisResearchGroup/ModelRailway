package modelrailway.util;

import java.util.Map;
import java.util.Map.Entry;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Event.SectionChanged;
import modelrailway.core.Route;
import modelrailway.core.Section;
import modelrailway.core.Train;
import modelrailway.simulation.Switch;
import modelrailway.simulation.Track;

public class ControlerCollision extends MovementController implements Controller{

	public ControlerCollision(Map<Integer, modelrailway.core.Train> trains,
			Map<Integer, Section> sections, Track head, Event.Listener trackControler) {
		super(trains, sections, head, trackControler);
		// TODO Auto-generated constructor stub
	}

	public void notify(Event e){

		Pair<Integer,Integer> trainPair = tryLocking(e); // try locking the section.
		Integer train = trainPair.fst;
		Integer sectionID = trainPair.snd;
		if(train == null){ // Since the train object is null, locking has not failed.
		  super.notify(e); // so notify listeners that the locking has not failed to do the movement.
		  //now that the train object has moved. The train has been adjusted to move in or out of the section supplied.
		  if(sectionID != null){ // if the locking succeeded, This can only occur if the event was a movement event.
		     Entry<Integer, Route> rtmap = super.getRoute(sectionID);
		     System.out.println("checking sectionID: "+ sectionID);
		     Route rt = rtmap.getValue();
		     Integer trn = rtmap.getKey(); // the train associated with the section ID.
		     if(rt != null){ // if there is a valid route that is not a loop and the stopSection has been moved into then notify the train to stop.
		       if(!rt.isALoop() && rt.isStopSection(sectionID)){
		    	   System.out.println("Trigger an emergency stop."+ trn);
		    	   this.stop(trn);
		           super.notify(new Event.EmergencyStop(trn)); // reached the end of the track.
		       }
		     }
		  }
		}else{
		  super.notify(e); // The train object is not null so a train has been returned when we tried to lock and failed.
		  this.stop(train);
		  super.notify(new Event.EmergencyStop(train)); // now we stop the train that failed locking.
		}



	}


	/**
	 * Try to obtain the lock for the section ahead of where we are traveling into if we are traveling into a section.
	 * try locking returns a pair containing the integer id of the train, and a section id that a train is in.
	 * The integer id of the train is null if the method succeeds and the sectionID of the train will be returned.
	 * @param e
	 * @return
	 */
	private Pair<Integer,Integer> tryLocking(Event e){

		if((e instanceof Event.SectionChanged)){ // when we are moving into a section
			Train tr = null;
			try{
				tr = adjustSection(e); // adjust train object for the next section.
			}catch(AlreadyHere ex){
				tr = ex.tr; // get the train.

			}
			if(tr == null) throw new RuntimeException("There was no train associated with a section changed movement. in tryLocking(Event e)");

			Integer sectionID = tr.currentSection(); // get the current section of the train we are working with.

			Map.Entry<Integer, Route> entry = super.getRoute(sectionID); // get the route associated with the train in the section the train is in.

			Integer train = entry.getKey(); // get the train, this is the id for the train tr.

			Integer nextSec = entry.getValue().nextSection(this.trainOrientations().get(train).currentSection());  // get section number the train changed into.

			if(tr.currentOrientation() == true){ // we are moving in the forwards direction.
				Section thisSec = this.sections().get(tr.currentSection());
				Track front = thisSec.get(0);
				Track notAltNext = front.getNext(false);
				Track altNext = front.getNext(true);
				boolean reserved = false ;

				if(notAltNext.getSection().getNumber() == nextSec){

					System.out.println("notalt next, not alt sec number: "+notAltNext.getSection().getNumber());
					boolean reserved1 = notAltNext.getSection().reserveSection(train);
					System.out.println("reserved1: "+reserved1);
					System.out.println("Movables In Section: "+ notAltNext.getSection().getMovableSet());
					System.out.println("Movables In Queue:"+notAltNext.getSection().getEntryRequests());
					boolean reserved2 = false;
					if(notAltNext.getAltSection() != null){ reserved2 = notAltNext.getAltSection().reserveSection(train);}
					else{ reserved2 = true;}
					//System.out.println("reserved:" +reserved);
					reserved = reserved1 && reserved2;
					// fix switches in reserved section.
					if(notAltNext.getSection().get(0) instanceof Switch){
					  super.moveSwitches(train, notAltNext.getSection().getNumber(), trainOrientations().get(train), notAltNext.getSection().get(0));
					}



				}else  if(notAltNext.getAltSection() != null && notAltNext.getAltSection().getNumber() == nextSec){
					System.out.println("notalt next, alt sec number: "+notAltNext.getSection().getNumber());
					boolean reserved1 = notAltNext.getAltSection().reserveSection(train);
					boolean reserved2 = notAltNext.getSection().reserveSection(train);
					reserved = reserved1 && reserved2;

					if(notAltNext.getAltSection().get(0) instanceof Switch){
					  super.moveSwitches(train, notAltNext.getAltSection().getNumber(), trainOrientations().get(train), notAltNext.getAltSection().get(0));
					}

				}else if(altNext.getSection().getNumber() == nextSec){
					//System.out.println();
					System.out.println("alt next, not alt sec number: "+altNext.getSection().getNumber());
					boolean reserved1 = altNext.getSection().reserveSection(train);
					boolean reserved2 = false;
					if(altNext.getAltSection() != null){reserved2 = altNext.getAltSection().reserveSection(train);}
					else {reserved2 = true;}
					reserved = reserved1 && reserved2;
					if(altNext.getSection().get(0) instanceof Switch){
				      super.moveSwitches(train, altNext.getSection().getNumber(), trainOrientations().get(train), altNext.getSection().get(0));
					}


				}else if(altNext.getAltSection() != null && altNext.getAltSection().getNumber() == nextSec){
					System.out.println("alt next, alt sec number: "+altNext.getAltSection().getNumber());
					boolean reserved1 = altNext.getAltSection().reserveSection(train);
					boolean reserved2 = altNext.getSection().reserveSection(train);
					reserved = reserved1 && reserved2;
					if(altNext.getAltSection().get(0) instanceof Switch){
				      super.moveSwitches(train, altNext.getAltSection().getNumber(), trainOrientations().get(train), altNext.getAltSection().get(0));
				    }
				}
				//System.out.println("reserved: "+reserved);
				if(reserved == false){ // we need to trigger an emergency stop
					System.out.println("========train is being stoped as reserved is false");

					System.out.println("altNext, section: "+altNext.getSection().getNumber() + "next, section "+notAltNext.getSection().getNumber() +" thisSec: " +thisSec.getNumber());
					System.out.println("Intended next section: "+nextSec);
					this.stop(train);
					System.out.println("sending emergency stop");

					return new Pair<Integer,Integer>(train,sectionID); // dont to the movement
				}
			}
			else{
				System.out.println("Orientation: "+this.trainOrientations().get(train).currentOrientation());
				Section thisSec = this.sections().get(this.trainOrientations().get(train).currentSection());
				Track back = thisSec.get(0); // length is not supported.
				System.out.println("back: "+ thisSec.getNumber());
				System.out.println("back prev: "+back.getPrevious(false).getSection().getNumber());
				System.out.println("nextSec: "+ nextSec);
				Track notAltPrev = back.getPrevious(false);
				Track altPrev = back.getPrevious(true);
				boolean reserved = false ;
				//System.out.println("backend: ");
				if(notAltPrev.getSection().getNumber() == nextSec){
					boolean reserved1 = notAltPrev.getSection().reserveSection(train);
					boolean reserved2 = false;
					if(notAltPrev.getAltSection() != null){reserved2 = notAltPrev.getAltSection().reserveSection(train);}
					else {reserved2 = true;}
					reserved = reserved1 && reserved2;
					if(notAltPrev.getSection().get(notAltPrev.getSection().size()-1) instanceof Switch){
					  super.moveSwitches(train, notAltPrev.getSection().getNumber(), trainOrientations().get(train), notAltPrev.getSection().get(notAltPrev.getSection().size()-1));
				    }

				} if(notAltPrev.getAltSection() != null && notAltPrev.getAltSection().getNumber() == nextSec){
					boolean reserved1 = notAltPrev.getSection().reserveSection(train);
					boolean reserved2 =notAltPrev.getAltSection().reserveSection(train);
					reserved = reserved1 && reserved2;
					if(notAltPrev.getAltSection().get(notAltPrev.getAltSection().size()-1) instanceof Switch){
						 super.moveSwitches(train, notAltPrev.getAltSection().getNumber(), trainOrientations().get(train), notAltPrev.getAltSection().get(notAltPrev.getAltSection().size()-1));
					}

				} if(altPrev.getSection().getNumber() == nextSec){
					boolean reserved1 = altPrev.getSection().reserveSection(train);
					System.out.println("reserved1: "+reserved1);
					
					boolean reserved2 = false;
					if(altPrev.getAltSection() != null) {reserved2 = altPrev.getAltSection().reserveSection(train);}
					else{reserved2 = true;}
					reserved = reserved1 && reserved2;
					if(altPrev.getSection().get(altPrev.getSection().size()-1) instanceof Switch){
						 super.moveSwitches(train, altPrev.getSection().getNumber(), trainOrientations().get(train), altPrev.getSection().get(altPrev.getSection().size()-1));
					}

				} if(altPrev.getAltSection() != null && altPrev.getAltSection().getNumber() == nextSec){
					boolean reserved1 = altPrev.getAltSection().reserveSection(train);
					boolean reserved2 = altPrev.getSection().reserveSection(train);
					reserved = reserved1 && reserved2;
					if(altPrev.getAltSection().get(notAltPrev.getAltSection().size()-1) instanceof Switch){
						 super.moveSwitches(train, altPrev.getAltSection().getNumber(), trainOrientations().get(train), altPrev.getAltSection().get(altPrev.getAltSection().size()-1));
					}
				}

				if(reserved == false){
					System.out.println("========train is being stoped as reserved is false");
					this.stop(train);
					//super.notify(new Event.EmergencyStop(train));
					return new Pair<Integer,Integer>(train,sectionID); // dont do the movement.
				}
			}
			return new Pair<Integer,Integer>(null,sectionID);
		}
		return new Pair<Integer,Integer>(null,null);
	}

}
