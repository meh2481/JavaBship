package com.meh2481.battleship;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Mark on 1/15/2016.
 *
 * Abstract class for ships that handles interactions between ships, facilitates launching missiles at ships, etc
 * See Ship_* classes for implementations of this class that contain ship-specific info (such as size, name, etc)
 */
public abstract class Ship
{
    private Sprite m_sShipHitSprite;    //Image for the ship being hit (inner image)
    private Sprite m_sShipOKSprite; //Image for the ship being ok (outer image)
    private boolean m_bHorizontal;  //If the ship is currently aligned horizontally or vertically
    private int m_iXPos, m_iYPos;   //x,y position of the ship on the map
    private Array<Integer> m_iHitPositions; //Positions on this ship that have been hit (order doesn't matter)

    //Ship types
    public static final int TYPE_CARRIER = 6;
    public static final int TYPE_BATTLESHIP = 5;
    public static final int TYPE_CRUISER = 4;
    public static final int TYPE_SUBMARINE = 3;
    public static final int TYPE_DESTROYER = 2;

    //Ship sizes (lengths of each ship type)
    public static final int SIZE_CARRIER = 5;
    public static final int SIZE_BATTLESHIP = 4;
    public static final int SIZE_CRUISER = 3;
    public static final int SIZE_SUBMARINE = 3;
    public static final int SIZE_DESTROYER = 2;

    //Names for each ship to display to user
    public static final String NAME_CARRIER = "Carrier";
    public static final String NAME_BATTLESHIP = "Battleship";
    public static final String NAME_CRUISER = "Cruiser";
    public static final String NAME_SUBMARINE = "Submarine";
    public static final String NAME_DESTROYER = "Destroyer";

    //How faded out a sunk ship looks
    public static final float SHIP_SUNK_ALPHA = 0.65f;

    //Abstract functions to be instantiated in child classes
    abstract public String getName();
    abstract public int getSize();
    abstract public int getType();

    //Getter/setter methods
    public void setHorizontal(boolean b) { m_bHorizontal = b; }     //Sets the alignment of the ship
    public boolean isHorizontal() { return m_bHorizontal; }         //Returns the alignment of the ship
    public boolean isVertical() { return !m_bHorizontal; }
    public void setPosition(int x, int y) { m_iXPos = x; m_iYPos = y; } //Sets the ship position
    public boolean isSunk() { return m_iHitPositions.size == getSize(); }   //Returns true if this ship has been sunk, false otherwise

    /**
     * Constructor. Requires a sprite for the hit image and one for the non-hit image
     * @param sShipHit  LibGDX sprite to use when drawing the center part of the ship (hit image)
     * @param sShipOK   LibGDX sprite to use when drawing the outside edge of the ship
     */
    public Ship(Sprite sShipHit, Sprite sShipOK)
    {
        m_sShipHitSprite = sShipHit;
        m_sShipOKSprite = sShipOK;
        reset();    //Set default values
    }

    /**
     * Resets this ship to default state (off board and not hit)
     */
    public void reset()
    {
        m_iXPos = m_iYPos = -1;
        m_bHorizontal = true;
        m_iHitPositions = new Array<Integer>();
    }

    /**
     * Fires at this ship. Returns true and marks as hit if hit, returns false on miss
     * @param iXpos board x position to test and see if on ship
     * @param iYpos board y position to test and see if on ship
     * @return  true on hit, false on miss
     */
    public boolean fireAtShip(int iXpos, int iYpos)
    {
        if(isHit(iXpos, iYpos))
        {
            if(isHorizontal())
                m_iHitPositions.add(iXpos - m_iXPos);
            else
                m_iHitPositions.add(iYpos - m_iYPos);
            return true;
        }
        return false;
    }

