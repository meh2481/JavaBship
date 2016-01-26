package com.meh2481.battleship;

import com.badlogic.gdx.math.MathUtils;
import java.awt.*;
import java.util.LinkedList;

/**
 * Created by Mark on 1/23/2016.
 *
 * Simple yet effective artificial intelligence for an enemy attempting to sink a player's ships.
 * Includes methods for AI decisions as well as helper enums for facilitating the results of these guesses.
 */
public class EnemyAI
{
    //Result from guessing a position on the map
    public enum Guess
    {
        MISS, HIT, SUNK
    }

    //Direction that our AI is guessing for a particular location. UNSPECIFIED means it'll guess any direction it can
    private enum Direction
    {
        UP, DOWN, LEFT, RIGHT, UNSPECIFIED
    }

    //Data store for figuring out where the AI is guessing and where it should guess next
    private class ShipPos
    {
        public Direction dir;
        public Point pt;
        public int type;    //Ship ID

        public ShipPos(Direction d, int x, int y, int iType)
        {
            pt = new Point(x,y);
            dir = d;
            type = iType;
        }

        public ShipPos(ShipPos p)
        {
            pt = new Point(p.pt);
            dir = p.dir;
            type = p.type;
        }
    }

    private LinkedList<ShipPos> m_lGuessingPositions;  //Hold onto the tiles where the AI has hit player ships, and current guessing directions for these tiles

    //Used to represent invalid guess position
    private final int INVALID_POS = -1;

    //Board tile states; can be these or an id of a ship
    private final int GUESS_NONE = -1;
    private final int GUESS_MISS = 0;

    private int[][] m_iBoardState;  //Guessed positions, in col/row format
    private Point m_ptNextGuess;    //Location on the map our next guess will be
    private boolean m_bHardMode;    //True for harder AI

    public boolean isHardMode() { return m_bHardMode; }     //returns true if ai is set to difficult, false if not
    public void setHardMode(boolean bHardMode) { m_bHardMode = bHardMode; } //set the difficulty of the ai
    public void setEasy() { m_bHardMode = false; }
    public void setHard() { m_bHardMode = true; }

    /**
     * Initialize a new AI for guessing board positions
     */
    public EnemyAI()
    {
        m_iBoardState = new int[Board.BOARD_SIZE][Board.BOARD_SIZE];
        m_ptNextGuess = new Point();
        m_lGuessingPositions = new LinkedList<ShipPos>();
        m_bHardMode = false;
        reset();
    }

    /**
     * Resets the AI to starting state
     */
    public void reset()
    {
        for(int i = 0; i < Board.BOARD_SIZE; i++)
        {
            for(int j = 0; j < Board.BOARD_SIZE; j++)
            {
                m_iBoardState[i][j] = GUESS_NONE;   //No guess at this position to start
            }
        }
        m_ptNextGuess.setLocation(INVALID_POS, INVALID_POS);
    }

    /**
     * Returns a copy of the given Point moved one tile in the direction specified
     * @param pt    Starting point
     * @param dir   Direction to move the point
     * @return      Final point after moving
     */
    private Point translate(Point pt, Direction dir)
    {
        Point ptGuess = new Point(pt);
        switch(dir)
        {
            case UP:
                ptGuess.y--;
                break;

            case DOWN:
                ptGuess.y++;
                break;

            case LEFT:
                ptGuess.x--;
                break;

            case RIGHT:
                ptGuess.x++;
                break;
        }

        return ptGuess;
    }

    /**
     * Returns the direction opposite of the direction specified
     * @param dir   Starting direction
     * @return      Final direction (pointing away from dir)
     */
    private Direction flipDir(Direction dir)
    {
        switch(dir)
        {
            case UP:
                return Direction.DOWN;

            case DOWN:
                return Direction.UP;

            case LEFT:
                return Direction.RIGHT;

            case RIGHT:
                return Direction.LEFT;
        }
        return Direction.UNSPECIFIED;
    }

