/*
 * Copyright 2016, 2017 Bert Laverman
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
package nl.rakis.fs.api.rules;

import nl.rakis.fs.auth.Token;
import nl.rakis.fs.info.FSData;

import javax.json.JsonObject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;

public abstract class ApiRules<T extends FSData> {

    public abstract void cleanRecord(T rec, Token token) throws NotAuthorizedException;

    public abstract void checkUpdate(T rec, JsonObject obj, Token token) throws NotAuthorizedException, BadRequestException;

    public abstract void checkCreate(JsonObject obj, Token token) throws NotAuthorizedException, BadRequestException;

    protected void required(JsonObject obj, String field) throws BadRequestException {
        if (obj.isNull(field)) {
            throw new BadRequestException("Missing field \"" + field + "\"");
        }
    }
}
