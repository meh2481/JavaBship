package com.meh2481.battleship;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.awt.*;
import java.util.Iterator;

/**
 * Created by Mark on 1/15/2016.
 */
public class Board
{
    public static final int BOARD_SIZE = 10;
    public static final int TILE_SIZE = 64;

    private Texture m_txBoardBg;    //Texture for the board background
    private Texture m_txMissImage;  //Image to draw when we've guessed somewhere and missed
    protected Array<Ship> m_lShips;   //Ships on this board
    private Array<Point> m_lMissGuessPos;   //Places on the map that have been guessed already, and were misses

    public Board(Texture txBg, Texture txMiss, Texture txCenter, Texture txEdge)
    {
        m_txBoardBg = txBg;
        m_txMissImage = txMiss;

        m_lShips = new Array<Ship>();
        m_lMissGuessPos = new Array<Point>();

        Sprite sCenter = new Sprite(txCenter);
        Sprite sEdge = new Sprite(txEdge);
        //Create ships and add them to our list
        m_lShips.add(new Ship_Carrier(sCenter, sEdge));
        m_lShips.add(new Ship_Battleship(sCenter, sEdge));
        m_lShips.add(new Ship_Cruiser(sCenter, sEdge));
        m_lShips.add(new Ship_Submarine(sCenter, sEdge));   //It's yellow and you live there
        m_lShips.add(new Ship_Destroyer(sCenter, sEdge));
    }

    public void reset()
    {
        for(Ship s : m_lShips)
            s.reset();

        m_lMissGuessPos.clear();
    }

    public void draw(boolean bHidden, Batch bBatch)
    {
        //Draw board background image
        bBatch.draw(m_txBoardBg, 0, 0);
        //Draw misses
        for(Point p : m_lMissGuessPos)
            bBatch.draw(m_txMissImage, p.x * TILE_SIZE, p.y * TILE_SIZE);
        //Draw ships
        for(Ship s : m_lShips)
            s.draw(bHidden, bBatch);
    }

    //Returns true if every ship on the board is sunk, false otherwise
    public boolean boardCleared()
    {
        for(Ship s : m_lShips)
        {
            if(!s.isSunk())
                return false;
        }
        return true;
    }

    public int shipsLeft()
    {
        int numLeft = 0;
        for(Ship s : m_lShips)
        {
            if(!s.isSunk())
                numLeft++;
        }
        return numLeft;
    }

    public void placeShipsRandom()
    {
        //Clear all positions
        for(Ship s : m_lShips)
            s.setPosition(-1, -1);

        //for(Ship s : m_lShips)
        for(int i = 0; i < m_lShips.size; i++)
        {
            int xPos = -1, yPos = -1;
            while(xPos < 0 || yPos < 0) //Loop until we find a good spot to place this ship
            {
                m_lShips.get(i).setHorizontal(MathUtils.randomBoolean());

                //Generate a new random position for this ship
                if(m_lShips.get(i).isHorizontal())
                {
                    xPos = MathUtils.random(0, BOARD_SIZE - m_lShips.get(i).getSize());
                    yPos = MathUtils.random(0, BOARD_SIZE - 1);
                }
                else
                {
                    xPos = MathUtils.random(0, BOARD_SIZE - 1);
                    yPos = MathUtils.random(0, BOARD_SIZE - m_lShips.get(i).getSize());
                }
                m_lShips.get(i).setPosition(xPos, yPos);

                //Make sure we're not colliding with any other ships in this location
                //for(Ship sTestCollide : m_lShips)
                for(int j = 0; j < i; j++)
                {
                    if(m_lShips.get(i).checkOverlap(m_lShips.get(j)))    //This ship overlaps another one
                    {
                        xPos = yPos = -1;
                        break;
                    }
                }

            }
        }
    }

    //Test if we've already fired at this position (true if we have, false if not)
    public boolean alreadyFired(int xPos, int yPos)
    {
        //Check if we've missed by guessing in this position already
        for(Point p : m_lMissGuessPos)
        {
            if(p.x == xPos && p.y == yPos)
                return true;
        }
        //Check if we've hit by guessing in this position already
        for(Ship s : m_lShips)
        {
            if(s.alreadyHit(xPos, yPos))
                return true;
        }
        //This position is clear for firing
        return false;
    }

    //Fire at this position, returning ship that was hit or null on miss
    public Ship fireAtPos(int xPos, int yPos)
    {
        for(Ship s : m_lShips)
        {
            if(s.fireAtShip(xPos, yPos))
                return s;   //Return the ship itself to facilitate getting the name and testing if sunk or not
        }

        //Miss; add to our miss positions and return nothing
        m_lMissGuessPos.add(new Point(xPos, yPos));
        return null;
    }
}













