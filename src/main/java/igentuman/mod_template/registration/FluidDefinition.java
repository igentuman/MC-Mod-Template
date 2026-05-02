package igentuman.mod_template.registration;

public class FluidDefinition {
    public int temperature;
    public int luminosity;
    public boolean isGas;
    public boolean isMolten;
    public boolean isToxic;

    public FluidDefinition() {

    }

    public static FluidDefinition metal() {
        FluidDefinition metal = new FluidDefinition();
        metal.isMolten = true;
        metal.temperature = 600;
        metal.luminosity = 100;
        return metal;
    }

    public FluidDefinition setTemperature(int temperature) {
        this.temperature = temperature;
        return this;
    }

    public FluidDefinition setLuminosity(int luminosity) {
        this.luminosity = luminosity;
        return this;
    }

    public FluidDefinition setIsGas(boolean isGas) {
        this.isGas = isGas;
        return this;
    }

    public FluidDefinition setIsMolten(boolean isMolten) {
        this.isMolten = isMolten;
        return this;
    }

    public FluidDefinition setIsToxic(boolean isToxic) {
        this.isToxic = isToxic;
        return this;
    }
}
