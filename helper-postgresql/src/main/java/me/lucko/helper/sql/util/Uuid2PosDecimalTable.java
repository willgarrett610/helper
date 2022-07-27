/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.helper.sql.util;

import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.sql.Sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * A wrapper around a table storing a single positive {@link BigDecimal} per {@link UUID} key.
 */
public class Uuid2PosDecimalTable extends Uuid2PosNumberTable<BigDecimal, Optional<BigDecimal>> {
    public Uuid2PosDecimalTable(Sql sql, String table) {
        super(sql, table);
    }

    @Override
    protected String getCreateStmt() {
        return "CREATE TABLE IF NOT EXISTS `{table}` (" +
                "`uuid` VARCHAR(36) NOT NULL, " +
                "`value` DECIMAL(19) UNSIGNED NOT NULL, " +
                "PRIMARY KEY (`uuid`))";
    }

    @Override
    protected void set(PreparedStatement ps, int paramIndex, BigDecimal value) throws SQLException {
        ps.setBigDecimal(paramIndex, value);
    }

    @Override
    protected BigDecimal get(ResultSet rs, String columnLabel) throws SQLException {
        return rs.getBigDecimal(columnLabel);
    }

    @Override
    protected Optional<BigDecimal> getOptional(ResultSet rs, String columnLabel) throws SQLException {
        return Optional.of(rs.getBigDecimal(columnLabel));
    }

    @Override
    protected Optional<BigDecimal> emptyOptional() {
        return Optional.empty();
    }

    /**
     * Adds the specified {@code amount}.
     *
     * @param uuid the uuid
     * @param amount the amount to add
     * @return a promise encapsulating the operation
     */
    public Promise<Void> add(UUID uuid, BigDecimal amount) {
        Objects.requireNonNull(uuid, "uuid");
        if (amount.equals(BigDecimal.ZERO)) {
            return Promise.completed(null);
        }
        if (amount.signum() == -1) {
            throw new IllegalArgumentException("amount < 0");
        }

        return doAdd(uuid, amount);
    }

    /**
     * Sets the specified {@code amount}.
     *
     * @param uuid the uuid
     * @param amount the amount to set
     * @return a promise encapsulating the operation
     */
    public Promise<Void> set(UUID uuid, BigDecimal amount) {
        Objects.requireNonNull(uuid, "uuid");
        if (amount.signum() == -1) {
            throw new IllegalArgumentException("amount < 0");
        }

        return doSet(uuid, amount);
    }

    /**
     * Tries to take the specified {@code amount}, failing if the amount would become negative.
     *
     * @param uuid the uuid
     * @param amount the amount to take
     * @return true if successful
     */
    public Promise<Boolean> take(UUID uuid, BigDecimal amount) {
        Objects.requireNonNull(uuid, "uuid");
        if (amount.equals(BigDecimal.ZERO)) {
            return Promise.completed(null);
        }
        if (amount.signum() == -1) {
            throw new IllegalArgumentException("amount < 0");
        }

        return doTake(uuid, amount);
    }

    /**
     * Gets the total of all values in the table.
     *
     * @return the total
     */
    public Promise<BigDecimal> total() {
        return Schedulers.async().call(() -> {
            try (Connection c = sql.getConnection()) {
                try (PreparedStatement ps = c.prepareStatement(SELECT_TOTAL.replace("{table}", table))) {
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getBigDecimal("total");
                        } else {
                            return BigDecimal.ZERO;
                        }
                    }
                }
            }
        });
    }

}