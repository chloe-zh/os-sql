/*
 *   Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package com.amazon.opendistroforelasticsearch.sql.ast.expression;

import com.amazon.opendistroforelasticsearch.sql.ast.AbstractNodeVisitor;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Getter
@EqualsAndHashCode(callSuper = false)
public class QualifiedName extends UnresolvedExpression {
    private final List<String> parts;

    public QualifiedName(String name) {
        this.parts = Collections.singletonList(name);
    }

    public QualifiedName(Iterable<String> parts) {
        List<String> partsList = StreamSupport.stream(parts.spliterator(), false).collect(toList());
        if (partsList.isEmpty()) {
            throw new IllegalArgumentException("parts is empty");
        }
        this.parts = partsList;
    }

    public static QualifiedName of(String first, String... rest) {
        requireNonNull(first);
        ArrayList<String> parts = new ArrayList<>();
        parts.add(first);
        parts.addAll(Arrays.asList(rest));
        return new QualifiedName(parts);
    }

    private static QualifiedName of(Iterable<String> parts) {
        return new QualifiedName(parts);
    }

    public Optional<QualifiedName> getPrefix() {
        if (parts.size() == 1) {
            return Optional.empty();
        }
        return Optional.of(QualifiedName.of(parts.subList(0, parts.size() - 1)));
    }

    public String getSuffix() {
        return parts.get(parts.size() - 1);
    }

    public String toString() {
        return String.join(".", this.parts);
    }

    @Override
    public List<UnresolvedExpression> getChild() {
        return ImmutableList.of();
    }

    @Override
    public <R, C> R accept(AbstractNodeVisitor<R, C> nodeVisitor, C context) {
        return nodeVisitor.visitQualifiedName(this, context);
    }
}