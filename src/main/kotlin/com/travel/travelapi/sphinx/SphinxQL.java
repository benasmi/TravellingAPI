package com.travel.travelapi.sphinx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SphinxQL {

    /**
     * Connection to sphinx searchd
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://izbg.l.dedikuoti.lt:9306?characterEncoding=utf8&maxAllowedPacket=512000","", "");
    }

    /**
     *
     * This method connect to Sphinx search engine and does indexed search.
     *
     * @param query to execute
     * @return ResultSet from sphinx query
     */
    public static List<String> executeQuery(String query){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<String> ids = new ArrayList<>();
        ResultSet aResultSet = null;
        try {
            Connection aConnection = getConnection();
            Statement aStatement = aConnection.createStatement();
            aResultSet = aStatement.executeQuery(query);
            while (aResultSet.next()){
                ids.add(aResultSet.getString(1));
            }


            aConnection.close();
            aStatement.close();
            aResultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}