package rosalila.studio.tmxtest.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Character extends Sprite
{
	private int board_x=-1;
	private int board_y=-1;
	boolean selected =false;
	
	public Character(int board_x,int board_y, String texture_path)
	{
		super(new Texture(texture_path));
		
		this.setBoardX(board_x);
		this.setBoardY(board_y);
	}
	
	public void render(SpriteBatch batch, float tile_size)
	{
		batch.draw(this, getBoardX()*tile_size-(tile_size*getScaleX()-tile_size)/2, getBoardY()*tile_size, tile_size*getScaleX(), tile_size*getScaleY());
	}
	
	void setBoardPosition(int board_x,int board_y)
	{
		this.setBoardX(board_x);
		this.setBoardY(board_y);
	}
	
	public void select()
	{
		selected=true;
		setScale(1.5f);
	}
	
	public void unSelect()
	{
		selected=false;
		setScale(1.f);
	}
	
	public boolean isSelected()
	{
		return selected;
	}

	public int getBoardX() {
		return board_x;
	}

	public void setBoardX(int board_x) {
		this.board_x = board_x;
	}

	public int getBoardY() {
		return board_y;
	}

	public void setBoardY(int board_y) {
		this.board_y = board_y;
	}
}
