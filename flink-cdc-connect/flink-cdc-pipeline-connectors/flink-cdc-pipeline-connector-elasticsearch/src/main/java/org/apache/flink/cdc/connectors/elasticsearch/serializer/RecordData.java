/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.cdc.connectors.elasticsearch.serializer;

import java.math.BigDecimal;
import java.time.*;
import java.util.Date;
import java.util.Map;

/**
 * A class representing a record with multiple fields of various types. Provides methods to access
 * fields by position and type.
 */
public class RecordData {
    private final Object[] fields;

    /**
     * Constructs a RecordData object with the given fields.
     *
     * @param fields An array of Objects representing the fields of the record.
     */
    public RecordData(Object[] fields) {
        this.fields = fields;
    }

    /** @return The number of fields in this record. */
    public int getArity() {
        return fields.length;
    }

    /**
     * Checks if the field at the given position is null.
     *
     * @param pos The position of the field.
     * @return true if the field is null, false otherwise.
     */
    public boolean isNullAt(int pos) {
        return fields[pos] == null;
    }

    /**
     * Gets the String value at the given position.
     *
     * @param pos The position of the field.
     * @return The String value at the given position.
     */
    public String getString(int pos) {
        return (String) fields[pos];
    }

    /**
     * Gets the Integer value at the given position.
     *
     * @param pos The position of the field.
     * @return The Integer value at the given position.
     */
    public Integer getInt(int pos) {
        return (Integer) fields[pos];
    }

    /**
     * Gets the Date value at the given position.
     *
     * @param pos The position of the field.
     * @return The Date value at the given position.
     */
    public Date getDate(int pos) {
        return (Date) fields[pos];
    }

    /**
     * Gets the LocalDate value at the given position.
     *
     * @param pos The position of the field.
     * @return The LocalDate value at the given position.
     * @throws IllegalArgumentException if the field is not a supported date type.
     */
    public LocalDate getLocalDate(int pos) {
        Object obj = fields[pos];
        if (obj instanceof Date) {
            return ((Date) obj).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (obj instanceof LocalDate) {
            return (LocalDate) obj;
        }
        throw new IllegalArgumentException("Unsupported date type: " + obj.getClass());
    }

    /**
     * Gets the LocalTime value at the given position.
     *
     * @param pos The position of the field.
     * @return The LocalTime value at the given position.
     * @throws IllegalArgumentException if the field is not a supported time type.
     */
    public LocalTime getLocalTime(int pos) {
        Object obj = fields[pos];
        if (obj instanceof Date) {
            return ((Date) obj).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        } else if (obj instanceof LocalTime) {
            return (LocalTime) obj;
        }
        throw new IllegalArgumentException("Unsupported time type: " + obj.getClass());
    }

    /**
     * Gets the LocalDateTime value at the given position.
     *
     * @param pos The position of the field.
     * @return The LocalDateTime value at the given position.
     * @throws IllegalArgumentException if the field is not a supported datetime type.
     */
    public LocalDateTime getLocalDateTime(int pos) {
        Object obj = fields[pos];
        if (obj instanceof Date) {
            return ((Date) obj).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        throw new IllegalArgumentException("Unsupported datetime type: " + obj.getClass());
    }

    /**
     * Gets the ZonedDateTime value at the given position.
     *
     * @param pos The position of the field.
     * @return The ZonedDateTime value at the given position.
     * @throws IllegalArgumentException if the field is not a supported zoned datetime type.
     */
    public ZonedDateTime getZonedDateTime(int pos) {
        Object obj = fields[pos];
        if (obj instanceof Date) {
            return ((Date) obj).toInstant().atZone(ZoneId.systemDefault());
        } else if (obj instanceof ZonedDateTime) {
            return (ZonedDateTime) obj;
        }
        throw new IllegalArgumentException("Unsupported zoned datetime type: " + obj.getClass());
    }

    /**
     * Gets the OffsetDateTime value at the given position.
     *
     * @param pos The position of the field.
     * @return The OffsetDateTime value at the given position.
     * @throws IllegalArgumentException if the field is not a supported offset datetime type.
     */
    public OffsetDateTime getOffsetDateTime(int pos) {
        Object obj = fields[pos];
        if (obj instanceof Date) {
            return ((Date) obj).toInstant().atOffset(ZoneOffset.UTC);
        } else if (obj instanceof OffsetDateTime) {
            return (OffsetDateTime) obj;
        }
        throw new IllegalArgumentException("Unsupported offset datetime type: " + obj.getClass());
    }

    /**
     * Gets the Boolean value at the given position.
     *
     * @param pos The position of the field.
     * @return The Boolean value at the given position.
     */
    public Boolean getBoolean(int pos) {
        return (Boolean) fields[pos];
    }

    /**
     * Gets the Double value at the given position.
     *
     * @param pos The position of the field.
     * @return The Double value at the given position.
     */
    public Double getDouble(int pos) {
        return (Double) fields[pos];
    }

    /**
     * Gets the Float value at the given position.
     *
     * @param pos The position of the field.
     * @return The Float value at the given position.
     */
    public Float getFloat(int pos) {
        return (Float) fields[pos];
    }

    /**
     * Gets the Long value at the given position.
     *
     * @param pos The position of the field.
     * @return The Long value at the given position.
     */
    public Long getLong(int pos) {
        return (Long) fields[pos];
    }

    /**
     * Gets the Byte value at the given position.
     *
     * @param pos The position of the field.
     * @return The Byte value at the given position.
     */
    public Byte getByte(int pos) {
        return (Byte) fields[pos];
    }

    /**
     * Gets the Short value at the given position.
     *
     * @param pos The position of the field.
     * @return The Short value at the given position.
     */
    public Short getShort(int pos) {
        return (Short) fields[pos];
    }

    /**
     * Gets the binary value at the given position.
     *
     * @param pos The position of the field.
     * @return The binary value as a byte array at the given position.
     */
    public byte[] getBinary(int pos) {
        return (byte[]) fields[pos];
    }

    /**
     * Gets the BigDecimal value at the given position.
     *
     * @param pos The position of the field.
     * @param precision The precision of the decimal (not used in this implementation).
     * @param scale The scale of the decimal (not used in this implementation).
     * @return The BigDecimal value at the given position.
     */
    public BigDecimal getDecimal(int pos, int precision, int scale) {
        return (BigDecimal) fields[pos];
    }

    /**
     * Gets the array value at the given position.
     *
     * @param pos The position of the field.
     * @return The array value as an Object array at the given position.
     */
    public Object[] getArray(int pos) {
        return (Object[]) fields[pos];
    }

    /**
     * Gets the map value at the given position.
     *
     * @param pos The position of the field.
     * @return The map value at the given position.
     */
    @SuppressWarnings("unchecked")
    public Map<Object, Object> getMap(int pos) {
        return (Map<Object, Object>) fields[pos];
    }

    /**
     * Gets a subset of fields as a new RecordData object.
     *
     * @param pos The starting position of the subset.
     * @param numFields The number of fields to include in the subset.
     * @return A new RecordData object containing the specified subset of fields.
     */
    public RecordData getRow(int pos, int numFields) {
        Object[] rowFields = new Object[numFields];
        System.arraycopy(fields, pos, rowFields, 0, numFields);
        return new RecordData(rowFields);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RecordData{");
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(fields[i]);
        }
        sb.append("}");
        return sb.toString();
    }
}
