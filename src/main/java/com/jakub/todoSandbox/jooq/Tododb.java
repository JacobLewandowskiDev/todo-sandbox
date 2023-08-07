/*
 * This file is generated by jOOQ.
 */
package com.jakub.todoSandbox.jooq;


import com.jakub.todoSandbox.jooq.tables.Step;
import com.jakub.todoSandbox.jooq.tables.Todo;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tododb extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>tododb</code>
     */
    public static final Tododb TODODB = new Tododb();

    /**
     * The table <code>tododb.step</code>.
     */
    public final Step STEP = Step.STEP;

    /**
     * The table <code>tododb.todo</code>.
     */
    public final Todo TODO = Todo.TODO;

    /**
     * No further instances allowed
     */
    private Tododb() {
        super("tododb", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Step.STEP,
            Todo.TODO
        );
    }
}
