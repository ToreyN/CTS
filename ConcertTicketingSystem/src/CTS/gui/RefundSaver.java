package CTS.gui;

import CTS.misc.RefundRequest;

import java.io.*;
import java.nio.file.Path;

public class RefundSaver {

    public static void appendRefund(RefundRequest rr, Path path) throws IOException {
        boolean exists = path.toFile().exists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(path.toFile(), true))) {

            // Header if file did not exist
            if (!exists) {
                pw.println("# refundId,orderId,createdAtMillis,reason,status,adminUserId");
            }

            pw.println(rr.toCsvRow());
        }
    }
}
