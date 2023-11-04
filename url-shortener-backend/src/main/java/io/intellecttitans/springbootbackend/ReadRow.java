package io.intellecttitans.springbootbackend;


import com.google.api.gax.rpc.NotFoundException;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;

public class ReadRow {

  public static void main(String... args) {
//    String projectId = args[0]; // my-gcp-project-id
//    String instanceId = args[1]; // my-bigtable-instance-id
//    String tableId = args[2]; // my-bigtable-table-id
//    
    String projectId="rice-comp-539-spring-2022";
    String instanceId="rice-shared";
    String tableId = "team2_urlshortener_urls";

    ReadRow(projectId, instanceId, tableId);
  }

  public static void ReadRow(String projectId, String instanceId, String tableId) {
    BigtableDataSettings settings =
        BigtableDataSettings.newBuilder().setProjectId(projectId).setInstanceId(instanceId).build();

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (BigtableDataClient dataClient = BigtableDataClient.create(settings)) {
      System.out.println("\nReading a single row by row key");
      Row row = dataClient.readRow(tableId, "r1");
      System.out.println("Row: " + row.getKey().toStringUtf8());
      for (RowCell cell : row.getCells()) {
        System.out.printf(
            "Family: %s    Qualifier: %s    Value: %s%n",
            cell.getFamily(), cell.getQualifier().toStringUtf8(), cell.getValue().toStringUtf8());
      }
    } catch (NotFoundException e) {
      System.err.println("Failed to read from a non-existent table: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Error during quickstart: \n" + e.toString());
    }
  }
}