package grupo.bolado.gui;

import static grupo.bolado.Game.camera;
import static grupo.bolado.Game.font;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button 
{
	public static ArrayList<Button> mainMenuButtons;
	
	Sprite buttonUpSprite;
	Sprite buttonPressedSprite;
	Sprite buttonHoverSprite;
	
	int state;
	
	int UP=0, HOVER=1, PRESSED=2;
	
	int x, y, w, h;
	String text;
	
	Action action;
	
	int textOffsetX = 40, textOffsetY = 50;
	
	public Button(int x, int y, String text, Sprite buttonUpSprite, Sprite buttonPressedSprite, Sprite buttonHoverSprite, Action action)
	{
		this.buttonUpSprite = buttonUpSprite;
		this.buttonHoverSprite = buttonHoverSprite;
		this.buttonPressedSprite = buttonPressedSprite;
		
		this.x = x;
		this.y = y;
		
		w = (int) buttonUpSprite.getWidth();
		h = (int) buttonUpSprite.getHeight();
		
		this.text = text;
		
		this.action = action;
	}
	
	public void processInput()
	{
		if (Gdx.input.getX() > x-camera.position.x+Gdx.graphics.getWidth()*0.5f && Gdx.input.getX() < x+w-camera.position.x+Gdx.graphics.getWidth()*0.5f)
		{
			if (Gdx.graphics.getHeight()-Gdx.input.getY() > y && Gdx.graphics.getHeight()-Gdx.input.getY() < y+h)
			{
				state = HOVER;
				
				if (Gdx.input.isButtonPressed(Buttons.LEFT))
				{
					state = PRESSED;
					action.execute();
				}
			}
			else
			{
				state = UP;
			}
		}
		else
		{
			state = UP;
		}
	}
	
	public void render(SpriteBatch batch)
	{
		batch.begin();
		if (state == UP)
		{
			buttonUpSprite.setPosition(x, y);
			buttonUpSprite.draw(batch);
			
			font.draw(batch, text, x+textOffsetX, y+textOffsetY);
		}
		if (state == HOVER)
		{
			buttonHoverSprite.setPosition(x, y);
			buttonHoverSprite.draw(batch);
			
			font.draw(batch, text, x+textOffsetX, y+textOffsetY);
		}
		if (state == PRESSED)
		{
			buttonPressedSprite.setPosition(x, y);
			buttonPressedSprite.draw(batch);
			
			font.draw(batch, text, x+textOffsetX, y+textOffsetY);
		}
		batch.end();
	}
	
	static public void processInputs()
	{
		for (Button bt : mainMenuButtons)
		{
			bt.processInput();
		}
	}
	
	static public void renderButtons(SpriteBatch batch)
	{
		for (Button bt : mainMenuButtons)
		{
			bt.render(batch);
		}
	}
	
}
