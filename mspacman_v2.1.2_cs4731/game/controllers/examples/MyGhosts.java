package game.controllers.examples;

import game.controllers.GhostController;
import game.core.Game.DM;
import game.core.Game;
import game.core.GameView;
import java.awt.Color;

public class MyGhosts implements GhostController
{
	private boolean Debugging = false;
	private long starttime;
	private boolean firstround;
	private int[][] targets;
	private static final int X = 0;
	private static final int Y = 1;
	private static final int INKY = 0;
	private static final int BLINKY = 1;
	private static final int PINKY = 2;
	private static final int CLIDE = 3;
	private static final int SCATTER = 0;
	private static final int CHASE = 1;


	public MyGhosts(boolean debugging)
	{
		Debugging = debugging;
		targets = new int[Game.NUM_GHOSTS][2];
		firstround = true;
	}

	private long dist(int[] pos, int[] target){
        long dx = pos[X]-target[X];
        long dy = pos[Y]-target[Y];
        return dx*dx+dy*dy;
	}

	private int dist(int indx, int[] target, Game g){
        int[] xy = new int[2];
        xy[X] = g.getX(indx);
        xy[Y] = g.getY(indx);
        return dist(xy, target);
	}

	private int move(int ghost){

	}

	private void inkyMove(Game game, int mode){
		switch (mode){
			case SCATTER:

			    break;
			case CHASE:

			    break;
			default:break;
		}
		return move(INKY);
	}

	private int blinkyMove(Game game, int mode){
		switch (mode){
			case SCATTER:

			    break;
			case CHASE:

			    break;
			default:break;
		}
		return move(BLINKY);
	}

	private int pinkyMove(Game game, int mode){
		switch (mode){
			case SCATTER:

			    break;
			case CHASE:

			    break;
			default:break;
		}
		return move(PINKY);
	}

	private int clideMove(Game game, int mode){
		switch (mode){
			case SCATTER:

			    break;
			case CHASE:

			    break;
			default:break;
		}
		return move(CLIDE);
	}


	public int[] getActions(Game game,long timeDue)
	{
		int[] directions=new int[Game.NUM_GHOSTS];
		DM[] dms=Game.DM.values();
		if(firstround){
			starttime = timeDue;
		}; firstround = false;

		directions[INKY] = inkyMove(game, CHASE);
		directions[BLINKY] = blinkyMove(game, CHASE);
		directions[PINKY] = pinkyMove(game, CHASE);
		directions[CLIDE] = clideMove(game, CHASE);
        if (Debugging) {
            for(int i=0;i<directions.length;i++) {
				Color color = Color.GRAY;
				if (i == 0) {
					color = Color.RED;
				}
				else if (i == 1) {
					color = Color.PINK;
				}
				else if (i == 2) {
					color = Color.ORANGE;
				}
				else {
					color = Color.BLUE;
				}
				GameView.addPoints(game, color, game.getGhostPath(i, game.getCurPacManLoc()));
				//GameView.addLines(game, color, game.getCurGhostLoc(i), game.getCurPacManLoc());
			}
		}

		return directions;
	}
}

