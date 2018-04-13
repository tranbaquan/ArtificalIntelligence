package romania;

import java.util.*;

public class HeuristicNodeComparator implements Comparator<Node>{
	public int compare(Node node1, Node node2) {
		if (node1.getF_score() > node2.getF_score() ){
			return 1;
		}else if (node1.getF_score()  < node2.getF_score() ){
			return -1;
		}else{
			return 0;
		}
	}
}