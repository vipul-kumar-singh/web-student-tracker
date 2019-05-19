package com.vkstech.web.jdbc;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private StudentDbUtil studentDbUtil;
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		// create our studentDbUtil instance .. and pass in the connection pool / dataSource
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			//read the command parameter
			String theCommand = request.getParameter("command");
			
			//if the command is missing, then default to listing students
			if(theCommand == null) {
				theCommand = "LIST";
			}
			
			//route to the appropriate method
			switch (theCommand) {
			case "LIST": 
				listStudents(request,response);
				break;

			case "ADD" :
				addStudent(request,response);
				break;
				
			case "LOAD" :
				loadStudent(request,response);
				break;
				
			case "UPDATE" :
				updateStudent(request,response);
				break;
				
			default:
				listStudents(request, response);
				break;
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
	}

	private void updateStudent(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		// read student info from form data
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		//create a new student object
		Student student = new Student(id,firstName, lastName, email);
		
		//perform update on database
		studentDbUtil.updateStudent(student);
		
		//send them back to "list students" page
		listStudents(request, response);
		
	}

	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//read student id from form data
		String theStudentId = request.getParameter("studentId");
		
		//get student from database (db util)
		Student student = studentDbUtil.getStudent(theStudentId);
		
		//place student in request attribute
		request.setAttribute("THE_STUDENT", student);
		
		//send to jsp page: update-student-form
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);
	}

	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// read student info from form data
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		//create a new student object
		Student student = new Student(firstName, lastName, email);
		
		//add the student to the database
		studentDbUtil.addStudent(student);
		
		//send back to main page (the student list)
		listStudents(request, response);
	}

	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//get student from db util 
		List<Student> students = studentDbUtil.getStudents(); 
		
		// add student to the request
		request.setAttribute("STUDENT_LIST", students);

		//send the JSP page (view)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}

}
