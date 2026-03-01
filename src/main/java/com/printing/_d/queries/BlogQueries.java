package com.printing._d.queries;

public class BlogQueries {

    public static final String GET_ALL_BLOGS = """
            SELECT
                b.id AS blog_id,
                b.title,
                b.subtitle,
                b.description,
                b.writer_id,
                b.views,
                bc.name AS blog_category,
                u_writer.username AS writer_name,
                cs_blog.name AS blog_status,
                b.created_at AS blog_created_at,
            
                -- Images (grouped as JSON array)
                (SELECT JSON_ARRAYAGG(JSON_OBJECT(
                    'id', bi.id,
                    'image_url', bi.image_url
                ))
                FROM blog_images bi
                WHERE bi.blog_id = b.id AND bi.status = 1
                ) AS images,
            
                -- Likes (count)
                (SELECT COUNT(*) FROM blog_likes bl WHERE bl.blog_id = b.id AND bl.status = 1) AS like_count,
            
                -- Reactions (grouped)
            COALESCE(
                (
                    SELECT JSON_ARRAYAGG(JSON_OBJECT(
                        'reaction_type_id', t.reaction_type_id,
                        'reaction_type_name', brt.name,
                        'count', t.cnt
                    ))
                    FROM (
                        SELECT bl.reaction_type_id, COUNT(*) AS cnt
                        FROM blog_likes bl
                        WHERE bl.blog_id = b.id
                        AND bl.status = 1
                        GROUP BY bl.reaction_type_id
                    ) AS t
                    LEFT JOIN blog_reactions_types brt ON t.reaction_type_id = brt.id
                ),
                JSON_ARRAY()
            ) AS blog_reactions
            
            ,
            
                -- Comments with replies and reactions
                (SELECT JSON_ARRAYAGG(
                    JSON_OBJECT(
                        'comment_id', bc.id,
                        'user_id', bc.user_id,
                        'username', u_comment.username,
                        'comment', bc.comment,
                        'comment_date', bc.comment_date,
                        'reactions', (
                            SELECT JSON_ARRAYAGG(JSON_OBJECT(
                                'user_id', cr.user_id,
                                'username', u_react.username,
                                'reaction_type', bcrt.name
                            ))
                            FROM blog_comment_reactions cr
                            LEFT JOIN user u_react ON cr.user_id = u_react.user_id
                            LEFT JOIN blog_reactions_types bcrt ON cr.reaction_type_id = bcrt.id
                            WHERE cr.comment_id = bc.id
                            AND cr.status = 1
                        ),
                        'replies', (
                            SELECT JSON_ARRAYAGG(
                                JSON_OBJECT(
                                    'comment_id', r.id,
                                    'user_id', r.user_id,
                                    'username', u_reply.username,
                                    'comment', r.comment,
                                    'comment_date', r.comment_date,
                                    'reactions', (
                                        SELECT JSON_ARRAYAGG(JSON_OBJECT(
                                            'user_id', cr2.user_id,
                                            'username', u_react2.username,
                                            'reaction_type', bcrt2.name
                                        ))
                                        FROM blog_comment_reactions cr2
                                        LEFT JOIN user u_react2 ON cr2.user_id = u_react2.user_id
                                        LEFT JOIN blog_reactions_types bcrt2 ON cr2.reaction_type_id = bcrt2.id
                                        WHERE cr2.comment_id = r.id
                                        AND cr2.status = 1
                                    )
                                )
                            )
                            FROM blog_comments r
                            LEFT JOIN user u_reply ON r.user_id = u_reply.user_id
                            WHERE r.parent_comment_id = bc.id
                        )
                    )
                )
                FROM blog_comments bc
                LEFT JOIN user u_comment ON bc.user_id = u_comment.user_id
                WHERE bc.blog_id = b.id AND bc.parent_comment_id IS NULL
                ) AS comments
            FROM blogs b
            LEFT JOIN user u_writer ON b.writer_id = u_writer.user_id
            LEFT JOIN common_status cs_blog ON b.status = cs_blog.id
            LEFT JOIN blog_category bc ON bc.id = b.category_id
            WHERE b.status = 1
            ORDER BY b.created_at DESC
            """;


