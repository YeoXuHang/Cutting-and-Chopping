package net.yeoxuhang.cutting_board.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.yeoxuhang.cutting_board.block_entity.CuttingBoardBlockEntity;

public class CuttingBoardBlockEntityRenderer<T extends CuttingBoardBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderer;

    public CuttingBoardBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(T entity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        Level world = entity.getLevel();
        assert world != null;

        if (entity.hasLevel() && !entity.isEmpty()) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(0));
            poseStack.translate(0.25, 0.1, 0.25);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            if (entity.getItem(0).is(Items.CAKE)){
                this.blockRenderer.renderSingleBlock(Blocks.CAKE.defaultBlockState().setValue(CakeBlock.BITES, entity.getSliceLevel()), poseStack, multiBufferSource, i, j);
            }
            //this.itemRenderer.renderStatic(entity.getItem(0), ItemDisplayContext.HEAD, i, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, entity.getLevel(), j);
            poseStack.popPose();
        }
    }
}
