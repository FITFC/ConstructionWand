package thetadev.constructionwand.wand.action;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import thetadev.constructionwand.api.IWandAction;
import thetadev.constructionwand.api.IWandSupplier;
import thetadev.constructionwand.basics.ConfigServer;
import thetadev.constructionwand.basics.WandUtil;
import thetadev.constructionwand.basics.option.WandOptions;
import thetadev.constructionwand.wand.undo.ISnapshot;
import thetadev.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class ActionAngel implements IWandAction
{
    @Nonnull
    @Override
    public List<ISnapshot> getSnapshots(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                        WandOptions options, ConfigServer.WandProperties properties, int limit,
                                        IWandSupplier supplier) {
        LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();

        Direction placeDirection = rayTraceResult.getFace();
        BlockPos currentPos = rayTraceResult.getPos();
        BlockState supportingBlock = world.getBlockState(currentPos);

        for(int i = 0; i < properties.getAngel(); i++) {
            currentPos = currentPos.offset(placeDirection.getOpposite());

            PlaceSnapshot snapshot = supplier.getPlaceSnapshot(world, currentPos, rayTraceResult, supportingBlock);
            if(snapshot != null) {
                placeSnapshots.add(snapshot);
                break;
            }
        }
        return placeSnapshots;
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshotsFromAir(World world, PlayerEntity player, BlockRayTraceResult rayTraceResult,
                                               WandOptions options, ConfigServer.WandProperties properties, int limit,
                                               IWandSupplier supplier) {
        LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();

        if(!player.isCreative() && !ConfigServer.ANGEL_FALLING.get() && player.fallDistance > 10) return placeSnapshots;

        Vector3d playerVec = WandUtil.entityPositionVec(player);
        Vector3d lookVec = player.getLookVec().mul(2, 2, 2);
        Vector3d placeVec = playerVec.add(lookVec);

        BlockPos currentPos = new BlockPos(placeVec);

        PlaceSnapshot snapshot = supplier.getPlaceSnapshot(world, currentPos, rayTraceResult,null);
        if(snapshot != null) placeSnapshots.add(snapshot);

        return placeSnapshots;
    }
}
