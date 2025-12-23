package net.minecraft.src;

public class FCBlockCement extends BlockFlowing {
	protected FCBlockCement(int iBlockID) {
		super(iBlockID, mod_FCBetterThanWolves.fcCementMaterial);
		this.setTickOnLoad(true);
	}
}
