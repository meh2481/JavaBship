package com.meh2481.battleship;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Mark on 1/23/2016.
 */
public class EnemyAI
{

    private int[][] m_iBoardState;

    public EnemyAI()
    {
        m_iBoardState = new int[Board.BOARD_SIZE][Board.BOARD_SIZE];
        reset();
    }

    public void reset()
    {
        for(int i = 0; i < Board.BOARD_SIZE; i++)
        {
            for(int j = 0; j < Board.BOARD_SIZE; j++)
            {
                m_iBoardState[i][j] = -1;   //No guess
            }
        }
    }

    public void guess(Board b)
    {
        while(true)
        {
            int xGuessPos = MathUtils.random(0, Board.BOARD_SIZE-1);
            int yGuessPos = MathUtils.random(0, Board.BOARD_SIZE-1);

            if(m_iBoardState[yGuessPos][xGuessPos] == -1)
            {
                Ship sHit = b.fireAtPos(xGuessPos, yGuessPos);
                if(sHit != null)
                    m_iBoardState[xGuessPos][yGuessPos] = sHit.getType();   //TODO Use this as basis for later guesses
                break;
            }
        }
    }
}
