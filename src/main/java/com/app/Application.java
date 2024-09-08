package com.app;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.stream.Collectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Application {

	public static void main(String[] args) {

		String jdbcUrl ="jdbc:postgresql://localhost:5432/mydatabase";
		String username = "postgres";
		String password = "admin123";
		try {
			File myObj = new File("C:\\backu.sql");
			Scanner myReader = new Scanner(myObj);
			ArrayList<String> list = new ArrayList<String>();

			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
			Statement statement = connection.createStatement();
			ResultSet resultSet = null;
			
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				if (data.startsWith("GRANT SELECT ON TABLE")) {
					list.add(data);
					continue;
				}
				if (data.startsWith("GRANT ALL ON TABLE")) {
					list.add(data);
					continue;
				}
				if (data.startsWith("GRANT SELECT,")) {
					list.add(data);
					continue;
				}
				if (data.startsWith("ALTER TABLE public")) {
					list.add(data);
					continue;
				}
				if (data.startsWith("GRANT USAGE ON SCHEMA")) {
					list.add(data);
					continue;
				}
				if (data.startsWith("GRANT ALL ON FUNCTION")) {
					list.add(data);
					continue;
				}				
			}		
			List<String> uniqueList = list.stream().distinct().collect(Collectors.toList());
			for (String item : uniqueList) {
				String[] parts = item.split(" ");
				String role = parts[parts.length - 1];
				role = role.replace("\"", "");
				role = "CREATE ROLE \"" + role.replace(";","") + "\"";
				System.out.println(role);
				try {
					statement.executeUpdate(role);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			statement.close();
			connection.close();
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
