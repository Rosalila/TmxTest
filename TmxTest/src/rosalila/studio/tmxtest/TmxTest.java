package rosalila.studio.tmxtest;


import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
* Super Mario Brothers like very basic platformer, using a tile map build
* via <a href="http://www.mapeditor.org/>Tiled</a> and a tileset
* and sprites by <a href="http://www.vickiwenderlich.com/">Vicky Wenderlich</a></p>
*
* Shows simple platformer collision detection as well as on-the-fly map modifications
* through destructable blocks!
* @author mzechner
*
*/
public class TmxTest extends GdxTest {
	//aspect ratio
	static float tile_size=128;
	float scale=1;
    private static final int VIRTUAL_WIDTH = (int)(12*tile_size);
    private static final int VIRTUAL_HEIGHT = (int)(8*tile_size);
    private static final float ASPECT_RATIO =
        (float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;
 
    private Camera camera;
    private Rectangle viewport;
	
    Sprite cursor;
	
	
	private Stage stage;
	BoardGame board_game;

	
	/**
	* The player character, has state and state time,
	*/
	static class Koala {
		static float WIDTH;
		static float HEIGHT;
		static float MAX_VELOCITY = 10f;
		static float JUMP_VELOCITY = 40f;
		static float DAMPING = 0.87f;
		
		enum State {
			Standing,
			Walking,
			Jumping
		}
		
		final Vector2 position = new Vector2();
		final Vector2 velocity = new Vector2();
		State state = State.Walking;
		float stateTime = 0;
		boolean facesRight = true;
		boolean grounded = false;
	}
	
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	//private OrthographicCamera camera;
	private Texture koalaTexture;
	private Animation stand;
	private Animation walk;
	private Animation jump;
	private Koala koala;
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() 
	{
		@Override
		protected Rectangle newObject () {
		return new Rectangle();
		}
	};
	private Array<Rectangle> tiles = new Array<Rectangle>();
	
	private static final float GRAVITY = -2.5f;
	
	@Override
	public void create () {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		// load the koala frames, split them, and assign them to Animations
		koalaTexture = new Texture("data/maps/koalio.png");
		TextureRegion[] regions = TextureRegion.split(koalaTexture, 18, 26)[0];
		stand = new Animation(0, regions[0]);
		jump = new Animation(0, regions[1]);
		walk = new Animation(0.15f, regions[2], regions[3], regions[4]);
		walk.setPlayMode(Animation.LOOP_PINGPONG);
		
		// figure out the width and height of the koala for collision
		// detection and rendering by converting a koala frames pixel
		// size into world units (1 unit == tile_size pixels)
		Koala.WIDTH = 1 / tile_size * regions[0].getRegionWidth();
		Koala.HEIGHT = 1 / tile_size * regions[0].getRegionHeight();
		
		// load the map, set the unit scale to 1/tile_size (1 unit == tile_size pixels)
		map = new TmxMapLoader().load("data/maps/level1.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / 1);
		
		// create an orthographic camera, shows us 30x20 units of the world
		//camera = new OrthographicCamera();
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		//camera.setToOrtho(false, 30, 20);
		//camera.setToOrtho(false, 9, 6);
		camera.position.x = 0*128+VIRTUAL_WIDTH/2;
		camera.position.y = 0*128+VIRTUAL_HEIGHT/2;
		camera.update();
		
		// create the Koala we want to move around the world
		koala = new Koala();
		koala.position.set(20, 20);
		
		getTiles(-1000, -1000, 1000, 1000, tiles);
		
//		characters.add(new Dragon(0,200,tiles,camera));
//		characters.add(new Knight(0,200,tiles,camera));
//		characters.add(new Djinn(0,200,tiles,camera));
//		characters.add(new Wizard(0,200,tiles,camera));
//		for(int i=0;i<characters.size();i++)
//			stage.addActor(characters.get(i));
		
		board_game = new BoardGame();
		cursor = new Sprite(new Texture("data/cursor.png"));
	}
	
	@Override
	public void render () {
        // update camera
        camera.update();
        camera.apply(Gdx.gl10);
 
        // set viewport
        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
                          (int) viewport.width, (int) viewport.height);
		
		
		// clear the screen
		Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// get the delta time
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		// update the koala (process input, collision detection, position update)
		//updateKoala(deltaTime);
		
		// let the camera follow the koala, x-axis only
//		camera.position.x = 100*128;
//		camera.position.y = 3*128;
//		camera.update();
		
		// set the tile map rendere view based on what the
		// camera sees and render the map
		renderer.setView((OrthographicCamera) camera);
//		camera.viewportWidth=1/800;
//		camera.viewportHeight=1/444;
		renderer.render();
		

		int touch_tile_x=getTileTouchX();
		int touch_tile_y=getTileTouchY();
		
		SpriteBatch batch = renderer.getSpriteBatch();
		batch.begin();
		//batch.draw(character, 1*tile_size, 1*tile_size, tile_size, tile_size);
		batch.draw(cursor, touch_tile_x*tile_size, touch_tile_y*tile_size, tile_size, tile_size);
		
		
		board_game.logic(touch_tile_x,touch_tile_y);
		board_game.render(batch,(int)tile_size);
		
		batch.end();
		
		
		// render the koala
		renderKoala(deltaTime);
		
//		if(Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A) || isTouched(0, 0.25f))
//		{
//			camera.position.x-=50;
//			camera.update();
//			
//		}
//		
//		if(Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.D) || isTouched(0, 0.25f))
//		{
//			camera.position.x+=50;
//			camera.update();
//		}
		
		
//		stage.act(Gdx.graphics.getDeltaTime());
//		stage.draw();
		
		// check input and apply to velocity & state
		


	}
	
	int getTileTouchX()
	{
		if(Gdx.input.isTouched())
		{
			for(float i=1.0f;i<=12.0f;i++)
			{
				float resize_multipier  = 1/(VIRTUAL_WIDTH*scale/(float)Gdx.graphics.getWidth());
				if((float)Gdx.input.getX() * resize_multipier / (float)Gdx.graphics.getWidth()<i/12.0f)
				{
					return (int)i-1;
				}
			}
		}
		return -1;
	}
	
	int getTileTouchY()
	{
		if(Gdx.input.isTouched())
		{
			for(float i=1.0f;i<=8.0f;i++)
			{
				float resize_multipier  = 1/(VIRTUAL_HEIGHT*scale/(float)Gdx.graphics.getHeight());
				float corrector_offset_y = (float)Gdx.graphics.getHeight()-(float)VIRTUAL_HEIGHT*scale;
				
				if(((float)Gdx.input.getY()-corrector_offset_y)*resize_multipier / (float)Gdx.graphics.getHeight()<i/8.0f)
				{
					return 8-(int)i;
				}
			}
		}
		return -1;
	}
	
	private Vector2 tmp = new Vector2();
	private void updateKoala(float deltaTime) {
		if(deltaTime == 0) return;
		koala.stateTime += deltaTime;	
		
		// check input and apply to velocity & state
		if((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.75f, 1)) && koala.grounded)
		{
			koala.velocity.y += Koala.JUMP_VELOCITY;
			koala.state = Koala.State.Jumping;
			koala.grounded = false;
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A) || isTouched(0, 0.25f)) 
		{
			koala.velocity.x = -Koala.MAX_VELOCITY;
			camera.position.x=0;
			if(koala.grounded) koala.state = Koala.State.Walking;
				koala.facesRight = false;
		}
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D) || isTouched(0.25f, 0.5f))
		{
			koala.velocity.x = Koala.MAX_VELOCITY;
			camera.position.x=0;
			camera.position.y=0;
			if(koala.grounded) koala.state = Koala.State.Walking;
				koala.facesRight = true;
		}
		
		// apply gravity if we are falling
		koala.velocity.add(0, GRAVITY);
		
		// clamp the velocity to the maximum, x-axis only
		if(Math.abs(koala.velocity.x) > Koala.MAX_VELOCITY) {
			koala.velocity.x = Math.signum(koala.velocity.x) * Koala.MAX_VELOCITY;
		}
		
		// clamp the velocity to 0 if it's < 1, and set the state to standign
		if(Math.abs(koala.velocity.x) < 1) {
			koala.velocity.x = 0;
			if(koala.grounded) koala.state = Koala.State.Standing;
		}
		
		// multiply by delta time so we know how far we go
		// in this frame
		koala.velocity.scl(deltaTime);
		
		// perform collision detection & response, on each axis, separately
		// if the koala is moving right, check the tiles to the right of it's
		// right bounding box edge, otherwise check the ones to the left
		Rectangle koalaRect = rectPool.obtain();
		
		
		///
