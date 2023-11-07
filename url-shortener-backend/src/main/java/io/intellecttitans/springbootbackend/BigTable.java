package io.intellecttitans.springbootbackend;

import java.util.ArrayList;
import java.util.List;

import com.google.api.gax.rpc.NotFoundException;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.protobuf.ByteString;

public class BigTable {
	String columnFamilyName;
	BigtableDataClient dataClient;
	String projectId;
	String instanceId;
	String tableId;

	public BigTable(String projectId, String instanceId, String tableId, String columnFamilyName) {
		// Initialize client that will be used to send requests. This client only needs
		// to be created once, and can be reused for multiple requests.
		try {
			this.columnFamilyName = columnFamilyName;
			this.projectId = projectId;
			this.instanceId = instanceId;
			this.tableId = tableId;
			BigtableDataSettings settings = BigtableDataSettings.newBuilder().setProjectId(projectId)
					.setInstanceId(instanceId).build();
			this.dataClient = BigtableDataClient.create(settings);
			System.out.println("Created data client");

		} catch (Exception e) {
			System.out.println("Error during client creation: \n" + e.toString());
		}

	}

	public static void main(String... args) {
		BigTable bigTable = new BigTable("rice-comp-539-spring-2022", "rice-shared", "team2_urlshortener_urls",
				"short_to_long_url");

		// Dummy testing code
		List<String> subFamily = new ArrayList<>();
		subFamily.add("long_url");
		subFamily.add("created");

		List<String> value = new ArrayList<>();
		value.add("www.temp.com");
		value.add("5_Nov");

//		bigTable.writeRow(value, subFamily, "temp.com");
		bigTable.getRow("temp.com");
//		bigTable.rowExists("temp.com");
//		System.out.println("done");
//		bigTable.rowExists("a.com");
	}

	public void getRow(String rowKey) {

		try {
			if (!this.rowExists(rowKey)) {
				return;
			}
			Row row = dataClient.readRow(tableId, rowKey);
			System.out.println("Row: " + row.getKey().toStringUtf8());
			for (RowCell cell : row.getCells()) {
				System.out.printf("Family: %s    Qualifier: %s    Value: %s%n", cell.getFamily(),
						cell.getQualifier().toStringUtf8(), cell.getValue().toStringUtf8());
			}

		} catch (NotFoundException e) {
			System.err.println("Failed to read from a non-existent table: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error during reading rows: \n" + e.toString());
		}
	}

	public boolean rowExists(String rowKey) {

		try {
			Row row = dataClient.readRow(tableId, rowKey);
			System.out.println("Row: " + row.getKey().toStringUtf8() + "exists");
			return true;
		} catch (Exception e) {
			System.err.println("Row" + rowKey +" does not exist " + e.getMessage());
			return false;
		}
	}

	public void writeRow(List<String> value, List<String> subFamily, String rowKey) {
		try {
			long timestamp = System.currentTimeMillis() * 1000;

			RowMutation rowMutation = RowMutation.create(tableId, rowKey);

			for (int i = 0; i < subFamily.size(); i++) {
				rowMutation.setCell(columnFamilyName, subFamily.get(i), value.get(i));
			}

			dataClient.mutateRow(rowMutation);
			System.out.printf("Successfully wrote row %s", rowKey);

		} catch (Exception e) {
			System.out.println("Error during WriteSimple: \n" + e.toString());
		}
	}

	@Override
	protected void finalize() throws Throwable {
		// Call the "close" method on the client to safely clean up any remaining
		// background
		// resources.
		try {
			this.dataClient.close();
			System.out.println("Closing data client");
		} finally {
			super.finalize();
		}
	}
}