package com.spoolbear.repository.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.response.NavBarResponse;
import com.spoolbear.model.response.NavBarSubmenuResponse;
import com.spoolbear.queries.NavBarQueries;
import com.spoolbear.repository.NavBarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class NavBarRepositoryImpl implements NavBarRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavBarRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NavBarRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<NavBarResponse> getActiveNavBarData() {
        try {
            LOGGER.info("Executing query to fetch nav bar data with submenus.");

            List<NavBarResponse> navBars = jdbcTemplate.query(
                    NavBarQueries.GET_ALL_NAV_BAR_DATA,
                    (rs, rowNum) -> createNavBarResponse(rs)
            );

            for (NavBarResponse navBar : navBars) {
                List<NavBarSubmenuResponse> submenus = jdbcTemplate.query(
                        NavBarQueries.GET_SUBMENU_BY_NAV_BAR_ID,
                        new Object[]{navBar.getId()},
                        (rs, rowNum) -> createSubmenuResponse(rs)
                );
                navBar.setSubmenus(submenus);
            }

            LOGGER.info("Successfully fetched {} nav bar items with submenus.", navBars.size());
            return navBars;

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching nav bar items: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch nav bar items from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching nav bar items: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching nav bar items");
        }
    }

    private NavBarResponse createNavBarResponse(ResultSet rs) throws SQLException {
        NavBarResponse response = new NavBarResponse();
        response.setId(rs.getInt("NAV_BAR_ID"));
        response.setName(rs.getString("NAME"));
        response.setDescription(rs.getString("DESCRIPTION"));
        response.setStatus(rs.getString("STATUS"));
        response.setLinkUrl(rs.getString("LINK_URL"));
        setNavBarTimestamps(response, rs);
        return response;
    }

    private NavBarSubmenuResponse createSubmenuResponse(ResultSet rs) throws SQLException {
        NavBarSubmenuResponse submenu = new NavBarSubmenuResponse();
        submenu.setId(rs.getInt("SUBMENU_ID"));
        submenu.setNavBarId(rs.getInt("NAV_BAR_ID"));
        submenu.setName(rs.getString("NAME"));
        submenu.setDescription(rs.getString("DESCRIPTION"));
        submenu.setLinkUrl(rs.getString("LINK_URL"));
        submenu.setIconClass(rs.getString("ICON_CLASS"));
        submenu.setSortOrder(rs.getInt("SORT_ORDER"));
        submenu.setOpensInNewTab(rs.getBoolean("OPENS_IN_NEW_TAB"));
        submenu.setIsFeatured(rs.getBoolean("IS_FEATURED"));
        submenu.setStatus(rs.getString("STATUS"));
        setSubmenuTimestamps(submenu, rs);
        return submenu;
    }

    private void setNavBarTimestamps(NavBarResponse response, ResultSet rs) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("CREATED_AT");
        response.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
        response.setCreatedBy(getNullableInt(rs, "CREATED_BY"));
        Timestamp updatedTs = rs.getTimestamp("UPDATED_AT");
        response.setUpdatedAt(updatedTs != null ? updatedTs.toLocalDateTime() : null);
        response.setUpdatedBy(getNullableInt(rs, "UPDATED_BY"));
        Timestamp terminatedTs = rs.getTimestamp("TERMINATED_AT");
        response.setTerminatedAt(terminatedTs != null ? terminatedTs.toLocalDateTime() : null);
        response.setTerminatedBy(getNullableInt(rs, "TERMINATED_BY"));
    }

    private void setSubmenuTimestamps(NavBarSubmenuResponse submenu, ResultSet rs) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("CREATED_AT");
        submenu.setCreatedAt(createdTs != null ? createdTs.toLocalDateTime() : null);
        submenu.setCreatedBy(getNullableInt(rs, "CREATED_BY"));
        Timestamp updatedTs = rs.getTimestamp("UPDATED_AT");
        submenu.setUpdatedAt(updatedTs != null ? updatedTs.toLocalDateTime() : null);
        submenu.setUpdatedBy(getNullableInt(rs, "UPDATED_BY"));
        Timestamp terminatedTs = rs.getTimestamp("TERMINATED_AT");
        submenu.setTerminatedAt(terminatedTs != null ? terminatedTs.toLocalDateTime() : null);
        submenu.setTerminatedBy(getNullableInt(rs, "TERMINATED_BY"));
    }

    private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        return rs.getObject(column) != null ? rs.getInt(column) : null;
    }

}
