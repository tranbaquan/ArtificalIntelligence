package romania;

public class Edge {
	public double cost;//chi phi
	public Node target;
	
	public Edge (Node targetNode, double costVal){
		target =targetNode;
		cost= costVal;
	}

}
