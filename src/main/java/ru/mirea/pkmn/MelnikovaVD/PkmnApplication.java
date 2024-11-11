package ru.mirea.pkmn.MelnikovaVD;

import ru.mirea.pkmn.Card;

public class PkmnApplication
{
    public static void main(String[] args)
    {
        CardImport cardImport = new CardImport();
        Card myCard = cardImport.readFromFile("src/main/resources/my_card.txt");
        System.out.printf("\u001b[38;5;111m\nTask PKMN:\u001b[38;5;0m\n");
        System.out.printf(myCard.toString());
//        CardExport.ExportToFile(myCard);
//        myCard = cardImport.importFromFile("D:\\Java\\Pkmn\\Morgrem.crd");
//        System.out.printf(myCard.toString());
    }
}