    /**
     * Returns the direction between the two given points (starting at ptFrom, returns direction to ptTo)
     * precondition -     assumes the direction between the two points is l/r/u/d, no diagonals
     *
     * @param ptFrom    Starting position
     * @param ptTo      Final position
     * @return          Direction to take from ptFrom to get to ptTo
     */
    private Direction dirBetween(Point ptFrom, Point ptTo)
    {
        if(ptFrom.x < ptTo.x)
            return Direction.RIGHT;
        if(ptFrom.x > ptTo.x)
            return Direction.LEFT;
        if(ptFrom.y < ptTo.y)
            return Direction.DOWN;
        if(ptFrom.y > ptTo.y)
            return Direction.UP;
        return Direction.UNSPECIFIED;
    }

    /**
     * Returns true if the AI can guess one tile in the given direction from the given point, false if invalid tile or guessed already
     * @param pt    Starting tile
     * @param d     Direction the AI is trying to guess
     * @return      true if this guess is possible, false if the new position is guessed already or off the map
     */
    private boolean canGuess(Point pt, Direction d)
    {
        if(pt.x != INVALID_POS && pt.y != INVALID_POS)
        {
            Point ptGuess = translate(pt, d);

            if(ptGuess.x < 0 || ptGuess.x >= Board.BOARD_SIZE || ptGuess.y < 0 || ptGuess.y >= Board.BOARD_SIZE)
                return false;

            if(m_iBoardState[ptGuess.x][ptGuess.y] == GUESS_NONE)
                return true;
        }
        return false;
    }

    /**
     * Generates a new guess position for the AI, or if it already exists, returns the next position on the map the AI will guess
     * @return  New map position the AI wants to guess at
     */
    public Point nextGuessPos()
    {
        if(m_lGuessingPositions.isEmpty())  //If we aren't currently trying to sink a ship, guess at random until we hit one
        {
            while(m_ptNextGuess.x == INVALID_POS || m_ptNextGuess.y == INVALID_POS) //Loop until we have a valid guess position
            {
                //Randomly guess an x,y position to guess next
                m_ptNextGuess.y = MathUtils.random(0, Board.BOARD_SIZE - 1);
                if(m_bHardMode) //In hard mode, only guess every other cell (since destroyer is the smallest ship at 2 tiles long)
                {
                    m_ptNextGuess.x = MathUtils.random(0, (Board.BOARD_SIZE/2) - 1);  //only have to guess one every other tile
                    if((m_ptNextGuess.y % 2) == 0)
                        m_ptNextGuess.x = m_ptNextGuess.x * 2 + 1;  //Every other row, shift this over one
                    else
                        m_ptNextGuess.x *= 2;
                }
                else    //In easy mode, will randomly guess any tile
                    m_ptNextGuess.x = MathUtils.random(0, Board.BOARD_SIZE - 1);

                if(m_iBoardState[m_ptNextGuess.x][m_ptNextGuess.y] != GUESS_NONE)   //Already guessed here; loop again
                    m_ptNextGuess.setLocation(INVALID_POS, INVALID_POS);
            }
        }
        else if(m_ptNextGuess.x == INVALID_POS || m_ptNextGuess.y == INVALID_POS)   //We're currently guessing somewhere, but don't know yet where
        {
            ShipPos sp = new ShipPos(m_lGuessingPositions.getFirst());

            if(sp.dir == Direction.UNSPECIFIED) //We just hit a ship and we're not sure which way to go next
            {
                //Just guess from right clockwise. Ideally this would be random, but more trouble than it's worth
                if(canGuess(sp.pt, Direction.RIGHT))
                    sp.dir = Direction.RIGHT;
                else if(canGuess(sp.pt, Direction.DOWN))
                    sp.dir = Direction.DOWN;
                else if(canGuess(sp.pt, Direction.LEFT))
                    sp.dir = Direction.LEFT;
                else if(canGuess(sp.pt, Direction.UP))
                    sp.dir = Direction.UP;
                else    //Nowhere we can guess; go back a point
                {
                    m_lGuessingPositions.pollFirst();   //Knock off stack
                    return nextGuessPos();              //Recursively find a new place to guess
                }
                m_ptNextGuess = translate(sp.pt, sp.dir);   //Next guess is this direction
            }
            else    //We know what direction we're guessing to hit this ship
            {
                if(canGuess(sp.pt, sp.dir)) //We're good for continuing to guess this direction
                    m_ptNextGuess = translate(sp.pt,sp.dir);
                else    //We hit a wall, another previous guess, or another ship
                {
                    m_lGuessingPositions.pollFirst();   //Pop this position off
                    return nextGuessPos();  //Recursively call to find a guess position
                }
            }
        }
        return new Point(m_ptNextGuess);
    }

