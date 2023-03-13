package ru.job4j.grabber;

import ru.job4j.grabber.utils.HabrCareerDateTimeParser;
import ru.job4j.quartz.AlertRabbit;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection cnn;

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = cfg.getProperty("url");
        String login = cfg.getProperty("username");
        String password = cfg.getProperty("password");
        cnn = DriverManager.getConnection(url, login, password);
    }

    public static Properties propReader() {
        Properties cfg = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cfg;

    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "insert into post(title, text, link, created) values (?, ?, ?, ?)"
                        + " ON CONFLICT (link) DO NOTHING;", Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            statement.execute();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    post.setId(resultSet.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(new Post(resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("link"),
                            resultSet.getString("text"),
                            resultSet.getTimestamp("created").toLocalDateTime()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = cnn.prepareStatement("select * from post where id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                post = new Post(resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("link"),
                        resultSet.getString("text"),
                        resultSet.getTimestamp("created").toLocalDateTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteAll() {
        boolean res = false;
        try (PreparedStatement statement = cnn.prepareStatement(
                "delete from post;"
                + "ALTER SEQUENCE post_id_seq RESTART WITH 1;"
                )) {
                res = statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }



    public static void main(String[] args) throws SQLException {
        HabrCareerParse parse = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> list = parse.list("https://career.habr.com/vacancies/java_developer?page=");
        PsqlStore psqlStore = new PsqlStore(propReader());
        for (Post post: list) {
            psqlStore.save(post);
        }
        psqlStore.getAll().forEach(System.out::println);
    }
}
