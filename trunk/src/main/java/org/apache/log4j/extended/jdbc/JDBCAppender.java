package org.apache.log4j.extended.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;


/**
 * The custom database appender
 */
public class JDBCAppender extends org.apache.log4j.AppenderSkeleton implements org.apache.log4j.Appender {
    /**
	 * Database connection parameters used to create a connection.
	 */
	private String driverClassName;
    private String url;
    private String username;
    private String password;

	/**
	 * DataSource used by default.  
	 * 
	 * The dataSource pool to create connection. Can be set using JNDI name:
	 * {@link #setDataSource(String)}
	 */
    protected DataSource dataSource;

    /**
	 * connection used when dataSource is not specified.  
	 * 
	 * The connection is opened the first time it
     * is needed and then held open until the appender is closed (usually at
     * garbage collection).  This behavior is best modified by creating a
     * sub-class and overriding the <code>getConnection</code> and
     * <code>closeConnection</code> methods.
     */
    protected Connection connection = null;

    /**
     * Stores the insert SQL prepared statement:
     * eg: insert into LogTable (Thread, Class, Message) values (?, ?, ?)
     */
    protected String sql = null;
    protected String table;
    
    protected List<DbColumn> columns = new ArrayList<DbColumn>();
    
    protected String columnSeparator = "\\s*\\|\\s*";
    protected String layoutClassPrefix = "%class:";
    protected String layoutPropertiesMacher = "\\s*=\\s*";
    
    /**
     * size of LoggingEvent buffer before writing to the database.
     * Default is 1.
     */
    protected int bufferSize = 1;

    protected boolean executeBatch;
    
    /**
     * ArrayList holding the buffer of Logging Events.
     */
    protected ArrayList<LoggingEvent> buffer;

    /**
     * Helper object for clearing out the buffer
     */
    protected ArrayList<LoggingEvent> removes;
    
    
    public JDBCAppender() {
      super();
      buffer = new ArrayList<LoggingEvent>(bufferSize);
      removes = new ArrayList<LoggingEvent>(bufferSize);
    }

    /**
     * Adds the event to the buffer.  When full the buffer is flushed.
     */
    public void append(LoggingEvent event) {
      buffer.add(event);
      
      if (buffer.size() >= bufferSize)
        flushBuffer();
    }

    /**
     * Override this to return the connection to a pool, or to clean up the
     * resource.
     *
     * The default behavior holds a single connection open until the appender
     * is closed (typically when garbage collected).
     */
    protected void closeConnection(Connection con) {
    }

    /**
     * Override this to link with your connection pooling system.
     *
     * By default this creates a single connection which is held open
     * until the object is garbage collected.
     */
    protected Connection getConnection() throws SQLException {
        if (connection == null) {
            if (dataSource != null) {
                connection = dataSource.getConnection();
            } else {
                try {
                    Class.forName(driverClassName);
                } catch (Exception e) {
                    errorHandler.error("Failed to load driver", e,
                           ErrorCode.GENERIC_FAILURE);
                }
                connection = DriverManager.getConnection(url, username, password);
            }
        }

        return connection;
    }

    /**
     * Closes the appender, flushing the buffer first then closing the default
     * connection if it is open.
     */
    public void close()
    {
      flushBuffer();

      try {
        if (connection != null && !connection.isClosed())
            connection.close();
      } catch (SQLException e) {
          errorHandler.error("Error closing connection", e, ErrorCode.GENERIC_FAILURE);
      }
      this.closed = true;
    }
    
