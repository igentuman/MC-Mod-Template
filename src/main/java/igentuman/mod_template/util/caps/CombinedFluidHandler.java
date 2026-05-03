package igentuman.mod_template.util.caps;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Combines multiple IFluidHandler instances into a single view.
 * Tanks are indexed sequentially across all delegates.
 */
public class CombinedFluidHandler implements IFluidHandler {

    private final IFluidHandler[] delegates;
    private final int totalTanks;

    public CombinedFluidHandler(IFluidHandler... delegates) {
        this.delegates = delegates;
        int total = 0;
        for (IFluidHandler handler : delegates) {
            total += handler.getTanks();
        }
        this.totalTanks = total;
    }

    private HandlerTank resolve(int tank) {
        int offset = 0;
        for (IFluidHandler handler : delegates) {
            int size = handler.getTanks();
            if (tank < offset + size) {
                return new HandlerTank(handler, tank - offset);
            }
            offset += size;
        }
        return null;
    }

    @Override
    public int getTanks() {
        return totalTanks;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        HandlerTank ht = resolve(tank);
        return ht != null ? ht.handler.getFluidInTank(ht.tank) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        HandlerTank ht = resolve(tank);
        return ht != null ? ht.handler.getTankCapacity(ht.tank) : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        HandlerTank ht = resolve(tank);
        return ht != null && ht.handler.isFluidValid(ht.tank, stack);
    }

    @Override
    public int fill(@NotNull FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        for (IFluidHandler handler : delegates) {
            int filled = handler.fill(resource, action);
            if (filled > 0) return filled;
        }
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        for (IFluidHandler handler : delegates) {
            FluidStack drained = handler.drain(resource, action);
            if (!drained.isEmpty()) return drained;
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0) return FluidStack.EMPTY;
        for (IFluidHandler handler : delegates) {
            FluidStack drained = handler.drain(maxDrain, action);
            if (!drained.isEmpty()) return drained;
        }
        return FluidStack.EMPTY;
    }

    private record HandlerTank(IFluidHandler handler, int tank) {}
}
