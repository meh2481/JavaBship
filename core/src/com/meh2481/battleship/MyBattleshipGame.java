package com.meh2481.battleship;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class MyBattleshipGame extends ApplicationAdapter
{
	private Texture m_txShipCenterImage;
	private Texture m_txShipEdgeImage;
    private Texture m_txBoardBg;
	private Sound missSound;
	private Music beginMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Board m_bBoard;

	@Override
	public void create()
	{
		// load the images for the droplet and the bucket, 64x64 pixels each
		m_txShipCenterImage = new Texture(Gdx.files.internal("ship_center.png"));
		m_txShipEdgeImage = new Texture(Gdx.files.internal("ship_edge.png"));
        m_txBoardBg = new Texture(Gdx.files.internal("board.png"));
        m_bBoard = new Board(m_txBoardBg, m_txShipCenterImage, m_txShipEdgeImage);

        //TEST Place ships randomly on this board and just draw it
        m_bBoard.placeShipsRandom();

		// load the drop sound effect and the rain background "music"
		//missSound = Gdx.audio.newSound(Gdx.files.internal("miss.ogg"));
		//beginMusic = Gdx.audio.newMusic(Gdx.files.internal("beginningMusic.ogg"));

		// start the playback of the background music immediately
		//beginMusic.setLooping(true);
		//beginMusic.play();

		//Set the origin 0,0 to be upper-left, not bottom-left like gdx default
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
	}

	@Override
	public void render()
	{
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		//Draw within this batch
		batch.begin();

        m_bBoard.draw(false, batch);

		batch.end();

		//TEST Move ships around as we press R
        if(Gdx.input.isKeyJustPressed(Keys.R))
        //if(Gdx.input.isKeyPressed(Keys.R))
            m_bBoard.placeShipsRandom();
	}

	@Override
	public void dispose()
	{
		// dispose of all the native resources
		m_txShipCenterImage.dispose();
		m_txShipEdgeImage.dispose();
		missSound.dispose();
		beginMusic.dispose();
		batch.dispose();
	}
}
