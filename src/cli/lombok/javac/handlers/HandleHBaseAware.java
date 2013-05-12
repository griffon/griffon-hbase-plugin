/*
 * Copyright 2012-2013 the original author or authors.
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
 */

package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import griffon.plugins.hbase.HBaseAware;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HBaseAwareConstants;
import lombok.core.handlers.HBaseAwareHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.ast.JavacType;

import static lombok.core.util.ErrorMessages.canBeUsedOnClassAndEnumOnly;
import static lombok.javac.handlers.JavacHandlerUtil.deleteAnnotationIfNeccessary;

/**
 * @author Andres Almiray
 */
public class HandleHBaseAware extends JavacAnnotationHandler<HBaseAware> {
    private final JavacHBaseAwareHandler handler = new JavacHBaseAwareHandler();

    @Override
    public void handle(final AnnotationValues<HBaseAware> annotation, final JCTree.JCAnnotation source, final JavacNode annotationNode) {
        deleteAnnotationIfNeccessary(annotationNode, HBaseAware.class);

        JavacType type = JavacType.typeOf(annotationNode, source);
        if (type.isAnnotation() || type.isInterface()) {
            annotationNode.addError(canBeUsedOnClassAndEnumOnly(HBaseAware.class));
            return;
        }

        JavacUtil.addInterface(type.node(), HBaseAwareConstants.HBASE_CONTRIBUTION_HANDLER_TYPE);
        handler.addHBaseProviderField(type);
        handler.addHBaseProviderAccessors(type);
        handler.addHBaseContributionMethods(type);
        type.editor().rebuild();
    }

    private static class JavacHBaseAwareHandler extends HBaseAwareHandler<JavacType> {
    }
}
