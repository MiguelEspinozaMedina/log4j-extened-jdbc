This project provides an extended version of JDBCAppender that provides a simple, and intuitive way to log to the database table using JDBC prepared statements.

    * JNDI DataSource? lookup
    * Support prepared statements.
    * Custom SQL (with '?' placeholders)
    * Buffering
    * JDBC Batch insertion mode (using buffer)
    * Simple column-based configuration 

    * Example of using AsyncLogger? with JDBCAppender for performance improvement 


Sample appender in log4j.xml


	<appender name="jdbc" class="org.apache.log4j.extended.jdbc.JDBCAppender">
		<!-- database connection (please chagne to use your own ) -->
		<!-- URL should include database name -->
		<param name="driverClassName" value="org.hsqldb.jdbc.JDBCDriver"/>
		<param name="url" value="jdbc:hsqldb:file:test"/>
		<param name="username" value="SA"/>
		<param name="password" value=""/>
		
		<!-- uncomment this to use the datasource JNDI name -->
		<!-- <param name="dataSource" value="java:jdbc/datasource"/> -->

		<!-- table -->
		<param name="table" value="applog"/>

		<!-- columns -->
		<!--
			Format: 
				column name | column type | formatting 
			Column options:
				name: 
					should match database name, value is right-trimmed
				type: 
					should match java.sql.Types constant name, value is trimmed
				layout: 
					can be a string that matches org.apache.log4j.PatternLayout.ConversionPattern or "%class: [Full Class Name]"
				formatting arguments: 
					only applies if layout is set to class that requires additional configuration.
					parameters should be passed on the format of: "name=value" separated by " | ", 
					where name is 'bean property name' and value is java.lang.String or java primitive.
		-->
		<param name="column" value="date        | TIMESTAMP | %class: org.apache.log4j.extended.jdbc.TimestampLayout"/>
		<param name="column" value="host        | VARCHAR   | %class: org.apache.log4j.extended.jdbc.HostLayout"/>
		<param name="column" value="application | VARCHAR   | order"/>
		<param name="column" value="thread      | VARCHAR   | %t"/>
		<param name="column" value="category    | VARCHAR   | %c"/>
		<param name="column" value="priority    | VARCHAR   | %p"/>
		<param name="column" value="message     | VARCHAR   | %m"/>
		<param name="column" value="throwable   | VARCHAR   | %class: org.apache.log4j.extended.jdbc.ThrowableLayout | throwableMaxChars=-1"/>
	</appender>
	
Sample appender in log4j.properties

	log4j.appender.jdbc=org.apache.log4j.extended.jdbc.JDBCAppender

	# database connection (please chagne to use your own)
	# URL should include database name
	log4j.appender.jdbc.driverClassName=org.hsqldb.jdbc.JDBCDriver
	log4j.appender.jdbc.url=jdbc:hsqldb:file:test
	log4j.appender.jdbc.username=
	log4j.appender.jdbc.password=

	# uncomment this to use the datasource JNDI name
	#log4j.appender.jdbc.datasource=java:jdbc/datasource

	# table
	log4j.appender.jdbc.table=applog

	# Format: 
	#    column name | column type | formatting 
	# Column options:
	#    name: 
	#        should match database name, value is right-trimmed
	#    type: 
	#        should match java.sql.Types constant name, value is trimmed
	#    layout: 
	#        can be a string that matches org.apache.log4j.PatternLayout.ConversionPattern or "%class: [Full Class Name]"
	#    formatting arguments: 
	#        only applies if layout is set to class that requires additional configuration.
	#        parameters should be passed on the format of: "name=value" separated by " | ", 
	#        where name is 'bean property name' and value is java.lang.String or java primitive.
	log4j.appender.jdbc.column=date        | TIMESTAMP | %class: org.apache.log4j.extended.jdbc.TimestampLayout"/>
	log4j.appender.jdbc.column=host        | VARCHAR   | %class: org.apache.log4j.extended.jdbc.HostLayout"/>
	log4j.appender.jdbc.column=application | VARCHAR   | order"/>
	log4j.appender.jdbc.column=thread      | VARCHAR   | %t"/>
	log4j.appender.jdbc.column=category    | VARCHAR   | %c"/>
	log4j.appender.jdbc.column=priority    | VARCHAR   | %p"/>
	log4j.appender.jdbc.column=message     | VARCHAR   | %m"
	log4j.appender.jdbc.column=throwable   | VARCHAR   | %class: org.apache.log4j.extended.jdbc.ThrowableLayout | throwableMaxChars=-1"/>

	
	