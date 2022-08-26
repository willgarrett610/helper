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

package me.lucko.helper.postgresql.plugin;

import me.lucko.helper.internal.HelperImplementationPlugin;
import me.lucko.helper.maven.MavenLibrary;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.postgresql.DatabaseCredentials;
import me.lucko.helper.postgresql.Sql;
import me.lucko.helper.postgresql.SqlProvider;

import javax.annotation.Nonnull;

@HelperImplementationPlugin
@MavenLibrary(groupId = "org.slf4j", artifactId = "slf4j-api", version = "1.7.30")
public class HelperSqlPlugin extends ExtendedJavaPlugin implements SqlProvider {
    private DatabaseCredentials globalCredentials;

    @Override
    protected void enable() {
        // expose all instances as services.
        provideService(SqlProvider.class, this);
    }

    @Nonnull
    @Override
    public Sql getSql(@Nonnull DatabaseCredentials credentials) {
        return new HelperSql(credentials);
    }

}
