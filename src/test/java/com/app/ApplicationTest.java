package com.app;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApplicationTest {

    private Application application;
    private Statement statement;

    @BeforeEach
    public void setUp() {
        application = new Application();
        statement = mock(Statement.class);
    }

    @Test
    public void testProcessData() {
        String inputData = "GRANT USAGE ON SCHEMA public TO role1;\nGRANT ALL ON FUNCTION func() TO role2;";
        Scanner myReader = new Scanner(new StringReader(inputData));

        List<String> result = application.processData(myReader);

        assertEquals(2, result.size());
        assertEquals("GRANT USAGE ON SCHEMA public TO role1;", result.get(0));
        assertEquals("GRANT ALL ON FUNCTION func() TO role2;", result.get(1));
    }

    @Test
    public void testExecuteRoles() throws SQLException {
        List<String> list = Arrays.asList("GRANT USAGE ON SCHEMA public TO role1;", "GRANT ALL ON FUNCTION func() TO role2;");

        application.executeRoles(list, statement);

        verify(statement, times(1)).executeUpdate("CREATE ROLE \"role1\"");
        verify(statement, times(1)).executeUpdate("CREATE ROLE \"role2\"");
    }

    @Test
    public void testCloseResources() {
        AutoCloseable resource1 = mock(AutoCloseable.class);
        AutoCloseable resource2 = mock(AutoCloseable.class);

        application.closeResources(resource1, resource2);

        try {
            verify(resource1, times(1)).close();
            verify(resource2, times(1)).close();
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }
}