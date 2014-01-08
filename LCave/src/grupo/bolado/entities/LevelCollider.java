package grupo.bolado.entities;

import static grupo.bolado.Game.b2World;
import static grupo.bolado.LevelManager.*;

import java.util.ArrayList;

import grupo.bolado.CameraHandler;
import grupo.bolado.Game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class LevelCollider 
{
	ArrayList<Body> fullLevel;

	public LevelCollider()
	{
		fullLevel = new ArrayList<Body>();
		
		for (MapObject collisionSurface : map.getLayers().get("Collider").getObjects())
		{
			float surfaceWidth = ((RectangleMapObject)collisionSurface).getRectangle().width / 2;
			float surfaceHeight = ((RectangleMapObject)collisionSurface).getRectangle().height / 2;
			float surfaceX = ((RectangleMapObject)collisionSurface).getRectangle().x + surfaceWidth;
			float surfaceY = ((RectangleMapObject)collisionSurface).getRectangle().y + surfaceHeight;
			
			BodyDef groundBodyDef = new BodyDef();
			groundBodyDef.position.set( surfaceX/CameraHandler.pixelsPerUnit, 
										surfaceY/CameraHandler.pixelsPerUnit);
			
			Body groundBody = b2World.createBody(groundBodyDef);
			groundBody.setUserData(IDs.LEVEL_ID);
			PolygonShape groundBox = new PolygonShape();
			groundBox.setAsBox( surfaceWidth/CameraHandler.pixelsPerUnit, 
								surfaceHeight/CameraHandler.pixelsPerUnit);
			
			FixtureDef boxFixture = new FixtureDef();
			boxFixture.shape = groundBox;
			boxFixture.density = 1.0f;
			boxFixture.friction = 0.0f;
			boxFixture.restitution = 0.0f;
			boxFixture.filter.groupIndex = 1;
			
//			groundBox.dispose();
			
			groundBody.createFixture(boxFixture);
			
			fullLevel.add(groundBody);
		}
	}
	
	public void dispose()
	{
		for (Body b : fullLevel)
		{
			Game.b2World.destroyBody(b);
		}
	}
}
