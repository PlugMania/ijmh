package info.plugmania.ijmh;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MySQL {
	
	static Connection con = null;
    static Statement st = null;
    static ResultSet rs = null;

    static String url = "jdbc:mysql://nsor.dk:3306/ijmh";
    static String user = "ijmh";
    static String password = "";

    ijmh plugin;
    
    public MySQL(ijmh instance) {
    	plugin = instance;
	}

    static Connection connect() {
    	try {
    		con = DriverManager.getConnection(url, user, password);
			if(con!=null) Util.toLog("MySQL connect success",true);
			return con;
		} catch (SQLException e) {
			Util.toLog("" + e.getMessage(),true);
			return null;
		}
    }
    
    static Statement statement() {
    	try {
    		st = con.createStatement();
			if(st!=null) Util.toLog("MySQL Statement accepted",true);
			return st;
		} catch (SQLException e) {
			Util.toLog("" + e.getMessage(),true);
			return null;
		}
    }    
    
    static ResultSet resultset(String sql) {
    	try {
    		rs = st.executeQuery(sql);
			if(rs!=null) Util.toLog("MySQL Query executed",true);
			return rs;
		} catch (SQLException e) {
			Util.toLog("" + e.getMessage(),true);
			return null;
		}
    } 
    
    static void close() {
    	try {
            if (rs != null)  rs.close();
            if (st != null)  st.close();
            if (con != null) con.close();
		} catch (SQLException e) {
			Util.toLog("" + e.getMessage(),true);
		}    	
    }
    
    static HashMap<Integer,List<String>> select(String sql) {
    	connect();
        if(con!=null) { statement(); if(st!=null) { resultset(sql);
        	if(rs!=null) {
        		HashMap<Integer,List<String>> query = new HashMap<Integer,List<String>>();
        		try {
					while( rs.next() ) {
						List<String> columns = new LinkedList<String>(); 
						for(int i=2;i<=rs.getFetchSize();i++) {
							columns.add(rs.getString(i));
						}
						query.put(rs.getInt(1), columns);
					}
				} catch (SQLException e) {
					Util.toLog("" + e.getMessage(),true);
				}
        		
        		close();
        		
        		return query;
        	} else return null; 
        } else return null; } else return null; 
    }
    
	static boolean test() {
		try {
        	connect();
            if(con!=null) {
            	statement();
            	if(st!=null) {
            		resultset("SELECT VERSION()");
            		if(rs!=null) {
            			if (rs.next()) Util.toLog("MySQL version: " + rs.getString(1),true);
            		} else return false;
            	} else return false;
            } else return false;

           close();
           
		} catch (SQLException e) {
			Util.toLog("" + e.getMessage(),true);
			return false;
		}
        
        return true;
    }
}
