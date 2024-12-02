package net.yeoxuhang.cutting_board.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.yeoxuhang.cutting_board.CuttingBoard;
import net.yeoxuhang.cutting_board.block_entity.CuttingBoardBlockEntity;
import org.antlr.v4.runtime.misc.NotNull;
import org.jetbrains.annotations.Nullable;

public class CuttingBoardBlock extends BaseEntityBlock {
    public CuttingBoardBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public boolean mayPlaceOn(BlockState groundState, BlockGetter worldIn, BlockPos pos) {
        return !groundState.is(CuttingBoard.CUTTING_BOARD.get()) || !groundState.isAir() || groundState.isSolid();
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState groundState = levelReader.getBlockState(blockpos);
        return this.mayPlaceOn(groundState, levelReader, blockpos);
    }

    public InteractionResult use(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        if (worldIn.getBlockEntity(pos) instanceof CuttingBoardBlockEntity cuttingBoardBlock) {
            if ((!player.isShiftKeyDown() || heldItem.getItem() != this.asItem()) && heldItem.is(CuttingBoard.FOOD)) {
                ItemStack stack = heldItem.copy();
                stack.setCount(1);
                if (cuttingBoardBlock.getItem(0).isEmpty()){
                    cuttingBoardBlock.setItem(0, stack);
                    if (!player.isCreative()){
                        heldItem.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                } else{
                    popResource(worldIn, pos, cuttingBoardBlock.getItem(0).copy());
                    cuttingBoardBlock.setItem(0, ItemStack.EMPTY);
                    return InteractionResult.SUCCESS;
                }
            } else if (heldItem.is(CuttingBoard.AMETHYST_KNIFE.get()) && !cuttingBoardBlock.isEmpty()) {
                if (cuttingBoardBlock.getSliceLevel() <= 5){
                    cuttingBoardBlock.setSliceLevel(cuttingBoardBlock.getSliceLevel() + 1);
                    dropCakeSlice(worldIn, pos);
                    heldItem.hurtAndBreak(1, player, (event) -> event.broadcastBreakEvent(handIn));
                } else if (cuttingBoardBlock.getSliceLevel() <= 6) {
                    cuttingBoardBlock.setItem(0, ItemStack.EMPTY);
                    cuttingBoardBlock.setSliceLevel(0);
                }
                return InteractionResult.SUCCESS;
            }

        }
        return InteractionResult.PASS;
    }

    public static void dropCakeSlice(Level level, BlockPos pos) {
        popResource(level, pos, new ItemStack(CuttingBoard.CAKE_SLICE.get(), 1));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardBlockEntity(pos, state);
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof CuttingBoardBlockEntity) {
            Containers.dropContents(worldIn, pos, (CuttingBoardBlockEntity) tileentity);
            worldIn.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
