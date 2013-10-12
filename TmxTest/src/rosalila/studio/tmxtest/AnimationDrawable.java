package rosalila.studio.tmxtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

class AnimationDrawable extends BaseDrawable
{
    int        FRAME_COLS;
	int        FRAME_ROWS;
	
    int        WIDTH;
	int        HEIGHT;
	
	String texture_path; 
	
	private boolean looped = false;
	
	
	public final Animation anim[];
	
	private float stateTime = 0;
	
	int current_row_animation;
	
	public AnimationDrawable(int FRAME_COLS, int FRAME_ROWS, int WIDTH, int HEIGHT, int current_row_animation, String texture_path, float frame_duration)
	{
		this.FRAME_COLS = FRAME_COLS;
		this.FRAME_ROWS = FRAME_ROWS;
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.current_row_animation = current_row_animation;
		this.anim = getAnimations(texture_path, frame_duration);
		setMinWidth(WIDTH);
		setMinHeight(HEIGHT);
		looped = false;
	}
	
	public Animation[] getAnimations(String texture_path, float frame_duration)
	{
        Texture texture = new Texture(Gdx.files.internal(texture_path));     // #9
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / FRAME_COLS, texture.getHeight() / FRAME_ROWS);                                // #10
		
		Animation animations[] = new Animation[FRAME_ROWS];
		for (int i = 0; i < FRAME_ROWS; i++) {
			TextureRegion texture_region[] = new TextureRegion[FRAME_COLS];
	        for (int j = 0; j < FRAME_COLS; j++) {
	        	texture_region[j] = tmp[i][j];
	        }
	        animations[i] = new Animation(frame_duration, texture_region);
		}
		
		return animations;
	}
	
	public void act(float delta)
	{
	    stateTime += delta;
	}
	
	public void reset()
	{
	    stateTime = 0;
	}
	
	public void animateRow(int row, boolean looped)
	{
		reset();
		this.looped = looped;
		current_row_animation = row;
	}
	
	@Override
	public void draw(SpriteBatch batch, float x, float y, float width, float height)
	{
		batch.draw(anim[current_row_animation].getKeyFrame(stateTime,looped), x, y, width, height);
	}
}