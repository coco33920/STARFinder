package fr.charlotte.help;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hugo
 */
public class DatabaseLite {

    private Connection connection;


    /**
     * Create a database instance and connect
     *
     * @param file
     */
    public DatabaseLite(String file) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+file);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Returns a value in the database
     *
     * @param query MySQL writted request
     * @param get   Field to get
     * @return
     */
    public Object read(String query, String get) {
        Object request = null;

        try {
            PreparedStatement sts = this.connection.prepareStatement(query);
            ResultSet result = sts.executeQuery();
            while (result.next())
                request = result.getObject(get);
            sts.close();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return request;
    }

    /**
     * Returns a list of values in the database
     *
     * @param query MySQL writted request
     * @param get   Field to get
     * @return
     */
    public List<Object> readList(String query, String get) {
        List<Object> request = new ArrayList();

        try {
            PreparedStatement sts = this.connection.prepareStatement(query);
            ResultSet result = sts.executeQuery();
            while (result.next())
                request.add(result.getObject(get));
            sts.close();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return request;
    }

    /**
     * Update or Remove or Set a row in database
     *
     * @param query MySQL writted request
     */
    public void update(String query) {
        try {
            PreparedStatement sts = this.connection.prepareStatement(query);
            sts.executeUpdate();
            sts.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param query MySQL writted request
     * @return
     */
    public ResultSet getResult(String query) {
        Object request = null;
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            return pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (ResultSet) request;
    }

    public void close(){
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}