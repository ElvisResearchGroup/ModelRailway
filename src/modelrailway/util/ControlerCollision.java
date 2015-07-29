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

		Pair<Integer,Integer> trainPair = tryLocking(e, true); // try locking the section.
		if(trainPair == null) return;
		Integer train = trainPair.fst;
		Integer sectionID = trainPair.snd;
		if(train == null){ // Since the train object is null, locking has not failed.
		  super.notify(e); // so notify listeners that the locking has not failed to do the movement.
		  //now that the train object has moved. The train has been adjusted to move in or out of the section supplied.
		  if(sectionID != null){ // if the locking succeeded, This can only occur if the event was a movement event.
		     Entry<Integer, Route> rtmap = super.getRoute(sectionID);

		    // System.out.println("checking sectionID: "+ sectionID);
		     Route rt = rtmap.getValue();

		     Integer trn = rtmap.getKey(); // the train associated with the section ID.
		    // if(rt.lastSection() == this.trainOrientations().get(trn).currentSection()) return
		     if(rt != null){ // if there is a valid route that is not a loop and the stopSection has been moved into then notify the train to stop.
		       if(!rt.isALoop() && rt.isStopSection(sectionID)){
		    	   System.out.println("Trigger an emergency stop."+ trn);
		    	   this.stop(trn);
		           super.notify(new Event.EmergencyStop(trn)); // reached the end of the track.
		       }
		     }
		  }

		    System.out.print("train: "+ trainPair.fst+", position: "+ sectionID);
	        if(e instanceof Event.SectionChanged) System.out.println(", e: "+ ((Event.SectionChanged)e).getSection());
		 //   System.out.println("1:"+sections().get(1).getEntryRequests().toString());
		//	System.out.println("2:"+sections().get(2).getEntryRequests().toString());
		//	System.out.println("3:"+sections().get(3).getEntryRequests().toString());
		//	System.out.println("4:"+sections().get(4).getEntryRequests().toString());
		//	System.out.println("5:"+sections().get(5).getEntryRequests().toString());
		//	System.out.println("6:"+sections().get(6).getEntryRequests().toString());
		//	System.out.println("7:"+sections().get(7).getEntryRequests().toString());
		//	System.out.println("8:"+sections().get(8).getEntryRequests().toString());
		//	System.out.println("9:"+sections().get(9).getEntryRequests().toString());
		//	System.out.println("10:"+sections().get(10).getEntryRequests().toString());
		  //  System.out.println("16:"+sections().get(16).getEntryRequests().toString());
		    System.out.flush();
		}else{
		  super.notify(e); // The train object is not null so a train has been returned when we tried to lock and failed.




		  System.out.println("Stop triggered: "+ train);
		  this.stop(train);
		   // it is necisary to unlock the section before the section we have just come from .
		  // as when the train stops it does not unlock the section it has just come from as it may be straddled across that section.
		  // so succesive stops can cause a long chain of locked sections. , The section is only unlocked if it was tested and found to be locked to avoid unnecisarily starting trains that may be waiting. 
		  
		  Integer loco = train;
		  Integer prev = this.routes().get(loco).prevSection(this.trainOrientations().get(loco).currentSection());
		  Integer prevPrevSection = routes().get(loco).prevSection(prev);
		  
		  for(Map.Entry<Integer, Train> trainEnt: this.trainOrientations().entrySet()){
		     if(trainEnt.getKey() == loco){
		    	 Section prevPrevSec = sections().get(prevPrevSection);
				 if(prevPrevSec != null && prevPrevSec.getEntryRequests().contains(trainEnt.getKey())){ //unlock prevPrev
				     unlockSection(prevPrevSec, trainEnt);
				 }
		     }
		  }
		  super.notify(new Event.EmergencyStop(train)); // now we stop the train that failed locking.
		}



	}


	/**
	 * Try to obtain the lock for the section ahead of where we are traveling into if we are traveling into a section.
	 * try locking returns a pair containing the integer id of the train, and a section id that a train is in.
	 * The integer id of the train is null if the method succeeds and the sectionID of the train will be returned.
	 * if the integer id is not null then the locking failed.
	 * @param e
	 * @return
	 */
	public Pair<Integer,Integer> tryLocking(Event e, boolean moving){
		//if(e instanceof Event.SpeedChanged && ((Event.SpeedChanged) e)
		if((e instanceof Event.SectionChanged)){ // when we are moving into a section
			Train tr = null;
			try{

				//System.out.println("Before Adjust");
				//System.out.println("moving: "+moving);
				tr = adjustSection(e, moving); // adjust train object for the next section.
				//System.out.println("After Adjust");
			}catch(AlreadyHere ex){
				tr = ex.tr; // get the train.

			}
			//System.out.println("section: "+ ((Event.SectionChanged )e).getSection());
			//System.out.println("section: "+ this.calculateSectionNumber((SectionChanged) e));

			if(tr == null) throw new RuntimeException("There was no train associated with a section changed movement. in tryLocking(Event e)");

			Integer sectionID = tr.currentSection(); // get the current section of the train we are working with.

			Map.Entry<Integer, Route> entry = this.getRoute(sectionID); // get the route associated with the train in the section the train is in.

			Integer train = entry.getKey(); // get the train, this is the id for the train tr.


			//System.out.println("tr.currentSection(): "+ sectionID);

			Integer nextSec = entry.getValue().nextSection(this.trainOrientations().get(train).currentSection());  // get the next section to lock.
			
			System.out.println("LOCK: "+ nextSec);
			if(nextSec == null){// in the event that there is no next section from the train section that the train is currently in, Stop the train.
				this.stop(train);
				this.notify(new Event.SpeedChanged(train, 0));
				return null;
			}
			//System.out.println("trainOrientations: "+ this.trainOrientations().get(train).currentSection());
			//System.out.println("trainID: "+ train);
			//System.out.println("trainSection: "+ tr.currentSection());
			if(tr.currentOrientation() == true){ // we are moving in the forwards direction.

				Section thisSec = this.sections().get(tr.currentSection()); //
				Track front = thisSec.get(0);
				//System.out.println("thisSec: "+ thisSec.getNumber());
				
				Track notAltNext = front.getNext(false);
				Track altNext = front.getNext(true);
				System.out.println("front: "+ thisSec.getNumber());
				System.out.println("front prev: "+front.getNext(false).getSection().getNumber());
				System.out.println("nextSec: "+ nextSec);
				boolean reserved = false ;

				if(notAltNext.getSection().getNumber() == nextSec){

					//System.out.println("notalt next, not alt sec number: "+notAltNext.getSection().getNumber());
					boolean reserved1 = notAltNext.getSection().reserveSection(train);
					//System.out.println("reserved1: "+reserved1);
					//System.out.println("Movables In Section: "+ notAltNext.getSection().getMovableSet());
					//System.out.println("Movables In Queue:"+notAltNext.getSection().getEntryRequests());
					boolean reserved2 = false;
					if(notAltNext.getAltSection() != null){ reserved2 = notAltNext.getAltSection().reserveSection(train);}
					else{ reserved2 = true;}
					//System.out.println("reserved:" +reserved);
					reserved = reserved1 && reserved2;
					// fix switches in reserved section.
					if(notAltNext.getSection().get(0) instanceof Switch){
					  moveSwitches(train, notAltNext.getSection().getNumber(), trainOrientations().get(train), notAltNext.getSection().get(0));
					}



				}else  if(notAltNext.getAltSection() != null && notAltNext.getAltSection().getNumber() == nextSec){
					//System.out.println("notalt next, alt sec number: "+notAltNext.getSection().getNumber());
					boolean reserved1 = notAltNext.getAltSection().reserveSection(train);
					boolean reserved2 = notAltNext.getSection().reserveSection(train);
					reserved = reserved1 && reserved2;

					if(notAltNext.getAltSection().get(0) instanceof Switch){
					  moveSwitches(train, notAltNext.getAltSection().getNumber(), trainOrientations().get(train), notAltNext.getAltSection().get(0));
					}

				}else if(altNext.getSection().getNumber() == nextSec){
					//System.out.println();
					//System.out.println("alt next, not alt sec number: "+altNext.getSection().getNumber());
					boolean reserved1 = altNext.getSection().reserveSection(train);
					boolean reserved2 = false;
					if(altNext.getAltSection() != null){reserved2 = altNext.getAltSection().reserveSection(train);}
					else {reserved2 = true;}
					reserved = reserved1 && reserved2;
					if(altNext.getSection().get(0) instanceof Switch){
				      moveSwitches(train, altNext.getSection().getNumber(), trainOrientations().get(train), altNext.getSection().get(0));
					}


				}else if(altNext.getAltSection() != null && altNext.getAltSection().getNumber() == nextSec){
					//System.out.println("alt next, alt sec number: "+altNext.getAltSection().getNumber());
					boolean reserved1 = altNext.getAltSection().reserveSection(train);
					boolean reserved2 = altNext.getSection().reserveSection(train);
					reserved = reserved1 && reserved2;
					if(altNext.getAltSection().get(0) instanceof Switch){
				      moveSwitches(train, altNext.getAltSection().getNumber(), trainOrientations().get(train), altNext.getAltSection().get(0));
				    }
				}
				//System.out.println("reserved: "+reserved);
				if(reserved == false){ // we need to trigger an emergency stop
					//System.out.println("========train is being stoped as reserved is false");

					//System.out.println("altNext, section: "+altNext.getSection().getNumber() + "next, section "+notAltNext.getSection().getNumber() +" thisSec: " +thisSec.getNumber());
					//System.out.println("Intended next section: "+nextSec);
					this.stop(train);
					//System.out.println("sending emergency stop");

					return new Pair<Integer,Integer>(train,sectionID); // dont to the movement
				}
			}
			else{
				//System.out.println("Orientation: "+this.trainOrientations().get(train).currentOrientation());
				Section thisSec = this.sections().get(this.trainOrientations().get(train).currentSection());
				Track back = thisSec.get(0); // length is not supported.
				//System.out.println("back: "+ thisSec.getNumber());
				//System.out.println("back prev: "+back.getPrevious(false).getSection().getNumber());
				//System.out.println("nextSec: "+ nextSec);
				Track notAltPrev = back.getPrevious(false);
				Track altPrev = back.getPrevious(true);
				if(notAltPrev != null) System.out.println("notAltPrev not null");
				if (altPrev != null) System.out.println("altPrev not null");
				System.out.println("backType: "+back.getClass().toString());
				boolean reserved = false ;
				//System.out.println("backend: ");
				if(notAltPrev.getSection().getNumber() == nextSec){
					boolean reserved1 = notAltPrev.getSection().reserveSection(train);
					boolean reserved2 = false;
					if(notAltPrev.getAltSection() != null){reserved2 = notAltPrev.getAltSection().reserveSection(train);}
					else {reserved2 = true;}
					reserved = reserved1 && reserved2;
					if(notAltPrev.getSection().get(notAltPrev.getSection().size()-1) instanceof Switch){
					  moveSwitches(train, notAltPrev.getSection().getNumber(), trainOrientations().get(train), notAltPrev.getSection().get(notAltPrev.getSection().size()-1));
				    }

				} if(notAltPrev.getAltSection() != null && notAltPrev.getAltSection().getNumber() == nextSec){
					boolean reserved1 = notAltPrev.getSection().reserveSection(train);
					boolean reserved2 =notAltPrev.getAltSection().reserveSection(train);
					reserved = reserved1 && reserved2;
					if(notAltPrev.getAltSection().get(notAltPrev.getAltSection().size()-1) instanceof Switch){
						 moveSwitches(train, notAltPrev.getAltSection().getNumber(), trainOrientations().get(train), notAltPrev.getAltSection().get(notAltPrev.getAltSection().size()-1));
					}

				} if(altPrev.getSection().getNumber() == nextSec){
					boolean reserved1 = altPrev.getSection().reserveSection(train);
				///	System.out.println("reserved1: "+reserved1);

					boolean reserved2 = false;
					if(altPrev.getAltSection() != null) {reserved2 = altPrev.getAltSection().reserveSection(train);}
					else{reserved2 = true;}
					reserved = reserved1 && reserved2;
					if(altPrev.getSection().get(altPrev.getSection().size()-1) instanceof Switch){
						 moveSwitches(train, altPrev.getSection().getNumber(), trainOrientations().get(train), altPrev.getSection().get(altPrev.getSection().size()-1));
					}

				} if(altPrev.getAltSection() != null && altPrev.getAltSection().getNumber() == nextSec){
					boolean reserved1 = altPrev.getAltSection().reserveSection(train);
					boolean reserved2 = altPrev.getSection().reserveSection(train);
					reserved = reserved1 && reserved2;
					if(altPrev.getAltSection().get(notAltPrev.getAltSection().size()-1) instanceof Switch){
						 moveSwitches(train, altPrev.getAltSection().getNumber(), trainOrientations().get(train), altPrev.getAltSection().get(altPrev.getAltSection().size()-1));
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

	protected void moveIntoSection( Train train){

		Integer eventsectionID = train.currentSection(); // The train object has already been adjusted

		System.out.println("In Controler Collision: "+ eventsectionID);
		
		for(Entry<Integer, Train> trainOrientation: trainOrientations().entrySet()){ // for all the trains on the track, we need to find the train id that matches the train we have.

			Integer section = trainOrientation.getValue().currentSection(); // get the section of the train
			System.out.println("section: "+section);
			// we compare sections to see that we are working with the same train as has been passed in.
			// so trainOrientation.getKey() will have the train id of the train object
	    	if(section ==  eventsectionID && routes().get(trainOrientation.getKey()) != null){ // check that the front of the train is in the section
	    		// first work out which track segments we are currently dealing with.
	    		Integer prevSection = routes().get(trainOrientation.getKey()).prevSection(section); // the previous section that we came from
	    		Section previous = sections().get(prevSection); // the previous section object matching the prevSection id.

	    		Section thisSection = sections().get(section); // the section object matching the id of the current section that the train is in

	    		Track thisTrack = sections().get(eventsectionID).get(0);
	    		System.out.println("prevSection: "+prevSection +" trainOrientation.getKey(): "+ trainOrientation.getKey());
	    		if(previous != null){
	    		   //System.out.println("Previous: "+previous.getNumber());
	    		   //System.out.println("");

	    		   System.out.println("UnlockedPrevious Section: "+ previous.getNumber());
	    		   Integer prevPrevSection = routes().get(trainOrientation.getKey()).prevSection(prevSection);
	    		   Section prevPrevSec = sections().get(prevPrevSection);
	    		   
	    		   if(prevPrevSec != null && prevPrevSec.getEntryRequests().contains(trainOrientation.getKey())){ //unlock prevPrev
	    			   unlockSection(prevPrevSec, trainOrientation);
	    		   }
	    		   
	    		   unlockSection(previous, trainOrientation); // unlock the section we just came from
	    	  	 
	    		}
	    		//System.out.println("Switch"+thisTrack);
	    		//System.out.println("trackSection: "+thisTrack.getSection().getNumber());

	    		if(thisTrack instanceof Switch){ // move switches
	    			Integer trainID = trainOrientation.getKey();
	    			Train trainObj = trainOrientation.getValue();
	    			moveSwitches(trainID, section, trainObj, thisTrack);
	    		}
	    	}
       }
	}

	/**
	 * unlock the provided section,
	 * remove the train in trainOrientation from the queue of trains waiting to leave the section.
	 * unlockSection will act on the section regardless of weather the train is in the section or not.
	 * unlockSection should not be called on a section that we do not have the lock to.
	 * @param previous
	 * @param trainOrientation
	 */
	protected void unlockSection(Section previous, Map.Entry<Integer,Train> trainOrientation){
		System.out.println("UNLOCK "+previous.getNumber() + " train: "+ trainOrientation.getKey() );
		if(previous == null) return;
		Track t = previous.get(0); // for the track segment.
	  //  boolean trainMoved = false; //
		System.out.println("Key: "+ trainOrientation.getKey()+", "+t.getSection().getNumber());
		Section tracSec = t.getSection(); // get the section
		System.out.println("trackSec.getNumber(): "+ tracSec.getNumber());;
		Section trackAltSec = t.getAltSection(); // get the alternate section


		if(tracSec != null){
			Pair<Boolean, Integer> pair = null;
			if(!tracSec.isQueueEmpty() && trainOrientation.getKey() != null){
				System.out.println("Queue: "+ tracSec.getEntryRequests().toString());
				pair = tracSec.removeFromQueue(trainOrientation.getKey());
			}
			//System.out.println("pair != null");
			if(pair != null){
				if(pair.snd != null){ // if there is a train waiting then allow it to resume.
					Integer train = tracSec.getEntryRequests().peek(); // get the train
					Integer nextSec = routes().get(train).nextSection(tracSec.getNumber()); // get the next section
					Section next = sections().get(nextSec);
					Integer prevSec = routes().get(train).prevSection(tracSec.getNumber());
					if(next.getEntryRequests().peek() == train){
						this.resumeTrain(train);
					}

					Section prev = sections().get(prevSec);
					if(prev.getEntryRequests().peek() == train){
						this.resumeTrain(train);

					}

				}
			}
		}
		if(trackAltSec != null){
			Pair<Boolean, Integer> pair = null;
			if(!trackAltSec.isQueueEmpty() && trainOrientation.getKey() != null){
				System.out.println("Queue: "+ trackAltSec.getEntryRequests().toString());
				pair = tracSec.removeFromQueue(trainOrientation.getKey());
			}
			//System.out.println("pair != null");
			if(pair != null){
				if(pair.snd != null){ // if there is a train waiting then allow it to resume.
					Integer train = tracSec.getEntryRequests().peek(); // get the train
					Integer nextSec = routes().get(train).nextSection(tracSec.getNumber()); // get the next section
					Integer prevSec = routes().get(train).prevSection(tracSec.getNumber());
					Section next = sections().get(nextSec);
					if(next.getEntryRequests().peek() == train){
						this.resumeTrain(train);
					}
					Section prev = sections().get(prevSec);
					if(prev.getEntryRequests().peek() == train){
						this.resumeTrain(train);

					}
				}
			}
		}
	}

}
