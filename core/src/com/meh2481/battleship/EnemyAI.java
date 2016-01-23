package com.meh2481.battleship;

import com.badlogic.gdx.math.MathUtils;

import java.awt.*;

/**
 * Created by Mark on 1/23/2016.
 */
public class EnemyAI
{
    private static final int INVALID_POS = -1;
    private static final int GUESS_NONE = -1;
    private static final int GUESS_MISS = 0;

    private int[][] m_iBoardState;
    private Point m_ptNextGuess;

    public EnemyAI()
    {
        m_iBoardState = new int[Board.BOARD_SIZE][Board.BOARD_SIZE];
        m_ptNextGuess = new Point();
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

    public Point nextGuessPos(Board b)
    {
        while(m_ptNextGuess.x == INVALID_POS || m_ptNextGuess.y == INVALID_POS)
        {
            m_ptNextGuess.x = MathUtils.random(0, Board.BOARD_SIZE-1);  //TODO Smarter AI than just randomly guessing
            m_ptNextGuess.y = MathUtils.random(0, Board.BOARD_SIZE-1);

            if(m_iBoardState[m_ptNextGuess.x][m_ptNextGuess.y] != GUESS_NONE)
                m_ptNextGuess.setLocation(INVALID_POS, INVALID_POS);
        }
        return m_ptNextGuess;
    }

    public void guess(Board b)
    {
        Point ptGuessPos = nextGuessPos(b);
        Ship sHit = b.fireAtPos(ptGuessPos.x, ptGuessPos.y);
        if(sHit != null)
            m_iBoardState[ptGuessPos.x][ptGuessPos.y] = sHit.getType();   //TODO Use this as basis for later guesses
        else
            m_iBoardState[ptGuessPos.x][ptGuessPos.y] = GUESS_MISS;
        m_ptNextGuess.setLocation(INVALID_POS, INVALID_POS);
    }
}
