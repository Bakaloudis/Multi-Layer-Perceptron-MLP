package MLP;

public class Data {
	
	private double x;
	private double y;
	private String C;
	
	public Data(double x, double y, String C) {
		this.x = x;
		this.y = y;
		this.C = C;
	}
	
	public double getx() {
		return x;
	}

	public void setx(double x) {
		this.x = x;
	}

	public double gety() {
		return y;
	}

	public void sety(double y) {
		this.y = y;
	}

	public String getC() {
		return C;
	}
	
	public void setC(String c) {
		C = c;
	}
	public double[] toVector() {
		double[] vec = {1,x,y};
		return vec;
	}
	public double[] toVectorNoBias(){
		double[] vec = {x, y};
		return vec;
	}
	
	public String toString() {
		return (x+","+y+","+C);
	}
}
