package org.apache.log4j.extended.jdbc;

import java.lang.reflect.Field;
import java.sql.Types;

import org.apache.log4j.Layout;

public class DbColumn {
    private String name;
    private int dataType;
    private Layout layout;
    private int maxLength;
    private boolean nullable;
    
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
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}
	/**
	 * @param maxLength the maxLength to set
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	
	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}
	/**
	 * @param nullable the nullable to set
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
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
    
    public String getDataTypeName() {
        Field[] fields = Types.class.getFields();
        for (Field field : fields) {
            // if is 'public static final' and of type int and name matches
            if (field.getModifiers() == 25 && int.class.equals(field.getType())) {
                try {
                    int dataType = field.getInt(null);
                    if (this.dataType == dataType) {
                    	return field.getName();
                    }
                } catch (Exception e) {
                    // ignore all errors
                }
            }
        }
        return "[unknown: " + dataType + "]";
    }
    
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dataType;
		result = prime * result + maxLength;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (nullable ? 1231 : 1237);
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DbColumn other = (DbColumn) obj;
		if (dataType != other.dataType)
			return false;
		if (maxLength != other.maxLength)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nullable != other.nullable)
			return false;
		return true;
	}
	
	@Override
    public String toString() {
	    return name + " " + getDataTypeName() + (maxLength > 0 ? "(" + maxLength + ") " : "") + (!nullable ? "NOT NULL " : "");
    }
}
