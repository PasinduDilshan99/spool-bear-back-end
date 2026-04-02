package com.spoolbear.queries;

public class MaterialQueries {
    public static final String GET_ALL_MATERIALS = """
                SELECT
                    m.material_id,
                    m.name AS material_name,
                    m.description AS material_description,
                    m.price_per_gram,
                    m.density,
                    m.strength,
                    m.flexibility,
                    m.temperature_resistance,
                    m.finish,
                    m.is_popular,
                    m.is_available,
                    m.min_layer_height,
                    m.max_layer_height,
                    mt.material_type_id,
                    mt.name AS material_type_name,
                    mt.description AS material_type_description,
                    mt.icon_url AS material_type_icon,
                    mt.typical_use_cases,
                    mt.display_order,
                    GROUP_CONCAT(DISTINCT mi.image_url) AS images,
                    GROUP_CONCAT(DISTINCT mp.pro_text) AS pros,
                    GROUP_CONCAT(DISTINCT mc.con_text) AS cons,
                    GROUP_CONCAT(DISTINCT CONCAT(mc2.color_name,'|', mc2.hex_code,'|',mc2.preview_image)) AS colors,
                    GROUP_CONCAT(DISTINCT CONCAT(mprop.property_name,'|', mprop.property_value)) AS properties
                FROM materials m
                JOIN material_type mt ON m.material_type_id = mt.material_type_id
                LEFT JOIN material_images mi ON mi.material_id = m.material_id AND mi.status = 1
                LEFT JOIN material_pros mp ON mp.material_id = m.material_id AND mp.status = 1
                LEFT JOIN material_cons mc ON mc.material_id = m.material_id AND mc.status = 1
                LEFT JOIN material_colors mc2 ON mc2.material_id = m.material_id AND mc2.status = 1
                LEFT JOIN material_properties mprop ON mprop.material_id = m.material_id AND mprop.status = 1
                WHERE m.status = 1
                GROUP BY m.material_id
                ORDER BY mt.display_order, m.name
            """;

    public static final String GET_MATERIAL_BY_ID = """
                SELECT
                    m.material_id,
                    m.name AS material_name,
                    m.description AS material_description,
                    m.price_per_gram,
                    m.density,
                    m.strength,
                    m.flexibility,
                    m.temperature_resistance,
                    m.finish,
                    m.is_popular,
                    m.is_available,
                    m.min_layer_height,
                    m.max_layer_height,
                    mt.material_type_id,
                    mt.name AS material_type_name,
                    mt.description AS material_type_description,
                    mt.icon_url AS material_type_icon,
                    mt.typical_use_cases,
                    mt.display_order,
                    GROUP_CONCAT(DISTINCT mi.image_url) AS images,
                    GROUP_CONCAT(DISTINCT mp.pro_text) AS pros,
                    GROUP_CONCAT(DISTINCT mc.con_text) AS cons,
                    GROUP_CONCAT(DISTINCT CONCAT(mc2.color_name,'|', mc2.hex_code,'|',mc2.preview_image)) AS colors,
                    GROUP_CONCAT(DISTINCT CONCAT(mprop.property_name,'|', mprop.property_value)) AS properties
                FROM materials m
                JOIN material_type mt ON m.material_type_id = mt.material_type_id
                LEFT JOIN material_images mi ON mi.material_id = m.material_id AND mi.status = 1
                LEFT JOIN material_pros mp ON mp.material_id = m.material_id AND mp.status = 1
                LEFT JOIN material_cons mc ON mc.material_id = m.material_id AND mc.status = 1
                LEFT JOIN material_colors mc2 ON mc2.material_id = m.material_id AND mc2.status = 1
                LEFT JOIN material_properties mprop ON mprop.material_id = m.material_id AND mprop.status = 1
                WHERE m.status = 1 AND m.material_id = ?
                GROUP BY m.material_id
            """;
}
