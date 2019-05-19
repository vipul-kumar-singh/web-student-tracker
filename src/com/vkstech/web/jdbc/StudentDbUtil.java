package com.vkstech.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

	public void addStudent(Student student) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			// get db connection
			connection = dataSource.getConnection();

			// create sql for insert
			String sql = "insert into student (first_name, last_name, email) values(?,?,?)";
			preparedStatement = connection.prepareStatement(sql);

			// set the param values for the student
			preparedStatement.setString(1, student.getFirstName());
			preparedStatement.setString(2, student.getLastName());
			preparedStatement.setString(3, student.getEmail());

			// execute sql insert
			preparedStatement.execute();
		} finally {
			// clean up JDBC objects
			closeConnection(connection, preparedStatement, null);
		}

	}

	public Student getStudent(String theStudentId) throws Exception {
		Student student = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int studentId;

		try {
			// convert student id to int
			studentId = Integer.parseInt(theStudentId);

			// get connection to database
			connection = dataSource.getConnection();

			// create sql to get selected student
			String sql = "select * from student where id=?";

			// create prepared statement
			preparedStatement = connection.prepareStatement(sql);

			// set params
			preparedStatement.setInt(1, studentId);

			// execute statement
			resultSet = preparedStatement.executeQuery();

			// retrieve data from result set row
			if (resultSet.next()) {
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				String email = resultSet.getString("email");

				// use the student id during construction
				student = new Student(studentId, firstName, lastName, email);
			} else {
				throw new Exception("Could not find student id: " + studentId);
			}

			return student;

		} finally {
		}

	}

	public void updateStudent(Student student) throws Exception {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			// get db connection
			connection = dataSource.getConnection();

			// create SQL update statement
			String sql = "update student set first_name = ?, last_name=?, email=? where id=?";

			// prepare statement
			preparedStatement = connection.prepareStatement(sql);

			// set params
			preparedStatement.setString(1, student.getFirstName());
			preparedStatement.setString(2, student.getLastName());
			preparedStatement.setString(3, student.getEmail());
			preparedStatement.setInt(4, student.getId());

			// execute SQL statement
			preparedStatement.execute();
		} finally {
			// clean up JDBC objects
			closeConnection(connection, preparedStatement, null);
		}
	}
}