//		Character c=characters.get(0);
//		
//		//koalaRect.set(koala.position.x, koala.position.y, Koala.WIDTH, Koala.HEIGHT);
//		koalaRect.set(c.getX(), c.getY(), 1/tile_size * c.getWidth(), 1/tile_size * c.getHeight());
//		
//		getTiles(-1000, -1000, 1000, 1000, tiles);
		//System.out.println(tiles.size);

		
//		System.out.println("x:"+c.getX());
//		System.out.println("y:"+c.getY());
		
		
//		for(Rectangle tile: tiles)
//		{
////			System.out.println("x:"+tile.x);
////			System.out.println("y:"+tile.y);
//			int offset=10;
//			if(c.getX()+c.getWidth()>tile.x*16-camera.position.x && c.getX()<tile.x*16-camera.position.x+16
//			&& c.getY()+c.getHeight()>tile.y*16 && c.getY()<tile.y*16+16
//					) 
////			if(tile.x*16>=c.getX() && tile.x*16<=c.getX()+c.getWidth()
////			&& tile.y*16>=c.getY() && tile.y*16<=c.getY()+c.getHeight()
////					) 
//			{
//				System.out.println("x:"+tile.x);
//				System.out.println("y:"+tile.y);
//				c.selected=false;
//				c.setX(c.corrector_x);
//				c.setY(c.corrector_y);
//				//System.exit(0);
//				break;
//			}
////			if(koalaRect.overlaps(tile)) {
////				System.out.println("x:"+tile.x);
////				System.out.println("y:"+tile.y);
////				c.selected=false;
////				break;
////			}
//		}
		
		
		