    public static final String GET_BLOG_DETAILS_BY_ID = """
            SELECT
                            b.id AS blog_id,
                            b.title,
                            b.subtitle,
                            b.description,
                            b.writer_id,
                            b.views,
                            bc.name AS blog_category,
                            u_writer.username AS writer_name,
                            u_writer.image_url AS writer_image_url,
                            cs_blog.name AS blog_status,
                            b.created_at AS blog_created_at,
                                        (SELECT JSON_ARRAYAGG(JSON_OBJECT(
                                'id', bi.id,
                                'image_url', bi.image_url
                            ))
                            FROM blog_images bi
                            WHERE bi.blog_id = b.id AND bi.status = 1
                            ) AS images,
            
                            (SELECT COUNT(*) FROM blog_likes bl WHERE bl.blog_id = b.id AND bl.status = 1) AS like_count,
            
                        COALESCE(
                            (
                                SELECT JSON_ARRAYAGG(JSON_OBJECT(
                                    'reaction_type_id', t.reaction_type_id,
                                    'reaction_type_name', brt.name,
                                    'count', t.cnt
                                ))
                                FROM (
                                    SELECT bl.reaction_type_id, COUNT(*) AS cnt
                                    FROM blog_likes bl
                                    WHERE bl.blog_id = b.id
                                    AND bl.status = 1
                                    GROUP BY bl.reaction_type_id
                                ) AS t
                                LEFT JOIN blog_reactions_types brt ON t.reaction_type_id = brt.id
                            ),
                            JSON_ARRAY()
                        ) AS blog_reactions
            
                        ,
            
                            (SELECT JSON_ARRAYAGG(
                                JSON_OBJECT(
                                    'comment_id', bc.id,
                                    'user_id', bc.user_id,
                                    'username', u_comment.username,
                                    'image_url', u_comment.image_url,
                                    'comment', bc.comment,
                                    'comment_date', bc.comment_date,
                                    'reactions', (
                                        SELECT JSON_ARRAYAGG(JSON_OBJECT(
                                            'user_id', cr.user_id,
                                            'username', u_react.username,
                                            'image_url', u_react.image_url,
                                            'reaction_type', bcrt.name
                                        ))
                                        FROM blog_comment_reactions cr
                                        LEFT JOIN user u_react ON cr.user_id = u_react.user_id
                                        LEFT JOIN blog_reactions_types bcrt ON cr.reaction_type_id = bcrt.id
                                        WHERE cr.comment_id = bc.id
                                        AND cr.status = 1
                                    ),
                                    'replies', (
                                        SELECT JSON_ARRAYAGG(
                                            JSON_OBJECT(
                                                'comment_id', r.id,
                                                'user_id', r.user_id,
                                                'username', u_reply.username,
                                                'image_url', u_reply.image_url,
                                                'comment', r.comment,
                                                'comment_date', r.comment_date,
                                                'reactions', (
                                                    SELECT JSON_ARRAYAGG(JSON_OBJECT(
                                                        'user_id', cr2.user_id,
                                                        'username', u_react2.username,
                                                        'image_url', u_react2.image_url,
                                                        'reaction_type', bcrt2.name
                                                    ))
                                                    FROM blog_comment_reactions cr2
                                                    LEFT JOIN user u_react2 ON cr2.user_id = u_react2.user_id
                                                    LEFT JOIN blog_reactions_types bcrt2 ON cr2.reaction_type_id = bcrt2.id
                                                    WHERE cr2.comment_id = r.id
                                                    AND cr2.status = 1
                                                )
                                            )
                                        )
                                        FROM blog_comments r
                                        LEFT JOIN user u_reply ON r.user_id = u_reply.user_id
                                        WHERE r.parent_comment_id = bc.id
                                    )
                                )
                            )
                            FROM blog_comments bc
                            LEFT JOIN user u_comment ON bc.user_id = u_comment.user_id
                            WHERE bc.blog_id = b.id AND bc.parent_comment_id IS NULL
                            ) AS comments
                        FROM blogs b
                        LEFT JOIN user u_writer ON b.writer_id = u_writer.user_id
                        LEFT JOIN common_status cs_blog ON b.status = cs_blog.id
                        LEFT JOIN blog_category bc ON bc.id = b.category_id
                        WHERE b.id= ?
                        ORDER BY b.created_at DESC
            """;

    public static final String INSERT_BLOG = """
            INSERT INTO blogs 
                (title, subtitle, description, writer_id, created_by, updated_by) 
            VALUES 
                (?, ?, ?, ?, ?, ?)
            """;

