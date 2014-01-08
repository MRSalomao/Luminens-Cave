package grupo.bolado.packer;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class PackTextures {
	public static void main (String[] args) throws Exception 
	{
		TexturePacker2.process("assets/sprites", "assets/spriteAtlas", "atlas");
	}
}