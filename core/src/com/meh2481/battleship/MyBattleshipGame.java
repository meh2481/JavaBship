package com.meh2481.battleship;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;

public class MyBattleshipGame extends ApplicationAdapter implements InputProcessor
{
	private Texture m_txShipCenterImage;
    private Texture m_txMissImage;
	private Texture m_txShipEdgeImage;
    private Texture m_txBoardBg;
    private Texture m_txFireCursorSm;
    private Texture m_txFireCursorLg;
	private Sound m_sMissSound;
	private Music m_mBeginMusic;
	private SpriteBatch m_bBatch;
	private OrthographicCamera m_cCamera;
	private Board_Player m_bPlayerBoard;        //Board the player places ships on and the enemy guesses onto
    private Board m_bEnemyBoard;                //Board the enemy places ships on and the player guesses onto
    private EnemyAI m_aiEnemy;
    //private boolean m_bPlacingShips;
    private Point m_ptCurMouseTile;   //Current tile the mouse is hovering over

	//Variables to handle current game state
    private int m_iGameMode;
    private final int MODE_PLACESHIP = 0;
    private final int MODE_PLAYERTURN = 1;
    private final int MODE_ENEMYTURN = 2;
    private final int MODE_GAMEOVER = 3;
    private long m_iModeCountdown;
    private long m_iEnemyGuessTimer;    //Pause for a bit before enemy guess so player can tell where they're guessing
    private final double MODESWITCHTIME = 0.6;

    //Variables to deal with flashing cursor
    private final float CURSOR_MIN_ALPHA = 0.45f;
    private final float CURSOR_MAX_ALPHA = 0.7f;
    private final double CURSOR_FLASH_FREQ = 1.65;
    private final double NANOSEC = 1000000000.0;
    private final double MAX_CROSSHAIR_SCALE = 20.0;

	@Override
	public void create()
	{
        //Tell GDX this class will be handling input
        Gdx.input.setInputProcessor(this);

		//Load the game images
		m_txShipCenterImage = new Texture(Gdx.files.internal("ship_center.png"));
		m_txShipEdgeImage = new Texture(Gdx.files.internal("ship_edge.png"));
        m_txMissImage = new Texture(Gdx.files.internal("miss.png"));
        m_txBoardBg = new Texture(Gdx.files.internal("board.png"));
        m_txFireCursorSm = new Texture(Gdx.files.internal("crosshair.png"));
        m_txFireCursorLg = new Texture(Gdx.files.internal("crosshair_lg.png"));
        m_bPlayerBoard = new Board_Player(m_txBoardBg, m_txMissImage, m_txShipCenterImage, m_txShipEdgeImage);
        m_bEnemyBoard = new Board(m_txBoardBg, m_txMissImage, m_txShipCenterImage, m_txShipEdgeImage);
        m_aiEnemy = new EnemyAI();

        m_bPlayerBoard.startPlacingShips();
        m_bEnemyBoard.placeShipsRandom();
        m_iGameMode = MODE_PLACESHIP;
        m_iModeCountdown = 0;
        m_iEnemyGuessTimer = 0;
        //m_bPlacingShips = true;

		//Load the sound effects and music
		m_sMissSound = Gdx.audio.newSound(Gdx.files.internal("miss.ogg"));
		m_mBeginMusic = Gdx.audio.newMusic(Gdx.files.internal("beginningMusic.ogg"));

		//TODO Start the playback of the background music immediately
		//m_mBeginMusic.setLooping(true);
		//m_mBeginMusic.play();

		//Set the origin 0,0 to be upper-left, not bottom-left like gdx default
		m_cCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		m_cCamera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        m_bBatch = new SpriteBatch();

        m_ptCurMouseTile = new Point(-1,-1);
	}

