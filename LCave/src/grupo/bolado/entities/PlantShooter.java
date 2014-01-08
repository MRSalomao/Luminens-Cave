package grupo.bolado.entities;

import static grupo.bolado.Game.rayHandler;
import grupo.bolado.Game;
import grupo.bolado.utils.AnimationContainer;

import java.util.ArrayList;

import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class PlantShooter 
{
	Body plantShooterBody;
	
	AnimationContainer plantAttack, currentAnim;
	
	PointLight plantLight;
	
	Vector2 plantPosition;
	
	public boolean bulletHit = false;
	
	public int plantID;
	
	public ArrayList<Bullet> bullets;
	
	int numberOfBullets = 2;
	float shootCounter;
	int bulletToShoot;
	
	int pointsRight;
	boolean rotated;
	
	public PlantShooter(MapObject startPoint, int plantID, int pointsRight, boolean rotated)
	{
		this.plantID = plantID;
//		Gdx.app.log("",  "" + this.plantID);
		
		this.pointsRight = pointsRight;
		this.rotated = rotated;
		
		plantPosition = (new Vector2( ((EllipseMapObject) startPoint).getEllipse().x/32,  
        							  ((EllipseMapObject) startPoint).getEllipse().y/32) );
		
		new PointLight(rayHandler, 600, new Color(1,0,0,0.3f), 30, plantPosition.x, plantPosition.y);
		
		BodyDef polygonDef = new BodyDef();
		polygonDef.type = BodyType.StaticBody;
		
		plantShooterBody = Game.b2World.createBody(polygonDef);
		
		FixtureDef pShooterFixture = new FixtureDef();
		
		// Portal's shape size (collisions)
		PolygonShape portalShape = new PolygonShape();
		if(rotated == false)
		{	
			portalShape.setAsBox(1.6f, 1);
		}
		else
		{
			portalShape.setAsBox(1, 1.6f);
		}
		
		// Portal's settings (physics)
		pShooterFixture.shape = portalShape;
		pShooterFixture.density = 1.0f;
		pShooterFixture.friction = 0.0f;
		pShooterFixture.restitution = 0.0f;
		pShooterFixture.isSensor = true;
		plantShooterBody.createFixture(pShooterFixture);
		
//		portalShape.dispose();
		
		// Identify it for later use in collision detection
		plantShooterBody.setUserData(IDs.PS_ID_RANGE + plantID);
		if(rotated == false)
		{	
			plantShooterBody.setTransform(plantPosition.x + 1.5f * pointsRight, plantPosition.y, 0f);
		}
		else
		{
			plantShooterBody.setTransform(plantPosition.x, plantPosition.y + 1.5f * pointsRight, 0f);
		}
		
		bullets = new ArrayList<Bullet>();
		for (int i=0; i<numberOfBullets; i++)
		{
			bullets.add( new Bullet(this, bullets.size()) );
		}
		
		// Load Shooter's animations
		plantAttack = new AnimationContainer("plant2Attack", "plantShooter");
		
		// The plant is pissed off. Initial animation: attacking
		currentAnim = plantAttack;
		
		plantAttack.flipVertically = true;
	}
	
	
	public void update()
	{
		shootCounter += Gdx.graphics.getDeltaTime();
		
		if (shootCounter > 1f)
		{
			shootCounter = 0f;
			
			bullets.get(bulletToShoot).shoot(pointsRight, rotated);
			
			bulletToShoot++;
			if (bulletToShoot == numberOfBullets)
			{
				bulletToShoot = 0;
			}
		}
		
		currentAnim.play();
	}
	
	float xAdjustment = 2f;
	public void renderBody()
	{
		
		if (pointsRight == -1) currentAnim.flipVertically = false;
		
		if (rotated) 
		{
			currentAnim.rotate90(true);
			currentAnim.render(new Vector2(plantPosition.x, plantPosition.y + xAdjustment * pointsRight));
		}
		else
		{
			currentAnim.render(new Vector2(plantPosition.x + xAdjustment * pointsRight, plantPosition.y + 0.15f));
		}
		
		for (Bullet b : bullets)
		{
			b.renderBullet();
			
			if (b.crashed) b.resetPosition();
		}
	}
	
	public void dispose()
	{
		Game.b2World.destroyBody(plantShooterBody);
		
		for (Bullet b : bullets)
		{
			b.dispose();
		}
	}

}
