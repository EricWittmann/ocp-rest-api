package io.apicurio.ocprestapi.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@ApplicationScoped
@Path("/foo")
public class FooEndpoint {
	
	@Resource(mappedName="java:jboss/datasources/OcpDS")
    private DataSource dataSource;

	@PostConstruct
	private void onPostConstruct() {
		System.out.println("Initializing database using DS: " + dataSource);
		try (Connection conn = dataSource.getConnection()) {
			System.out.println("Connection: " + conn);
			PreparedStatement statement = conn.prepareStatement("CREATE TABLE foo (id BIGINT NOT NULL, name VARCHAR(255) NOT NULL, description VARCHAR(2048))");
			statement.executeUpdate();
			statement = conn.prepareStatement("ALTER TABLE foo ADD PRIMARY KEY (id)");
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@GET
	@Produces("application/json")
	public List<FooBean> doGet() {
		System.out.println("Getting a list of all Foos");
		List<FooBean> rval = new ArrayList<>();
		try (Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM foo");
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				FooBean foo = new FooBean();
				foo.setId(resultSet.getLong("id"));
				foo.setName(resultSet.getString("name"));
				foo.setDescription(resultSet.getString("description"));
				rval.add(foo);
			}
			return rval;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public FooBean addFoo(FooBean foo) {
		System.out.println("Adding a foo: " + foo);
		try (Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("INSERT INTO foo (id, name, description) VALUES (?, ?, ?)");
			long id = System.currentTimeMillis();
			statement.setLong(1, id);
			statement.setString(2, foo.getName());
			statement.setString(3, foo.getDescription());
			statement.executeUpdate();
			foo.setId(id);
			return foo;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}