	@Override
	public void render()
	{
		//Tell the camera to update its matrices.
		m_cCamera.update();

		//Tell the SpriteBatch to render in the coordinate system specified by the camera.
        m_bBatch.setProjectionMatrix(m_cCamera.combined);

        //Update our state machine
        if(m_iGameMode == MODE_PLAYERTURN && m_iModeCountdown > 0)
        {
            if(System.nanoTime() >= m_iModeCountdown)
            {
                m_iModeCountdown = 0;

                //TODO Pause before enemy guess
                m_iGameMode = MODE_ENEMYTURN;
                m_iEnemyGuessTimer = (long)(System.nanoTime() + MODESWITCHTIME * NANOSEC);
                //m_aiEnemy.guess(m_bPlayerBoard);
            }
        }
        else if(m_iGameMode == MODE_ENEMYTURN)   //&& m_iModeCountdown > 0
        {
            if(m_iEnemyGuessTimer > 0)
            {
                if(System.nanoTime() >= m_iEnemyGuessTimer)
                {
                    m_aiEnemy.guess(m_bPlayerBoard);
                    m_iModeCountdown = (long)(System.nanoTime() + MODESWITCHTIME * NANOSEC);
                    m_iEnemyGuessTimer = 0;
                }
            }
            else if(m_iModeCountdown > 0)
            {
                if (System.nanoTime() >= m_iModeCountdown)
                {
                    m_iGameMode = MODE_PLAYERTURN;
                    m_iModeCountdown = 0;
                }
            }
        }

		//Draw within this batch
        m_bBatch.begin();

		//TODO Determine any text overlays to draw
        //Draw crosshair over currently highlighted tile
        if(m_iGameMode == MODE_PLAYERTURN)
        {
            //Draw enemy's board and player's guessed positions
            m_bEnemyBoard.draw(true, m_bBatch);
            if(m_iModeCountdown == 0)   //Draw a crosshair on the tile where the mouse cursor currently is hovering
            {
                //Set the alpha to sinusoidally increase/decrease for a nice effect
                Color cCursorCol = new Color(1, 1, 1, CURSOR_MAX_ALPHA);    //Start at high alpha
                double fSecondsElapsed = (double) System.nanoTime() / NANOSEC;   //Use current time for sin alpha multiply
                cCursorCol.lerp(1, 1, 1, CURSOR_MIN_ALPHA, (float) Math.sin(fSecondsElapsed * Math.PI * CURSOR_FLASH_FREQ));   //Linearly interpolate this color to final value
                m_bBatch.setColor(cCursorCol);
                m_bBatch.draw(m_txFireCursorSm, m_ptCurMouseTile.x * Board.TILE_SIZE, m_ptCurMouseTile.y * Board.TILE_SIZE);
                m_bBatch.setColor(Color.WHITE); //Reset color to default
            }
        }
        else if(m_iGameMode == MODE_ENEMYTURN)
        {
            //Draw player's board, showing player where they had placed their ships
            m_bPlayerBoard.draw(false, m_bBatch);
            if(m_iEnemyGuessTimer > 0)  //Draw enemy homing in on their shot
            {
                //First step: Find pixel coordinates of center of where the crosshair will be
                Point ptEnemyGuessPos = m_aiEnemy.nextGuessPos(m_bPlayerBoard);
                double xCrosshairCenter = ptEnemyGuessPos.x * Board.TILE_SIZE + (double)Board.TILE_SIZE / 2.0;
                double yCrosshairCenter = ptEnemyGuessPos.y * Board.TILE_SIZE + (double)Board.TILE_SIZE / 2.0;

                //Scale this inwards as time elapses
                double fCrosshairScale = ((double)(m_iEnemyGuessTimer - System.nanoTime()) / NANOSEC) * MODESWITCHTIME * MAX_CROSSHAIR_SCALE + ((double)Board.TILE_SIZE / (double)m_txFireCursorLg.getHeight());
                double fDrawSize = fCrosshairScale * m_txFireCursorLg.getHeight();

                //Draw centered on the position enemy will be guessing
                m_bBatch.draw(m_txFireCursorLg, (float)(xCrosshairCenter - fDrawSize / 2.0), (float)(yCrosshairCenter - fDrawSize / 2.0), (float)fDrawSize, (float)fDrawSize);
            }
        }
        else if(m_iGameMode == MODE_PLACESHIP)
        {
            m_bPlayerBoard.draw(false, m_bBatch);
        }

        m_bBatch.end();
	}

	@Override
	public boolean keyDown(int keycode)
	{
		/*/TEST Move ships around as we press R
		if(keycode == Keys.R)
        {
            m_bPlayerBoard.reset();
            m_bPlayerBoard.startPlacingShips();
            m_bPlacingShips = true;
        }
        else if(keycode == Keys.T)
        {
            m_bPlayerBoard.placeShipsRandom();
            m_bPlacingShips = false;
        }*/

		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
        int iTileX, iTileY;
        iTileX = screenX / Board.TILE_SIZE;
        iTileY = screenY / Board.TILE_SIZE;
		if(button == Input.Buttons.LEFT)
		{
            if(m_iGameMode == MODE_PLACESHIP)
            {
                if(m_bPlayerBoard.placeShip(iTileX, iTileY))
                    m_iGameMode = MODE_PLAYERTURN;    //Done placing ships; start playing now   //TODO Start player/enemy going first randomly?
            }
            else if(m_iGameMode == MODE_PLAYERTURN && m_iModeCountdown == 0)   //Playing
            {
                if (!m_bEnemyBoard.alreadyFired(iTileX, iTileY))
                {
                    Ship sHit = m_bEnemyBoard.fireAtPos(iTileX, iTileY);
                    if (sHit != null)
                    {
                        //TODO Handle hitting a ship
                    }
                    m_iModeCountdown = (long)(System.nanoTime() + MODESWITCHTIME * NANOSEC);
                }
            }
			//posX = screenX - sprite.getWidth()/2;
			//posY = Gdx.graphics.getHeight() - screenY - sprite.getHeight()/2;
		}
		else if(button == Input.Buttons.RIGHT)
		{
			if(m_iGameMode == MODE_PLACESHIP)
                m_bPlayerBoard.rotateShip();
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
        int iTileX, iTileY;
        iTileX = screenX / Board.TILE_SIZE;
        iTileY = screenY / Board.TILE_SIZE;
        m_ptCurMouseTile.x = iTileX;
        m_ptCurMouseTile.y = iTileY;
        if(m_iGameMode == MODE_PLACESHIP)
        {
            m_bPlayerBoard.moveShip(iTileX, iTileY);
        }
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}

	@Override
	public void dispose()
	{
		//Clean up resources
		m_txShipCenterImage.dispose();
		m_txShipEdgeImage.dispose();
        m_txMissImage.dispose();
        m_txBoardBg.dispose();
        m_txFireCursorSm.dispose();
        m_txFireCursorLg.dispose();
		m_sMissSound.dispose();
		m_mBeginMusic.dispose();
        m_bBatch.dispose();
	}
}
