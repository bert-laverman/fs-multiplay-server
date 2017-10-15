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

import org.junit.Assert;
import org.junit.Test;

public class TestPasswordStorage {

    @Test
    public void testCreateHash()
    {
        String password = "password";
        final String hash;
        try {
            hash = PasswordStorage.createHash(password);
            PasswordStorage.verifyPassword(password, hash);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            Assert.fail("Failed to create hash");
        } catch (PasswordStorage.InvalidHashException e) {
            Assert.fail("Failed to verify hash");
        }
    }
}