    public static final String INSERT_BLOG_IMAGES = """
            INSERT INTO blog_images 
                (blog_id, image_url, created_by, updated_by)
            VALUES 
                (?, ?, ?, ?)
            """;

    public static final String GET_BLOGS_BY_WRITER = """
            SELECT
            	b.id AS blog_id,
            	b.title,
            	b.subtitle,
            	b.description,
            	b.writer_id,
            	b.views,
            	bc.name AS blog_category,
            	u_writer.username AS writer_name,
            	cs_blog.name AS blog_status,
            	b.created_at AS blog_created_at,
            
            	(SELECT JSON_ARRAYAGG(JSON_OBJECT(
            		'id', bi.id,
            		'image_url', bi.image_url
            	))
            	FROM blog_images bi
            	WHERE bi.blog_id = b.id AND bi.status = 1
            	) AS images,
            
            	(SELECT COUNT(*) FROM blog_likes bl WHERE bl.blog_id = b.id AND bl.status = 1) AS like_count,
            
            COALESCE(
            	(
            		SELECT JSON_ARRAYAGG(JSON_OBJECT(
            			'reaction_type_id', t.reaction_type_id,
            			'reaction_type_name', brt.name,
            			'count', t.cnt
            		))
            		FROM (
            			SELECT bl.reaction_type_id, COUNT(*) AS cnt
            			FROM blog_likes bl
            			WHERE bl.blog_id = b.id
            			AND bl.status = 1
            			GROUP BY bl.reaction_type_id
            		) AS t
            		LEFT JOIN blog_reactions_types brt ON t.reaction_type_id = brt.id
            	),
            	JSON_ARRAY()
            ) AS blog_reactions
            
            ,
            
            	(SELECT JSON_ARRAYAGG(
            		JSON_OBJECT(
            			'comment_id', bc.id,
            			'user_id', bc.user_id,
            			'username', u_comment.username,
            			'comment', bc.comment,
            			'comment_date', bc.comment_date,
            			'reactions', (
            				SELECT JSON_ARRAYAGG(JSON_OBJECT(
            					'user_id', cr.user_id,
            					'username', u_react.username,
            					'reaction_type', bcrt.name
            				))
            				FROM blog_comment_reactions cr
            				LEFT JOIN user u_react ON cr.user_id = u_react.user_id
            				LEFT JOIN blog_reactions_types bcrt ON cr.reaction_type_id = bcrt.id
            				WHERE cr.comment_id = bc.id
            				AND cr.status = 1
            			),
            			'replies', (
            				SELECT JSON_ARRAYAGG(
            					JSON_OBJECT(
            						'comment_id', r.id,
            						'user_id', r.user_id,
            						'username', u_reply.username,
            						'comment', r.comment,
            						'comment_date', r.comment_date,
            						'reactions', (
            							SELECT JSON_ARRAYAGG(JSON_OBJECT(
            								'user_id', cr2.user_id,
            								'username', u_react2.username,
            								'reaction_type', bcrt2.name
            							))
            							FROM blog_comment_reactions cr2
            							LEFT JOIN user u_react2 ON cr2.user_id = u_react2.user_id
            							LEFT JOIN blog_reactions_types bcrt2 ON cr2.reaction_type_id = bcr2.id
            							WHERE cr2.comment_id = r.id
            							AND cr2.status = 1
            						)
            					)
            				)
            				FROM blog_comments r
            				LEFT JOIN user u_reply ON r.user_id = u_reply.user_id
            				WHERE r.parent_comment_id = bc.id
            			)
            		)
            	)
            	FROM blog_comments bc
            	LEFT JOIN user u_comment ON bc.user_id = u_comment.user_id
            	WHERE bc.blog_id = b.id AND bc.parent_comment_id IS NULL
            	) AS comments
            FROM blogs b
            LEFT JOIN user u_writer ON b.writer_id = u_writer.user_id
            LEFT JOIN common_status cs_blog ON b.status = cs_blog.id
            LEFT JOIN blog_category bc ON bc.id = b.category_id
            WHERE b.status = 1
            AND u_writer.username = ?
            ORDER BY b.created_at DESC
            """;


    public static final String GET_BLOG_IDS_BY_TAG_NAME = """
            SELECT b.id AS blog_id
            FROM blogs b
            JOIN blog_tags bt ON b.id = bt.blog_id
            JOIN tags t ON bt.tag_id = t.id
            WHERE t.name = ?
            """;

