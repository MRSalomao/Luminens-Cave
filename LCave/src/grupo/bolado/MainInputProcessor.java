package grupo.bolado;

import grupo.bolado.entities.Plu;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class MainInputProcessor implements InputProcessor {
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.SPACE) 
		{
			Plu.singleton.dash();
		}
		if (keycode == Keys.SHIFT_LEFT) 
		{
			Plu.singleton.changeForm();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		// F2 turns debug mode on
		if (keycode == Keys.F2) 
		{
			Game.debugOn = !Game.debugOn;
		}
		if (keycode == Keys.F10) 
		{
			if ( Gdx.graphics.isFullscreen() )
			{
				Gdx.graphics.setDisplayMode(1280, 720, false);
			}
			else
			{
				Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
			}
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
}