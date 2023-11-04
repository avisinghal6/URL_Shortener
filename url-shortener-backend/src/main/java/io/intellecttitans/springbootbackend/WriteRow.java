package io.intellecttitans.springbootbackend;


import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.protobuf.ByteString;

public class WriteRow {
  private static final String COLUMN_FAMILY_NAME = "cf1";
  
  public static void main(String... args) {
//    String projectId = args[0]; // my-gcp-project-id
//    String instanceId = args[1]; // my-bigtable-instance-id
//    String tableId = args[2]; // my-bigtable-table-id
//    
    String projectId="rice-comp-539-spring-2022";
    String instanceId="rice-shared";
    String tableId = "team2_urlshortener_urls";

    writeSimple(projectId, instanceId, tableId);
  }
  public static void writeSimple(String projectId, String instanceId, String tableId) {
    // String projectId = "my-project-id";
    // String instanceId = "my-instance-id";
    // String tableId = "mobile-time-series";

    try (BigtableDataClient dataClient = BigtableDataClient.create(projectId, instanceId)) {
      long timestamp = System.currentTimeMillis() * 1000;

      String rowkey = "r1";

      RowMutation rowMutation =
          RowMutation.create(tableId, rowkey)
              .setCell(
                  COLUMN_FAMILY_NAME,
                  ByteString.copyFrom("c1".getBytes()),
                  timestamp,
                  1)
              .setCell(
                  COLUMN_FAMILY_NAME,
                  ByteString.copyFrom("c2".getBytes()),
                  timestamp,
                  1)
              .setCell(COLUMN_FAMILY_NAME, "os_build", timestamp, "PQ2A.190405.003")
              .setCell(COLUMN_FAMILY_NAME, "c1", "avi");

      dataClient.mutateRow(rowMutation);
      System.out.printf("Successfully wrote row %s", rowkey);

    } catch (Exception e) {
      System.out.println("Error during WriteSimple: \n" + e.toString());
    }
  }
}