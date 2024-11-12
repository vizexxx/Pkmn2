package ru.mirea.pkmn.MelnikovaVD;

import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.*;
import ru.mirea.pkmn.MelnikovaVD.web.http.PkmnHttpClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.mirea.pkmn.MelnikovaVD.PkmnApplication.pkmnHttpClient;

public class CardImport
{
    Card card = new Card();
    PkmnHttpClient pkmnHttpClient = new PkmnHttpClient();

    public CardImport() throws IOException {
    }

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
            card.setNumber(reader.readLine().split("\\. ")[1]);
            JsonNode cardJson = pkmnHttpClient.getPokemonCard(card.getName(), card.getNumber());
            System.out.println(cardJson.toPrettyString());
            System.out.println("\n\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return card;
    }
    public static Card setDescriptionsFromAPI(Card card, PkmnHttpClient httpClient) throws IOException {
        if(card.getEvolvesFrom() != null)
            setDescriptionsFromAPI(card.getEvolvesFrom(), httpClient);

        JsonNode cardNode = httpClient.getPokemonCard(card.getName(), card.getNumber());
        Stream<JsonNode> attackStream = cardNode.findValues("attacks").stream();
        JsonNode attacks = attackStream.toList().getFirst();
        for(JsonNode attack : attacks) {
            card = CardImport.SkillDescription(card,
                    attack.findValue("name").asText(),
                    attack.findValue("text").asText());
        }
        attackStream.close();
        return card;
    }

    public static Card SkillDescription(Card card, String skillName, String description) {
        for(AttackSkill skill : card.getSkills()) {
            if(skill.getName().equals(skillName)) {
                card.getSkills().get(card.getSkills().indexOf(skill)).setDescription(description);
            }
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
