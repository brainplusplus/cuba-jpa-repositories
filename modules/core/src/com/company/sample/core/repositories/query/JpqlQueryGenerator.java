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

package com.company.sample.core.repositories.query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * Ignite query generator for Spring Data framework.
 */
public final class JpqlQueryGenerator {

    public static JpqlMetadata generateJpqlMetadata(RepositoryMetadata metadata, PartTree parts) {
        StringBuilder sql = new StringBuilder();
        List<String> parameters = new ArrayList<>();
        String alias = getEntityClassName(metadata);

        if (parts.isDelete()) {
            sql.append("DELETE ");
        } else {
            sql.append("SELECT ");

            if (parts.isDistinct()) {
                sql.append("DISTINCT");
            }

            if (parts.isCountProjection())
                sql.append("COUNT(").append(alias).append(") ");
            else {
                sql.append(" ").append(alias).append(" ");
            }
        }

        Entity annotation = metadata.getDomainType().getAnnotation(Entity.class);
        sql.append("FROM ").append(annotation.name()).append(" ").append(alias);

        addWhereClause(parts, sql, parameters);

        addSorting(sql, parts.getSort());

        if (parts.isLimiting()) {
            sql.append(" LIMIT ");
            sql.append(parts.getMaxResults().intValue());
        }
        return new JpqlMetadata(sql.toString(), parameters, false);
    }

    private static void addWhereClause(PartTree parts, StringBuilder sql, List<String> parameters) {
        if (parts.iterator().hasNext()) {
            sql.append(" WHERE ");

            for (PartTree.OrPart orPart : parts) {
                sql.append("(");
                for (Part part : orPart) {
                    handleQueryPart(sql, part, parameters);
                    sql.append(" AND ");
                }

                sql.delete(sql.length() - 5, sql.length());

                sql.append(") OR ");
            }

            sql.delete(sql.length() - 4, sql.length());
        }
    }

    public static String getEntityClassName(RepositoryMetadata metadata) {
        return metadata.getDomainType().getSimpleName();
    }

    /**
     * Add a dynamic part of query for the sorting support.
     *
     * @param sql  SQL text string.
     * @param sort Sort method.
     */
    private static StringBuilder addSorting(StringBuilder sql, Sort sort) {
        if (sort != null) {
            sql.append(" ORDER BY ");

            for (Sort.Order order : sort) {
                sql.append(order.getProperty()).append(" ").append(order.getDirection());

                if (order.getNullHandling() != Sort.NullHandling.NATIVE) {
                    sql.append(" ").append("NULL ");
                    switch (order.getNullHandling()) {
                        case NULLS_FIRST:
                            sql.append("FIRST");
                            break;
                        case NULLS_LAST:
                            sql.append("LAST");
                            break;
                    }
                }
                sql.append(", ");
            }

            sql.delete(sql.length() - 2, sql.length());
        }

        return sql;
    }

    /**
     * Add a dynamic part of a query for the pagination support.
     *
     * @param sql      Builder instance.
     * @param pageable Pageable instance.
     * @return Builder instance.
     */
    public static StringBuilder addPaging(StringBuilder sql, Pageable pageable) {
        if (pageable.getSort() != null)
            addSorting(sql, pageable.getSort());

        sql.append(" LIMIT ").append(pageable.getPageSize()).append(" OFFSET ").append(pageable.getOffset());

        return sql;
    }

    /**
     * Transform part to sql expression
     */
    private static void handleQueryPart(StringBuilder sql, Part part, List<String> parameters) {
        sql.append("(");

        sql.append(part.getProperty());

        String paramName = part.getProperty().getLeafProperty().getSegment();
        switch (part.getType()) {
            case SIMPLE_PROPERTY:
                sql.append(" = :").append(paramName);
                parameters.add(paramName);
                break;
            case NEGATING_SIMPLE_PROPERTY:
                sql.append("<> :").append(paramName);
                break;
            case AFTER:
            case GREATER_THAN:
                sql.append("> :").append(paramName);
                parameters.add(paramName);
                break;
            case GREATER_THAN_EQUAL:
                sql.append(">= :").append(paramName);
                parameters.add(paramName);
                break;
            case BEFORE:
            case LESS_THAN:
                sql.append("< :").append(paramName);
                parameters.add(paramName);
                break;
            case LESS_THAN_EQUAL:
                sql.append("<= :").append(paramName);
                parameters.add(paramName);
                break;
            case IS_NOT_NULL:
                sql.append(" IS NOT NULL");
                break;
            case IS_NULL:
                sql.append(" IS NULL");
                break;
            case FALSE:
                sql.append(" = FALSE");
                break;
            case TRUE:
                sql.append(" = TRUE");
                break;
            case CONTAINING:
                sql.append(" LIKE '%' || ").append(paramName).append(" || '%'");
                parameters.add(paramName);
                break;
            case NOT_CONTAINING:
                sql.append(" NOT LIKE '%' || ").append(paramName).append(" || '%'");
                parameters.add(paramName);
                break;
            case LIKE:
                sql.append(" LIKE '%' || ").append(paramName).append(" || '%'");
                parameters.add(paramName);
                break;
            case NOT_LIKE:
                sql.append(" NOT LIKE '%' || ").append(paramName).append(" || '%'");
                parameters.add(paramName);
                break;
            case STARTING_WITH:
                sql.append(" LIKE  ").append(paramName).append(" || '%'");
                parameters.add(paramName);
                break;
            case ENDING_WITH:
                sql.append(" LIKE '%' || ").append(paramName);
                parameters.add(paramName);
                break;
            case IN:
                sql.append(" IN :").append(paramName);
                parameters.add(paramName);
                break;
            case NOT_IN:
                sql.append(" NOT IN :").append(paramName);
                parameters.add(paramName);
                break;
            case REGEX:
                sql.append(" REGEXP :").append(paramName);
                parameters.add(paramName);
                break;
            case BETWEEN:
            case NEAR:
            case EXISTS:
            default:
                throw new UnsupportedOperationException(part.getType() + " is not supported!");
        }

        sql.append(")");
    }
}

