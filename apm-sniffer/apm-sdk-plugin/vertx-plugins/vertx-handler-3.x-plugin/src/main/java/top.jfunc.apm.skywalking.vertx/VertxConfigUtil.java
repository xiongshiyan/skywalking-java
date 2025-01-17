/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

package top.jfunc.apm.skywalking.vertx;

import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.plugin.match.IndirectMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.PrefixMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.logical.LogicalMatchOperation;

import java.util.ArrayList;
import java.util.List;

import static org.apache.skywalking.apm.agent.core.plugin.match.PrefixMatch.nameStartsWith;

public class VertxConfigUtil {
    private static final ILog LOGGER = LogManager.getLogger(VertxConfigUtil.class);

    public static IndirectMatch prefixesMatches() {
        final String jointPrefixes = VertxHandlerPluginConfig.Plugin.VertxHandler.HANDLER_CLASS_PREFIXES;
        LOGGER.info("prefix is {}", jointPrefixes);
        if (jointPrefixes == null || jointPrefixes.trim().isEmpty()) {
            return null;
        }

        final String[] prefixes = jointPrefixes.split(",");

        final List<PrefixMatch> prefixMatches = new ArrayList<PrefixMatch>();

        for (final String prefix : prefixes) {
            if (prefix.startsWith("java.") || prefix.startsWith("javax.") || prefix.startsWith("sun.")) {
                LOGGER.warn("prefix {} is ignored", prefix);
                continue;
            }
            prefixMatches.add(nameStartsWith(prefix));
        }

        if (prefixMatches.size() == 0) {
            return null;
        }

        return LogicalMatchOperation.or(prefixMatches.toArray(new PrefixMatch[0]));
    }
}
