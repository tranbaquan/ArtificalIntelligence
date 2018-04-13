package romania;

public class Node { 
	public  String tenTP;// nhan dinh
	public double g_score=0;// 
	public double h_score=0; //ham danh gia tu dinh N hien tai den go dich 
	public double f_score=0; // ham danh gia tu goc den dich (f=g+h)
	public Edge[] adjacencies;
	public Node parent;
	public Node(String val, double hVal){
		tenTP=val;
		h_score=hVal;
	}
	

	public double getF_score() {
		return f_score;
	}
	public void setF_score(double f_score) {
		this.f_score = f_score;
	}
	
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	@Override
	public String toString() {
		return " [" + this.tenTP + ", g=" + this.g_score + ", h="
				+ this.h_score + ", f=" + this.f_score + "]";
	}

	
	

}
