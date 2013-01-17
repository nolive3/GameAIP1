package game.controllers.examples;

import game.controllers.GhostController;
import game.core.Game.DM;
import game.core.Game;
import game.core.GameView;
import java.awt.Color;

public class MyGhosts implements GhostController
{
	private boolean Debugging = false;
	private int[][] targets;
	private static final int X = 0;
	private static final int Y = 1;
	private static final int BLINKY = 0;
	private static final int PINKY = 1;
	private static final int CLIDE = 2;
	private static final int INKY = 3;
	private static final int WIDTH = 4;
	private static final int HIGHT = 4;
	private static final int[] BLINKYHOME = {25*WIDTH,-3*HIGHT};
	private static final int[] PINKYHOME = {2*WIDTH,-3*HIGHT};
	private static final int[] CLIDEHOME = {0*WIDTH,32*HIGHT};
	private static final int[] INKYHOME = {27*WIDTH,32*HIGHT};
	private static final int SCATTER = 0;
	private static final int CHASE = 1;
	private static final int[] DIRS = {Game.UP,Game.LEFT,Game.DOWN,Game.RIGHT};


	public MyGhosts(boolean debugging)
	{
		Debugging = debugging;
		targets = new int[Game.NUM_GHOSTS][2];
	}

	private long dist(int[] pos, int[] target){ // the euclidian distance except squared because floats suck
        long dx = pos[X]-target[X];
        long dy = pos[Y]-target[Y];
        return dx*dx+dy*dy;
	}

	private int[] getXY(int indx, Game g){
        int[] xy = new int[2];
        xy[X] = g.getX(indx);
        xy[Y] = g.getY(indx);
        return xy;
	}
    /*
        these just replaces the ones that are passed as index with x,y and sends the work to the main int[],int[] function
    */
	private long dist(int pos, int[] target, Game g){
        return dist(getXY(pos,g), target);
	}

	private long dist(int pos, int target, Game g){
        return dist(getXY(pos,g), getXY(target,g));
	}

	private long dist(int[] pos, int target, Game g){
        return dist(pos, getXY(target,g));
	}

	private int move(int ghost, Game g){
        int[] moves = g.getGhostNeighbours(ghost);
        long min = Long.MAX_VALUE;
        int move = -1;
        for(int dir:DIRS){
            if(moves[dir]!=-1){
                long cur = dist(moves[dir], targets[ghost], g);
                if(cur<min){
                    min = cur;
                    move = dir;
                }
            }
        }
        return move;
	}

	private int blinkyMove(Game game, int mode){
		switch (mode){
			case SCATTER:
                targets[BLINKY] = BLINKYHOME;
			    break;
			case CHASE:
                targets[BLINKY] = getXY(game.getCurPacManLoc(), game); // pacman
			    break;
			default:break;
		}
		return move(BLINKY, game);
	}

	private int pinkyMove(Game game, int mode){
		switch (mode){
			case SCATTER:
                targets[PINKY] = PINKYHOME;
			    break;
			case CHASE:
                targets[PINKY] = getXY(game.getCurPacManLoc(), game);
                switch (game.getCurPacManDir()){
                    case Game.UP:
                        targets[PINKY][Y] -= 4*HIGHT;
                        targets[PINKY][X] -= 4*WIDTH; //recreating the bug
                        break;
                    case Game.DOWN:
                        targets[PINKY][Y] += 4*HIGHT;
                        break;
                    case Game.LEFT:
                        targets[PINKY][X] -= 4*WIDTH;
                        break;
                    case Game.RIGHT:
                        targets[PINKY][X] += 4*WIDTH;
                        break;
                    default: break;
                } // spot 4 in front of pacman
			    break;
			default:break;
		}
		return move(PINKY, game);
	}

