package ru.mirea.pkmn.MelnikovaVD;

import ru.mirea.pkmn.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class CardImport
{
    Card card = new Card();
    public Card readFromFile(String filename)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            card.setPokemonStage(PokemonStage.valueOf(reader.readLine().split(" ")[1]));
            card.setName(reader.readLine().split(" ")[1]);
            card.setHp(Integer.parseInt(reader.readLine().split(" ")[1]));
            card.setPokemonType(EnergyType.valueOf(reader.readLine().split(" ")[1]));
            String lineEvolvesFrom = reader.readLine().split("\\. ")[1];
            if (!lineEvolvesFrom.equals("-"))
            {
                CardImport cartImport2 = new CardImport();
                Card card2 = cartImport2.readFromFile(lineEvolvesFrom);
                card.setEvolvesFrom(card2);
            }
            else
                card.setEvolvesFrom(null);
            String[] skills = reader.readLine().split("\\. ")[1].split("\\, ");
            List<AttackSkill> skillList = new ArrayList<>();
            for (String i : skills)
            {
                String[] meaningattack = i.split("/");
                AttackSkill attack = new AttackSkill (meaningattack[1], "",
                        meaningattack[0], Integer.parseInt(meaningattack[2]));
                skillList.add(attack);
            }
            card.setSkills(skillList);
            card.setWeaknessType(EnergyType.valueOf(reader.readLine().split(" ")[1]));
            card.setResistanceType(EnergyType.valueOf(reader.readLine().split(" ")[1]));
            card.setRetreatCost(reader.readLine().split(" ")[1]);
            card.setGameSet(reader.readLine().split("\\. ")[1]);
            card.setRegulationMark(reader.readLine().split(" ")[1].charAt(0));
            String stringown = reader.readLine().split("\\. ")[1];
            if (!stringown.equals("-"))
            {
                String[] owner = stringown.split("\\/");
                Student studentowner = new Student(owner[0], owner[1], owner[2], owner[3]);
                card.setPokemonOwner(studentowner);
            }
            else
                card.setPokemonOwner(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return card;
    }

    public Card importFromFile(String fileName)
    {
        try
        {
            FileInputStream fileInput = new FileInputStream(fileName);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            card = (Card) objectInput.readObject();
            System.out.println("\u001b[38;5;15mДесериализация выполнена.\n\u001b[38;5;0m");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return card;
    }
}
