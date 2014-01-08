package grupo.bolado.gui;

import java.io.File;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import static grupo.bolado.Game.*;

public class MainMenu implements Renderable
{
	SpriteBatch batch;
	
	// Background image
	Sprite backgroundImage;
	
	int timer =0 ;
	// Lumen
	Sprite lumen;
	
	// Play
	Sprite playButtonUpSprite;
	Sprite playButtonPressedSprite;
	Sprite playButtonHoverSprite;
	
	// Credits
	Sprite creditsButtonUpSprite;
	Sprite creditsButtonPressedSprite;
	Sprite creditsButtonHoverSprite;
	
	// Quit
	Sprite quitButtonUpSprite;
	Sprite quitButtonPressedSprite;
	Sprite quitButtonHoverSprite;
	
	// Level Select Bg
	Sprite levelSelectBg;
	
	// Up Levels
	ArrayList<Sprite> upLevels;
	
	// Hover Levels
	ArrayList<Sprite> hoverLevels;
	
	// Pressed Levels
	ArrayList<Sprite> pressedLevels;
	
	// Back button
	Sprite backUp;
	Sprite backPressed;
	Sprite backHover;
	
	boolean credits = false;
	Sprite creditsSprite;
	
	float camXTarget = 0;
	float t; // counter for lumen's oscilation
	ArrayList<Sprite> plus;
	Sprite pluToBeDrawn;
	float t2; // counter for plu's animation
	