	private int inkyMove(Game game, int mode){
		switch (mode){
			case SCATTER:
                targets[INKY] = INKYHOME;
			    break;
			case CHASE:
                targets[INKY] = getXY(game.getCurPacManLoc(), game);
                switch (game.getCurPacManDir()){
                    case Game.UP:
                        targets[PINKY][Y] -= 2*HIGHT;
                        targets[PINKY][X] -= 2*WIDTH; //recreating the bug
                        break;
                    case Game.DOWN:
                        targets[PINKY][Y] += 2*HIGHT;
                        break;
                    case Game.LEFT:
                        targets[PINKY][X] -= 2*WIDTH;
                        break;
                    case Game.RIGHT:
                        targets[PINKY][X] += 2*WIDTH;
                        break;
                    default: break;
                } // gets spot 2 in front of pacman
                int[] xy = getXY(game.getCurGhostLoc(BLINKY), game); // BLINKY's position
                targets[INKY][X] -= xy[X];
                targets[INKY][Y] -= xy[Y]; // subtract off BLINKY's position to get a blinky relative vector
                targets[INKY][X] *= 2;
                targets[INKY][Y] *= 2; // double the length of the pinky relative vector
                targets[INKY][X] += xy[X];
                targets[INKY][Y] += xy[Y]; //add back BLINKY's position to get back to a 0,0 relative position
			    break;
			default:break;
		}
		return move(INKY, game);
	}

	private int clideMove(Game game, int mode){
		switch (mode){
			case SCATTER:
                targets[CLIDE] = CLIDEHOME;
			    break;
			case CHASE:
                long distToPacman = dist(game.getCurGhostLoc(CLIDE), game.getCurPacManLoc(), game);
                targets[CLIDE] = CLIDEHOME; // go home
                if(distToPacman > 8*4*8*4){ // 8*8 because dist calculates with out the sqrt to avoid needing floating point
                    targets[CLIDE] = getXY(game.getCurPacManLoc(), game); // same target as BLINKY if too far from pacman
                }
			    break;
			default:break;
		}
		return move(CLIDE, game);
	}


	public int[] getActions(Game game,long timeDue)
	{
		int[] directions=new int[Game.NUM_GHOSTS];
		int time = game.getLevelTime(); // 1000/Game.DELAY per second
		int mode = CHASE;
		boolean CruiseElroyMode = game.getNumActivePills()/(float)game.getNumberPills()<.5; // override scatter mode for blinky

		/*
            time in seconds < t seconds
            time*Game.DELAY < t*1000

            Scatter for 7 seconds, then Chase for 20 seconds.
            Scatter for 7 seconds, then Chase for 20 seconds.
            Scatter for 5 seconds, then Chase for 20 seconds.
            Scatter for 5 seconds, then switch to Chase mode permanently.
        */
        if(time*Game.DELAY < 7*1000){
            mode = SCATTER;
        } else if(time*Game.DELAY < (7+20)*1000){
            mode = CHASE;
        } else if(time*Game.DELAY < (7+20+7)*1000){
            mode = SCATTER;
        } else if(time*Game.DELAY < (7+20+7+20)*1000){
            mode = CHASE;
        } else if(time*Game.DELAY < (7+20+7+20+5)*1000){
            mode = SCATTER;
        } else if(time*Game.DELAY < (7+20+7+20+5+20)*1000){
            mode = CHASE;
        } else if(time*Game.DELAY < (7+20+7+20+5+20+5)*1000){
            mode = SCATTER;
        } else {
            mode = CHASE;
        }

        if(game.ghostRequiresAction(BLINKY)){
            directions[BLINKY] = blinkyMove(game, CruiseElroyMode?CHASE:mode);
		}
        if(game.ghostRequiresAction(PINKY)){
            directions[PINKY] = pinkyMove(game, mode);
		}
        if(game.ghostRequiresAction(CLIDE)){
            directions[CLIDE] = clideMove(game, mode);
		}
        if(game.ghostRequiresAction(INKY)){
            directions[INKY] = inkyMove(game, mode);
		}
        if (Debugging) {
            int[] pacmanPos = getXY(game.getCurPacManLoc(), game);
            System.out.println("time: "+time+" ("+time*Game.DELAY/1000.0+") mode:"+ (mode == CHASE? "CHASE":mode==SCATTER?"SCATTER":"???")+ "CruiseElroyMode: "+CruiseElroyMode);
            for(int i=0;i<targets.length;i++) {
                int[] ghostPos = getXY(game.getCurGhostLoc(i), game);
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
				//GameView.addPoints(game, color, game.getGhostPath(i, game.getCurPacManLoc()));
				GameView.addLines(game, color, targets[i][X], targets[i][Y], ghostPos[X], ghostPos[Y]);
			}
		}

		return directions;
	}
}

