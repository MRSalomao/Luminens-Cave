package grupo.bolado.entities;

import static grupo.bolado.Game.rayHandler;
import grupo.bolado.Game;
import grupo.bolado.LevelManager;
import box2dLight.Light;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Lumen 
{
	Vector2 lumenPosition;
	
	Sprite lumenSprite;
	
	public float bodyRadiusGround = .4f;
	
	Body lumenBody;
	
	PointLight lumenLight;
	
	int lumenID;
	
	public boolean followingPlu = false, wasFollowingPlu = false;
	
	FixtureDef lumenFixtureAfter;
	
	public static float targetBaseY = 1.1f;
	
	public Lumen(MapObject startPoint, int lumenID) 
	{
		this.lumenID = lumenID;
		
		lumenPosition = (new Vector2( ((EllipseMapObject) startPoint).getEllipse().x/32,  
        							  ((EllipseMapObject) startPoint).getEllipse().y/32) );
		
		BodyDef circleDef = new BodyDef();
		circleDef.type = BodyType.DynamicBody;
		
		lumenBody = Game.b2World.createBody(circleDef);
		
		FixtureDef lumenFixture = new FixtureDef();
		
		// Lumen's shape size (collisions)
		CircleShape lumenShape = new CircleShape();
		lumenShape.setRadius(bodyRadiusGround);
		
		// Lumens's settings (physics)
		lumenFixture.shape = lumenShape;
		lumenFixture.density = 1.0f;
		lumenFixture.friction = 0.0f;
		lumenFixture.restitution = 0.0f;
		lumenBody.createFixture(lumenFixture);
		
//		lumenShape.dispose();
		
		// Identify it for later use in collision detection
		lumenBody.setUserData(IDs.LUMEN_ID_RANGE + lumenID);
		lumenBody.setGravityScale(0);
		lumenBody.setTransform(lumenPosition, 0f);
		
		// Sprite to the body
		lumenPosition = lumenBody.getPosition();
		
		lumenSprite = new Sprite( Game.atlas.findRegion("lumen"));
		lumenSprite.setSize((lumenSprite.getRegionWidth()/32f)/2f, (lumenSprite.getRegionHeight()/32f)/2f);
		
		// Lumen's light
		lumenLight = new PointLight(rayHandler, 600, new Color(.1f, .1f, .1f, 0.9f), 20f, lumenPosition.x, lumenPosition.y);
		lumenLight.attachToBody(lumenBody, 0, 0);
		Light.setContactFilter((short)-1,(short)1,(short)0);
	}
	
	public void attachToPlu()
	{
		followingPlu = true;
	}
	
	float timer, timer2, randomTargetX = MathUtils.random(), randomTargetY = MathUtils.random(), permRandonTargetX = MathUtils.random()*3.2f;
	boolean wasAttached = false;
	
	public void update()
	{
		if (Plu.singleton.pluBody.getPosition().cpy().dst(lumenBody.getPosition()) < 1.5f && !wasAttached)
		{
			attachToPlu();
			Plu.singleton.lumensCollected++;
			
			Gdx.app.log("Lumen", "Got a new lumen! Count now is: " + Plu.singleton.lumensCollected );
			
			wasAttached = true;
		}
		
		lumenPosition = lumenBody.getPosition();
		if (followingPlu)
		{
			timer += Gdx.graphics.getDeltaTime();
			timer2 += Gdx.graphics.getDeltaTime();
			if (timer > .3f)
			{
				randomTargetX = MathUtils.random()*1.5f;
				randomTargetY = MathUtils.random()*1.5f;
			
				timer = 0f;
			}
			if (timer2 > 1f)
			{
				permRandonTargetX = MathUtils.random()*3.2f;
				
				timer2 = 0;
			}
			
			lumenBody.setLinearVelocity(Plu.singleton.pluBody.getPosition().cpy().add(permRandonTargetX+randomTargetX-2f,targetBaseY+randomTargetY).sub(lumenBody.getPosition()).scl(5f));
//			lumenBody.setLinearDamping(.1f);
			//lumenBody.setTransform(Plu.singleton.pluBody.getPosition().x + 0.95f, Plu.singleton.pluBody.getPosition().y + 0.95f, 0f);
		}	
	}
	
	public void renderLumen()
	{
		lumenSprite.setPosition(lumenPosition.x - 0.3f, lumenPosition.y - 0.3f);
		
		LevelManager.renderer.getSpriteBatch().begin();
		lumenSprite.draw(LevelManager.renderer.getSpriteBatch());
		LevelManager.renderer.getSpriteBatch().end();
	}

	public void dispose() 
	{
		Game.b2World.destroyBody(lumenBody);
	}

}
