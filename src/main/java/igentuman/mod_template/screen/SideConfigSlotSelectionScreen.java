package igentuman.mod_template.screen;

import igentuman.mod_template.container.UniversalProcessorContainer;
import igentuman.mod_template.registration.ModEntry;
import igentuman.mod_template.setup.ModEntries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static igentuman.mod_template.Main.rl;

public class SideConfigSlotSelectionScreen extends Screen {

    private static final ResourceLocation TEXTURE      = rl("textures/gui/small_window.png");
    private static final ResourceLocation SLOTS_TEX    = rl("textures/gui/slots.png");

    private static final int WIN_W = 120;
    private static final int WIN_H = 120;
    private static final int BTN   = 18;
    private static final int PAD   = 4;

    private final AbstractContainerScreen<UniversalProcessorContainer> parentScreen;

    private int winX;
    private int winY;

    private record SlotEntry(int handlerSlotId, boolean isFluid, int col, int row) {}

    private final List<SlotEntry> slotEntries = new ArrayList<>();

    public SideConfigSlotSelectionScreen(AbstractContainerScreen<UniversalProcessorContainer> parent) {
        super(Component.translatable("screen.modtemplate.slot_selection"));
        this.parentScreen = parent;
    }

    @Override
    protected void init() {
        winX = (this.width - WIN_W) / 2;
        winY = (this.height - WIN_H) / 2;

        slotEntries.clear();
        UniversalProcessorContainer menu = parentScreen.getMenu();
        ModEntry entry = ModEntries.get(menu.getBlockEntity().name);

        int inputItemCount  = entry.itemCap()  != null ? entry.itemCap().inputSlots          : 0;
        int outputItemCount = entry.itemCap()  != null ? entry.itemCap().outputSlots          : 0;
        int inputFluidCount = entry.fluidCap() != null ? entry.fluidCap().inputTanks.size()   : 0;
        int outputFluidCount= entry.fluidCap() != null ? entry.fluidCap().outputTanks.size()  : 0;
        int totalItemSlots  = inputItemCount + outputItemCount;

        int col = 0, row = 0;
        int maxCols = Math.max(1, (WIN_W - PAD * 2) / (BTN + PAD));

        for (int i = 0; i < inputItemCount; i++) {
            slotEntries.add(new SlotEntry(i, false, col, row));
            col++;
            if (col >= maxCols) { col = 0; row++; }
        }
        for (int i = 0; i < inputFluidCount; i++) {
            slotEntries.add(new SlotEntry(totalItemSlots + i, true, col, row));
            col++;
            if (col >= maxCols) { col = 0; row++; }
        }
        for (int i = 0; i < outputItemCount; i++) {
            slotEntries.add(new SlotEntry(inputItemCount + i, false, col, row));
            col++;
            if (col >= maxCols) { col = 0; row++; }
        }
        for (int i = 0; i < outputFluidCount; i++) {
            slotEntries.add(new SlotEntry(totalItemSlots + inputFluidCount + i, true, col, row));
            col++;
            if (col >= maxCols) { col = 0; row++; }
        }

        int contentStartY = winY + 16;
        for (SlotEntry se : slotEntries) {
            int bx = winX + PAD + se.col() * (BTN + PAD);
            int by = contentStartY + PAD + se.row() * (BTN + PAD);
            final int slotId = se.handlerSlotId();
            addRenderableWidget(Button.builder(Component.empty(), btn ->
                            Minecraft.getInstance().setScreen(
                                    new SideConfigScreen(parentScreen, slotId)))
                    .pos(bx, by)
                    .size(BTN, BTN)
                    .build());
        }

        addRenderableWidget(Button.builder(Component.literal("X"), btn ->
                        Minecraft.getInstance().setScreen(parentScreen))
                .pos(winX + WIN_W - 14, winY + 2)
                .size(12, 12)
                .build());
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(TEXTURE, winX, winY, 0, 0, WIN_W, WIN_H, WIN_W, WIN_H);
        guiGraphics.drawCenteredString(font, this.title, winX + WIN_W / 2, winY + 6, 0x404040);

        int contentStartY = winY + 16;
        for (SlotEntry se : slotEntries) {
            int bx = winX + PAD + se.col() * (BTN + PAD);
            int by = contentStartY + PAD + se.row() * (BTN + PAD);
            int texOffset = se.isFluid() ? 18 : 0;
            guiGraphics.blit(SLOTS_TEX, bx - 1, by - 1, texOffset, 0, 18, 18);
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parentScreen);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
