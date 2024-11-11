package ru.mirea.pkmn;

import java.io.Serializable;
import java.util.List;

public class Card implements Serializable {
    private PokemonStage pokemonStage;
    private String name;
    private int hp;
    private EnergyType pokemonType;
    private Card evolvesFrom;
    private List<AttackSkill> skills;
    private EnergyType weaknessType;
    private EnergyType resistanceType;
    private String retreatCost;
    private String gameSet;
    private char regulationMark;
    private Student pokemonOwner;
    public static final long serialVersionUID = 1L;

    public Card()
    {

    }

    public Card (String name)
    {
        this.name = name;
    }

    public Card(PokemonStage pokemonStage, String name, int hp, EnergyType pokemonType,
                Card evolvesFrom, List<AttackSkill> skills, EnergyType weaknessType, EnergyType resistanceType,
                String retreatCost, String gameSet, char regulationMark, Student pokemonOwner)
    {
        this.pokemonStage = pokemonStage;
        this.name = name;
        this.hp = hp;
        this.pokemonType = pokemonType;
        this.evolvesFrom = evolvesFrom;
        this.skills = skills;
        this.weaknessType = weaknessType;
        this.resistanceType = resistanceType;
        this.retreatCost = retreatCost;
        this.gameSet = gameSet;
        this.regulationMark = regulationMark;
        this.pokemonOwner = pokemonOwner;
    }

    public PokemonStage getPokemonStage()
    {
        return pokemonStage;
    }
    public void setPokemonStage(PokemonStage pokemonStage)
    {
        this.pokemonStage = pokemonStage;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getHp()
    {
        return hp;
    }
    public void setHp(int hp)
    {
        this.hp = hp;
    }

    public EnergyType getPokemonType()
    {
        return pokemonType;
    }
    public void setPokemonType(EnergyType pokemonType)
    {
        this.pokemonType = pokemonType;
    }

    public Card getEvolvesFrom()
    {
        return evolvesFrom;
    }
    public void setEvolvesFrom(Card evolvesFrom)
    {
        this.evolvesFrom = evolvesFrom;
    }

    public List<AttackSkill> getSkills()
    {
        return skills;
    }
    public void setSkills(List<AttackSkill> skills)
    {
        this.skills = skills;
    }

    public EnergyType getWeaknessType()
    {
        return weaknessType;
    }
    public void setWeaknessType(EnergyType weaknessType)
    {
        this.weaknessType = weaknessType;
    }

    public EnergyType getResistanceType()
    {
        return resistanceType;
    }
    public void setResistanceType(EnergyType resistanceType)
    {
        this.resistanceType = resistanceType;
    }

    public String getRetreatCost()
    {
        return retreatCost;
    }
    public void setRetreatCost(String retreatCost)
    {
        this.retreatCost = retreatCost;
    }

    public String getGameSet()
    {
        return gameSet;
    }
    public void setGameSet(String gameSet)
    {
        this.gameSet = gameSet;
    }

    public char getRegulationMark()
    {
        return regulationMark;
    }
    public void setRegulationMark(char regulationMark)
    {
        this.regulationMark = regulationMark;
    }

    public Student getPokemonOwner()
    {
        return pokemonOwner;
    }
    public void setPokemonOwner(Student pokemonOwner)
    {
        this.pokemonOwner = pokemonOwner;
    }

    @Override
    public String toString() {
            return String.format("\u001b[38;5;111m%s pokemon:\u001b[38;5;15m \n" +
                    "1. ", this.pokemonStage) + this.pokemonStage + "\n2. " + name + "\n3. " + hp + "\n4. " + pokemonType + "\n5. " + evolvesFrom + "\n6. " + skills + "\n7. " + weaknessType + "\n8. " + resistanceType + "\n9. " + retreatCost + "\n10. " + gameSet + "\n11. " + regulationMark + "\n12. " + pokemonOwner + "\n";
    }
}
