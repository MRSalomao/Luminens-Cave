package grupo.bolado.entities;

import grupo.bolado.Game;
import grupo.bolado.LevelManager;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Portal {
	
	Vector2 portalPosition;
	
	Sprite portalSprite;
	
	Body portalBody;
	
	public Portal(MapObject startPoint) 
	{
		portalPosition = (new Vector2( ((EllipseMapObject) startPoint).getEllipse().x/32,  
        		((EllipseMapObject) startPoint).getEllipse().y/32) );
		
		portalSprite = new Sprite( Game.atlas.findRegion("portal"));
		portalSprite.setSize(portalSprite.getRegionWidth()/32f, portalSprite.getRegionHeight()/32f);
		
		BodyDef polygonDef = new BodyDef();
		polygonDef.type = BodyType.StaticBody;
		
		portalBody = Game.b2World.createBody(polygonDef);
		
		FixtureDef portalFixture = new FixtureDef();
		
		// Portal's shape size (collisions)
		PolygonShape portalShape = new PolygonShape();
		portalShape.setAsBox(portalSprite.getWidth()/3.7f, portalSprite.getHeight()/4f);
		
		// Portal's settings (physics)
		portalFixture.shape = portalShape;
		portalFixture.density = 1.0f;
		portalFixture.friction = 0.0f;
		portalFixture.restitution = 0.0f;
		portalBody.createFixture(portalFixture);
		
//		portalShape.dispose();
		
		// Identify it for later use in collision detection
		portalBody.setUserData(IDs.PORTAL_ID);
		portalBody.setTransform(portalPosition.x, portalPosition.y + portalSprite.getHeight()/4f, 0f);
	}
	
	public void renderPortal()
	{
		portalSprite.setPosition(portalPosition.x - portalSprite.getWidth()/2, portalPosition.y);
		
		LevelManager.renderer.getSpriteBatch().begin();
		portalSprite.draw(LevelManager.renderer.getSpriteBatch());
		LevelManager.renderer.getSpriteBatch().end();
	}

	public void dispose() 
	{
		Game.b2World.destroyBody(portalBody);
	}

}
