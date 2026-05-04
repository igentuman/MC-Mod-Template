package igentuman.mod_template.screen;

import igentuman.mod_template.Main;
import igentuman.mod_template.block_entity.UniversalProcessorBE;
import igentuman.mod_template.container.UniversalProcessorContainer;
import igentuman.mod_template.util.GuiFluidRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import static igentuman.mod_template.Main.rl;

public class UniversalProcessorScreen extends AbstractContainerScreen<UniversalProcessorContainer> {

    private static final ResourceLocation TEXTURE = rl("textures/gui/processor.png");

    public UniversalProcessorScreen(UniversalProcessorContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 180;
        imageHeight = 180;
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Render progress arrow (example: 24px wide arrow at position 80, 35 in GUI)
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            int arrowWidth = (int) (24.0f * progress / maxProgress);
            guiGraphics.blit(TEXTURE, x + 80, y + 35, 176, 0, arrowWidth, 17);
        }

        // Render fluid tanks (18x18 each)
        UniversalProcessorBE be = menu.getBlockEntity();
        if (be.hasFluidTanks()) {
            FluidTank[] tanks = be.fluidTanks;
            for (int i = 0; i < tanks.length; i++) {
                int tankX = x + 8 + i * 22;
                int tankY = y + 17;
                GuiFluidRenderer.renderFluidTank(guiGraphics, tankX, tankY, 18, 18,
                        tanks[i].getFluid(), tanks[i].getCapacity());
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // Render fluid tank tooltips
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        UniversalProcessorBE be = menu.getBlockEntity();
        if (be.hasFluidTanks()) {
            FluidTank[] tanks = be.fluidTanks;
            for (int i = 0; i < tanks.length; i++) {
                int tankX = x + 8 + i * 22;
                int tankY = y + 17;
                GuiFluidRenderer.renderFluidTooltip(guiGraphics, mouseX, mouseY,
                        tankX, tankY, 18, 18,
                        tanks[i].getFluid(), tanks[i].getCapacity());
            }
        }
    }
}
