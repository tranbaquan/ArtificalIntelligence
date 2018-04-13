package sv_code;

import aima.core.environment.updatevacuum.*;
import aima.core.util.datastructure.XYLocation;
import aima.core.agent.*;
import aima.core.agent.impl.*;

import java.util.*;

class TBQAgentState {
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN = 0;
	final int WALL = 1;
	final int CLEAR = 2;
	final int DIRT = 3;
	final int HOME = 4;
	final int ACTION_NONE = 0;
	final int ACTION_MOVE_FORWARD = 1;
	final int ACTION_TURN_RIGHT = 2;
	final int ACTION_TURN_LEFT = 3;
	final int ACTION_SUCK = 4;

	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;

	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	UpdateVacuumEnvironment n = new UpdateVacuumEnvironment(world.length, world.length);

	TBQAgentState() {
		for (int i = 0; i < world.length; i++)
			for (int j = 0; j < world[i].length; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}

	// Based on the last action and the received percept updates the x & y agent
	// position
	public void updatePosition(DynamicPercept p) {
		Boolean bump = (Boolean) p.getAttribute("bump");

		if (agent_last_action == ACTION_MOVE_FORWARD && !bump) {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
		}

	}

	// update information environment
	public void updateWorld(int x_position, int y_position, int info) {
		world[x_position][y_position] = info;
	}

	public void printWorldDebug() {
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				if (world[j][i] == UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i] == WALL)
					System.out.print(" 1 ");
				if (world[j][i] == CLEAR)
					System.out.print(" 0 ");
				if (world[j][i] == DIRT)
					System.out.print(" D ");
				if (world[j][i] == HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}

	// find home path
	public List<XYLocation> findPath() {
		// list path
		List<XYLocation> path = new ArrayList<>();
		// list visited position
		List<XYLocation> visited = new ArrayList<>();
		path.add(new XYLocation(agent_x_position, agent_y_position));
		visited.add(new XYLocation(agent_x_position, agent_y_position));

		// next location
		XYLocation nextLoc;
		boolean check;
		while (true) {
			check = false;
			// current location
			XYLocation currentLoc = path.get(path.size() - 1);
			// 4 position around current location
			XYLocation[] arrayLoc = {
					// top:
					new XYLocation(currentLoc.getXCoOrdinate(), currentLoc.getYCoOrdinate() - 1),
					// right:
					new XYLocation(currentLoc.getXCoOrdinate() - 1, currentLoc.getYCoOrdinate()),
					// left:
					new XYLocation(currentLoc.getXCoOrdinate() + 1, currentLoc.getYCoOrdinate()),
					// behind:
					new XYLocation(currentLoc.getXCoOrdinate(), currentLoc.getYCoOrdinate() + 1) };

			// if next location is HOME => return path
			for (int i = 0; i < arrayLoc.length; i++) {
				nextLoc = arrayLoc[i];
				if (world[nextLoc.getXCoOrdinate()][nextLoc.getYCoOrdinate()] == HOME) {
					path.add(nextLoc);
					return path;
				}
			}
			// if next location is CLEAR and not yet visited => add to visited
			// otherwise, if next location is UNKNOWN, is WALL or visited => skip
			loop2: for (int i = 0; i < arrayLoc.length; i++) {
				nextLoc = arrayLoc[i];
				if ((world[nextLoc.getXCoOrdinate()][nextLoc.getYCoOrdinate()] == CLEAR)
						&& (!visited.contains(nextLoc))) {
					path.add(nextLoc);
					visited.add(nextLoc);
					check = true;
					break loop2;
				}
			}
			// if no location add in path => remove current location
			if (!check) {
				path.remove(currentLoc);
			}
		}
	}

}

class TBQAgentProgram implements AgentProgram {

	public TBQAgentState state = new TBQAgentState();
	// number step to clean environment
	int countDownForwardMove = state.world.length * 2;
	// home path list
	List<XYLocation> homePath = new ArrayList<>();

