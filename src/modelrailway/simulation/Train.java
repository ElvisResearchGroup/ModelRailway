package modelrailway.simulation;
import java.util.HashSet;
import java.util.Set;

import modelrailway.core.Controller;
import modelrailway.core.Event;


/**
 * The Train is a Movable object and contains other movable objects.
 *
 * @author powleybenj
 *
 */
public class Train extends Movable {
	private Movable[] trainParts;
	public Train(Movable[] movable) {
		super(getTracks(movable), movable[0].getDistance(), getTotalLength(movable), findSmallestMaxSpeed(movable), movable[0].getOnAlt());
		//assume train pieces are coupled.
		trainParts = movable;


	}

	public int move(){
		return this.move(null);
	}
	public int move(Event.Listener ls){
		super.move(ls); //
		//System.out.println("supermove dist: "+ super.getDistance());
		for(int i = 1; i<trainParts.length; i++){
			trainParts[i].move(ls);
		}
		//System.out.println("move locomotive");
		int retval = trainParts[0].move(ls);
		return retval;


	}

	public int stop(){
		System.out.println("===================================================================");
		System.out.println("============A stop has been triggered for the train================");
		System.out.println("===================================================================");
		Thread.dumpStack();
		System.out.println("===================================================================");
		super.stop();
		for(int i = 1; i<trainParts.length; i++){
			trainParts[i].stop();
		}
		return trainParts[0].stop();
	}

	public int start(){
		int sp = findSmallestMaxSpeed(trainParts);
		//System.out.println("smallestmax: "+ sp);
		super.setMaxSpeed(sp);
		super.start();
		for(int i = 1; i< trainParts.length; i++){
			trainParts[i].start();
		}
		//System.out.println("Start locomotive");
		return trainParts[0].start();

	}

    /**
     * Finds then sets all max speeds to the smallest max speed for the movable objects in the supplied array.
     * @param mv
     * @return
     */
    private static int findSmallestMaxSpeed(Movable [] mv){
    	int ret = Integer.MAX_VALUE;
   // 	System.out.println("size: "+mv.length);
    	for(Movable m : mv){
    		int sp = m.getMaxSpeed();
    		if (sp < ret ) ret = sp;
    	}
    	for(Movable m : mv){
    		m.setMaxSpeed(ret);
    	}
    	return ret;
    }


	private static int getTotalLength(Movable[] movable){
		int l = 0;
		for(int i=0; i<movable.length; i++){
			l += movable[i].getLength();
		}
		return l;
	}

	private  static Track[] getTracks(Movable[] movable) {
		return new Track[]{movable[0].getFront(), movable[movable.length-1].getBack()};
	}

	public Movable[] getParts(){
		return trainParts;
	}
	public void toggleDirection(){
		super.toggleDirection();
		for(int x = 0; x< trainParts.length; x++){
			trainParts[x].toggleDirection();
		}
	}

	public void notifySectionFree(Integer trainID, Controller ctl){
		System.out.println("TRAIN ID: " + trainID);
		ctl.notify(new Event.SpeedChanged(trainID, 0.75f));
		ctl.resumeTrain(trainID);

	}

}
