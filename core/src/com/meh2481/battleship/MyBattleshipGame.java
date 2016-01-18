package com.meh2481.battleship;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyBattleshipGame extends ApplicationAdapter implements InputProcessor
{
	private Texture m_txShipCenterImage;
    private Texture m_txMissImage;
	private Texture m_txShipEdgeImage;
    private Texture m_txBoardBg;
	private Sound m_sMissSound;
	private Music m_mBeginMusic;
	private SpriteBatch m_bBatch;
	private OrthographicCamera m_cCamera;
	private Board m_bBoard;

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
        m_bBoard = new Board(m_txBoardBg, m_txMissImage, m_txShipCenterImage, m_txShipEdgeImage);

        //TEST Place ships randomly on this board and just draw it
        m_bBoard.placeShipsRandom();

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
	}

	@Override
	public void render()
	{
		//Tell the camera to update its matrices.
		m_cCamera.update();

		//Tell the SpriteBatch to render in the coordinate system specified by the camera.
        m_bBatch.setProjectionMatrix(m_cCamera.combined);

		//Draw within this batch
        m_bBatch.begin();

		//TODO Determine board to draw and any text overlays
        m_bBoard.draw(false, m_bBatch);

        m_bBatch.end();

        //if(Gdx.input.isKeyJustPressed(Keys.R))
        //    m_bBoard.placeShipsRandom();
	}

	@Override
	public boolean keyDown(int keycode)
	{
		//TEST Move ships around as we press R
		if(keycode == Keys.R)
			m_bBoard.placeShipsRandom();

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
        iTileX = screenX / m_bBoard.TILE_SIZE;
        iTileY = screenY / m_bBoard.TILE_SIZE;
		if(button == Input.Buttons.LEFT)
		{
            if(!m_bBoard.alreadyFired(iTileX, iTileY))
            {
                Ship sHit = m_bBoard.fireAtPos(iTileX, iTileY);
                if(sHit != null)
                {
                    //TODO Handle hitting a ship
                }
            }
			//posX = screenX - sprite.getWidth()/2;
			//posY = Gdx.graphics.getHeight() - screenY - sprite.getHeight()/2;
		}
		else if(button == Input.Buttons.RIGHT)
		{
			//posX = Gdx.graphics.getWidth()/2 - sprite.getWidth()/2;
			//posY = Gdx.graphics.getHeight()/2 - sprite.getHeight()/2;
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
		m_sMissSound.dispose();
		m_mBeginMusic.dispose();
        m_bBatch.dispose();
	}
}
