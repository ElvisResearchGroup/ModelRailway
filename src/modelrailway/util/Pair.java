package modelrailway.util;

public class Pair<R,T> {
	public Pair(R retValue, T listedTrain){this.fst = retValue; this.snd = listedTrain;}
	public R fst;
	public T snd;

	public int hashCode(){
		return (fst.hashCode() ^ snd.hashCode());
	}
	public boolean equals  ( Object o){
		if(o instanceof Pair ){
			Pair p2 = (Pair)o;
			return (p2.fst.equals(this.fst) && p2.snd.equals(this.snd));
		}
		return false;
	}
	
	public String toString(){
		
	   return "<"+fst.toString()+","+snd.toString()+">";
	}

}
