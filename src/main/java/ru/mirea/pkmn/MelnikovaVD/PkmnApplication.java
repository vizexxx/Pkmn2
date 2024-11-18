package ru.mirea.pkmn.MelnikovaVD;

import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.MelnikovaVD.web.http.PkmnHttpClient;
import ru.mirea.pkmn.MelnikovaVD.web.jdbc.DatabaseService;
import ru.mirea.pkmn.MelnikovaVD.web.jdbc.DatabaseServiceImpl;

import java.io.IOException;
import java.sql.SQLException;

public class PkmnApplication
{
    static PkmnHttpClient pkmnHttpClient = new PkmnHttpClient();
    public static void main(String[] args) throws IOException, SQLException {
        CardImport cardImport = new CardImport();
        Card myCard = cardImport.readFromFile("src/main/resources/my_card.txt");
        myCard = CardImport.setDescriptionsFromAPI(myCard, pkmnHttpClient);
        JsonNode card = pkmnHttpClient.getPokemonCard(myCard.getName(), myCard.getNumber());
        System.out.println(card.toPrettyString());
        System.out.println("\n\n");
        System.out.printf("\u001b[38;5;111m\nTask PKMN:\u001b[38;5;0m\n");

        
        DatabaseServiceImpl db = new DatabaseServiceImpl();
        //System.out.println(db.getCardFromDatabase("Azumarill"));
        //db.createPokemonOwner(myCard.getPokemonOwner());
//        while (myCard.getEvolvesFrom() != null) {
//            db.saveCardToDatabase(myCard);
//            myCard = myCard.getEvolvesFrom();
//        }
        db.saveCardToDatabase(myCard);
    }



}
