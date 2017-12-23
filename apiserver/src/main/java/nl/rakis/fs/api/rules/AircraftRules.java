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
import nl.rakis.fs.info.AircraftInfo;
import nl.rakis.fs.info.JsonFields;

import javax.json.JsonObject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;

public class AircraftRules extends ApiRules<AircraftInfo>
{
    @Override
    public void cleanRecord(AircraftInfo rec, Token token) throws NotAuthorizedException {
        // No secrets here
    }

    @Override
    public void checkUpdate(AircraftInfo rec, JsonObject obj, Token token) throws NotAuthorizedException, BadRequestException {
        if (!token.getUsername().equals(rec.getUsername())) {
            throw new NotAuthorizedException("Not your aircraft");
        }
        if (!obj.isNull(JsonFields.FIELD_USERNAME) && !rec.getUsername().equals(obj.getString(JsonFields.FIELD_USERNAME))) {
            throw new BadRequestException("Cannot give away aircraft");
        }
        if (!obj.isNull(JsonFields.FIELD_ATC_ID) && !rec.getAtcId().equals(obj.getString(JsonFields.FIELD_ATC_ID))) {
            throw new BadRequestException("Cannot change ATC Id (callsign)");
        }
    }

    @Override
    public void checkCreate(JsonObject obj, Token token) throws NotAuthorizedException, BadRequestException {
        required(obj, JsonFields.FIELD_USERNAME);
        if (!token.getUsername().equals(obj.getString(JsonFields.FIELD_USERNAME))) {
            throw new NotAuthorizedException("Cannot create other people's aircraft");
        }
        required(obj, JsonFields.FIELD_TITLE);
        required(obj, JsonFields.FIELD_ATC_ID);
    }
}
