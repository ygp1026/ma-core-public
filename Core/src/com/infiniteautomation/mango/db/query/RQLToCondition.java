/**
 * Copyright (C) 2017 Infinite Automation Software. All rights reserved.
 */

package com.infiniteautomation.mango.db.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.impl.DSL;

import net.jazdw.rql.parser.ASTNode;

/**
 * Transforms RQL node into a jOOQ Condition along with sort fields, limit and offset
 * 
 * @author Jared Wiltshire
 */
public class RQLToCondition {

    protected final Map<String, Field<Object>> fieldMapping;
    protected final Name tableName;
    
    protected List<SortField<Object>> sortFields = null;
    protected Integer limit = null;
    protected Integer offset = null;

    public RQLToCondition(Map<String, Field<Object>> fieldMapping, Name tableName) {
        this.fieldMapping = fieldMapping;
        this.tableName = tableName;
    }

    public ConditionSortLimit visit(ASTNode node) {
        Condition condition = visitNode(node);
        return new ConditionSortLimit(condition, sortFields, limit, offset);
    }

    protected Condition visitNode(ASTNode node) {
        switch (node.getName()) {
        case "":
        case "and":
            return DSL.and(visitAndOr(node));
        case "or":
            return DSL.or(visitAndOr(node));
        case "eq":
            if (node.getArgument(1) == null) {
                return getField(node).isNull();
            }
            return getField(node).eq(node.getArgument(1));
        case "gt":
            return getField(node).gt(node.getArgument(1));
        case "ge":
            return getField(node).ge(node.getArgument(1));
        case "lt":
            return getField(node).lt(node.getArgument(1));
        case "le":
            return getField(node).le(node.getArgument(1));
        case "ne":
            if (node.getArgument(1) == null) {
                return getField(node).isNotNull();
            }
            return getField(node).ne(node.getArgument(1));
        case "match":
        case "like":
            return getField(node).like((String) node.getArgument(1));
        case "in":
            return getField(node).in(node.getArguments().subList(1, node.getArgumentsSize()));
        case "sort":
            sortFields = getSortFields(node);
            return DSL.and();
        case "limit":
            limit = (Integer) node.getArgument(0);
            if (node.getArgumentsSize() > 1) {
                offset = (Integer) node.getArgument(1);
            }
            return DSL.and();
        default:
            throw new RuntimeException("Unknown node type " + node.getName());
        }
    }

    protected List<Condition> visitAndOr(ASTNode node) {
        List<Condition> conditions = new ArrayList<>();
        for (Object obj : node) {
            ASTNode arg = (ASTNode) obj;
            conditions.add(visitNode(arg));
        }
        return conditions;
    }
    
    protected Field<Object> getField(ASTNode node) {
        String property = (String) node.getArgument(0);
        return getField(property);
    }
    
    protected Field<Object> getField(String property) {
        if (this.fieldMapping.containsKey(property)) {
            return this.fieldMapping.get(property);
        }
        return DSL.field(tableName.append(property));
    }

    protected List<SortField<Object>> getSortFields(ASTNode node) {
        List<SortField<Object>> fields = new ArrayList<>(node.getArgumentsSize());
        
        for (Object obj : node) {
            boolean descending = false;
            String property = (String) obj;
            if (property.startsWith("-")) {
                descending = true;
                property = property.substring(1);
            } else if (property.startsWith("+")) {
                property = property.substring(1);
            }

            SortField<Object> field = getField(property).sort(descending ? SortOrder.DESC : SortOrder.ASC);
            fields.add(field);
        }
        
        return fields;
    }
}