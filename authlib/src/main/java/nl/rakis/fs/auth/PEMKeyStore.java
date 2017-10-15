/*
 * Copyright 2017 Bert Laverman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package nl.rakis.fs.auth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PEMKeyStore
{

    private static final Logger log = LogManager.getLogger(PEMKeyStore.class);

    private File path;

    protected PEMKeyStore(File path) {
        this.path = path;
    }

    protected byte[] readData() throws IOException {
        StringBuilder bld = new StringBuilder();

        try (FileReader fr = new FileReader(path);
             BufferedReader br = new BufferedReader(fr))
        {
            String line = br.readLine();
            while ((line != null) && !line.startsWith("-----BEGIN ") && !line.endsWith("-----")) {
                line = br.readLine();
            }
            if (line != null) { // Skip beyond BEGIN
                line = br.readLine();
            }
            while ((line != null) && !line.startsWith("-----END ") && !line.endsWith("-----")) {
                bld.append(line);
                line = br.readLine();
            }
        }
        return Base64.getMimeDecoder().decode(bld.toString());
    }

    protected void storeKey(String keyType, byte[] keyData) {
        try {
            try (FileWriter fw = new FileWriter(path);
                PrintWriter pr = new PrintWriter(fw))
            {
                pr.println("-----BEGIN " + keyType.toUpperCase() + " -----");
                pr.println(Base64.getMimeEncoder().encodeToString(keyData));
                pr.println("-----END " + keyType.toUpperCase() + " -----");
            }
        }
        catch (IOException e) {
            log.error("storeKey(): Exception writing key", e);
        }
    }

    public File getPath() {
        return path;
    }
}