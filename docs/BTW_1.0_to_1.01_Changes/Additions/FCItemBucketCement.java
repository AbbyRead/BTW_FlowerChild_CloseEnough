package net.minecraft.src;

public class FCItemBucketCement extends ItemBucket {
	public static final Item fcBucketCement = (new FCItemBucketCement(222, mod_FCBetterThanWolves.fcCementMoving.blockID)).setIconCoord(13, 4).setItemName("bucketCement").setContainerItem(bucketEmpty);

	public FCItemBucketCement(int iBlockID, int iFullOfBlockID) {
		super(iBlockID, iFullOfBlockID);
	}
}
