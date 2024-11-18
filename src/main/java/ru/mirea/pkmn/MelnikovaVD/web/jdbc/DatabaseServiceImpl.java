package ru.mirea.pkmn.MelnikovaVD.web.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.mirea.pkmn.*;
import ru.mirea.pkmn.MelnikovaVD.CardImport;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class DatabaseServiceImpl implements DatabaseService {
    private final Connection connection;

    private final Properties databaseProperties;

    public DatabaseServiceImpl() throws SQLException, IOException {

        // Загружаем файл database.properties

        databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream("src/main/resources/database.properties"));

        // Подключаемся к базе данных

        connection = DriverManager.getConnection(
                databaseProperties.getProperty("database.url"),
                databaseProperties.getProperty("database.user"),
                databaseProperties.getProperty("database.password")
        );
        System.out.println("Connection is "+(connection.isValid(0) ? "up" : "down"));
    }

    @Override
    public Card getCardFromDatabase(String cardName) {
        String query = "select * from card where \"name\" = ?";
        Card card = new Card();
        Student ownerPokemon = new Student();
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, cardName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                card.setName(resultSet.getString("name"));
                card.setHp(resultSet.getInt("hp"));

                if (resultSet.getObject("evolves_from") != null) {
                    Card evolvesFromCard = getEvolve((UUID) resultSet.getObject("evolves_from"));
                    card.setEvolvesFrom(evolvesFromCard);
                }
                else
                    card.setEvolvesFrom(null);
                card.setGameSet(resultSet.getString("game_set"));

                if (resultSet.getObject("pokemon_owner") != null) {
                    ownerPokemon = getOwner((UUID) resultSet.getObject("pokemon_owner"));
                    card.setPokemonOwner(ownerPokemon);
                }
                else
                    card.setPokemonOwner(null);
                card.setPokemonStage(PokemonStage.valueOf(resultSet.getString("stage")));
                if (!resultSet.getString("retreat_cost").equals("null"))
                    card.setRetreatCost(resultSet.getString("retreat_cost"));
                else
                    card.setRetreatCost(null);
                if (!resultSet.getString("weakness_type").equals("null"))
                    card.setWeaknessType(EnergyType.valueOf(resultSet.getString("weakness_type")));
                else
                    card.setWeaknessType(null);
                if (!resultSet.getString("resistance_type").equals("null"))
                    card.setResistanceType(EnergyType.valueOf(resultSet.getString("resistance_type")));
                else
                    card.setResistanceType(null);
                card.setSkills(getSkills(resultSet.getString("attack_skills")));
                card.setPokemonType(EnergyType.valueOf(resultSet.getString("pokemon_type")));
                card.setRegulationMark(resultSet.getString("regulation_mark").charAt(0));
                card.setNumber(resultSet.getString("card_number"));
                statement.close();
                return card;
            }
            return null;
        }
        catch (SQLException | JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }
    public Student getStudentFromDatabaseId(String id) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM student WHERE \"id\" = '" + id + "';");
        Student student = null;
        if (resultSet.next()) {
            String firstName = resultSet.getString("familyName");
            String surName = resultSet.getString("firstName");
            String familyName = resultSet.getString("patronicName");
            String group = resultSet.getString("group");

            student = new Student(firstName, surName, familyName, group);
        }
        statement.close();
        return student;
    }


    @Override
    public Student getStudentFromDatabase(String studentFullName) {
        String query = "select * from student where \"familyName\" = ? and" + "\"firstName\" = ? and"
                + " \"patronicName\" = ?";
        Student student = new Student();
        String[] splitName = studentFullName.split("/");
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, splitName[0]);
            statement.setString(2, splitName[1]);
            statement.setString(3, splitName[2]);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                student.setSurName(resultSet.getString("firstName"));
                student.setFirstName(resultSet.getString("surName"));
                student.setFamilyName(resultSet.getString("patronicName"));
                student.setGroup(resultSet.getString("group"));
                return student;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }
    @Override
    public void createPokemonOwner (Student owner) {
        String query = "insert into student(id, " + "\"familyName\",\"firstName\",\"patronicName\",\"group\") " +
                "values(?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, UUID.randomUUID());
            statement.setString(2, owner.getFirstName());
            statement.setString(3, owner.getSurName());
            statement.setString(4, owner.getFamilyName());
            statement.setString(5, owner.getGroup());
            statement.execute();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void saveCardToDatabase(Card card)
    {
        String query ="insert into card(id, name, hp, evolves_from, " +
                "game_set, pokemon_owner, stage, retreat_cost, " +
                "weakness_type, resistance_type, attack_skills, " +
                "pokemon_type, regulation_mark, card_number) VALUES(" +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::json, ?, ?, ?)";
        //String query = "insert into card (\"id\" = ? and" +"\"name\" = ? and "+
//                "\"hp\" = ? and" + "\"evolves_from\" = ? and" + "\"game_set\" = ? and" +
//                "\"pokemon_owner\" = ? and" +  "\"stage\" = ? and" + "\"retreat_cost\" = ? and" +
//                "\"weakness_type\" = ? and" + "\"resistance_type\" = ? and"+
//                "\"attack_skills\" = ? and"+"\"pokemon_type\" = ? and" +
//                "\"regulation_mark\" = ? and" + "\"card_number\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1,UUID.randomUUID());
            statement.setString(2, card.getName());
            statement.setInt(3, card.getHp());
            if (card.getEvolvesFrom() != null) {

                saveCardToDatabase(card.getEvolvesFrom());
                statement.setObject(4, saveEvolve(card.getEvolvesFrom()));
            }
            else
                statement.setObject(4, null);
            statement.setString(5, card.getGameSet());
            if (card.getPokemonOwner() != null) {
                Student owner = card.getPokemonOwner();
//                if (getStudentFromDatabase(owner.toString())!=null)
//                    statement.setObject(6, saveOwner(owner));
//                else {
                    createPokemonOwner(owner);
                    statement.setObject(6, saveOwner(owner));
//                }
            }
            else
                statement.setObject(6, null);
            statement.setString(7, card.getPokemonStage().toString());
            statement.setString(8, card.getRetreatCost());
            statement.setString(9, card.getWeaknessType().toString());
            statement.setString(10, card.getResistanceType().toString());
            statement.setString(11,saveSkills(card.getSkills()));
            statement.setString(12, card.getPokemonType().toString());
            statement.setString(13, String.valueOf(card.getRegulationMark()));
            statement.setString(14, card.getNumber());
            //System.out.println(statement.toString());
            statement.execute();
        }
        catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Card getEvolve(UUID ID) {
        String query = "select * from card where \"id\" = ?";
        Card evolves = null;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setObject(1, ID);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String nameEvolvesFrom;
                    nameEvolvesFrom = resultSet.getString("name");
                    evolves = getCardFromDatabase(nameEvolvesFrom);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        return evolves;
    }
    public Student getOwner(UUID ID) {
        Student student = new Student();
        String query = "select * from student where \"id\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, ID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                student.setFirstName(resultSet.getString("familyName"));
                student.setSurName(resultSet.getString("firstName"));
                student.setFamilyName(resultSet.getString("patronicName"));
                student.setGroup(resultSet.getString("group"));
            }
            else
                student = null;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return student;
    }
    public List<AttackSkill> getSkills(String attack_skills) throws JsonProcessingException {
        Gson gson = new Gson();
        Type type = new TypeToken<List<AttackSkill>>() {}.getType();
        List<AttackSkill> skills = gson.fromJson(attack_skills,type);
        return skills;
    }
    public UUID saveEvolve(Card evolveFrom) {
        String query = "select * from card where \"name\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, evolveFrom.getName());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return UUID.fromString(resultSet.getString("id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public UUID saveOwner(Student owner) {
        String query = "select * from student where \"firstName\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, owner.getSurName());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return UUID.fromString(resultSet.getString("id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public String saveSkills(List<AttackSkill> attack_skills) throws JsonProcessingException {
        return new Gson().toJson(attack_skills);
    }
}
