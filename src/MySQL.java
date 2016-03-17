/**
 * Created by joey on 16-3-14.
 */
import java.sql.*;
import java.util.Date;

public class MySQL {
    protected static Connection conn = null;
    protected static Statement stmt = null;
    protected static String url = "jdbc:mysql://localhost:3306/signichat_net_test?"
            + "user=root&password=elwg324&useUnicode=true&characterEncoding=UTF8";

    public static void connect() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(url);
        stmt = conn.createStatement();
    }

    public static String addBoard(String userId, String title, int isPrivate, String description, int field) {
        try {
            connect();
            Date date = new Date();
            String time = String.format("%tF %tT", date, date);
            String deleted = "0";
            String sql = String.format("INSERT INTO board(creator_id,create_time,title,description,field,private,deleted) " +
                    "VALUES('%s','%s','%s','%s','%s','%s','%s')", userId, time, title, description, field, isPrivate, deleted);
            stmt.executeUpdate(sql);
            sql = String.format("SELECT * FROM board WHERE create_time = '%s'", time);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getString("id");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("ERROR: MySQL.addBoard");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: MySQL.addBoard");
        } finally {
            try {
                conn.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                System.out.println("ERROR: MySQL.addBoard");
            }
        }
        return null;
    }

    public static void addPrivateMember(String boardId, String userId) {
        try {
            connect();
            Date date = new Date();
            String time = String.format("%tF %tT", date, date);
            String sql = String.format("INSERT INTO private_board_member(board_id,user_id,add_time) VALUES('%s','%s','%s')", boardId,userId,time);
            stmt.executeUpdate(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("ERROR: MySQL.addPrivateMember");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: MySQL.addPrivateMember");
        } finally {
            try {
                conn.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                System.out.println("ERROR: MySQL.addPrivateMember");
            }
        }
    }

    public static String addImage(String userId, String imageName, String title, int width, int height, int imageGrade) {
        try {
            connect();
            Date date = new Date();
            String time = String.format("%tF %tT", date, date);
            String sql = String.format("INSERT INTO image(image_grade,creator_id,image_name,title,width,height,upload_time,deleted) " +
                    "VALUES('%s','%s','%s','%s','%s','%s','%s','%s')", imageGrade, userId, imageName, title, width, height, time, 0);
            stmt.executeUpdate(sql);
            sql = String.format("SELECT * FROM image WHERE image_name = '%s' AND deleted = '%s'", imageName, 0);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getString("id");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("ERROR: MySQL.addImage");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: MySQL.addImage");
        } finally {
            try {
                conn.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                System.out.println("ERROR: MySQL.addImage");
            }
        }
        return null;
    }

    public static void addBoardImage(String boardId, String imageId) {
        try {
            connect();
            Date date = new Date();
            String time = String.format("%tF %tT", date, date);
            String sql = String.format("INSERT INTO board_has_image(board_id,image_id,add_time) VALUES('%s','%s','%s')", boardId,imageId,time);
            stmt.executeUpdate(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("ERROR: MySQL.addBoardImage");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: MySQL.addBoardImage");
        } finally {
            try {
                conn.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                System.out.println("ERROR: MySQL.addBoardImage");
            }
        }
    }
}