//		System.exit(0);
		
//		int startX, startY, endX, endY;
//		if(koala.velocity.x > 0)
//		{
//			startX = endX = (int)(koala.position.x + Koala.WIDTH + koala.velocity.x);
//		} else {
//			startX = endX = (int)(koala.position.x + koala.velocity.x);
//		}
//		startY = (int)(koala.position.y);
//		endY = (int)(koala.position.y + Koala.HEIGHT);
//		getTiles(startX, startY, endX, endY, tiles);
//		koalaRect.x += koala.velocity.x;
//		for(Rectangle tile: tiles)
//		{
//			if(koalaRect.overlaps(tile)) {
//				koala.velocity.x = 0;
//			break;
//			}
//		}
//		koalaRect.x = koala.position.x;
//		
//		// if the koala is moving upwards, check the tiles to the top of it's
//		// top bounding box edge, otherwise check the ones to the bottom
//		if(koala.velocity.y > 0)
//		{
//			startY = endY = (int)(koala.position.y + Koala.HEIGHT + koala.velocity.y);
//		} else {
//			startY = endY = (int)(koala.position.y + koala.velocity.y);
//		}
//		startX = (int)(koala.position.x);
//		endX = (int)(koala.position.x + Koala.WIDTH);
//		getTiles(startX, startY, endX, endY, tiles);
//		koalaRect.y += koala.velocity.y;
//		for(Rectangle tile: tiles) {
//			if(koalaRect.overlaps(tile))
//			{
//				// we actually reset the koala y-position here
//				// so it is just below/above the tile we collided with
//				// this removes bouncing :)
//				if(koala.velocity.y > 0)
//				{
//					koala.position.y = tile.y - Koala.HEIGHT;
//					// we hit a block jumping upwards, let's destroy it!
//					TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(1);
//					layer.setCell((int)tile.x, (int)tile.y, null);
//				} else 
//				{
//					koala.position.y = tile.y + tile.height;
//					// if we hit the ground, mark us as grounded so we can jump
//					koala.grounded = true;
//				}
//				koala.velocity.y = 0;
//				break;
//			}
//		}
//		rectPool.free(koalaRect);
//		
//		// unscale the velocity by the inverse delta time and set
//		// the latest position
//		koala.position.add(koala.velocity);
//		koala.velocity.scl(1/deltaTime);
//		
//		// Apply damping to the velocity on the x-axis so we don't
//		// walk infinitely once a key was pressed
//		koala.velocity.x *= Koala.DAMPING;
	
	}
	
	private boolean isTouched(float startX, float endX) {
		// check if any finge is touch the area between startX and endX
		// startX/endX are given between 0 (left edge of the screen) and 1 (right edge of the screen)
		for(int i = 0; i < 2; i++) {
			float x = Gdx.input.getX() / (float)Gdx.graphics.getWidth();
			if(Gdx.input.isTouched(i) && (x >= startX && x <= endX))
			{
				return true;
			}
		}
		return false;
	}
		
	private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(1);
		rectPool.freeAll(tiles);
		tiles.clear();
		for(int y = startY; y <= endY; y++) {
			for(int x = startX; x <= endX; x++) {
				Cell cell = layer.getCell(x, y);
				if(cell != null) {
					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
				}
			}
		}
	}
	
	private void renderKoala(float deltaTime) {
		// based on the koala state, get the animation frame
		TextureRegion frame = null;
		switch(koala.state) {
			case Standing: frame = stand.getKeyFrame(koala.stateTime); break;
			case Walking: frame = walk.getKeyFrame(koala.stateTime); break;
			case Jumping: frame = jump.getKeyFrame(koala.stateTime); break;
		}
		
		// draw the koala, depending on the current velocity
		// on the x-axis, draw the koala facing either right
		// or left
		SpriteBatch batch = renderer.getSpriteBatch();
		batch.begin();
		if(koala.facesRight)
		{
			batch.draw(frame, koala.position.x, koala.position.y, Koala.WIDTH, Koala.HEIGHT);
		}else
		{
			batch.draw(frame, koala.position.x + Koala.WIDTH, koala.position.y, -Koala.WIDTH, Koala.HEIGHT);
		}
		
		batch.end();
	}
	
	@Override
	public void dispose () {
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		//stage.setViewport(800, 444, true);
		
        // calculate new viewport
        float aspectRatio = (float)width/(float)height;
        //scale = 1f;
        Vector2 crop = new Vector2(0f, 0f);
        
        if(aspectRatio > ASPECT_RATIO)
        {
            scale = (float)height/(float)VIRTUAL_HEIGHT;
            crop.x = (width - VIRTUAL_WIDTH*scale)/2f;
        }
        else if(aspectRatio < ASPECT_RATIO)
        {
            scale = (float)width/(float)VIRTUAL_WIDTH;
            crop.y = (height - VIRTUAL_HEIGHT*scale)/2f;
        }
        else
        {
            scale = (float)width/(float)VIRTUAL_WIDTH;
        }
 
        float w = (float)VIRTUAL_WIDTH*scale;
        float h = (float)VIRTUAL_HEIGHT*scale;
        viewport = new Rectangle(0, 0, w, h);
	}
}