package lab2_SV;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class SolutionProblem {
	public static State bfs(State initial) {
		Queue<State> q = new LinkedList<>();
		if (initial.isGoal())
			return initial;
		q.add(initial);
		while (!q.isEmpty()) {
			State node = q.poll();
			for (State state : node.generateSuccessors()) {
				if (state.isGoal())
					return state;
				q.add(state);
			}
		}
		return null;
	}

	public static State dfs(State initial) {
		Stack<State> s = new Stack<>();
		if (initial.isGoal())
			return initial;
		s.push(initial);
		while (!s.isEmpty()) {
			State node = s.remove(0);
			for (State state : node.generateSuccessors()) {
				if (state.isGoal())
					return state;
				s.push(state);
			}
		}
		return null;
	}

	public static State dls(State initial, int limit) {
		return dfsLimit(initial, limit);
	}

	private static State dfsLimit(State state, int limit) {
		if (state.isGoal())
			return state;
		if (limit == 0)
			return null;
		for (State chilren : state.generateSuccessors()) {
			State r = dfsLimit(chilren, limit - 1);
			if(r != null) return r;
		}
		return null;
	}

	public static List<State> listSolutionBfs(State initial, int numSolution) {
		List<State> s = new ArrayList<>();
		Stack<State> stack = new Stack<>();
		if (initial.isGoal()) {
			s.add(initial);
		}
		stack.push(initial);
		while (!stack.isEmpty()) {
			State node = stack.remove(0);
			for (State state : node.generateSuccessors()) {
				if (state.isGoal()) {
					if (!s.contains(state)) {
						s.add(state);
					}
					if (s.size() == numSolution) {
						return s;
					}
				}
				stack.push(state);
			}
		}
		return s;
	}

	public static List<State> listSolutionDfs(State initial, int numSolution) {
		List<State> s = new ArrayList<>();
		Queue<State> q = new LinkedList<>();
		if (initial.isGoal()) {
			s.add(initial);
		}
		q.add(initial);
		while (!q.isEmpty()) {
			State node = q.poll();
			for (State state : node.generateSuccessors()) {
				if (state.isGoal()) {
					if (!s.contains(state)) {
						s.add(state);
					}
					if (s.size() == numSolution) {
						return s;
					}
				}
				q.add(state);
			}
		}
		return s;
	}

	public static void main(String[] args) {
		// State state = bfs(new State(3, 3, Position.LEFT, 0, 0));
		// state.print();
		// System.out.println("-------------------");
		// State state1 = dfs(new State(3, 3, Position.LEFT, 0, 0));
		// state1.print();
		// List<State> solutions = listSolutionBfs(new State(3, 3, Position.LEFT, 0, 0),
		// 4);
		// for (State state : solutions) {
		// state.print();
		// System.out.println("-------------------");
		// }
		State state = dfsLimit(new State(3, 3, Position.LEFT, 0, 0), 12);
		state.print();
	}
}
