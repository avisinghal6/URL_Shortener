package io.intellecttitans.springbootbackend.configurations;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.google.api.gax.rpc.NotFoundException;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserTable {
	@Value("${userTable.columnFamilyName}")
	String columnFamilyName;

	@Value("${userTable.projectId}")
	String projectId;

	@Value("${userTable.instanceId}")
	String instanceId;

	@Value("${userTable.tableId}")
	String tableId;

	BigtableDataClient dataClient;

	@PostConstruct
	public void init() {
		try {
			BigtableDataSettings settings = BigtableDataSettings.newBuilder().setProjectId(projectId)
					.setInstanceId(instanceId).build();
			dataClient = BigtableDataClient.create(settings);
			System.out.println("Created data client for user table");
		} catch (Exception e) {
			System.err.println("Error during client creation for user table: \n" + e.toString());
		}
	}


	public List<String> getRow(String rowKey) {

		try {
			if (!rowExists(rowKey)) {
				return null;
			}
			Row row = dataClient.readRow(tableId, rowKey);
			System.out.println("Row: " + row.getKey().toStringUtf8());
			List<String> data= new ArrayList<>();
			
			for (RowCell cell : row.getCells()) {
				System.out.printf("Family: %s    Qualifier: %s    Value: %s%n", cell.getFamily(),
						cell.getQualifier().toStringUtf8(), cell.getValue().toStringUtf8());
				data.add(cell.getValue().toStringUtf8());
			}
			
			return data;

		} catch (NotFoundException e) {
			System.err.println("Failed to read from a non-existent table: " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.err.println("Error during reading rows: \n" + e.toString());
			return null;
		}
	}

	public boolean rowExists(String rowKey) {

		try {
			Row row = dataClient.readRow(tableId, rowKey);
			System.out.println("Row: " + row.getKey().toStringUtf8() + "exists");
			return true;
		} catch (Exception e) {
			System.err.println("Row " + rowKey + " does not exist " + e.getMessage());
			return false;
		}
	}

	public boolean writeRow(List<String> value, List<String> subFamily, String rowKey) {
		try {

			RowMutation rowMutation = RowMutation.create(tableId, rowKey);

			for (int i = 0; i < subFamily.size(); i++) {
				rowMutation.setCell(columnFamilyName, subFamily.get(i), value.get(i));
			}

			dataClient.mutateRow(rowMutation);
			System.out.printf("Successfully wrote row %s", rowKey);
			return true;

		} catch (Exception e) {
			System.err.println("Error during WriteSimple: \n" + e.toString());
			return false;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		// Call the "close" method on the client to safely clean up any remaining
		// background
		// resources.
		try {
			dataClient.close();
			System.out.println("Closing data client for user table");
		} finally {
			super.finalize();
		}
	}
}