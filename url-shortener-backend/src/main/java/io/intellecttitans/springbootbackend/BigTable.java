package io.intellecttitans.springbootbackend;

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
			System.out.println("Error during quickstart: \n" + e.toString());
		}

	}

	public static void main(String... args) {
		BigTable bigTable = new BigTable("rice-comp-539-spring-2022", "rice-shared", "team2_urlshortener_urls", "cf1");

		// Dummy testing code
		bigTable.writeRow("new data", "c1", "r1");
		bigTable.getRow("r1");
	}

	public void getRow(String rowKey) {

		try {
			Row row = dataClient.readRow(tableId, rowKey);
			System.out.println("Row: " + row.getKey().toStringUtf8());
			for (RowCell cell : row.getCells()) {
				System.out.printf("Family: %s    Qualifier: %s    Value: %s%n", cell.getFamily(),
						cell.getQualifier().toStringUtf8(), cell.getValue().toStringUtf8());
			}

		} catch (NotFoundException e) {
			System.err.println("Failed to read from a non-existent table: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error during quickstart: \n" + e.toString());
		}
	}

	public void writeRow(String value, String subfamily, String rowKey) {
		try {
			long timestamp = System.currentTimeMillis() * 1000;

			RowMutation rowMutation = RowMutation.create(tableId, rowKey)
					.setCell(columnFamilyName, subfamily, value);

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