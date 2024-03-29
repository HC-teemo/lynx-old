package org.grapheco.lynx.logical

import org.opencypher.v9_0.ast.Statement

trait LogicalPlanner {
  def plan(statement: Statement, plannerContext: LogicalPlannerContext): LPTNode
}