    /**
     * loops through the buffer of LoggingEvents, gets a
     * sql string from getLogStatement() and sends it to execute().
     * Errors are sent to the errorHandler.
     *
     * If a statement fails the LoggingEvent stays in the buffer!
     */
    public void flushBuffer() {
      //Do the actual logging
      removes.ensureCapacity(buffer.size());
      
      try {
          Connection con = getConnection();
          String sql = getSql();
          
          PreparedStatement statement = con.prepareStatement(sql);
          
          int logCount = buffer.size();
          
          try {
              for (LoggingEvent logEvent : buffer) {

                  int parameterCount = 1;
                  
                  for (DbColumn column : columns) {
                      Object value;
                      
                      Layout layout = column.getLayout();
                      
                      if (layout instanceof ObjectLayout) {
                          value = ((ObjectLayout)layout).formatObject(logEvent);
                      } else {
                          value = layout.format(logEvent);
                      }
                        
                      statement.setObject(parameterCount, value, column.getDataType());

                      parameterCount++;
                  }
                  if (executeBatch) {
                	  statement.addBatch();
                  } else {
                      statement.execute();
                	  connection.commit();
                  }
                  removes.add(logEvent);
              }

              if (executeBatch) {
            	  statement.executeBatch();
            	  connection.commit();
              }
          } catch (Exception e) {
              errorHandler.error("Failed to insert " + logCount + " log entries.", e, ErrorCode.FLUSH_FAILURE);
          } finally {
              statement.close();
          }
      } catch (Exception e) {
          errorHandler.error("Failed to insert log entries.", e, ErrorCode.FLUSH_FAILURE);
      } 
      
    
      
      // remove from the buffer any events that were reported
      buffer.removeAll(removes);
      
      // clear the buffer of reported events
      removes.clear();
    }

    private String getColumnList() {
        String columnList = null;
        
        for (DbColumn column : columns) {
            if (columnList == null) {
                columnList = column.getName();
            } else {
                columnList += ", " + column.getName();
            }            
        }
        
        return columnList;
    }

    private String getColumnPlaceholders() {
        StringBuilder columnPlaceholders = new StringBuilder();
        
        for (int i = 0; i < columns.size(); i++) {
            if (columnPlaceholders.length() == 0) {
                columnPlaceholders.append("?");
            } else {
                columnPlaceholders.append(", ?");
            }
        }
        
        return columnPlaceholders.toString();
    }

    /** closes the appender before disposal */
    public void finalize() {
      close();
    }

    /**
     * JDBCAppender does not require a single layout.
     */
    public boolean requiresLayout() {
      return false;
    }

    /**
     *
     */
    public void setSql(String s) {
      sql = s;
    }

