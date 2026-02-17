package com.printing._d.repository.impl;

import com.printing._d.exception.DataAccessErrorExceptionHandler;
import com.printing._d.exception.InternalServerErrorExceptionHandler;
import com.printing._d.model.response.GalleryResponse;
import com.printing._d.queries.GalleryQueries;
import com.printing._d.repository.GalleryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GalleryRepositoryImpl implements GalleryRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(GalleryRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GalleryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<GalleryResponse> getActiveImages() {
        String GET_ALL_GALLERY_IMAGES = GalleryQueries.GET_ALL_GALLERY_IMAGES;
        try {
            LOGGER.info("Executing query to fetch all images.");

            List<GalleryResponse> results = jdbcTemplate.query(GET_ALL_GALLERY_IMAGES, (rs, rowNum) -> {
                GalleryResponse gallery = new GalleryResponse();
                gallery.setImageId(rs.getInt("image_id"));
                gallery.setImageName(rs.getString("image_name"));
                gallery.setImageDescription(rs.getString("image_description"));
                gallery.setLocation(rs.getString("location"));
                gallery.setImageLink(rs.getString("image_link"));
                gallery.setImageOwner(rs.getString("image_owner"));
                gallery.setImageSource(rs.getString("image_source"));
                gallery.setImageSourceLink(rs.getString("image_source_link"));
                gallery.setColor(rs.getString("color"));
                gallery.setHoverColor(rs.getString("hover_color"));
                gallery.setImageStatus(rs.getString("image_status"));
                gallery.setImageStatusStatus(rs.getString("iamge_status_status"));
                gallery.setCreatedAt(rs.getTimestamp("created_at"));
                gallery.setUpdatedAt(rs.getTimestamp("updated_at"));
                gallery.setTerminatedAt(rs.getTimestamp("terminated_at"));
                return gallery;
            });

            LOGGER.info("Successfully fetched {} images.", results.size());
            return results;

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching images: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch images from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching images: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching images");
        }
    }

}
