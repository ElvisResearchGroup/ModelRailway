package modelrailway.simulation;
import java.util.HashSet;
import java.util.Set;

/**
 * The Train is a Movable object and contains other movable objects.
 * 
 * @author powleybenj
 *
 */
public class Train extends Movable {
	private Movable[] trainParts;
	public Train(Movable[] movable,int maxSpeed,boolean onAlt) {
		super(getTracks(movable), movable[0].getDistance(), getTotalLength(movable), maxSpeed, onAlt);
		//assume train pieces are coupled.
	}
	public int move(){
		for(int i = 1; i<trainParts.length; i++){
			trainParts[i].move();
		}
		return trainParts[0].move();
	}

	public int stop(){
		for(int i = 1; i<trainParts.length; i++){
			trainParts[i].stop();
		}
		return trainParts[0].stop();
	}

    public Track getFront(){
	    return trainParts[0].getFront();
    }
    public Track getBack(){
	    return trainParts[trainParts.length-1].getBack();
	}
	private static int getTotalLength(Movable[] movable){
		int l = 0;
		for(int i=0; i<movable.length; i++){
			l += movable[i].getLength();
		}
		return l;
	}

	private  static Track[] getTracks(Movable[] movable) {
		Set<Track> set = new HashSet<Track>();
		for(int i =0; i< movable.length; i++){
			set.add(movable[i].getFront());
			set.add(movable[i].getBack());
		}
		return  (Track[]) set.toArray();
	}

	public Movable[] getParts(){
		return trainParts.clone();
	}


}
