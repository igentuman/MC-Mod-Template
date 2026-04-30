package igentuman.mod_template.block;

import com.mojang.serialization.MapCodec;
import igentuman.mod_template.block_entity.UniversalProcessorBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import static igentuman.mod_template.setup.ModEntries.COMMON_BLOCK_PROPS;

public class UniversalProcessorBlock extends BaseEntityBlock {

    public static MapCodec<UniversalProcessorBlock> CODEC;
    private final String name;

    public UniversalProcessorBlock(String name) {
        super(COMMON_BLOCK_PROPS);
        this.name = name;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        if(CODEC == null) {
            CODEC = simpleCodec((BlockBehaviour.Properties props) -> new UniversalProcessorBlock(name));
        }
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UniversalProcessorBE(pos, state, name);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof UniversalProcessorBE) {
                serverPlayer.openMenu((UniversalProcessorBE) be, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof UniversalProcessorBE exampleBE) {
                exampleBE.drops();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
