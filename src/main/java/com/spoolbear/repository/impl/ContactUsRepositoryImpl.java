package com.spoolbear.repository.impl;

import com.spoolbear.model.request.InsertInquiryRequest;
import com.spoolbear.repository.ContactUsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContactUsRepositoryImpl implements ContactUsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactUsRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ContactUsRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addInquiry(InsertInquiryRequest insertInquiryRequest) {
        String sql = """
                    INSERT INTO inquiries (name, email, subject, message, status_id, isCheck,contact_number)
                    VALUES (?, ?, ?, ?, ?, ?,?)
                """;

        int rowsAffected = jdbcTemplate.update(
                sql,
                insertInquiryRequest.getName(),
                insertInquiryRequest.getEmail(),
                insertInquiryRequest.getSubject(),
                insertInquiryRequest.getMessage(),
                1,
                false,
                insertInquiryRequest.getContactNumber()
        );

        return rowsAffected > 0;
    }
}