    public static final String GET_BLOGS_BY_BLOG_IDS = """
            SELECT
            	b.id AS blog_id,
            	b.title,
            	b.subtitle,
            	b.description,
            	b.writer_id,
            	b.views,
            	bc.name AS blog_category,
            	u_writer.username AS writer_name,
            	cs_blog.name AS blog_status,
            	b.created_at AS blog_created_at,
            
            	(SELECT JSON_ARRAYAGG(JSON_OBJECT(
            		'id', bi.id,
            		'image_url', bi.image_url
            	))
            	FROM blog_images bi
            	WHERE bi.blog_id = b.id AND bi.status = 1
            	) AS images,
            
            	(SELECT COUNT(*) FROM blog_likes bl WHERE bl.blog_id = b.id AND bl.status = 1) AS like_count,
            
            COALESCE(
            	(
            		SELECT JSON_ARRAYAGG(JSON_OBJECT(
            			'reaction_type_id', t.reaction_type_id,
            			'reaction_type_name', brt.name,
            			'count', t.cnt
            		))
            		FROM (
            			SELECT bl.reaction_type_id, COUNT(*) AS cnt
            			FROM blog_likes bl
            			WHERE bl.blog_id = b.id
            			AND bl.status = 1
            			GROUP BY bl.reaction_type_id
            		) AS t
            		LEFT JOIN blog_reactions_types brt ON t.reaction_type_id = brt.id
            	),
            	JSON_ARRAY()
            ) AS blog_reactions
            
            ,
            
            	(SELECT JSON_ARRAYAGG(
            		JSON_OBJECT(
            			'comment_id', bc.id,
            			'user_id', bc.user_id,
            			'username', u_comment.username,
            			'comment', bc.comment,
            			'comment_date', bc.comment_date,
            			'reactions', (
            				SELECT JSON_ARRAYAGG(JSON_OBJECT(
            					'user_id', cr.user_id,
            					'username', u_react.username,
            					'reaction_type', bcrt.name
            				))
            				FROM blog_comment_reactions cr
            				LEFT JOIN user u_react ON cr.user_id = u_react.user_id
            				LEFT JOIN blog_reactions_types bcrt ON cr.reaction_type_id = bcrt.id
            				WHERE cr.comment_id = bc.id
            				AND cr.status = 1
            			),
            			'replies', (
            				SELECT JSON_ARRAYAGG(
            					JSON_OBJECT(
            						'comment_id', r.id,
            						'user_id', r.user_id,
            						'username', u_reply.username,
            						'comment', r.comment,
            						'comment_date', r.comment_date,
            						'reactions', (
            							SELECT JSON_ARRAYAGG(JSON_OBJECT(
            								'user_id', cr2.user_id,
            								'username', u_react2.username,
            								'reaction_type', bcrt2.name
            							))
            							FROM blog_comment_reactions cr2
            							LEFT JOIN user u_react2 ON cr2.user_id = u_react2.user_id
            							LEFT JOIN blog_reactions_types bcrt2 ON cr2.reaction_type_id = bcrt2.id
            							WHERE cr2.comment_id = r.id
            							AND cr2.status = 1
            						)
            					)
            				)
            				FROM blog_comments r
            				LEFT JOIN user u_reply ON r.user_id = u_reply.user_id
            				WHERE r.parent_comment_id = bc.id
            			)
            		)
            	)
            	FROM blog_comments bc
            	LEFT JOIN user u_comment ON bc.user_id = u_comment.user_id
            	WHERE bc.blog_id = b.id AND bc.parent_comment_id IS NULL
            	) AS comments
            FROM blogs b
            LEFT JOIN user u_writer ON b.writer_id = u_writer.user_id
            LEFT JOIN common_status cs_blog ON b.status = cs_blog.id
            LEFT JOIN blog_category bc ON bc.id = b.category_id
            WHERE b.status = 1
            AND b.id IN (:ids)
            ORDER BY b.created_at DESC
            """;

