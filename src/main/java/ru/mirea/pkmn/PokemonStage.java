package ru.mirea.pkmn;

public enum PokemonStage {
    BASIC("BASIC"),
    STAGE1("STAGE1"),
    STAGE2("STAGE2"),
    VSTAR("VSTAR"),
    VMAX("VMAX");

    private String pokemonStage;

    PokemonStage(String pokemonStage)
    {
        this.pokemonStage = pokemonStage;
    }

    public String getPokemonStage()
    {
        return pokemonStage;
    }
}
