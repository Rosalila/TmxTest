package rosalila.studio.tmxtest;

import java.util.ArrayList;

import rosalila.studio.tmxtest.characters.Character;
import rosalila.studio.tmxtest.characters.Melee;
import rosalila.studio.tmxtest.characters.Range;
import rosalila.studio.tmxtest.characters.Shifter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BoardGame {
	ArrayList<Character>characters;
	int last_selected_x=-1;
	int last_selected_y=-1;
	String phase = "selecting";
	boolean waiting_touch_up_to_selecting=false;
	
	public BoardGame()
	{
		characters=new ArrayList<Character>();
		characters.add(new Melee(1,1));
		characters.add(new Range(3,6));
		characters.add(new Shifter(2,3));
	}
	
	void logic(int touch_tile_x, int touch_tile_y)
	{
		if(touch_tile_x!=-1 && touch_tile_y!=-1)
		{
			if(phase=="selecting")
			{
				//Deselect all
				unSelectAll();

				//Select if unit touched
				Character character_temp=getCharacter(touch_tile_x, touch_tile_y);
				if(character_temp!=null)
				{
					character_temp.select();
					phase="moving";
				}
			}
			else if(phase=="moving")
			{
				Character character_temp=getCharacter(touch_tile_x, touch_tile_y);
				if(character_temp==null)
				{
					Character character = getSelectedCharacter(); 
					if(character!=null && !waiting_touch_up_to_selecting)
					{
						character.setBoardX(touch_tile_x);
						character.setBoardY(touch_tile_y);
						character.unSelect();
						waiting_touch_up_to_selecting=true;
					} 
				}
			}
		}else
		{
			if(waiting_touch_up_to_selecting)
			{
				waiting_touch_up_to_selecting=false;				
				phase="selecting";
			}
		}
	}
	
	void render(SpriteBatch batch, int tile_size)
	{
		for(int i=0;i<characters.size();i++)
		{
			characters.get(i).render(batch, tile_size);
		}
	}
	
	Character getCharacter(int board_x,int board_y)
	{
		for(int i=0;i<characters.size();i++)
		{
			if(characters.get(i).getBoardX()==board_x
			   && characters.get(i).getBoardY()==board_y)
			{
				return characters.get(i);
			}
		}
		return null;
	}
	
	void unSelectAll()
	{
		for(int i=0;i<characters.size();i++)
		{
			characters.get(i).unSelect();
		}
	}
	
	Character getSelectedCharacter()
	{
		for(int i=0;i<characters.size();i++)
		{
			if(characters.get(i).isSelected())
			{
				return characters.get(i);
			}
		}
		return null;
	}
}
