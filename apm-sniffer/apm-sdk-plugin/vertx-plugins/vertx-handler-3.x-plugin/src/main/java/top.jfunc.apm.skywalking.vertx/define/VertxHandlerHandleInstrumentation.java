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

package top.jfunc.apm.skywalking.vertx.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.DeclaredInstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.IndirectMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.logical.LogicalMatchOperation;
import top.jfunc.apm.skywalking.vertx.VertxConfigUtil;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static org.apache.skywalking.apm.agent.core.plugin.match.HierarchyMatch.byHierarchyMatch;

/**
 * 针对{@link io.vertx.core.Handler#handle(Object)}的实现类进行增强，又因为此类的实现很多都是框架内部的，所以以自定义的类为准，
 * 其实可以利用{@link org.apache.skywalking.apm.agent.core.plugin.match.ClassAnnotationMatch}通过注解的方式，但是对应用代码有侵入，所以暂不使用
 */
public class VertxHandlerHandleInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String HANDLER_HANDLE_INTERCEPTOR = "top.jfunc.apm.skywalking.vertx.HandlerHandleInterceptor";
    private static final String HANDLER_CLASS = "io.vertx.core.Handler";
    private static final String INTERCEPT_METHOD_NAME = "handle";

    @Override
    protected ClassMatch enhanceClass() {
        IndirectMatch prefixesMatches = VertxConfigUtil.prefixesMatches();
        if(null == prefixesMatches){
            return null;
        }
        return LogicalMatchOperation.and(prefixesMatches, byHierarchyMatch(HANDLER_CLASS));
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new DeclaredInstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named(INTERCEPT_METHOD_NAME);
                }

                @Override
                public String getMethodsInterceptor() {
                    return HANDLER_HANDLE_INTERCEPTOR;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }
}
