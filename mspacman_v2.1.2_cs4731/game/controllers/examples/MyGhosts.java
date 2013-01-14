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
	private static final int INKY = 0;
	private static final int BLINKY = 1;
	private static final int PINKY = 2;
	private static final int CLIDE = 3;
	
	public MyGhosts(boolean debugging)
	{
		Debugging = debugging;
		firstround = true;
	}

	private int inkyMove(Game game, long curtime){
		switch ((int)((curtime-starttime)/1000)%4){
			case 0: return Game.UP;
			case 1: return Game.DOWN;
			case 2: return Game.LEFT;
			default:return Game.RIGHT;
		}
	}

	private int blinkyMove(Game game, long curtime){
		return game.getNextGhostDir(BLINKY,game.getCurPacManLoc(),true,Game.DM.EUCLID);
	}

	private int pinkyMove(Game game, long curtime){
		return game.getNextGhostDir(PINKY,game.getCurPacManLoc(),true,Game.DM.EUCLID);
	}

	private int clideMove(Game game, long curtime){
		return game.getNextGhostDir(CLIDE,game.getCurPacManLoc(),true,Game.DM.EUCLID);
	}
	
	
	public int[] getActions(Game game,long timeDue)
	{
		int[] directions=new int[Game.NUM_GHOSTS];
		DM[] dms=Game.DM.values();
		if(firstround){
			starttime = timeDue;
		}; firstround = false;

		directions[INKY] = inkyMove(game, timeDue);
		directions[BLINKY] = blinkyMove(game, timeDue);
		directions[PINKY] = pinkyMove(game, timeDue);
		directions[CLIDE] = clideMove(game, timeDue);
		for(int i=0;i<directions.length;i++) {
			if (Debugging) {
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

