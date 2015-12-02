package Support;

public class Pair<X, Y> {
	public final X x; 
	public final Y y; 
	
	public Pair(X x, Y y) { 
		this.x = x; 
		this.y = y; 
	} 
	
	public X getX() {
		return this.x;
	}
	
	public Y getY() {
		return this.y;
	}
}
