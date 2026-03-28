package com.spoolbear.queries;

public class ReviewQueries {
    public static final String GET_REVIEW_DETAILS = """
            SELECT
            	pr.review_id AS review_id,
            	p.product_id AS product_id,
            	p.name AS product_name,
            	pr.comment AS comment,
            	pr.rating,
            	cs_pr.name AS review_status,
            	pr.created_by AS review_created_by,
            	pr.created_at AS review_created_at,
            	pr.updated_by AS review_updated_by,
            	pr.updated_at AS review_updated_at,
            	ri.review_image_id AS image_id,
            	ri.image_url AS image_url,
            	ri.created_by AS image_created_by,
            	ri.created_at AS image_created_at,
            	prr.id AS review_reaction_id,
            	prr.product_review_id AS reaction_review_id,
            	prr.user_id AS reaction_user_id,
            	u1.username AS reaction_user_name,
            	rt.name AS reaction_type,
            	cs_prr.name AS review_reaction_status,
            	prr.created_at AS reaction_created_at,
            	prc.id AS comment_id,
            	prc.product_review_id AS comment_review_id,
            	prc.user_id AS comment_user_id,
            	u2.username AS comment_user_name,
            	prc.parent_comment_id,
            	prc.comment,
            	cs_prc.name AS comment_status,
            	prc.created_at AS comment_created_at,
            	prc.created_by AS comment_created_by,
            	prcr.id AS comment_reaction_id,
            	prcr.comment_id AS comment_reaction_comment_id,
            	prcr.user_id AS comment_reaction_user_id,
            	u3.username AS comment_reaction_user_name,
            	rt2.name AS comment_reaction_type,
            	cs_prcr.name AS comment_reaction_status,
            	prcr.created_by AS comment_reaction_created_by,
            	prcr.created_at AS comment_reaction_created_at
            FROM product_reviews pr
            LEFT JOIN products p
            	ON p.product_id = pr.product_id
            LEFT JOIN common_status cs_pr
            	ON cs_pr.id = pr.status
            LEFT JOIN review_images ri
            	ON ri.review_id = pr.review_id
            LEFT JOIN product_review_reaction prr
            	ON prr.product_review_id = pr.review_id
            LEFT JOIN reaction_type rt
            	ON rt.id = prr.reaction_type_id
            LEFT JOIN user u1
            	ON u1.user_id = prr.user_id
            LEFT JOIN common_status cs_prr
            	ON cs_prr.id = prr.status
            LEFT JOIN product_review_comment prc
            	ON prc.product_review_id = pr.review_id
            LEFT JOIN user u2
            	ON u2.user_id = prc.user_id
            LEFT JOIN common_status cs_prc
            	ON cs_prc.id = prc.status
            LEFT JOIN product_review_comment_reaction prcr
            	ON prcr.comment_id = prc.id
            LEFT JOIN reaction_type rt2
            	ON rt2.id = prcr.reaction_type_id
            LEFT JOIN user u3
            	ON u3.user_id = prcr.user_id
            LEFT JOIN common_status cs_prcr
            	ON cs_prcr.id = prcr.status
            ORDER BY pr.review_id, prc.id, prcr.id
            """;

    public static final String GET_REVIEW_DETAILS_BY_ID = """
            SELECT
            	pr.review_id AS review_id,
            	p.product_id AS product_id,
            	p.name AS product_name,
            	pr.comment AS comment,
            	pr.rating,
            	cs_pr.name AS review_status,
            	pr.created_by AS review_created_by,
            	pr.created_at AS review_created_at,
            	pr.updated_by AS review_updated_by,
            	pr.updated_at AS review_updated_at,
            	ri.review_image_id AS image_id,
            	ri.image_url AS image_url,
            	ri.created_by AS image_created_by,
            	ri.created_at AS image_created_at,
            	prr.id AS review_reaction_id,
            	prr.product_review_id AS reaction_review_id,
            	prr.user_id AS reaction_user_id,
            	u1.username AS reaction_user_name,
            	rt.name AS reaction_type,
            	cs_prr.name AS review_reaction_status,
            	prr.created_at AS reaction_created_at,
            	prc.id AS comment_id,
            	prc.product_review_id AS comment_review_id,
            	prc.user_id AS comment_user_id,
            	u2.username AS comment_user_name,
            	prc.parent_comment_id,
            	prc.comment,
            	cs_prc.name AS comment_status,
            	prc.created_at AS comment_created_at,
            	prc.created_by AS comment_created_by,
            	prcr.id AS comment_reaction_id,
            	prcr.comment_id AS comment_reaction_comment_id,
            	prcr.user_id AS comment_reaction_user_id,
            	u3.username AS comment_reaction_user_name,
            	rt2.name AS comment_reaction_type,
            	cs_prcr.name AS comment_reaction_status,
            	prcr.created_by AS comment_reaction_created_by,
            	prcr.created_at AS comment_reaction_created_at
            FROM product_reviews pr
            LEFT JOIN products p
            	ON p.product_id = pr.product_id
            LEFT JOIN common_status cs_pr
            	ON cs_pr.id = pr.status
            LEFT JOIN review_images ri
            	ON ri.review_id = pr.review_id
            LEFT JOIN product_review_reaction prr
            	ON prr.product_review_id = pr.review_id
            LEFT JOIN reaction_type rt
            	ON rt.id = prr.reaction_type_id
            LEFT JOIN user u1
            	ON u1.user_id = prr.user_id
            LEFT JOIN common_status cs_prr
            	ON cs_prr.id = prr.status
            LEFT JOIN product_review_comment prc
            	ON prc.product_review_id = pr.review_id
            LEFT JOIN user u2
            	ON u2.user_id = prc.user_id
            LEFT JOIN common_status cs_prc
            	ON cs_prc.id = prc.status
            LEFT JOIN product_review_comment_reaction prcr
            	ON prcr.comment_id = prc.id
            LEFT JOIN reaction_type rt2
            	ON rt2.id = prcr.reaction_type_id
            LEFT JOIN user u3
            	ON u3.user_id = prcr.user_id
            LEFT JOIN common_status cs_prcr
            	ON cs_prcr.id = prcr.status
            WHERE pr.user_id = ? 
            ORDER BY pr.review_id, prc.id, prcr.id
            """;
}