    /**
     * Returns custom insert SQL statement
     */
    public String getSql() {
        if (sql == null) {
            sql = "INSERT INTO " + table + " (" + getColumnList() + ") VALUES(" + getColumnPlaceholders() + ")";
        }
        return sql;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(String dataSourceName) {
       try {
    	   InitialContext context = new InitialContext();
    	   dataSource = (DataSource) context.lookup(dataSourceName);
       } catch (NamingException ex) {
    	   errorHandler.error("Unable to find the JNDI DataSource: " + dataSourceName + ", explanation: " + ex.getExplanation(), ex, ErrorCode.GENERIC_FAILURE);
       } catch (Exception ex) {
    	   errorHandler.error("DataSource: " + ex.getMessage(), ex, ErrorCode.GENERIC_FAILURE);
       }
    }
    
    public void setBufferSize(int newBufferSize) {
      bufferSize = newBufferSize;
      buffer.ensureCapacity(bufferSize);
      removes.ensureCapacity(bufferSize);
    }

    public int getBufferSize() {
      return bufferSize;
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the columns
     */
    public List<DbColumn> getColumns() {
        return columns;
    }

    /**
     * Define a database column to the appender
     * 
     * @param columnString String representation of a column
     * <code>
     * Column Format: 
     *    name | type | layout
     *     
     * Column options:
     *    name: 
     *      should match database name, value is right-trimmed
     *    type: 
     *      should match java.sql.Types constant name, value is trimmed
     *    layout: 
     *      can be a string that matches org.apache.log4j.PatternLayout.ConversionPattern or "%class: [Full Class Name]"
     *    layout arguments: 
     *      only applies if layout is set to class that requires additional configuration.
     *      parameters should be passed on the format of: "name=value", 
     *      where name is 'bean property name' and value is java.lang.String or java primitive.
     * </code>
     */    
    public void setColumn(String columnString) {
        String[] columnArgs = columnString.split(columnSeparator);

        DbColumn column = new DbColumn();
        if (columnArgs.length > 0) {
            column.setName(columnArgs[0]);
        }
        if (columnArgs.length > 1) {
            column.setDataType(columnArgs[1]);
        }
        if (columnArgs.length > 2) {
            if (columnArgs[2].startsWith(layoutClassPrefix)) {
                String className = columnArgs[2].substring(layoutClassPrefix.length()).trim();
                
                try {
                    Class<?> layoutClass = Class.forName(className);
                    Object layout = layoutClass.newInstance();
                    if (layout instanceof Layout) {
                        ((Layout)layout).activateOptions();
                        column.setLayout((Layout)layout);
                        
                        for (int i = 3; i < columnArgs.length; i++) {
                            populateProperty(layout, columnArgs[i]);
                        }
                    } else {
                        throw new IllegalArgumentException("Unable to cast class: " + className + " to " + Layout.class.getName());
                    }
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Unable to find class: " + className, e);
                } catch (InstantiationException e) {
                    throw new IllegalArgumentException("Unable to instantiate class: " + className, e);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Unable to access class: " + className, e);
                }
            } else {
                column.setLayout(columnArgs[2]);
            }
        }
        this.columns.add(column);
    }
    
    private void populateProperty(Object object, String prop) {
        Class<?> objectClass = object.getClass();

        try {
            String[] nameValuePair = prop.split("\\s*=\\s*");
            if (nameValuePair.length == 0) {
                return;
            }
            
            String name = nameValuePair[0];
            Object value = null;
            
            String valueString = "true";
            if (nameValuePair.length > 0) {
                valueString = nameValuePair[1];
            }
                    
            Method[] methods = objectClass.getMethods();
            
            Method propertyMethod = null;
            Class<?> propertyType = null;
            
            for (Method method : methods) {
                if (method.getName().equalsIgnoreCase("set" + name) && 
                    method.getParameterTypes().length == 1 && 
                    (method.getParameterTypes()[0].isPrimitive() || 
                    String.class.equals(method.getParameterTypes()[0]))) {
                    
                    propertyMethod  = method;
                    propertyType = method.getParameterTypes()[0];
                    break;
                }
            }
            
            if (propertyMethod == null) {
                 throw new IllegalArgumentException("Unable to find " + name + " with primitive argument.");
            }
        
            if (String.class.equals(propertyType)) {
                if ("null".equalsIgnoreCase(valueString)) {
                    value = null;
                } else {
                    value = valueString;
                }
            } else if (boolean.class.equals(propertyType) || Boolean.class.equals(propertyType)) {
                value = Boolean.valueOf(valueString);
            } else if (char.class.equals(propertyType) || Character.class.equals(propertyType)) {
                value = Character.valueOf(valueString.charAt(0));
            } else if (byte.class.equals(propertyType) || Byte.class.equals(propertyType)) {
                value = Byte.valueOf(valueString);
            } else if (short.class.equals(propertyType) || Short.class.equals(propertyType)) {
                value = Short.valueOf(valueString);
            } else if (int.class.equals(propertyType) || Integer.class.equals(propertyType)) {
                value = Integer.valueOf(valueString);
            } else if (long.class.equals(propertyType) || Long.class.equals(propertyType)) {
                value = Long.valueOf(valueString);
            } else if (float.class.equals(propertyType) || Float.class.equals(propertyType)) {
                value = Float.valueOf(valueString);
            } else if (double.class.equals(propertyType) || Double.class.equals(propertyType)) {
                value = Double.valueOf(valueString);            
            }

            propertyMethod.invoke(object, new Object[] { value });
            
        } catch (Exception ex) {
            errorHandler.error("Unable to set custom property for " + objectClass.getName() + " [" + prop + "]", ex, ErrorCode.MISSING_LAYOUT);
        }
    }
    
    /**
     * @param driverClassName the driverClassName to set
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
	 * @param executeBatch the executeBatch to set
	 */
	public void setExecuteBatch(boolean executeBatch) {
		this.executeBatch = executeBatch;
	}

	/**
     * @param columnSeparator the columnSeparator to set
     */
    public void setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
    }

    /**
     * @param layoutClassPrefix the layoutClassPrefix to set
     */
    public void setLayoutClassPrefix(String layoutClassPrefix) {
        this.layoutClassPrefix = layoutClassPrefix;
    }

    /**
     * @param layoutPropertiesMacher the layoutPropertiesMacher to set
     */
    public void setLayoutPropertiesMacher(String layoutPropertiesMacher) {
        this.layoutPropertiesMacher = layoutPropertiesMacher;
    }
}
