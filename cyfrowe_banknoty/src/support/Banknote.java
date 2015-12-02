package support;

public class Banknote {
	public double amount;
	// id unique for every banknote
	public byte[] id;
	// t and w creates right side of Alice's identifiers
	public byte[][] t_series;
	public byte[][] w_series;
	// s and u creates left side of Alice's identifiers
	public byte[][] s_series;
	public byte[][] u_series;

	Banknote(double amount, byte[] id) {
		this.amount = amount;
		this.id = id;
	}
	
	public byte[] getId() {
		return this.id;
	}
	
	public void setTSeries(byte[][] t_series) {
		this.t_series = t_series;
	}
	
	public byte[][] getTSeries() {
		return this.t_series;
	}
	
	public byte[] getTSeries(int no) {
		return this.t_series[no];
	}
	
	public void setWSeries(byte[][] w_series) {
		this.w_series = w_series;
	}
	
	public byte[][] getWSeries() {
		return this.w_series;
	}
	
	public byte[] getWSeries(int no) {
		return this.w_series[no];
	}
	
	public void setSSeries(byte[][] s_series) {
		this.s_series = s_series;
	}
	
	public byte[][] getSSeries() {
		return this.s_series;
	}
	
	public byte[] getSSeries(int no) {
		return this.s_series[no];
	}
	
	public void setUSeries(byte[][] u_series) {
		this.u_series = u_series;
	}
	
	public byte[][] getUSeries() {
		return this.u_series;
	}
	
	public byte[] getUSeries(int no) {
		return this.u_series[no];
	}
}
