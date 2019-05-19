package com.vkstech.web.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {
	private DataSource dataSource;

	public StudentDbUtil(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<Student> getStudents() throws SQLException {
		List<Student> studentList = new ArrayList<>();
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			// get a connection
			connection = dataSource.getConnection();

			// create sql statement
			String sql = "select * from student order by last_name";
			statement = connection.createStatement();

			// execute query
			resultSet = statement.executeQuery(sql);

			// process result set
			while (resultSet.next()) {
				// retrieve data from resultSet row
				int id = resultSet.getInt("id");
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				String email = resultSet.getString("email");

				// create new student object
				Student student = new Student(id, firstName, lastName, email);

				// add it to list of students
				studentList.add(student);
			}
			return studentList;
		} finally {
			// close JDBC object
			closeConnection(connection, statement, resultSet);
		}
	}

	private void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connection != null) {
				connection.close(); // does not really close it ... just puts it back to connection pool
			}
		} catch (Exception e) {
		}
	}
}