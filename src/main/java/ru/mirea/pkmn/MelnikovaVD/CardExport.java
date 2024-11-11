package ru.mirea.pkmn.MelnikovaVD;

import ru.mirea.pkmn.Card;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CardExport
{
    public static void ExportToFile(Card card)
    {
        String fileName = card.getName() + ".crd";
        FileOutputStream myFile = null;
        try
        {
            myFile = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(myFile);
            out.writeObject(card);
            System.out.println("\u001b[38;5;15mСериализация выполнена.\u001b[38;5;0m");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }
}
