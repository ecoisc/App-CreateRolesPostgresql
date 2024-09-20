package com.app;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {

	public List<String> processData(Scanner myReader) {
		List<String> list = new ArrayList<>();
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			if (data.startsWith("GRANT SELECT ON TABLE") ||
					data.startsWith("GRANT ALL ON TABLE") ||
					data.startsWith("GRANT SELECT") ||
					data.startsWith("ALTER TABLE public") ||
					data.startsWith("GRANT USAGE ON SCHEMA") ||
					data.startsWith("GRANT ALL ON FUNCTION")) {
				list.add(data);
			}
		}
		 // Eliminar duplicados
		 Set<String> set = new HashSet<>(list);
		 List<String> uniqueList = new ArrayList<>(set);
		return uniqueList;
	}

	public void executeRoles(List<String> list, Statement statement) throws SQLException {
		for (String item : list) {
			String[] parts = item.split(" ");
			String role = parts[parts.length - 1];
			role = role.replace("\"", "");
			role = "CREATE ROLE \"" + role.replace(";", "") + "\"";
			System.out.println(role);
			try {
				statement.executeUpdate(role);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeResources(AutoCloseable... resources) {
		for (AutoCloseable resource : resources) {
			if (resource != null) {
				try {
					resource.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		Application app = new Application();
		Scanner myReader = null;
		Connection connection = null;
		Statement statement = null;

		String jdbcUrl ="jdbc:postgresql://localhost:5432/mydatabasehere";
		String username = "myuserhere";
		String password = "mypasswordhere";

		try {
			File myObj = new File("C:\\backuphere.sql");
			myReader = new Scanner(myObj);

			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(jdbcUrl, username, password);
			statement = connection.createStatement();

			List<String> list = app.processData(myReader);
			app.executeRoles(list, statement);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			app.closeResources(statement, connection, myReader);
		}
	}
}
