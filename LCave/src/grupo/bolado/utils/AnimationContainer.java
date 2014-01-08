package grupo.bolado.utils;

import static grupo.bolado.Game.atlas;
import grupo.bolado.LevelManager;
import grupo.bolado.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;

public class AnimationContainer extends Sprite
{
	public Animation animation;
	int numberOfFrames;
	float frameRate;
	
	public float sizeX, sizeY, offsetX, offsetY, sizeMultiplier = 1;
	
	
	public boolean flipVertically = false;
	public boolean looping = true;
	public boolean freeze = false;
	public boolean reverse = false;
	public float animationInstant = 0f;
	
	// Loads the corresponding animation from the texture atlas
	public AnimationContainer(String name, String owner)
	{
		super();
		
		Element animations = Main.root.getChildByName(owner).getChildByName("animations");
		
		// Get animation properties from xml
		numberOfFrames = animations.getChildByName(name).getInt("frames");
		frameRate = animations.getChildByName(name).getFloat("framerate");
		
		// Create an array of frames to be filled with regions from the atlas
		AtlasRegion[] animationFrames = new AtlasRegion[numberOfFrames];
		
		// Get corresponding frames from the texture atlas
		for(int i = 0; i < numberOfFrames; i++)
		{
			animationFrames[i] = atlas.findRegion(owner + "/" + name + "/" + name + String.format("%03d", i+1));
		}
		
		// Create and return the animation
		animation = new Animation(1f/frameRate, animationFrames);
		
		offsetX = animations.getChildByName(name).getFloat("offsetX") * Main.root.getChildByName(owner).getFloat("size");
		offsetY = animations.getChildByName(name).getFloat("offsetY") * Main.root.getChildByName(owner).getFloat("size");
		sizeX = animations.getChildByName(name).getFloat("sizeX") * Main.root.getChildByName(owner).getFloat("size") * animationFrames[0].getRegionWidth() / 32f;
		sizeY = animations.getChildByName(name).getFloat("sizeY") * Main.root.getChildByName(owner).getFloat("size") * animationFrames[0].getRegionHeight() / 32f;
		
		setOrigin(offsetX, offsetY);
	}
	
	public void goToFrame(int frame)
	{
		animationInstant = animation.frameDuration * frame;
		freeze = false;
	}
	
	// Play the animation
	public void play()
	{
		// Get the correct animation frame
		this.setRegion(animation.getKeyFrame(animationInstant));
		
		// Increment animation instant's time, if not frozen
		if (!freeze)
		{
			if (reverse)
			{
				animationInstant -= Gdx.graphics.getDeltaTime();
				
				if (animationInstant < 0)
				{
					if(looping)
					{
						animationInstant = animation.animationDuration;
					}
					else
					{
						animationInstant = 0;
					}
				}
			}
			else
			{
				animationInstant += Gdx.graphics.getDeltaTime();
				
				if (animationInstant > animation.animationDuration)
				{
					if(looping)
					{
						animationInstant = 0;
					}
					else
					{
						animationInstant = animation.animationDuration;
					}
				}
			}
		
			// Don't let it go over it's duration (in other words, loop the animation)
			if (reverse)
			animationInstant = animationInstant % animation.animationDuration;
		}
	}
	
	public void render(Vector2 position)
	{
		if (flipVertically)
		{
			setSize(sizeX*sizeMultiplier, sizeY*sizeMultiplier);
			setPosition(position.x - sizeX/2f*sizeMultiplier + offsetX/sizeMultiplier, 
						position.y - sizeY/2f*sizeMultiplier + offsetY*sizeMultiplier);
		}
		else
		{
			setSize(-sizeX*sizeMultiplier, sizeY*sizeMultiplier);
			setPosition(position.x + sizeX/2f*sizeMultiplier - offsetX/sizeMultiplier, 
						position.y - sizeY/2f*sizeMultiplier + offsetY*sizeMultiplier);
		}
			
		
		LevelManager.renderer.getSpriteBatch().begin();
			draw(LevelManager.renderer.getSpriteBatch());
		LevelManager.renderer.getSpriteBatch().end();
	}
}
