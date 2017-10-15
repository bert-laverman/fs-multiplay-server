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
import org.junit.jupiter.api.DisplayName;

@DisplayName("Test for handling the BASIC auth header")
public class TestBasicAuthUtil
{

    public static final String DUMMY_BASIC_AUTH = "Basic QWxhZGRpbjpPcGVuU2VzYW1l";
    public static final String DUMMY_USERNAME = "Aladdin";
    public static final String DUMMY_PASSWORD = "OpenSesame";

    @Test
    public void testHeaderDecode()
    {
        BasicAuth ba = BasicAuthUtil.decodeAuthorizationHeader(DUMMY_BASIC_AUTH);
        Assert.assertEquals("Failed extracting the username", DUMMY_USERNAME, ba.username);
        Assert.assertEquals("Failed extracting the password", DUMMY_PASSWORD, ba.password);
    }
}