package grupo.bolado.entities;

import grupo.bolado.Game;
import grupo.bolado.LevelManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Bullet 
{
	Body bulletBody;
	
	public float bulletRadius = .45f;
	
	Sprite bulletSprite;
	
	PlantShooter owner;
	
	int bulletID;
	
	public boolean crashed = false;

	public Bullet(PlantShooter owner, int bulletID)
	{
		this.owner = owner;
		this.bulletID = bulletID;
		
		BodyDef circleDef = new BodyDef();
		circleDef.type = BodyType.DynamicBody;
		
		bulletBody = Game.b2World.createBody(circleDef);
		
		// Bullet's shape size (collisions)
		CircleShape bulletShape = new CircleShape();
		bulletShape.setRadius(bulletRadius);
		
		// Bullet's settings (physics)
		FixtureDef bulletFixture = new FixtureDef();
		bulletFixture.shape = bulletShape;
		bulletFixture.density = 1.0f;
		bulletFixture.friction = 0.0f;
		bulletFixture.restitution = 0.0f;
		bulletBody.createFixture(bulletFixture);
		
//		bulletShape.dispose();
		
		// Identify it for later use in collision detection
		bulletBody.setUserData(IDs.BULLET_ID_RANGE + 50 * owner.plantID + bulletID);
//		Gdx.app.log("", ""+ (IDs.BULLET_ID_RANGE + 50 * owner.plantID + bulletID));
		resetPosition();
		
		bulletBody.setGravityScale(0);
		
		// Bullet texture
		bulletSprite = new Sprite( Game.atlas.findRegion("bullet"));
		bulletSprite.setSize(0.75f, 0.75f);
		
	}
	
	public void shoot(int pointsRight, boolean rotated)
	{
		if (rotated)
		{
			bulletBody.setTransform(owner.plantPosition.x + 0.2f, owner.plantPosition.y + 2f*pointsRight, 0f);
			
			bulletBody.setLinearVelocity(0, 30 * pointsRight);
		}
		else
		{
			bulletBody.setTransform(owner.plantPosition.x + 2f*pointsRight, owner.plantPosition.y , 0f);
			
			bulletBody.setLinearVelocity(30 * pointsRight, 0);
		}
		
	}
	
	public void resetPosition()
	{
		bulletBody.setTransform(new Vector2(-10,-10), 0f);
		
		crashed = false;
	}
	
	public void renderBullet()
	{
		bulletSprite.setPosition(bulletBody.getPosition().x - 0.46f, bulletBody.getPosition().y - 0.46f);
		bulletSprite.rotate90(true);
		
		LevelManager.renderer.getSpriteBatch().begin();
		bulletSprite.draw(LevelManager.renderer.getSpriteBatch());
		LevelManager.renderer.getSpriteBatch().end();
	}
	
	public void dispose()
	{
		Game.b2World.destroyBody(bulletBody);
	}
}