    public static final String GET_ALL_BLOG_TAGS = """
            SELECT
                t.id,
                t.name,
                t.description,
                t.status AS status_id,
                cs.name AS status_name,
                t.created_at,
                t.created_by,
                t.updated_at,
                t.updated_by,
                t.terminated_at,
                t.terminated_by
            FROM tags t
            LEFT JOIN common_status cs ON t.status = cs.id
            """;
    public static final String UPDATE_BLOG_VIEW_COUNT_BY_ONE = """
            UPDATE blogs
            SET views = views + 1
            WHERE id = ?
            """;

    public static final String IS_BLOG_EXISTS = """
                SELECT COUNT(*) 
                FROM blogs 
                WHERE id = ? AND status = 1
            """;

    public static final String INSERT_BLOG_BOOKMARK = """
                INSERT INTO blog_bookmarks (user_id, blog_id, status, created_by)
                VALUES (?, ?, 1, ?)
            """;

    public static final String IS_BLOG_ALREADY_BOOKMARKED = """
                SELECT COUNT(*) 
                FROM blog_bookmarks 
                WHERE blog_id = ? AND user_id = ? AND status = 1
            """;

    public static final String REMOVE_BLOG_BOOKMARK = """
                UPDATE blog_bookmarks
                SET status = 2,
                    terminated_at = CURRENT_TIMESTAMP,
                    terminated_by = ?
                WHERE blog_id = ? AND user_id = ? AND status = 1
            """;

    public static final String GET_BLOG_PREVIOUS_REACT = """
            SELECT
            	bl.blog_id,
                bl.user_id,
                brt.name
            FROM blog_likes bl
            LEFT JOIN blog_reactions_types brt ON bl.reaction_type_id = brt.id
            WHERE blog_id = ? AND user_id = ? AND status = 1
            """;

    public static final String ADD_REACTION_TO_BLOG = """
            INSERT INTO blog_likes (blog_id, user_id, reaction_type_id, status, created_by)
            SELECT
                ?,
                ?,
                brt.id,
                1,
                ?
            FROM blog_reactions_types brt
            WHERE brt.name = ?
              AND brt.common_status_id = 1
            """;

    public static final String REMOVE_BLOG_REACTION = """
            UPDATE blog_likes
            SET status = 2,
                terminated_at = CURRENT_TIMESTAMP,
                terminated_by = ?
            WHERE blog_id = ? AND user_id = ? AND status = 1
            """;


    public static final String INSERT_BLOG_COMMENT = """
            INSERT INTO blog_comments (
                blog_id,
                user_id,
                parent_comment_id,
                comment,
                status,
                created_by
            )
            VALUES (?, ?, ?, ?, 1, ?)
            """;

    public static final String CHECK_BLOG_COMMENT_EXISTS = """
                SELECT COUNT(1)
                FROM blog_comments
                WHERE id = ?
                  AND status = 1
            """;

    public static final String IS_BLOG_COMMENT_ALREADY_REACTED = """
            SELECT
                bcr.comment_id,
                bcr.user_id,
                brt.name
            FROM blog_comment_reactions bcr
            LEFT JOIN blog_reactions_types brt
                ON bcr.reaction_type_id = brt.id
            WHERE bcr.comment_id = ?
              AND bcr.user_id = ?
              AND bcr.status = 1
            """;
    public static final String ADD_REACTION_TO_BLOG_COMMENT = """
            INSERT INTO blog_comment_reactions (
                comment_id,
                user_id,
                reaction_type_id,
                status,
                created_by
            )
            SELECT
                ?,
                ?,
                brt.id,
                1,
                ?
            FROM blog_reactions_types brt
            WHERE brt.name = ?
              AND brt.common_status_id = 1
            """;

    public static final String REMOVE_BLOG_COMMENT_REACTION = """
            UPDATE blog_comment_reactions
            SET status = 2,
                terminated_at = CURRENT_TIMESTAMP,
                terminated_by = ?
            WHERE comment_id = ? AND user_id = ? AND status = 1
            """;

    public static final String GET_ALL_BLOG_TAGS_BY_BLOG_ID = """
            SELECT
                t.id,
                t.name,
                t.description,
                t.status AS status_id,
                cs.name AS status_name,
                t.created_at,
                t.created_by,
                t.updated_at,
                t.updated_by,
                t.terminated_at,
                t.terminated_by
            FROM blog_tags bt
            LEFT JOIN tags t ON bt.tag_id = t.id
            LEFT JOIN blogs b ON bt.blog_id = b.id
            LEFT JOIN common_status cs ON t.status = cs.id
            WHERE b.id = ?
            """;
}
