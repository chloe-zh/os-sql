/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

/*
 *   Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.sql.planner.logical;

import static org.opensearch.sql.ast.dsl.AstDSL.argument;
import static org.opensearch.sql.ast.dsl.AstDSL.booleanLiteral;
import static org.opensearch.sql.ast.dsl.AstDSL.dedupe;
import static org.opensearch.sql.ast.dsl.AstDSL.defaultDedupArgs;
import static org.opensearch.sql.ast.dsl.AstDSL.exprList;
import static org.opensearch.sql.ast.dsl.AstDSL.field;
import static org.opensearch.sql.ast.dsl.AstDSL.intLiteral;
import static org.opensearch.sql.ast.dsl.AstDSL.relation;
import static org.opensearch.sql.data.type.ExprCoreType.DOUBLE;
import static org.opensearch.sql.data.type.ExprCoreType.INTEGER;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opensearch.sql.analysis.AnalyzerTestBase;
import org.opensearch.sql.expression.DSL;
import org.opensearch.sql.expression.config.ExpressionConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Configuration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ExpressionConfig.class, AnalyzerTestBase.class})
class LogicalDedupeTest extends AnalyzerTestBase {
  @Test
  public void analyze_dedup_with_two_field_with_default_option() {
    assertAnalyzeEqual(
        LogicalPlanDSL.dedupe(
            LogicalPlanDSL.relation("schema"),
            DSL.ref("integer_value", INTEGER),
            DSL.ref("double_value", DOUBLE)),
        dedupe(
            relation("schema"),
            defaultDedupArgs(),
            field("integer_value"), field("double_value")
        ));
  }

  @Test
  public void analyze_dedup_with_one_field_with_customize_option() {
    assertAnalyzeEqual(
        LogicalPlanDSL.dedupe(
            LogicalPlanDSL.relation("schema"),
            3, false, true,
            DSL.ref("integer_value", INTEGER),
            DSL.ref("double_value", DOUBLE)),
        dedupe(
            relation("schema"),
            exprList(
                argument("number", intLiteral(3)),
                argument("keepempty", booleanLiteral(false)),
                argument("consecutive", booleanLiteral(true))
            ),
            field("integer_value"), field("double_value")
        ));
  }
}