    /**
     * Test if iXpos, iYpos is a position the ship has already been hit
     * @param iXpos x board position to test
     * @param iYpos y board position to test
     * @return  true if this position on the ship has already been hit, false if not
     */
    public boolean alreadyHit(int iXpos, int iYpos)
    {
        if(isHit(iXpos, iYpos))
        {
            for(int i : m_iHitPositions)
            {
                if(isHorizontal())
                {
                    if(i == iXpos - m_iXPos)
                        return true;
                }
                else
                {
                    if(i == iYpos - m_iYPos)
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Test if iXpos, iYpos is a location on the ship
     * @param iXpos x board position to test
     * @param iYpos y board position to test
     * @return  true if this is on the ship, false otherwise
     */
    public boolean isHit(int iXpos, int iYpos)
    {
        if(isHorizontal())   //Ship aligned horizontally
        {
            if(iYpos == m_iYPos &&  //Has to be on the same Y position
               iXpos >= m_iXPos &&  //Has to be equal to or to the right of the left side
               iXpos < m_iXPos + getSize()) //and has to be to the left of the right side
                return true;
        }
        else    //Ship aligned vertically
        {
            if(iXpos == m_iXPos &&  //Has to be on the same X position
               iYpos >= m_iYPos &&  //Has to be equal to or lower than the top side
               iYpos < m_iYPos + getSize()) //and has to be higher than the bottom side
                return true;
        }
        return false;
    }

    /**
     * Draw the ship to the specified SpriteBatch
     * @param bHidden   if ship should be considered "hidden," that is only tiles that have previously been hit should be drawn
     * @param bBatch    LibGDX batch to draw the ship to
     */
    public void draw(boolean bHidden, Batch bBatch)
    {
        if(m_sShipHitSprite == null || m_sShipOKSprite == null) return; //Don't draw if no sprite textures
        if(m_iXPos < 0 || m_iYPos < 0) return;  //Don't draw if off board (position not set yet)

        if(isSunk())    //Change ship's appearance slightly if it's been sunk
        {
            m_sShipHitSprite.setColor(1, 1, 1, SHIP_SUNK_ALPHA); //Draw at half alpha
            m_sShipOKSprite.setColor(1, 1, 1, SHIP_SUNK_ALPHA);
        }

        if(bHidden) //Only draw ship tiles that have been hit (enemy board generally)
        {
            for(int i : m_iHitPositions)
            {
                //Draw both center and edge for hit tiles
                float x = (m_iXPos + ((isHorizontal())?(i):(0))) * m_sShipHitSprite.getWidth();
                float y = (m_iYPos + ((isHorizontal())?(0):(i))) * m_sShipHitSprite.getHeight();
                m_sShipOKSprite.setPosition(x, y);
                m_sShipOKSprite.draw(bBatch);
                m_sShipHitSprite.setPosition(x, y);
                m_sShipHitSprite.draw(bBatch);
            }
        }
        else    //Draw all tiles, including ones that haven't been hit (generally player board)
        {
            //Draw all ship tiles first
            for (int i = 0; i < getSize(); i++)
            {
                //Draw horizontally or vertically depending on our rotation
                m_sShipOKSprite.setPosition((m_iXPos + ((isHorizontal())?(i):(0))) * m_sShipOKSprite.getWidth(), (m_iYPos + ((isHorizontal())?(0):(i))) * m_sShipOKSprite.getHeight());
                m_sShipOKSprite.draw(bBatch);
            }
            //Draw image for tiles on the ship that have been hit
            for(int i : m_iHitPositions)
            {
                m_sShipHitSprite.setPosition((m_iXPos + ((isHorizontal())?(i):(0))) * m_sShipHitSprite.getWidth(), (m_iYPos + ((isHorizontal())?(0):(i))) * m_sShipHitSprite.getHeight());
                m_sShipHitSprite.draw(bBatch);
            }
        }

        if(isSunk())
        {
            m_sShipOKSprite.setColor(Color.WHITE);  //Reset to default color since other ships share this sprite
            m_sShipHitSprite.setColor(Color.WHITE);
        }
    }

    /**
     * Test and see if another ship overlaps this one
     * @param sOther    other ship to test
     * @return          true if the ships overlap, false otherwise
     */
    public boolean checkOverlap(Ship sOther)
    {
        boolean bOverlapping = false;
        //Both ships horizontal
        if(isHorizontal() && sOther.isHorizontal())
        {
            //Only colliding if on the same row
            if(m_iYPos == sOther.m_iYPos)
            {
                if((m_iXPos <= sOther.m_iXPos && m_iXPos + getSize() > sOther.m_iXPos) || //To the left of other ship and overlapping
                   (sOther.m_iXPos + sOther.getSize() > m_iXPos && sOther.m_iXPos <= m_iXPos)) //To the right of other ship and overlapping
                    bOverlapping = true;
            }
        }
        //Both ships vertical
        else if(!isHorizontal() && !sOther.isHorizontal())
        {
            //Only colliding if in the same column
            if(m_iXPos == sOther.m_iXPos)
            {
                if((m_iYPos <= sOther.m_iYPos && m_iYPos + getSize() > sOther.m_iYPos) || //Above other ship and overlapping
                   (sOther.m_iYPos + sOther.getSize() > m_iYPos && sOther.m_iYPos <= m_iYPos)) //Below other ship and overlapping
                    bOverlapping = true;
            }
        }
        //This ship horizontal, other ship vertical
        else if(isHorizontal())
        {
            //Test to see if any square of both ships are colliding
            if(m_iXPos <= sOther.m_iXPos && //Our left side has to be to the left of or colliding with the other ship
               m_iXPos + getSize() > sOther.m_iXPos && //Our right side has to be to the right of or colliding with the other ship
               sOther.m_iYPos <= m_iYPos && //The other ship's top side has to be above or colliding with our ship
               sOther.m_iYPos + sOther.getSize() > m_iYPos) //And the other ship's bottom side has to be below or colliding with our ship
                bOverlapping = true;
        }
        //This ship vertical, other ship horizontal
        else
        {
            //Same as above test, just swap which ship is which
            if(sOther.m_iXPos <= m_iXPos &&
               sOther.m_iXPos + sOther.getSize() > m_iXPos &&
               m_iYPos <= sOther.m_iYPos &&
               m_iYPos + getSize() > sOther.m_iYPos)
                bOverlapping = true;
        }

        return bOverlapping;
    }
}


























