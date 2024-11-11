package ru.mirea.pkmn.MelnikovaVD.web.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mirea.pkmn.*;
import ru.mirea.pkmn.MelnikovaVD.CardImport;

import java.io.FileInputStream;
import java.io.IOException;
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
        databaseProperties.load(new FileInputStream("/src/main/resources/database.properties"));

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
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, cardName);
            ResultSet resultSet = statement.executeQuery();

            card.setName(resultSet.getString("name"));
            card.setHp(resultSet.getInt("hp"));
            if ((UUID) resultSet.getObject("evolves_from") != null)
                getEvolve((UUID) resultSet.getObject("evolves_from"));
            card.setGameSet(resultSet.getString("game_set"));
            if ((UUID) resultSet.getObject("pokemon_owner") != null)
                card.setPokemonOwner(getOwner((UUID) resultSet.getObject
                        ("pokemon_owner")));
            card.setPokemonStage(PokemonStage.valueOf(resultSet.getString("stage")));
            card.setRetreatCost(resultSet.getString("retreat_cost"));
            card.setWeaknessType(EnergyType.valueOf(resultSet.getString("weakness_type")));
            card.setResistanceType(EnergyType.valueOf(resultSet.getString("resistance_type")));
            card.setSkills(getSkills(resultSet.getString("attack_skills")));
            card.setPokemonType(EnergyType.valueOf(resultSet.getString("pokemon_type")));
            card.setRegulationMark(resultSet.getString("regulation_mark").charAt(0));
            card.setNumber(resultSet.getString("card_number"));
            return card;
        }
        catch (SQLException | JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Student getStudentFromDatabase(String studentFullName) {
        String query = "select * from student where \"surName\" = ? and" + "\"firstName\" = ? and"
                + " \"patronicName\" = ?";
        Student student = new Student();
        String[] splitName = studentFullName.split(" ");
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, splitName[1]);
            statement.setString(2, splitName[2]);
            statement.setString(3, splitName[3]);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                student.setSurName(resultSet.getString("surName"));
                student.setFirstName(resultSet.getString("firstName"));
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
            statement.setString(2, owner.getSurName());
            statement.setString(3, owner.getFirstName());
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
        String query = "select * from card where \"id\" = ? and" +"\"name\" = ?"+
                "\"hp\" = ? and" + "\"evolves_from\" = ? and" + "\"game_set\" = ? and" +
                "\"pokemon_owner\" = ? and" +  "\"stage\" = ? and" + "\"retreat_cost\" = ? and" +
                "\"weakness_type\" = ? and" + "\"resistance_type\" = ? and"+
                "\"attack_skills\" = ? and"+"\"pokemon_type\" = ? and" +
                "\"regulation_mark\" = ? and" + "\"card_number\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1,UUID.randomUUID());
            statement.setString(2, card.getName());
            statement.setInt(3, card.getHp());
            if (card.getEvolvesFrom() != null)
                statement.setObject(4, saveEvolve(card.getEvolvesFrom()));
            statement.setString(5, card.getGameSet());
            if (card.getPokemonOwner() != null) {
                Student owner = card.getPokemonOwner();
                if (getStudentFromDatabase(owner.toString())!=null)
                    statement.setObject(4, saveOwner(card.getPokemonOwner()));
                else
                    createPokemonOwner(card.getPokemonOwner());
            }
            statement.setString(7, card.getPokemonStage().toString());
            statement.setString(8, card.getRetreatCost());
            statement.setString(9, card.getWeaknessType().toString());
            statement.setString(10, card.getResistanceType().toString());
            statement.setString(11,saveSkills(card.getSkills()));
            statement.setString(12, card.getPokemonType().toString());
            statement.setString(13, String.valueOf(card.getRegulationMark()));
            statement.setString(14, card.getNumber());
            statement.execute();
        }
        catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void getEvolve(UUID ID) {
            String query = "select * from card where \"id\" = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setObject(1, ID);
                ResultSet resultSet = statement.executeQuery();
                String nameEvolvesFrom;
                nameEvolvesFrom = resultSet.getString("name");
                getCardFromDatabase(nameEvolvesFrom);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }
    public Student getOwner(UUID ID) {
        Student student = new Student();
        String query = "select * from student where \"id\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, ID);
            ResultSet resultSet = statement.executeQuery();
            student.setFirstName(resultSet.getString("firstName"));
            student.setSurName(resultSet.getString("familyName"));
            student.setFamilyName(resultSet.getString("patronicName"));
            student.setGroup(resultSet.getString("group"));
            return student;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    public List<AttackSkill> getSkills(String attack_skills) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(attack_skills);
        List<AttackSkill> skills = new ArrayList<>();
        for (JsonNode attackNode : jsonNode) {
            AttackSkill attack = new AttackSkill(attackNode.path("name").asText(),
                    attackNode.path("cost").asText(),
                    attackNode.path("description").asText(),
                    attackNode.path("damage").asInt());
            skills.add(attack);
        }
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
        String query = "select * from card where \"firstName\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, owner.getFirstName());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return UUID.fromString(resultSet.getString("id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public String saveSkills(List<AttackSkill> attack_skills) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(attack_skills);
        return objectMapper.writeValueAsString(jsonNode);
    }
}
