package grupo.bolado;

import grupo.bolado.entities.IDs;
import grupo.bolado.entities.Lumen;
import grupo.bolado.entities.Plu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.WorldManifold;

public class WorldContactListener implements ContactListener{

	@Override
	public void beginContact(Contact contact) 
	{
		int bodyA = (Integer)contact.getFixtureA().getBody().getUserData();
		int bodyB = (Integer)contact.getFixtureB().getBody().getUserData();

		if (bodyB == IDs.PLU_ID && bodyA / IDs.PS_ID_RANGE == 1)
        {
        	Plu.singleton.isDying = true;
        }
        
        if (bodyB == IDs.PLU_ID && bodyA / IDs.PE_ID_RANGE == 1)
        {
        	Plu.singleton.isDying = true;
        }
        
        if (bodyB == IDs.PLU_ID && bodyA / IDs.PORTAL_ID == 1)
        {
        	if (Plu.singleton.lumensCollected == LevelManager.totalLumens)
        	{
        		Plu.singleton.levelComplete = true;
        	}
        }
	}

	
	@Override
	public void endContact(Contact contact) 
	{

	}
	

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) 
	{
		int bodyA = (Integer)contact.getFixtureA().getBody().getUserData();
		int bodyB = (Integer)contact.getFixtureB().getBody().getUserData();

        if ((bodyA == IDs.PLU_ID && bodyB / IDs.LUMEN_ID_RANGE == 1) || (bodyB == IDs.PLU_ID && bodyA / IDs.LUMEN_ID_RANGE == 1))
        {
        	contact.setEnabled(false);
        }
        
		
	}

	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
	{
		int bodyA = (Integer)contact.getFixtureA().getBody().getUserData();
		int bodyB = (Integer)contact.getFixtureB().getBody().getUserData();
	
        WorldManifold worldManifold = contact.getWorldManifold();
        
        if (bodyA == IDs.LEVEL_ID && bodyB == IDs.PLU_ID)
        {
        	if (worldManifold.getNormal().y < -0.8f)
        	{
//        		Gdx.app.log("", "on the ceiling");
        		Plu.singleton.hitTheCeiling();
        	}
        	else if (worldManifold.getNormal().y > 0.9f)
        	{
//        		Gdx.app.log("", "on the ground");
        		Plu.singleton.onTheGround = true;
        		Plu.singleton.jumping = false;
        	}
        }
        
        if (bodyA == IDs.LEVEL_ID && bodyB / IDs.PS_ID_RANGE == 1)
        {
        	LevelManager.plantsShooter.get(bodyB % IDs.PS_ID_RANGE);
        }
        
        if (bodyA == IDs.LEVEL_ID && bodyB / IDs.BULLET_ID_RANGE == 1)
        {
        	LevelManager.plantsShooter.get( (bodyB % IDs.BULLET_ID_RANGE) / 50 ).bullets.get( (bodyB % IDs.BULLET_ID_RANGE) % 50 ).crashed = true;
        }
        
        if (bodyA == IDs.PLU_ID && bodyB / IDs.BULLET_ID_RANGE == 1)
        {
        	Plu.singleton.isDying = true;
        }

    }
		
}


