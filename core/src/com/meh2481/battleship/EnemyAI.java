package com.meh2481.battleship;

import com.badlogic.gdx.math.MathUtils;
import java.awt.*;
import java.util.LinkedList;

/**
 * Created by Mark on 1/23/2016.
 */
public class EnemyAI
{
    public enum Guess
    {
        MISS, HIT, SUNK
    }

    private enum Direction
    {
        UP, DOWN, LEFT, RIGHT, UNSPECIFIED
    }

    private class ShipPos
    {
        public Direction dir;
        public Point pt;
        public int type;

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

    private final int INVALID_POS = -1;
    private final int GUESS_NONE = -1;
    private final int GUESS_MISS = 0;

    private int[][] m_iBoardState;  //Guessed positions, in col/row format
    private Point m_ptNextGuess;

    public EnemyAI()
    {
        m_iBoardState = new int[Board.BOARD_SIZE][Board.BOARD_SIZE];
        m_ptNextGuess = new Point();
        m_lGuessingPositions = new LinkedList<ShipPos>();
        reset();
    }

    public void reset()
    {
        for(int i = 0; i < Board.BOARD_SIZE; i++)
        {
            for(int j = 0; j < Board.BOARD_SIZE; j++)
            {
                m_iBoardState[i][j] = GUESS_NONE;   //No guess
            }
        }
        m_ptNextGuess.setLocation(INVALID_POS, INVALID_POS);
    }

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

    public Point nextGuessPos()
    {
        //System.out.println("nextGuessPos()");
        if(m_lGuessingPositions.isEmpty())  //Guess at random until we hit another ship
        {
            while(m_ptNextGuess.x == INVALID_POS || m_ptNextGuess.y == INVALID_POS)
            {
                System.out.println("guessing random");
                m_ptNextGuess.x = MathUtils.random(0, Board.BOARD_SIZE - 1);  //TODO Smarter AI than just randomly guessing
                m_ptNextGuess.y = MathUtils.random(0, Board.BOARD_SIZE - 1);

                if(m_iBoardState[m_ptNextGuess.x][m_ptNextGuess.y] != GUESS_NONE)
                    m_ptNextGuess.setLocation(INVALID_POS, INVALID_POS);
            }
        }
        else if(m_ptNextGuess.x == INVALID_POS || m_ptNextGuess.y == INVALID_POS)   //We're currently guessing somewhere, but don't know yet where
        {
            System.out.println("gen new guess pos");
            ShipPos sp = new ShipPos(m_lGuessingPositions.getFirst());

            if(sp.dir == Direction.UNSPECIFIED) //We just hit a ship and we're not sure which way to go next
            {
                System.out.println("unspecified dir");
                if(canGuess(sp.pt, Direction.RIGHT))
                    sp.dir = Direction.RIGHT;
                else if(canGuess(sp.pt, Direction.LEFT))
                    sp.dir = Direction.LEFT;
                else if(canGuess(sp.pt, Direction.DOWN))
                    sp.dir = Direction.DOWN;
                else if(canGuess(sp.pt, Direction.UP))
                    sp.dir = Direction.UP;
                else    //Nowhere we can guess; go back a point
                {
                    m_lGuessingPositions.pollFirst();   //Knock off stack
                    return nextGuessPos();              //Recursively find a new place to guess
                }
                m_ptNextGuess = translate(sp.pt, sp.dir);   //Next guess is this direction
                System.out.println("new guess: " + m_ptNextGuess.x + ", " + m_ptNextGuess.y);
            }
            else    //We know what direction we're guessing to hit this ship
            {
                System.out.println("know dir");
                if(canGuess(sp.pt, sp.dir)) //We're good for continuing to guess this direction
                    m_ptNextGuess = translate(sp.pt,sp.dir);
                else    //We hit a wall, another previous guess, or another ship
                {
                    System.out.println("hit wall/prev guess");
                    m_lGuessingPositions.pollFirst();   //Pop this position off
                    return nextGuessPos();  //Recursively call to find a guess position
                }
            }
        }
        return m_ptNextGuess;
    }

    public Guess guess(Board b)
    {
        Guess g = Guess.MISS;
        Point ptGuessPos = nextGuessPos();
        Ship sHit = b.fireAtPos(ptGuessPos.x, ptGuessPos.y);
        if(sHit != null)
        {
            m_iBoardState[ptGuessPos.x][ptGuessPos.y] = sHit.getType();
            if(sHit.isSunk())
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
                {
                    System.out.println("empty & hit new ship");
                    m_lGuessingPositions.add(new ShipPos(Direction.UNSPECIFIED, ptGuessPos.x, ptGuessPos.y, sHit.getType()));
                }
                else    //Hit a ship while we're trying for one
                {
                    System.out.println("Hit ship. Same one?");
                    //Test if new ship (push onto back), or same ship (Update front)
                    ShipPos sTest = m_lGuessingPositions.getFirst();
                    if(sTest.type == sHit.getType())    //Same ship; update dir
                    {
                        System.out.println("Same one");
                        if(sTest.dir == Direction.UNSPECIFIED)  //This was first hit on ship; store dir pointing in OPPOSITE direction
                        {
                            //sTest = m_lGuessingPositions.pollFirst();
                            System.out.println("update old dir");
                            sTest.dir = dirBetween(ptGuessPos, sTest.pt);   //reverse from & to to get opposite dir
                            m_lGuessingPositions.addFirst(new ShipPos(dirBetween(sTest.pt, ptGuessPos), ptGuessPos.x, ptGuessPos.y, sHit.getType()));
                            //m_lGuessingPositions.addFirst(sTest);
                        }
                        else    //This was another guess; update front of stack
                        {
                            //sTest = new ShipPos(m_lGuessingPositions.pollFirst());   //Pop it off the front
                            System.out.println("updating front");
                            sTest.pt = new Point(ptGuessPos);                      //Move it forward, keeping dir
                           // m_lGuessingPositions.addFirst(sTest);       //Push back onto front
                        }
                    }
                    else    //Different ship; save for later (back of stack)
                    {
                        System.out.println("Diff one. Adding to end");
                        m_lGuessingPositions.addLast(new ShipPos(Direction.UNSPECIFIED, ptGuessPos.x, ptGuessPos.y, sHit.getType()));
                    }
                }
            }
        }
        else
        {
            m_iBoardState[ptGuessPos.x][ptGuessPos.y] = GUESS_MISS;

            System.out.println("miss");
        }
        m_ptNextGuess.setLocation(INVALID_POS, INVALID_POS);

        return g;
    }
}
