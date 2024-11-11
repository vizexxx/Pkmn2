package ru.mirea.pkmn.MelnikovaVD;

import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.MelnikovaVD.web.http.PkmnHttpClient;
import java.io.IOException;

public class PkmnApplication
{
    static PkmnHttpClient pkmnHttpClient = new PkmnHttpClient();
    public static void main(String[] args) throws IOException {
        CardImport cardImport = new CardImport();
        Card myCard;
        //= cardImport.readFromFile("src/main/resources/my_card.txt");
        //JsonNode card = pkmnHttpClient.getPokemonCard(myCard.getName(), myCard.getNumber());
        //myCard = CardImport.setDescriptionsFromAPI(myCard, pkmnHttpClient);
        System.out.printf("\u001b[38;5;111m\nTask PKMN:\u001b[38;5;0m\n");
        //System.out.println(card.toPrettyString());
        //CardExport.ExportToFile(myCard);
        //System.out.println("\n\n");
        //System.out.println(myCard);
        myCard = cardImport.importFromFile("Pyroar.crd");
        System.out.println(myCard);
    }



}
