package com.iacn.falsebattery;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by iAcn on 2018/3/11
 * Email i@iacn.me
 */

public class RootUtils {

    public static void exec(String command) {
        Process process = null;
        DataOutputStream stream = null;

        try {
            process = Runtime.getRuntime().exec("su");
            stream = new DataOutputStream(process.getOutputStream());
            command += "\n";
            stream.writeBytes(command);
            stream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) process.destroy();

            try {
                if (stream != null) stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}