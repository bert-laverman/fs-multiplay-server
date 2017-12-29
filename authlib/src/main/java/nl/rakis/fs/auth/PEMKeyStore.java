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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;

public class PEMKeyStore
{

    private static final Logger log = LogManager.getLogger(PEMKeyStore.class);

    private File path;

    protected PEMKeyStore(File path) {
        this.path = path;
    }

    protected byte[] readData() throws IOException {
        try (FileReader fr = new FileReader(path);
             PemReader pr = new PemReader(fr))
        {
            PemObject po = pr.readPemObject();
            return po.getContent();
        }
    }

    protected void storeKey(byte[] key, String description) {
        try {
            try (FileWriter fw = new FileWriter(path);
                 PemWriter pw = new PemWriter(fw))
            {
                pw.writeObject(new PemObject(description, key));
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