	public MainMenu()
	{
		batch = new SpriteBatch();
		
		Button.mainMenuButtons = new ArrayList<Button>();
		
		font = new BitmapFont(new FileHandle(new File("assets/fonts/arial.fnt")), 
							  new FileHandle(new File("assets/fonts/arial_00.png")), false);
		
		// ---------------- //
		// Background image //
		// ---------------- //
		// Load the background image
		backgroundImage = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/menuBg.png"))));
		// Resize the background image
		backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// Avoids aliasing artifacts
		backgroundImage.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		
		// ----- //
		// Lumen //
		// ----- //
		// Load the lumen
		lumen = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/lumen.png"))));
		
		// --- //
		// Plu //
		// --- //
		// Load all plu's frames
		plus = new ArrayList<Sprite>();
		for(int i=0; i<6; i++)
		{
			plus.add( new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/pluAnimation/0"+(i+1)+".png")))) );
		}
		
		// ----------- //
		// Play button //
		// ----------- //
		// Load play buttons
		playButtonUpSprite = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/playUp.png"))));
		playButtonHoverSprite = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/playHover.png"))));
		playButtonPressedSprite = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/playPressed.png"))));
		// Resize play buttons
		playButtonUpSprite.setSize(0.15f * playButtonPressedSprite.getWidth(), 0.15f * playButtonPressedSprite.getHeight());
		playButtonUpSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		playButtonHoverSprite.setSize(0.15f * playButtonPressedSprite.getWidth(), 0.15f * playButtonPressedSprite.getHeight());
		playButtonHoverSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		playButtonPressedSprite.setSize(0.15f * playButtonPressedSprite.getWidth(), 0.15f * playButtonPressedSprite.getHeight());
		playButtonPressedSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		
		// -------------- //
		// Credits button //
		// -------------- //
		// Load options buttons
		creditsButtonUpSprite = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/creditsUp.png"))));
		creditsButtonHoverSprite = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/creditsHover.png"))));
		creditsButtonPressedSprite = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/creditsPressed.png"))));
		// Resize options buttons
		creditsButtonUpSprite.setSize(0.15f * creditsButtonPressedSprite.getWidth(), 0.15f * creditsButtonPressedSprite.getHeight());
		creditsButtonUpSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		creditsButtonHoverSprite.setSize(0.15f * creditsButtonPressedSprite.getWidth(), 0.15f * creditsButtonPressedSprite.getHeight());
		creditsButtonHoverSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		creditsButtonPressedSprite.setSize(0.15f * creditsButtonPressedSprite.getWidth(), 0.15f * creditsButtonPressedSprite.getHeight());
		creditsButtonPressedSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		// ----------- //
		// Quit button //
		// ----------- //
		// Load options buttons
		quitButtonUpSprite = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/quitUp.png"))));
		quitButtonHoverSprite = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/quitHover.png"))));
		quitButtonPressedSprite = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/quitPressed.png"))));
		// Resize options buttons
		quitButtonUpSprite.setSize(0.15f * quitButtonPressedSprite.getWidth(), 0.15f * quitButtonPressedSprite.getHeight());
		quitButtonUpSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		quitButtonHoverSprite.setSize(0.15f * quitButtonPressedSprite.getWidth(), 0.15f * quitButtonPressedSprite.getHeight());
		quitButtonHoverSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		quitButtonPressedSprite.setSize(0.15f * quitButtonPressedSprite.getWidth(), 0.15f * quitButtonPressedSprite.getHeight());
		quitButtonPressedSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		// --------------- //
		// Level Select Bg //
		// --------------- //
		// Load the levelSelectBg image
		levelSelectBg = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/levelSelectBg.png"))));
		// Resize the levelSelectBg image
		levelSelectBg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// Avoids aliasing artifacts
		levelSelectBg.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		// ---------------------------------- //
		// Load all buttons from Level Select //
		// ---------------------------------- //
		upLevels = new ArrayList<Sprite>();
		for(int i=0; i<10; i++)
		{
			upLevels.add( new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/levels/0"+(i+1)+"Up.png")))) );
		}
		
		hoverLevels = new ArrayList<Sprite>();
		for(int i=0; i<10; i++)
		{
			hoverLevels.add( new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/levels/0"+(i+1)+"Hover.png")))) );
		}
		
		pressedLevels = new ArrayList<Sprite>();
		for(int i=0; i<10; i++)
		{
			pressedLevels.add( new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/levels/0"+(i+1)+"Pressed.png")))) );
		}
			
		
		resize();
		camXTarget = camera.position.x;

		// Play button
		Button.mainMenuButtons.add(new Button ((int) (Gdx.graphics.getWidth()/2 - playButtonUpSprite.getWidth()/2), (int) (Gdx.graphics.getHeight()/2.5 + Gdx.graphics.getHeight()/8 - playButtonUpSprite.getWidth()/2), "", playButtonUpSprite, playButtonPressedSprite, playButtonHoverSprite, new Action() {
			
			@Override
			public void execute() 
			{
				camXTarget = Gdx.graphics.getWidth()*1.5f;
			}
		}));
		
		// Credits button
		Button.mainMenuButtons.add(new Button((int) (Gdx.graphics.getWidth()/2 - playButtonUpSprite.getWidth()/2), (int) (Gdx.graphics.getHeight()/2.5 - playButtonUpSprite.getWidth()/2), "", creditsButtonUpSprite, creditsButtonPressedSprite, creditsButtonHoverSprite, new Action() {
			
			@Override
			public void execute() 
			{
				if (credits == false && timer > 20)
				{
					credits = true;
					timer=0;
				}
				else if ( timer > 20)
				{
					credits = false;
					timer=0;
				}
				
			}
		}));
		
		// Back button
		Button.mainMenuButtons.add(new Button((int) (Gdx.graphics.getWidth()/2 - playButtonUpSprite.getWidth()/2),  (int) (Gdx.graphics.getHeight()/2.5 - Gdx.graphics.getHeight()/8 - playButtonUpSprite.getWidth()/2), "", quitButtonUpSprite, quitButtonPressedSprite, quitButtonHoverSprite, new Action() {
			
			@Override
			public void execute() 
			{
				Gdx.app.exit();
			}
		}));
		
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// WARNING: Falcatrua braba

		
		// All buttons from Level Select menu
		for (int i=0; i<5; i++)
		{
			upLevels.get(i).setSize(0.25f * upLevels.get(i).getWidth(), 0.25f * upLevels.get(i).getHeight());
			pressedLevels.get(i).setSize(0.25f * pressedLevels.get(i).getWidth(), 0.25f * pressedLevels.get(i).getHeight());
			hoverLevels.get(i).setSize(0.25f * hoverLevels.get(i).getWidth(), 0.25f * hoverLevels.get(i).getHeight());
			final int j = i + 1;
			Button.mainMenuButtons.add(new Button(Gdx.graphics.getWidth()+Gdx.graphics.getWidth()/6 + (i*Gdx.graphics.getWidth()/6) - (int) upLevels.get(i).getWidth()/2, Gdx.graphics.getHeight()/2 + Gdx.graphics.getHeight()/8 - (int) upLevels.get(i).getHeight()/2, "", upLevels.get(i), pressedLevels.get(i), hoverLevels.get(i), new Action() {
				
				@Override
				public void execute() 
				{
					levelManager.loadMap("1_"+j);
					
					activeRenderer = levelManager;
				}
			}));
		}
		
		for (int i=5; i<10; i++)
		{
			upLevels.get(i).setSize(0.25f * upLevels.get(i).getWidth(), 0.25f * upLevels.get(i).getHeight());
			pressedLevels.get(i).setSize(0.25f * pressedLevels.get(i).getWidth(), 0.25f * pressedLevels.get(i).getHeight());
			hoverLevels.get(i).setSize(0.25f * hoverLevels.get(i).getWidth(), 0.25f * hoverLevels.get(i).getHeight());
			final int j = i + 1;
			Button.mainMenuButtons.add(new Button(Gdx.graphics.getWidth()+Gdx.graphics.getWidth()/6 + ((i-5)*Gdx.graphics.getWidth()/6) - (int) upLevels.get(i).getWidth()/2, Gdx.graphics.getHeight()/2 - Gdx.graphics.getHeight()/8 - (int) upLevels.get(i).getHeight()/2, "", upLevels.get(i), pressedLevels.get(i), hoverLevels.get(i), new Action() {
				
				@Override
				public void execute() 
				{
					levelManager.loadMap("1_"+j);
					
					activeRenderer = levelManager;
				}
			}));
		}
		
		// Back button
		
		backUp = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/levels/backUp.png"))));
		backUp.setSize(0.1f * backUp.getWidth(), 0.1f * backUp.getHeight());
		backHover = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/levels/backHover.png"))));
		backHover.setSize(0.1f * backHover.getWidth(), 0.1f * backHover.getHeight());
		backPressed = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/levels/backPressed.png"))));
		backPressed.setSize(0.1f * backPressed.getWidth(), 0.1f * backPressed.getHeight());
		
		
		Button.mainMenuButtons.add(new Button((int) ((3*Gdx.graphics.getWidth())/2) - (int) backUp.getWidth()/2,  20, "", backUp, backPressed, backHover, new Action() {
			
			@Override
			public void execute() 
			{
				camXTarget = Gdx.graphics.getWidth()/2;
			}
		}));

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
		// Level Select menu to the right position
		levelSelectBg.setPosition(Gdx.graphics.getWidth(), 0);
		
		creditsSprite = new Sprite(new Texture(new FileHandle(new File("assets/GUI/mainMenu/credits2.png"))));
		creditsSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		creditsSprite.setSize(0.5f * creditsSprite.getWidth(), 0.5f * creditsSprite.getHeight());
		
	}
	
	public void updateLumen()
	{
		t += Gdx.graphics.getDeltaTime();
		lumen.setPosition(Gdx.graphics.getWidth()/2f - Gdx.graphics.getWidth()/4f - lumen.getWidth()/2f, 30 * MathUtils.sin(2*t));
	}
	
	public void pluAnimation()
	{
		t2 += 5*Gdx.graphics.getDeltaTime();
		int[] spriteList = {01,02,03,04,04,03,02,01,01,02,03,04,04,03,02,01,01,05,03,06,04,03,02,01,01,02,03,04,04,03,02,01};
		if (t2 >= spriteList.length)
		{
			t2 = 0;
		}
		pluToBeDrawn = plus.get(spriteList[(int) t2] -1);
		pluToBeDrawn.setPosition(Gdx.graphics.getWidth()/2f + Gdx.graphics.getWidth()/4f - pluToBeDrawn.getWidth()/2f, Gdx.graphics.getHeight()/32f);
	}
	
	@Override
	public void render() 
	{
		timer++;
		
		updateLumen();
		pluAnimation();
		// Clear the screen
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Adjust the camera
		camera.position.set(camera.position.x + (camXTarget - camera.position.x) * 0.1f, camera.position.y, camera.position.z);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		// Draw background, lumen
		batch.begin();
		levelSelectBg.draw(batch);
		backgroundImage.draw(batch);
		lumen.draw(batch);
		pluToBeDrawn.draw(batch);
		if (credits)
			creditsSprite.draw(batch);
		batch.end();		
		
		// Draw buttons
		Button.renderButtons(batch);
		
		Button.processInputs();
	}


	@Override
	public void resize() {
		// Adapt the camera to the new window resolution
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
	}

}
