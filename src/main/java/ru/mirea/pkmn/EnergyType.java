package ru.mirea.pkmn;

public enum EnergyType {
    FIRE("FIRE"),
    GRASS("GRASS"),
    WATER("WATER"),
    LIGHTNING("LIGHTNING"),
    PSYCHIC("PSYCHIC"),
    FIGHTING("FIGHTING"),
    DARKNESS("DARKNESS"),
    METAL("METAL"),
    FAIRY("FAIRY"),
    DRAGON("DRAGON"),
    COLORLESS("COLORLESS");

    private String energyType;

    EnergyType(String energyType)
    {
        this.energyType = energyType;
    }

    public String getEnergyType()
    {
        return energyType;
    }
}
