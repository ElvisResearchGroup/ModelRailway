package modelrailway.util;

public class Pair<R,T> {
	public Pair(R retValue, T listedTrain){this.retValue = retValue; this.listedTrain = listedTrain;}
	public R retValue;
	public T listedTrain;

	public int hashCode(){
		return (retValue.hashCode() ^ listedTrain.hashCode());
	}
	public boolean equals  ( Object o){
		if(o instanceof Pair ){
			Pair p2 = (Pair)o;
			return (p2.retValue.equals(this.retValue) && p2.listedTrain.equals(this.listedTrain));
		}
		return false;
	}

}
