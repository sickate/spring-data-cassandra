package org.springframework.data.cassandra.test.integration;

import static org.springframework.cassandra.core.keyspace.DropTableSpecification.dropTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.test.integration.AbstractEmbeddedCassandraIntegrationTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.test.integration.support.SpringDataBuildProperties;
import org.springframework.data.cassandra.test.integration.template.CassandraDataOperationsTest.Config;
import org.springframework.util.Assert;

import com.datastax.driver.core.TableMetadata;

public class AbstractSpringDataEmbeddedCassandraIntegrationTest extends AbstractEmbeddedCassandraIntegrationTest {

	public static List<String> SCRIPT;
	public static List<TableMetadata> TABLES;

	static {
		SpringDataBuildProperties props = new SpringDataBuildProperties();
		CASSANDRA_NATIVE_PORT = props.getCassandraPort();
	}

	public Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public CassandraOperations template;

	/**
	 * Saves all table metadata, then drops & creates all tables.
	 */
	public void recreateAllTables() {

		saveAllTableMetadata();

		for (TableMetadata table : TABLES) {
			template.execute(dropTable(table.getName()));
			template.execute(table.asCQLQuery());
		}
	}

	public void saveAllTableMetadata() {
		saveAllTableMetadata(false);
	}

	/**
	 * Saves all table metadata statically.
	 */
	public void saveAllTableMetadata(boolean force) {

		if (TABLES != null && !force) {
			return;
		}

		TABLES = new ArrayList<TableMetadata>(template.getSession().getCluster().getMetadata()
				.getKeyspace(Config.KEYSPACE_NAME).getTables());
	}

	public List<String> readScriptLines(String resourceName) {
		return readScriptLines(resourceName);
	}

	/**
	 * Reads the lines from the script referenced by {@link #RESOURCE}.
	 */
	public List<String> readScriptLines(String resourceName, boolean force) throws IOException {

		if (SCRIPT != null && !force) {
			return SCRIPT;
		}

		Assert.hasText(resourceName);

		return SCRIPT = FileUtils.readLines(new ClassPathResource(resourceName).getFile());
	}
}