	// update environment state : bump, dirt, home
	private void memorizeWorld(boolean bump, boolean dirt, boolean home) {
		// if current location is WALL
		if (bump) {
			// go back 1 step
			System.out.println("dir=" + printDirection(state.agent_direction));
			if (state.agent_direction == TBQAgentState.EAST) {
				state.agent_x_position--;
			} else if (state.agent_direction == TBQAgentState.NORTH) {
				state.agent_y_position++;
			} else if (state.agent_direction == TBQAgentState.SOUTH) {
				state.agent_y_position--;
			} else
				state.agent_x_position++;
			// Update wall position
			switch (state.agent_direction) {
			case TBQAgentState.NORTH:
				state.updateWorld(state.agent_x_position, state.agent_y_position - 1, state.WALL);
				break;
			case TBQAgentState.EAST:
				state.updateWorld(state.agent_x_position + 1, state.agent_y_position, state.WALL);
				break;
			case TBQAgentState.SOUTH:
				state.updateWorld(state.agent_x_position, state.agent_y_position + 1, state.WALL);
				break;
			case TBQAgentState.WEST:
				state.updateWorld(state.agent_x_position - 1, state.agent_y_position, state.WALL);
				break;
			}
		}
		if (dirt)
			state.updateWorld(state.agent_x_position, state.agent_y_position, state.DIRT);
		else
			state.updateWorld(state.agent_x_position, state.agent_y_position, state.CLEAR);
		if (home) {
			state.world[1][1] = state.HOME;
		}

	}

