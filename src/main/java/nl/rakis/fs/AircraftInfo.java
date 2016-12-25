/*
 * Copyright 2016 Bert Laverman
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
package nl.rakis.fs;

/**
 * Created by bertl on 11/19/2016.
 */
public class AircraftInfo {
    private String title;
    private String atcId;
    private String atcModel;
    private String atcType;
    private String atcAirline;
    private String atcFlightNumber;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAtcId() {
        return atcId;
    }

    public void setAtcId(String atcId) {
        this.atcId = atcId;
    }

    public String getAtcModel() {
        return atcModel;
    }

    public void setAtcModel(String atcModel) {
        this.atcModel = atcModel;
    }

    public String getAtcType() {
        return atcType;
    }

    public void setAtcType(String atcType) {
        this.atcType = atcType;
    }

    public String getAtcAirline() {
        return atcAirline;
    }

    public void setAtcAirline(String atcAirline) {
        this.atcAirline = atcAirline;
    }

    public String getAtcFlightNumber() {
        return atcFlightNumber;
    }

    public void setAtcFlightNumber(String atcFlightNumber) {
        this.atcFlightNumber = atcFlightNumber;
    }
}
