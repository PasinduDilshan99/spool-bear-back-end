package com.spoolbear.queries;

public class UserProfileQueries {
    public static final String GET_USER_PROFILE_ADDRESS_ID = """
            SELECT address_id
            FROM user
            WHERE user_id = ?
            """;

    public static final String UPDATE_USER_PROFILE_ADDRESS = """
            UPDATE address
            SET
                number = ?,
                address_line1 = ?,
                address_line2 = ?,
                city = ?,
                district = ?,
                province = ?,
                country_id = (SELECT country_id FROM country WHERE name = ? LIMIT 1),
                postal_code = ?
            WHERE address_id = ?;
            """;

    public static final String INSERT_USER_PROFILE_ADDRESS = """
            INSERT INTO address
            (
                number,
                address_line1,
                address_line2,
                city,
                district,
                province,
                country_id,
                postal_code
            )
            VALUES (?, ?, ?, ?, ?, ?, (SELECT country_id FROM country WHERE name = ? LIMIT 1), ?)
            """;

    public static final String UPDATE_USER_PROFILE_DETAILS = """
            UPDATE user
            SET
                first_name = ?,
                middle_name = ?,
                last_name = ?,
                address_id = ?,
                nic = ?,
                gender_id = (SELECT gender_id FROM gender WHERE name = ? LIMIT 1),
                email = ?,
                mobile_number = ?,
                country_id = (SELECT country_id FROM country WHERE name = ? LIMIT 1),
                date_of_birth = ?,
                image_url = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE user_id = ?
            """;

    public static final String GET_USER_PROFILE_SIDEBAR = """
            SELECT
                ups.id,
                ups.parent_id,
                ups.name,
                ups.description,
                p.name AS privilege_name,
                cs.name AS status_name,
                ups.url
            FROM user_profile_sidebar ups
            LEFT JOIN privileges p ON ups.privilege_id = p.id
            LEFT JOIN common_status cs ON ups.status_id = cs.id
            ORDER BY
                COALESCE(ups.parent_id, ups.id),
                ups.id
            """;

    public static final String GET_USER_PROFILE_DETAILS = """
            SELECT
                u.user_id,
                u.username,
                u.first_name,
                u.middle_name,
                u.last_name,
                u.nic,
                u.email,
                u.mobile_number,
                u.date_of_birth,
                u.image_url,
                u.created_at,
                u.updated_at,
                u.benefits_points_count,
                a.number AS address_number,
                a.address_line1,
                a.address_line2,
                a.city,
                a.district,
                a.province,
                c_country.name AS country_name,
                a.postal_code,
                g.name AS gender,
                ut.name AS user_type,
                ut.description AS user_type_description,
                cs.name AS user_status,
                cs.description AS user_status_description
            FROM user u
            LEFT JOIN address a ON u.address_id = a.address_id
            LEFT JOIN country c_country ON a.country_id = c_country.country_id
            LEFT JOIN gender g ON u.gender_id = g.gender_id
            LEFT JOIN user_type ut ON u.user_type_id = ut.user_type_id
            LEFT JOIN common_status cs ON u.status = cs.id
            WHERE u.user_id = ?
            """;

}