    /**
     * Make a guess onto the given board, altering the board and the AI state as necessary. Returns the result of this guess
     * @param b     Board to guess onto
     * @return      Result of the guess (hit, miss, or sunk)
     */
    public Guess guess(Board b)
    {
        Guess g = Guess.MISS;
        Point ptGuessPos = nextGuessPos();  //Generate a new guess position
        Ship sHit = b.fireAtPos(ptGuessPos.x, ptGuessPos.y);    //Fire at this position and keep our result
        if(sHit != null)    //We've hit a ship
        {
            m_iBoardState[ptGuessPos.x][ptGuessPos.y] = sHit.getType(); //Store the ship that we guessed at this position
            if(sHit.isSunk())   //Sunk a ship; pop off stack accordingly
            {
                g = Guess.SUNK;
                //Pop all guesses for this ship off the front of the stack
                while(!m_lGuessingPositions.isEmpty())
                {
                    ShipPos sTest = m_lGuessingPositions.getFirst();
                    if(sTest == null)
                        break;
                    if(sTest.type == sHit.getType())  //Same ship
                        m_lGuessingPositions.pollFirst();   //Pop off list
                    else
                        break;
                }
            }
            else    //Not sunk
            {
                g = Guess.HIT;
                if(m_lGuessingPositions.isEmpty())  //We've hit a new ship; store it
                    m_lGuessingPositions.add(new ShipPos(Direction.UNSPECIFIED, ptGuessPos.x, ptGuessPos.y, sHit.getType()));
                else    //Hit a ship while we're trying for one
                {
                    //Test if new ship (push onto back), or same ship (Update front)
                    ShipPos sTest = m_lGuessingPositions.getFirst();
                    if(sTest.type == sHit.getType())    //Same ship; update dir
                    {
                        if(sTest.dir == Direction.UNSPECIFIED)  //This was first hit on ship; store dir pointing in OPPOSITE direction
                        {
                            sTest.dir = dirBetween(ptGuessPos, sTest.pt);   //reverse from & to to get opposite dir
                            //Store this new hit as a new point on the front of the stack to keep guessing that direction
                            m_lGuessingPositions.addFirst(new ShipPos(dirBetween(sTest.pt, ptGuessPos), ptGuessPos.x, ptGuessPos.y, sHit.getType()));
                        }
                        else    //This was another guess; update front of stack
                            sTest.pt = ptGuessPos;                      //Update the front of the stack to this new position
                    }
                    else    //Different ship; save for later (back of stack)
                        m_lGuessingPositions.addLast(new ShipPos(Direction.UNSPECIFIED, ptGuessPos.x, ptGuessPos.y, sHit.getType()));
                }
            }
        }
        else
            m_iBoardState[ptGuessPos.x][ptGuessPos.y] = GUESS_MISS; //Store a miss at this position

        m_ptNextGuess.setLocation(INVALID_POS, INVALID_POS);    //Invalidate our current guess position to generate a new one next pass

        return g;
    }
}
