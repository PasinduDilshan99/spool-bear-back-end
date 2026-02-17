package com.printing._d.queries;

public class NavBarQueries {

    private NavBarQueries() {
    }

    public static final String GET_ALL_NAV_BAR_DATA = """
            SELECT
                nb.id AS NAV_BAR_ID,
                nb.name AS NAME,
                nb.description AS DESCRIPTION,
                nbs.name AS STATUS,
                nb.link_url AS LINK_URL,
                nb.created_at AS CREATED_AT,
                nb.created_by AS CREATED_BY,
                nb.updated_at AS UPDATED_AT,
                nb.updated_by AS UPDATED_BY,
                nb.terminated_at AS TERMINATED_AT,
                nb.terminated_by AS TERMINATED_BY
            FROM nav_bar nb
            LEFT JOIN nav_bar_status nbs ON nb.status_id = nbs.id
            """;

    public static final String GET_SUBMENU_BY_NAV_BAR_ID = """
            SELECT
                nbs.id AS SUBMENU_ID,
                nbs.nav_bar_id AS NAV_BAR_ID,
                nbs.name AS NAME,
                nbs.description AS DESCRIPTION,
                nbs.link_url AS LINK_URL,
                nbs.icon_class AS ICON_CLASS,
                nbs.sort_order AS SORT_ORDER,
                nbs.opens_in_new_tab AS OPENS_IN_NEW_TAB,
                nbs.is_featured AS IS_FEATURED,
                nbs_status.name AS STATUS,
                nbs.created_at AS CREATED_AT,
                nbs.created_by AS CREATED_BY,
                nbs.updated_at AS UPDATED_AT,
                nbs.updated_by AS UPDATED_BY,
                nbs.terminated_at AS TERMINATED_AT,
                nbs.terminated_by AS TERMINATED_BY
            FROM nav_bar_submenu nbs
            LEFT JOIN nav_bar_status nbs_status ON nbs.status_id = nbs_status.id
            WHERE nbs.nav_bar_id = ?
              AND nbs.parent_submenu_id IS NULL
            ORDER BY nbs.sort_order ASC, nbs.name ASC
            """;

}
