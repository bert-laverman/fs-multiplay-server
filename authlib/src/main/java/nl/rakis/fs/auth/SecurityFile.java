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

import java.io.*;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class SecurityFile
{

    private static final Logger log = LogManager.getLogger(SecurityFile.class);

    protected void checkEmpty(String fldName, String fld) throws IOException {
        if ((fld != null) && (fld.length() > 0) && !fld.equals("x") && !fld.equals("*")) {
            throw new IOException(fldName + " should be empty: \"" + fld + "\"");
        }
    }

    protected void checkNonEmpty(String fldName, String fld) throws IOException {
        if ((fld == null) || (fld.length() == 0)) {
            throw new IOException(fldName + " should not be empty: \"" + fld + "\"");
        }
    }

    protected String getTimestamp() {
        return String.format("%1$tY%1$tm%1$td-%1$tH%1$tM%1$tS.%1$tL", GregorianCalendar.getInstance());
    }

    protected boolean store(String entities, File path, Consumer<PrintWriter> doWrite)
    {
        if (log.isInfoEnabled()) {
            log.info("store(): (Re)writing " + entities + " in \"" + path.getAbsolutePath() + "\"");
        }

        boolean result = false;

        File dir = path.getParentFile();
        File tmpOut = null;
        try {
            tmpOut = File.createTempFile("tmp_", getTimestamp(), dir);
            if (log.isDebugEnabled()) {
                log.debug("store(): Creating \"" + tmpOut.getAbsolutePath() + "\" and writing " + entities);
            }
            try (FileWriter wr = new FileWriter(tmpOut);
                 PrintWriter pr = new PrintWriter(wr))
            {
                doWrite.accept(pr);
            }
        } catch (IOException e) {
            log.error("store(): Failed to create temp file", e);
        }
        if ((tmpOut != null) && tmpOut.exists()) {
            File backup = new File(path.getAbsolutePath() + "_" + getTimestamp());
            if (log.isDebugEnabled()) {
                log.debug("store(): Creating backup \"" + backup.getAbsolutePath() + "\"");
            }
            try {
                if (!path.renameTo(backup)) {
                    log.error("store(): Cannot rename " + path.getAbsolutePath() + " to " + backup.getAbsolutePath());
                }
                else {
                    if (log.isDebugEnabled()) {
                        log.debug("store(): Moving temporary file to \"" + path.getAbsolutePath() + "\"");
                    }
                    if (!tmpOut.renameTo(path)) {
                        log.error("store(): Cannot rename " + tmpOut.getAbsolutePath() + " to " + path.getAbsolutePath());
                        // Ok, we're getting desperate here
                        if (!backup.renameTo(path)) {
                            log.fatal("store(): Renamed original file, failed to rename new version, now failed to restore original!");
                        }
                    }
                    else { // Ok, we're done
                        if (log.isInfoEnabled()) {
                            log.info("store(): Successfully updated " + entities);
                        }
                        result = true;
                    }
                }
            }
            catch (SecurityException e) {
                log.fatal("store(): Not enough rights on filesystem", e);
            }
        }
        else {
            log.error("Failed to write " + entities);
        }
        return result;
    }

    protected void load(File path, Predicate<String> doParse)
    {
        log.trace("load()");

        if (log.isDebugEnabled()) {
            log.debug("load(): Reading \"" + path + "\"");
        }
        try {
            try (FileReader fr = new FileReader(path);
                 BufferedReader br=new BufferedReader(fr))
            {
                if (log.isTraceEnabled()) {
                    log.trace("load(): Userlist \"" + path + "\" opened");
                }
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    if (log.isTraceEnabled()) {
                        log.trace("load(): Read \"" + line + "\"");
                    }

                    if (doParse.test(line)) {
                        break;
                    }
                }
            }
        }
        catch (IOException e) {
            log.error("load(): Failed to load \"" + path + "\"", e);
        }
    }

}
