package com.printing._d.queries;

public class GalleryQueries {

    public static final String GET_ALL_GALLERY_IMAGES = """
            SELECT
                g.id AS image_id,
                g.name AS image_name,
                g.description AS image_description,
                g.location,
                g.image_link,
                g.image_owner,
                g.image_source,
                g.image_source_link,
                g.color,
                g.hover_color,
                gs.name AS image_status,
                cs.name AS iamge_status_status,
                g.created_at,
                g.updated_at,
                g.terminated_at
            FROM gallery g
            JOIN gallery_status gs ON g.status_id = gs.id
            JOIN common_status cs ON gs.status_id = cs.id
            WHERE g.terminated_at IS NULL
            ORDER BY g.id
            """;

}
