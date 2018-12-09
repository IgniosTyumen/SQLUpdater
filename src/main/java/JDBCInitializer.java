import org.sqlite.JDBC;

import java.sql.*;
import java.util.List;

public class JDBCInitializer {
    private static Connection connection;

    private static int lines = 0;
    public static void  connect() {
        connection = null;

        try {
            String url = "jdbc:sqlite:AC.db";
            DriverManager.registerDriver(new JDBC());
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);
            System.out.println("connection success");
        } catch (SQLException exception) {
            exception.printStackTrace();

        }
    }
    public static void checkifexists(Ean ean){
        //language=sql
        String sqluni = "INSERT OR REPLACE INTO good (id,ean,article) VALUES ((SELECT id FROM good WHERE article="+ean.getArticle()+"),'"+ean.getEan()+"',"+ean.getArticle()+")";
        //String sqlst = "SELECT *  FROM good WHERE article="+ean.article;
        //language=sql
        //String sqlnew = "INSERT INTO good(ean,article) VALUES ('"+ean.ean+"',"+ean.article+")";
        //language=sql
        //String sqlupd = "UPDATE good SET ean='"+ean.ean+"',article="+ean.article+" WHERE article="+ean.article;
//        try {
//            Statement statement = connection.createStatement();
//            ResultSet rs = statement.executeQuery(sqlst);
//            boolean trigger = rs.next();
//            statement.close();
//            if (!trigger){
//
//                Statement statement1 = connection.createStatement();
//                statement1.execute(sqlnew);
//                rs.close();
//                }
//            else {
//                Statement statement2 = connection.createStatement();
//                statement2.execute(sqlupd);
//                rs.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        try {
            Statement statement = connection.createStatement();
            statement.execute(sqluni);
            lines++;
            if (lines>10_000) {
                System.out.println("10k lines prepared");
                connection.commit();
                System.out.println("10k lines commitet");
                lines=0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    static void close(){
        try {
            connection.commit();
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
