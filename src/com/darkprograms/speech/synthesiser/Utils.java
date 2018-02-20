package com.darkprograms.speech.synthesiser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 *
 * @author santana
 */
public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());
    
    public static LinkedList<HashMap<String, Object>> select(String sql) {
        logger.debug(sql);
        
        LinkedList<HashMap<String, Object>> rows = new LinkedList<>();
        
        try(Connection con = DriverManager.getConnection(Config.database, Config.dbUser, Config.dbPassword);
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            try(ResultSet rs = ps.executeQuery();) {
                
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while(rs.next()) {
                    HashMap<String, Object> map = new HashMap<>();
                    
                    for(int n = 1; n <= columnCount; n++) {
                        map.put(metaData.getColumnName(n), rs.getObject(n));
                    }
                    
                    rows.add(map);
                }
            } catch(SQLException ex) {
                logger.error(ex.toString(), ex);
            }
        } catch (SQLException ex) {
            logger.error(ex.toString(), ex);
        }
        
        return rows;
    }
    
    public static void insertInto(String table, LinkedList<Pair> values) {
        String columnsString = "";
        String valuesString = "";
        
        for(Pair p : values) {
            columnsString += p.getName() + ",";
            valuesString += "?,";
        }
        columnsString = columnsString.substring(0, columnsString.length()-1);
        valuesString = valuesString.substring(0, valuesString.length()-1);
        
        String sql = "INSERT INTO "+table+"("+columnsString+") VALUES("+valuesString+")";
        logger.debug(sql);
        
        try(
            Connection con = DriverManager.getConnection(Config.database, Config.dbUser, Config.dbPassword);
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            int n = 1;
            for(Pair p : values) {
                Object obj = p.getValue();

                if(obj instanceof Boolean) {
                    ps.setBoolean(n, (Boolean) obj);
                }
                else if(obj instanceof Byte) {
                    ps.setByte(n, (Byte) obj);
                }
                else if(obj instanceof Short) {
                    ps.setShort(n, (Short) obj);
                }
                else if(obj instanceof Integer) {
                    ps.setInt(n, (Integer) obj);
                }
                else if(obj instanceof Long) {
                    ps.setLong(n, (Long) obj);
                }
                else if(obj instanceof Float) {
                    ps.setFloat(n, (Float) obj);
                }
                else if(obj instanceof Double) {
                    ps.setDouble(n, (Double) obj);
                }
                else if(obj instanceof String) {
                    ps.setString(n, (String) obj);
                }
                
                n++;
            }
            
            ps.executeUpdate();
        } catch(SQLException ex) {
            logger.error(ex.toString(), ex);
        }
    }
    
    public static void update(String table, LinkedList<Pair> values, long id) {
        String attributionString = "";
        
        for(Pair p : values) {
            String name = p.getName();
            Object value = p.getValue();

            attributionString += (value instanceof String) ?
                name+"='"+value+"', " :
                name+"="+value+", ";
        }

        attributionString = attributionString.substring(0, attributionString.length()-2);
        
        String sql = "UPDATE "+table+" SET "+attributionString+" WHERE id="+id;
        logger.debug(sql);
        
        try(
            Connection con = DriverManager.getConnection(Config.database, Config.dbUser, Config.dbPassword);
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.executeUpdate();
        } catch(SQLException ex) {
            logger.error(ex.toString(), ex);
        }
    }
    
    public static void deleteFrom(String table) {
        deleteFrom(table, null);
    }
    
    public static void deleteFrom(String table, String condition) {
        String sql = (condition == null) ?
            "DELETE FROM "+table :
            "DELETE FROM "+table+" WHERE "+condition;
        logger.debug(sql);

        try(
            Connection con = DriverManager.getConnection(Config.database, Config.dbUser, Config.dbPassword);
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.executeUpdate();
        } catch(SQLException ex) {
            logger.error(ex.toString(), ex);
        }
    }
    
    public static String toStringTime(long timeMillis) {
        int milliseconds =  (int) (timeMillis % 1000);
        
        long time = timeMillis / 1000;
        int hours = (int) (time / 3600);
        time -= hours * 3600;
        int minutes = (int) (time / 60);
        time -= minutes * 60;
        int seconds = (int) time;
        
        return String.format(Locale.US, "%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }
}