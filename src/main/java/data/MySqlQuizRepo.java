package data;

import mechanics.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MySqlQuizRepo {
    private static String url = "jdbc:h2:~/quiz";
    private static String sqlUser = "sa";
    private static String sqlPass = "";
    private static MySqlQuizRepo ourInstance = new MySqlQuizRepo();

    public static MySqlQuizRepo getInstance() {
        return ourInstance;
    }

    public List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, sqlUser, sqlPass)) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM questions  ORDER BY RAND() LIMIT 20")) {
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        List<String> answers = new ArrayList<>(Arrays.asList(rs.getString("answers").split(",")));
                        questions.add(new Question(rs.getString("question"),answers,rs.getInt("correct")));
                    }
                    return questions;
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        return null;
    }
}