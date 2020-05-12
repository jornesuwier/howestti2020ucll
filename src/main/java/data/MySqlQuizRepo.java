package data;

import mechanics.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MySqlQuizRepo {
    /*
  CREATE TABLE `questions` (
  `QUESTIONID` INT NOT NULL AUTO_INCREMENT,
  `QUESTION` VARCHAR(255) NULL,
  `ANSWERS` VARCHAR(500) NULL,
  `CORRECT` INTEGER(10) NULL,
  PRIMARY KEY (`QUESTIONID`));
*/
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

    public boolean addQuestions(String question, String answers, int correct) {
        try (Connection con = DriverManager.getConnection(url, sqlUser, sqlPass)) {
            try (PreparedStatement st = con.prepareStatement("INSERT INTO questions(question,answers,correct) VALUES (?,?,?)")) {
                st.setString(1,question);
                st.setString(2,answers);
                st.setInt(3,correct);
                st.executeUpdate();
                con.commit();
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        return true;
    }
}