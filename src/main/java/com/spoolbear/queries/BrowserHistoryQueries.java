package com.spoolbear.queries;

public class BrowserHistoryQueries {
    public static final String INSERT_BROWSER_HISTORY_REQUEST = """
                INSERT INTO browser_history ( product_id, name, user_id, status_id)
                VALUES ( ?, ?, ?,(SELECT id FROM common_status WHERE name = ? LIMIT 1))
            """;

    public static final String TERMINATE_BROWSER_HISTORY = """
                UPDATE browser_history
                SET status_id = ?
                WHERE id = ? AND (status_id IS NOT NULL)
            """;

    public static final String TERMINATE_ALL_BROWSER_HISTORY_BY_USER = """
                UPDATE browser_history
                SET status_id = ?
                WHERE user_id = ? AND (status_id IS NOT NULL)
            """;

}
