package org.apache.log4j.extended.jdbc;

import java.lang.reflect.Field;
import java.sql.Types;

import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

public class DbColumn {
    private String name;
    private int dataType;
    private Layout layout;
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the layout
     */
    public Layout getLayout() {
        return layout;
    }
    /**
     * @param layout the layout to set
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
    }
    
    /**
     * @param pattern convert pattern to {@link PatternLayout} and set the layout
     */
    public void setLayout(String pattern) {
        this.layout = new PatternLayout(pattern);
    }
    /**
     * @return the dataType
     */
    public int getDataType() {
        return dataType;
    }
    /**
     * @param dataType the dataType to set
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }    
    /**
     * @param dataTypeName convert dataType to java.sql.Types constant and set the dataType
     */
    public void setDataType(String dataTypeName) {
        Field[] fields = Types.class.getFields();
        for (Field field : fields) {
            // if is 'public static final' and of type int and name matches
            if (field.getModifiers() == 25 && 
                field.getName().equalsIgnoreCase(dataTypeName) &&
                int.class.equals(field.getType())) {
                try {
                    this.dataType = field.getInt(null);
                    return;
                } catch (Exception e) {
                    // ignore all errors
                }
            }
        }
        
        throw new IllegalArgumentException("Unable to convert: " + dataTypeName + " to " + Types.class.getName());
    }

}
