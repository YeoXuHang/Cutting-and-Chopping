package net.yeoxuhang.cutting_board.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.yeoxuhang.cutting_board.CuttingBoard;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CuttingBoardBlockEntity extends BlockEntity implements WorldlyContainer {
    // Slice level for the cake
    int sliceLevel;
    //Field for getting item in the block entity inventory
    ItemStack item;

    public CuttingBoardBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CuttingBoard.CUTTING_BOARD_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        this.item = ItemStack.EMPTY;
        this.sliceLevel = 0;
    }
    @Override
    public int[] getSlotsForFace(Direction direction) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack stack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.item.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return i == 0 ? this.item : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        ItemStack item = this.removeItemNoUpdate(i);
        this.update(j);
        return item;
    }

    public void update(int j) {
        this.setChanged();
        if (this.getLevel() != null) {
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), j);
        }

    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        if (i == 0) {
            ItemStack crystal = this.item;
            this.item = ItemStack.EMPTY;
            return crystal;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        if (i == 0) {
            this.item = itemStack;
            this.setChanged();
        }
    }

    public void setSliceLevel(int level){
        this.sliceLevel = level;
        this.setChanged();
    }

    public int getSliceLevel() {
        return sliceLevel;
    }

    public void clearContent() {
        this.item = ItemStack.EMPTY;
    }



    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        // Load item compound
        if (compoundTag.contains("item")) {
            this.item = ItemStack.of(compoundTag.getCompound("item"));
            this.sliceLevel = compoundTag.getInt("sliceLevel");
        }
    }

    protected void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        // Save item compound
        if (!this.item.isEmpty()) {
            compoundTag.put("item", this.item.save(new CompoundTag()));
            compoundTag.putInt("sliceLevel", this.sliceLevel);
        }
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    public boolean stillValid(Player player) {
        return this.worldPosition.distSqr(player.blockPosition()) <= 16.0;
    }

    // Set the max stack size for the slot
    public int getMaxStackSize() {
        return 1;
    }
}