	/**
	 * The rule: if countDownForwardMove = 0 => go home if location is DIRT => clean
	 * if location is WALL => go back and turn right if forward the standing
	 * location is UNKOWN => go forward if forward the standing location is CLEAR =>
	 * checking the right side and then the left side if all of left, right, forward
	 * is CLEAR => go forward
	 */
	// State update based on the percept value and the last action
	@Override
	public Action execute(Percept percept) {

		DynamicPercept p = (DynamicPercept) percept;
		Boolean bump = (Boolean) p.getAttribute("bump");
		Boolean dirt = (Boolean) p.getAttribute("dirt");
		Boolean home = (Boolean) p.getAttribute("home");

		// In thong tin
		System.out.println("percept: " + p);
		System.out.println("- Number of move: " + countDownForwardMove);
		System.out.println("x=" + state.agent_x_position);
		System.out.println("y=" + state.agent_y_position);
		System.out.println("dir=" + printDirection(state.agent_direction));
		state.printWorldDebug();

		// vi tri hien tai bump, dirt, home
		memorizeWorld(bump, dirt, home);

		/**
		 * find path
		 */
		if (countDownForwardMove != 0) {
			if (dirt) {
				System.out.println("DIRT -> choosing SUCK action!");
				state.agent_last_action = state.ACTION_SUCK;
				return UpdateVacuumEnvironment.ACTION_SUCK;
			} else { // WALL or UNKNOWN
				// if p is WALL => turn right
				if (bump.booleanValue()) {
					state.agent_last_action = state.ACTION_TURN_RIGHT;
					state.agent_direction = ((state.agent_direction + 1) % 4);
					System.out.println("BUMP -> TURN_RIGHT if forward is WALL!");
					return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
				} else {// UNKNOWN
					// EAST
					if (state.agent_direction == TBQAgentState.EAST) {
						// forward of EAST:
						if (state.world[state.agent_x_position + 1][state.agent_y_position] == state.UNKNOWN) {
							state.agent_last_action = state.ACTION_MOVE_FORWARD;
							state.updatePosition((DynamicPercept) percept);
							System.out.println("- MOVE FORWARD EAST");
							System.out.println("x=" + state.agent_x_position);
							System.out.println("y=" + state.agent_y_position);
							countDownForwardMove--;
							return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
						}
						// right of EAST:
						if (state.world[state.agent_x_position][state.agent_y_position + 1] == state.UNKNOWN) {
							state.agent_direction = TBQAgentState.SOUTH;
							state.agent_last_action = state.ACTION_TURN_RIGHT;
							return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
						}
						// left of EAST:
						if (state.world[state.agent_x_position][state.agent_y_position - 1] == state.UNKNOWN) {
							state.agent_direction = TBQAgentState.NORTH;
							state.agent_last_action = state.ACTION_TURN_LEFT;
							return UpdateVacuumEnvironment.ACTION_TURN_LEFT;
						}
						state.agent_last_action = state.ACTION_MOVE_FORWARD;
						state.updatePosition((DynamicPercept) percept);
						System.out.println("- MOVE FORWARD EAST");
						countDownForwardMove--;
						return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
					}

					// NORTH
					if (state.agent_direction == TBQAgentState.NORTH) {
						// forward of NORTH
						if (state.world[state.agent_x_position][state.agent_y_position - 1] == state.UNKNOWN) {
							state.agent_last_action = state.ACTION_MOVE_FORWARD;
							state.updatePosition((DynamicPercept) percept);
							System.out.println("- MOVE FORWARD NORTH");
							System.out.println("x=" + state.agent_x_position);
							System.out.println("y=" + state.agent_y_position);
							countDownForwardMove--;
							return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
						}
						// right of NORTH
						if (state.world[state.agent_x_position + 1][state.agent_y_position] == state.UNKNOWN) {
							state.agent_direction = TBQAgentState.EAST;
							state.agent_last_action = state.ACTION_TURN_RIGHT;
							return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
						}
						// left of NORTH
						if (state.world[state.agent_x_position - 1][state.agent_y_position] == state.UNKNOWN) {
							state.agent_direction = TBQAgentState.WEST;
							state.agent_last_action = state.ACTION_TURN_LEFT;
							return UpdateVacuumEnvironment.ACTION_TURN_LEFT;
						}
						state.agent_last_action = state.ACTION_MOVE_FORWARD;
						state.updatePosition((DynamicPercept) percept);
						System.out.println("- MOVE FORWARD NORTH");
						countDownForwardMove--;
						return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
					}
					// SOUTH
					if (state.agent_direction == TBQAgentState.SOUTH) {
						// forward of SOUTH
						if (state.world[state.agent_x_position][state.agent_y_position + 1] == state.UNKNOWN) {
							state.agent_last_action = state.ACTION_MOVE_FORWARD;
							state.updatePosition((DynamicPercept) percept);
							System.out.println("- MOVE FORWARD SOUTH");
							System.out.println("x=" + state.agent_x_position);
							System.out.println("y=" + state.agent_y_position);
							countDownForwardMove--;
							return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
						}
						// right of SOUTH
						if (state.world[state.agent_x_position - 1][state.agent_y_position] == state.UNKNOWN) {
							state.agent_direction = TBQAgentState.WEST;
							state.agent_last_action = state.ACTION_TURN_RIGHT;
							return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
						}
						// left of SOUTH
						if (state.world[state.agent_x_position + 1][state.agent_y_position] == state.UNKNOWN) {
							state.agent_direction = TBQAgentState.EAST;
							state.agent_last_action = state.ACTION_TURN_LEFT;
							return UpdateVacuumEnvironment.ACTION_TURN_LEFT;
						}
						state.agent_last_action = state.ACTION_MOVE_FORWARD;
						state.updatePosition((DynamicPercept) percept);
						System.out.println("- MOVE FORWARD SOUTH");
						countDownForwardMove--;
						return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
					}
//				WEST
					if (state.agent_direction == TBQAgentState.WEST) {
						// forward of WEST
						if (state.world[state.agent_x_position - 1][state.agent_y_position] == state.UNKNOWN) {
							state.agent_last_action = state.ACTION_MOVE_FORWARD;
							state.updatePosition((DynamicPercept) percept);
							System.out.println("- MOVE FORWARD WEST");
							System.out.println("x=" + state.agent_x_position);
							System.out.println("y=" + state.agent_y_position);
							countDownForwardMove--;
							return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
						}
						// right of WEST
						if (state.world[state.agent_x_position][state.agent_y_position - 1] == state.UNKNOWN) {
							state.agent_direction = TBQAgentState.NORTH;
							state.agent_last_action = state.ACTION_TURN_RIGHT;
							return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
						}
						// left of WEST
						if (state.world[state.agent_x_position][state.agent_y_position + 1] == state.UNKNOWN) {
							state.agent_direction = TBQAgentState.SOUTH;
							state.agent_last_action = state.ACTION_TURN_LEFT;
							return UpdateVacuumEnvironment.ACTION_TURN_LEFT;
						}
						state.agent_last_action = state.ACTION_MOVE_FORWARD;
						state.updatePosition((DynamicPercept) percept);
						System.out.println("- MOVE FORWARD WEST");
						countDownForwardMove--;
						return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
					}
				}
			}
		}
		// if countDownForwardMove = 0 => go home
		else {
			System.out.println("---- GO HOME ----");
			if (state.agent_x_position == 1 & state.agent_y_position == 1) {
				System.out.println("TURN OFF");
				return NoOpAction.NO_OP;
			}
			// find path
			if (homePath.isEmpty()) {
				homePath = state.findPath();
			}
//			current location
			XYLocation currentLoc = new XYLocation(state.agent_x_position, state.agent_y_position);
			// next location
			XYLocation nextLoc = homePath.get(0);
			if (currentLoc.equals(nextLoc)) {
				homePath.remove(0);
				nextLoc = homePath.get(0);
			}

			System.out.println("- Home path: ");
			for (XYLocation e : homePath) {
				System.out.print(e.toString() + " -> ");
			}

			// nextLoc above currentLoc:
			if (defineDirectionOf(currentLoc, nextLoc) == TBQAgentState.NORTH) {
				// NORTH => go forward
				if (state.agent_direction == TBQAgentState.NORTH) {
					state.agent_last_action = state.ACTION_MOVE_FORWARD;
					state.updatePosition((DynamicPercept) percept);
					System.out.println("- Move forward");
					System.out.println("x=" + state.agent_x_position);
					System.out.println("y=" + state.agent_y_position);
					return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
				}
				// EAST => turn left
				if (state.agent_direction == TBQAgentState.EAST) {
					state.agent_direction = TBQAgentState.NORTH;
					state.agent_last_action = state.ACTION_TURN_LEFT;
					return UpdateVacuumEnvironment.ACTION_TURN_LEFT;
				}
				// SOUTH => turn right
				if (state.agent_direction == TBQAgentState.SOUTH) {
					state.agent_direction = TBQAgentState.EAST;
					state.agent_last_action = state.ACTION_TURN_RIGHT;
					return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
				}
				// WEST => turn right
				if (state.agent_direction == TBQAgentState.WEST) {
					state.agent_direction = TBQAgentState.NORTH;
					state.agent_last_action = state.ACTION_TURN_RIGHT;
					System.out.println("- Turn right, new dir: " + printDirection(state.agent_direction));
					return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
			// nextLoc below currentLoc:
			if (defineDirectionOf(currentLoc, nextLoc) == TBQAgentState.SOUTH) {
				// SOUTH => go forward
				if (state.agent_direction == TBQAgentState.SOUTH) {
					state.agent_last_action = state.ACTION_MOVE_FORWARD;
					state.updatePosition((DynamicPercept) percept);
					System.out.println("- Move forward");
					System.out.println("x=" + state.agent_x_position);
					System.out.println("y=" + state.agent_y_position);
					return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
				}
				// WEST => turn right
				if (state.agent_direction == TBQAgentState.WEST) {
					state.agent_direction = TBQAgentState.SOUTH;
					state.agent_last_action = state.ACTION_TURN_LEFT;
					return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
				}
				// NORTH => turn left
				if (state.agent_direction == TBQAgentState.NORTH) {
					state.agent_direction = TBQAgentState.WEST;
					state.agent_last_action = state.ACTION_TURN_LEFT;
					return UpdateVacuumEnvironment.ACTION_TURN_LEFT;
				}
				// EAST => turn left
				if (state.agent_direction == TBQAgentState.EAST) {
					state.agent_direction = TBQAgentState.SOUTH;
					state.agent_last_action = state.ACTION_TURN_RIGHT;
					return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
			// nextLoc the right currentLoc:
			if (defineDirectionOf(currentLoc, nextLoc) == TBQAgentState.EAST) {
				// EAST => go forward
				if (state.agent_direction == TBQAgentState.EAST) {
					state.agent_last_action = state.ACTION_MOVE_FORWARD;
					state.updatePosition((DynamicPercept) percept);
					System.out.println("- Move forward");
					System.out.println("x=" + state.agent_x_position);
					System.out.println("y=" + state.agent_y_position);
					return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
				}
				// SOUTH => turn left
				if (state.agent_direction == TBQAgentState.SOUTH) {
					state.agent_direction = TBQAgentState.EAST;
					state.agent_last_action = state.ACTION_TURN_LEFT;
					return UpdateVacuumEnvironment.ACTION_TURN_LEFT;
				}
				// WEST => turn left
				if (state.agent_direction == TBQAgentState.WEST) {
					state.agent_direction = TBQAgentState.SOUTH;
					state.agent_last_action = state.ACTION_TURN_LEFT;
					System.out.println("- Turn left, new dir: " + printDirection(state.agent_direction));
					return UpdateVacuumEnvironment.ACTION_TURN_LEFT;
				}
				// NORTH => turn right
				if (state.agent_direction == TBQAgentState.NORTH) {
					state.agent_direction = TBQAgentState.EAST;
					state.agent_last_action = state.ACTION_TURN_RIGHT;
					return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
			// nextLoc the left currentLoc:
			if (defineDirectionOf(currentLoc, nextLoc) == TBQAgentState.WEST) {
				// WEST => go forward
				if (state.agent_direction == TBQAgentState.WEST) {
					state.agent_last_action = state.ACTION_MOVE_FORWARD;
					state.updatePosition((DynamicPercept) percept);
					System.out.println("- Move forward");
					System.out.println("x=" + state.agent_x_position);
					System.out.println("y=" + state.agent_y_position);
					return UpdateVacuumEnvironment.ACTION_MOVE_FORWARD;
				}
				// NORTH => turn left
				if (state.agent_direction == TBQAgentState.NORTH) {
					state.agent_direction = TBQAgentState.WEST;
					state.agent_last_action = state.ACTION_TURN_LEFT;
					return UpdateVacuumEnvironment.ACTION_TURN_LEFT;
				}
				// EAST => turn left
				if (state.agent_direction == TBQAgentState.EAST) {
					state.agent_direction = TBQAgentState.NORTH;
					state.agent_last_action = state.ACTION_TURN_LEFT;
					return UpdateVacuumEnvironment.ACTION_TURN_LEFT;
				}
				// SOUTH => turn right
				if (state.agent_direction == TBQAgentState.SOUTH) {
					state.agent_direction = TBQAgentState.WEST;
					state.agent_last_action = state.ACTION_TURN_RIGHT;
					return UpdateVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
		}
		return null;

	}

	private String printDirection(int dir) {
		if (dir == 0)
			return "NORTH";
		if (dir == 1)
			return "EAST";
		if (dir == 2)
			return "SOUTH";
		else
			return "WEST";
	}

	/** Xac dinh vi tri cua diem d so voi diem s: */
	private int defineDirectionOf(XYLocation s, XYLocation d) {
		// NORTH:
		if (d.getYCoOrdinate() == (s.getYCoOrdinate() - 1))
			return 0;
		// EAST:
		if (d.getXCoOrdinate() == (s.getXCoOrdinate() + 1))
			return 1;
		// SOUTH:
		if (d.getYCoOrdinate() == (s.getYCoOrdinate() + 1))
			return 2;
		// WEST:
		return 3;
	}

}

public class TBQVacuumAgent extends AbstractAgent {
	public TBQVacuumAgent() {
		super(new TBQAgentProgram());
	}

}
