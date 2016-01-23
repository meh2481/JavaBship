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
    //Variables for image/sound resources
	private Texture m_txShipCenterImage;
    private Texture m_txMissImage;
	private Texture m_txShipEdgeImage;
    private Texture m_txBoardBg;
    private Texture m_txFireCursorSm;
    private Texture m_txFireCursorLg;
	private Sound m_sMissSound;
	private Music m_mBeginMusic;

    //Variables to handle rendering
	private SpriteBatch m_bBatch;
	private OrthographicCamera m_cCamera;

    //Classes that hold game information
	private Board_Player m_bPlayerBoard;        //Board the player places ships on and the enemy guesses onto
    private Board m_bEnemyBoard;                //Board the enemy places ships on and the player guesses onto
    private EnemyAI m_aiEnemy;                  //Enemy player AI
    private Point m_ptCurMouseTile;   //Current tile the mouse is hovering over

	//Variables & constants to handle current game state
    private int m_iGameMode;    //State machine value for current game mode
    private final int MODE_PLACESHIP = 0;
    private final int MODE_PLAYERTURN = 1;
    private final int MODE_ENEMYTURN = 2;
    private final int MODE_GAMEOVER = 3;
    private long m_iModeCountdown;  //Delay timer for counting down to next game state change
    private long m_iEnemyGuessTimer;    //Pause timer for before enemy guess so the player can tell where they're guessing
    private final double MODESWITCHTIME = 0.6;

    //Constants to deal with flashing cursor
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

		//Load the game resources
		m_txShipCenterImage = new Texture(Gdx.files.internal("ship_center.png"));
		m_txShipEdgeImage = new Texture(Gdx.files.internal("ship_edge.png"));
        m_txMissImage = new Texture(Gdx.files.internal("miss.png"));
        m_txBoardBg = new Texture(Gdx.files.internal("board.png"));
        m_txFireCursorSm = new Texture(Gdx.files.internal("crosshair.png"));
        m_txFireCursorLg = new Texture(Gdx.files.internal("crosshair_lg.png"));
        m_sMissSound = Gdx.audio.newSound(Gdx.files.internal("miss.ogg"));
        m_mBeginMusic = Gdx.audio.newMusic(Gdx.files.internal("beginningMusic.ogg"));

        //Create game logic classes
        m_bPlayerBoard = new Board_Player(m_txBoardBg, m_txMissImage, m_txShipCenterImage, m_txShipEdgeImage);
        m_bEnemyBoard = new Board(m_txBoardBg, m_txMissImage, m_txShipCenterImage, m_txShipEdgeImage);
        m_aiEnemy = new EnemyAI();
        m_bBatch = new SpriteBatch();

        //Initialize game state
        m_bPlayerBoard.startPlacingShips();
        m_bEnemyBoard.placeShipsRandom();
        m_iGameMode = MODE_PLACESHIP;
        m_iModeCountdown = 0;
        m_iEnemyGuessTimer = 0;
        m_ptCurMouseTile = new Point(-1,-1);

		//TODO Start the playback of the background music immediately
		//m_mBeginMusic.setLooping(true);
		//m_mBeginMusic.play();

		//Set the camera origin 0,0 to be upper-left, not bottom-left like the gdx default (makes math easier)
		m_cCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		m_cCamera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void render()
	{
		//Tell the camera to update its matrices.
		m_cCamera.update();

		//Tell the SpriteBatch to render in the coordinate system specified by the camera.
        m_bBatch.setProjectionMatrix(m_cCamera.combined);

        //Update our state machine
        if(m_iGameMode == MODE_PLAYERTURN && m_iModeCountdown > 0)  //Player turn waiting for enemy turn
        {
            if(System.nanoTime() >= m_iModeCountdown)   //Timer expired; set to enemy turn
            {
                m_iModeCountdown = 0;   //Reset timer
                m_iGameMode = MODE_ENEMYTURN;   //Change mode
                m_iEnemyGuessTimer = (long)(System.nanoTime() + MODESWITCHTIME * NANOSEC);  //Set timer for the pause before an enemy guess
            }
        }
        else if(m_iGameMode == MODE_ENEMYTURN)  //Enemy turn
        {
            if(m_iEnemyGuessTimer > 0)  //If we're waiting for the enemy firing animation
            {
                if(System.nanoTime() >= m_iEnemyGuessTimer) //If we've waited long enough
                {
                    m_aiEnemy.guess(m_bPlayerBoard);    //Make enemy AI fire at their guessed position
                    m_iModeCountdown = (long)(System.nanoTime() + MODESWITCHTIME * NANOSEC);    //Start countdown for switching to player's turn
                    m_iEnemyGuessTimer = 0; //Stop counting down
                }
            }
            else if(m_iModeCountdown > 0)   //If we're waiting until countdown is done for player's turn
            {
                if(System.nanoTime() >= m_iModeCountdown)  //Countdown is done
                {
                    m_iGameMode = MODE_PLAYERTURN;  //Switch to player's turn
                    m_iModeCountdown = 0;   //Reset timer
                }
            }
        }

        //---------------------------------
		//Begin drawing
        //---------------------------------
        m_bBatch.begin();

		//TODO Determine any text overlays to draw

        if(m_iGameMode == MODE_PLAYERTURN)  //On the player's turn, draw enemy board, guessed positions, and cursor
        {
            //Draw enemy's board and player's guessed positions
            m_bEnemyBoard.draw(true, m_bBatch);
            if(m_iModeCountdown == 0)   //Draw a crosshair on the tile where the mouse cursor currently is hovering
            {
                //Set the cursor's alpha to sinusoidally increase/decrease for a nice pulsating effect
                Color cCursorCol = new Color(1, 1, 1, CURSOR_MAX_ALPHA);    //Start at high alpha
                double fSecondsElapsed = (double) System.nanoTime() / NANOSEC;   //Use current time for sinusoidal alpha multiply
                cCursorCol.lerp(1, 1, 1, CURSOR_MIN_ALPHA, (float) Math.sin(fSecondsElapsed * Math.PI * CURSOR_FLASH_FREQ));   //Linearly interpolate this color to final value
                m_bBatch.setColor(cCursorCol);
                m_bBatch.draw(m_txFireCursorSm, m_ptCurMouseTile.x * Board.TILE_SIZE, m_ptCurMouseTile.y * Board.TILE_SIZE);
                m_bBatch.setColor(Color.WHITE); //Reset color to default
            }
        }
        else if(m_iGameMode == MODE_ENEMYTURN)  //On enemy's turn, draw player's board and crosshair animation if applicable
        {
            //Draw player's board, showing player where they had placed their ships
            m_bPlayerBoard.draw(false, m_bBatch);
            if(m_iEnemyGuessTimer > 0)  //Draw enemy homing in on their shot
            {
                //Find pixel coordinates of the center of where the crosshair will be
                Point ptEnemyGuessPos = m_aiEnemy.nextGuessPos(m_bPlayerBoard);
                double xCrosshairCenter = ptEnemyGuessPos.x * Board.TILE_SIZE + (double)Board.TILE_SIZE / 2.0;
                double yCrosshairCenter = ptEnemyGuessPos.y * Board.TILE_SIZE + (double)Board.TILE_SIZE / 2.0;

                //Scale this crosshair inwards as time elapses
                double fCrosshairScale = ((double)(m_iEnemyGuessTimer - System.nanoTime()) / NANOSEC) * MODESWITCHTIME * MAX_CROSSHAIR_SCALE + ((double)Board.TILE_SIZE / (double)m_txFireCursorLg.getHeight());
                double fDrawSize = fCrosshairScale * m_txFireCursorLg.getHeight();

                //Draw the crosshair centered on the position where the enemy will be guessing
                m_bBatch.draw(m_txFireCursorLg, (float)(xCrosshairCenter - fDrawSize / 2.0), (float)(yCrosshairCenter - fDrawSize / 2.0), (float)fDrawSize, (float)fDrawSize);
            }
        }
        else if(m_iGameMode == MODE_PLACESHIP)  //If we're placing ships, just draw board as normal
        {
            m_bPlayerBoard.draw(false, m_bBatch);
        }

        //---------------------------------
        //End drawing
        //---------------------------------
        m_bBatch.end();
	}

	@Override
	public boolean keyDown(int keycode)
	{
		//TODO Exit on Esc, start new game on some other key press
		//if(keycode == Keys.R)

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
        //Find what tile we're clicking on
        int iTileX, iTileY;
        iTileX = screenX / Board.TILE_SIZE;
        iTileY = screenY / Board.TILE_SIZE;

		if(button == Input.Buttons.LEFT)    //Clicking left mouse button
		{
            if(m_iGameMode == MODE_PLACESHIP)   //Placing ships; lock this ship's position and go to next ship
            {
                if(m_bPlayerBoard.placeShip(iTileX, iTileY))
                    m_iGameMode = MODE_PLAYERTURN;    //Done placing ships; start playing now   //TODO Start player/enemy going first randomly?
            }
            else if(m_iGameMode == MODE_PLAYERTURN && m_iModeCountdown == 0)   //Playing; fire at a ship
            {
                if (!m_bEnemyBoard.alreadyFired(iTileX, iTileY))    //If we haven't fired here already
                {
                    Ship sHit = m_bEnemyBoard.fireAtPos(iTileX, iTileY);    //Fire!
                    if(sHit != null)    //If we hit a ship
                    {
                        //TODO Handle hitting a ship
                    }
                    else
                        //TODO Handle missing a ship
                    m_iModeCountdown = (long)(System.nanoTime() + MODESWITCHTIME * NANOSEC);    //Start countdown timer for the start of the enemy turn
                }
            }
		}
		else if(button == Input.Buttons.RIGHT)  //Clicking right mouse button
		{
			if(m_iGameMode == MODE_PLACESHIP)   //Rotate ships on RMB if we're currently placing them
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
        //Find the tile the player moved the mouse to
        int iTileX, iTileY;
        iTileX = screenX / Board.TILE_SIZE;
        iTileY = screenY / Board.TILE_SIZE;

        //Save this tile position for later
        m_ptCurMouseTile.x = iTileX;
        m_ptCurMouseTile.y = iTileY;

        if(m_iGameMode == MODE_PLACESHIP)   //If the player is currently placing ships, move ship preview to this location
            m_bPlayerBoard.moveShip(iTileX, iTileY);

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
