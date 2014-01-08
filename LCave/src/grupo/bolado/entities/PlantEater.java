package grupo.bolado.entities;

import static grupo.bolado.Game.rayHandler;
import grupo.bolado.Game;
import grupo.bolado.LevelManager;
import grupo.bolado.utils.AnimationContainer;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class PlantEater 
{
	
	Body plantBody;
	
	AnimationContainer plantAttack, plantSwallow, currentAnim;
	
	PointLight baitLight;
	
	Vector2 plantPosition, baitPosition, baitReturnPoint;
	
	Sprite baitSprite, tongueSprite;
	
	float baitSizeX=1f, baitSizeY=1f;
	float baitRadius = .3f;
	
	static public int attackingPlant = -1;
	
	public int plantID;
	
	// States
	public boolean isEating = false;
	public boolean isGrabbingPlu = false;
	public boolean wasGrabbingPlu = false;
	
	public PlantEater(MapObject startPoint, int plantID)
	{
		// Our plant's ID
		this.plantID = plantID;
		
		// Getting it's position from the tmx file
		float y1 = ( ((PolylineMapObject) startPoint).getPolyline().getVertices()[1] + ((PolylineMapObject) startPoint).getPolyline().getY() ) / 32f;
		float y2 = ( ((PolylineMapObject) startPoint).getPolyline().getVertices()[3] + ((PolylineMapObject) startPoint).getPolyline().getY() ) / 32f;
		
		float x = ((PolylineMapObject) startPoint).getPolyline().getX()/32f;
				
		if (y1 > y2)
		{
			plantPosition = new Vector2(x, y1);
			new PointLight(rayHandler, 600, new Color(1,0,0,0.3f), 30, x, y1-0.5f);
			
			baitReturnPoint = new Vector2(x, y2);
		}
		else
		{
			plantPosition = new Vector2(x, y2);
			new PointLight(rayHandler, 600, new Color(1,0,0,0.3f), 30, x, y1-0.5f);
			baitReturnPoint = new Vector2(x, y1);
		}

		// baitPosition starts as it's limit (where it returns to)
		baitPosition = baitReturnPoint.cpy();
		
		// Load eater plant's animations
		plantAttack = new AnimationContainer("plant1Attack", "plantEater");
		plantSwallow = new AnimationContainer("plant1swallow", "plantEater");
		
		// Bait and tongue sprites
		baitSprite = new Sprite( Game.atlas.findRegion("bait") );
		tongueSprite = new Sprite( Game.atlas.findRegion("black") );
		baitSprite.setSize(baitSizeX, baitSizeY);
		
		// First animation is "closed"
		currentAnim = plantAttack;
		plantAttack.goToFrame(0);
		plantAttack.freeze = true;
		
		BodyDef polygonDef = new BodyDef();
		polygonDef.type = BodyType.StaticBody;
		
		plantBody = Game.b2World.createBody(polygonDef);
		
		FixtureDef pShooterFixture = new FixtureDef();
		
		// Portal's shape size (collisions)
		PolygonShape portalShape = new PolygonShape();
		portalShape.setAsBox(1.4f, 1.2f);
		
		// Portal's settings (physics)
		pShooterFixture.shape = portalShape;
		pShooterFixture.density = 1.0f;
		pShooterFixture.friction = 0.0f;
		pShooterFixture.restitution = 0.0f;
		pShooterFixture.isSensor = true;
		plantBody.createFixture(pShooterFixture);
		
//		portalShape.dispose();
		
		// Identify it for later use in collision detection
		plantBody.setUserData(IDs.PE_ID_RANGE + plantID);
		plantBody.setTransform(plantPosition.x , plantPosition.y - 1.2f, 0f);

		// Baits's light
		baitLight = new PointLight(rayHandler, 300, new Color(.6f,.2f,.1f,0.2f), 5, 0, 0);
	}

	public int jumpCounter = 0;
	
	public void update()
	{
		// If JumpCounter is 0, Plu can be grabbed. When it jumps, the counter gets increased. Making it, ungrabbable for a while
		if (jumpCounter == 0)
		{
			// Check if it was grabbed
			isGrabbingPlu = baitPosition.dst(Plu.singleton.pluBody.getPosition()) < Plu.singleton.bigBodyRadiusGround + baitRadius;
			
			// Just got grabbed?
			if (!wasGrabbingPlu && isGrabbingPlu)
			{
				// Set this plant as the attacking one
				attackingPlant = this.plantID;
				
				// Tell Plu he got grabbed and move him along with the bait
				Plu.singleton.isGrabbed = true;
				Plu.singleton.pluBody.setTransform(baitPosition.x , baitPosition.y-.2f, 0);
				
				// Start attack animation
				currentAnim = plantAttack;
				currentAnim.goToFrame(0);
				currentAnim.freeze = false;
				currentAnim.reverse = false;
				currentAnim.looping = true;
			}
		}
		else jumpCounter--;
		
		// Just got released?
		if (wasGrabbingPlu && !isGrabbingPlu)
		{
			// Play swallow animation
			currentAnim = plantSwallow;
			currentAnim.goToFrame(0);
			currentAnim.freeze = false;
			currentAnim.reverse = false;
			currentAnim.looping = false;
		}
		
		if (isEating)
		{
			Plu.singleton.pluBody.setTransform(baitPosition.x , baitPosition.y-.2f, 0);
		}
		else
		{
			// Pull it up, if it got grabbed
			if (isGrabbingPlu)
			{
				baitPosition.y += 30f * Gdx.graphics.getDeltaTime();
				Plu.singleton.pluBody.setTransform(baitPosition.x , baitPosition.y-.2f, 0);
			}
			
			// If it's not grabbed, return bait to it's original position
			else
			{
				if (baitPosition.y > baitReturnPoint.y)
				{
					baitPosition.y -= 10f * Gdx.graphics.getDeltaTime();
				}
				else
				{
					baitPosition.y = baitReturnPoint.y;
				}
			}
		}
		
		// Test is Plu got eaten
		if (baitPosition.y > plantPosition.y  && !isEating)
		{
			Plu.singleton.isDying = true;
			
			isEating = true;
			
			// Play swallow animation
			currentAnim = plantSwallow;
			currentAnim.goToFrame(0);
			currentAnim.freeze = false;
			currentAnim.reverse = false;
			currentAnim.looping = false;
		}
		
		// Roar effect
		if (plantAttack.animationInstant > 6f/7f * plantAttack.animation.animationDuration)
		{
			plantAttack.goToFrame(5);
		}
		
		// Finished swallowing Plu?
		if (isEating && plantSwallow.animationInstant == plantSwallow.animation.animationDuration)
		{
//			LevelManager.singleton.restartLevel();
		}
		
		// Record last state
		wasGrabbingPlu = isGrabbingPlu;
		
		// Bait's light position
		baitLight.setPosition(baitPosition);
		
		// Update it's animation
		currentAnim.play();
	}
	
	public void renderBody()
	{
		currentAnim.render(new Vector2(plantPosition.x, plantPosition.y-1.35f));
	}
	
	public void renderTongue()
	{
		baitSprite.setPosition(baitPosition.x - baitSizeX*.5f, baitPosition.y - baitSizeY*.5f);
		
		tongueSprite.setSize(4f/24f, plantPosition.y - baitPosition.y + baitSizeY*.5f);
		tongueSprite.setPosition(baitPosition.x - 4f/48f, baitPosition.y);
		
		LevelManager.renderer.getSpriteBatch().begin();
		baitSprite.draw(LevelManager.renderer.getSpriteBatch());
		tongueSprite.draw(LevelManager.renderer.getSpriteBatch());
		LevelManager.renderer.getSpriteBatch().end();
	}

	public void dispose() 
	{
		Game.b2World.destroyBody(plantBody);
	}
	
}
