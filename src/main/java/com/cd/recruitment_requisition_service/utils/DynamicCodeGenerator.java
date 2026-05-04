package com.cd.recruitment_requisition_service.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DynamicCodeGenerator {

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public String generateCode(String sequenceName, String prefix, String dateFormat) {
        createSequenceIfNotExists(sequenceName);

        Long nextSeq = getNextSequenceValue(sequenceName);

        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormat));

        if (prefix == null) prefix = "";
        return prefix + datePart + String.format("%04d", nextSeq);
    }

    private void createSequenceIfNotExists(String sequenceName) {
        try {
            Query query = entityManager.createNativeQuery(
                    "CREATE SEQUENCE IF NOT EXISTS " + sequenceName + " START WITH 1 INCREMENT BY 1"
            );
            query.executeUpdate();
        } catch (Exception e) {
            System.err.println("Sequence creation failed: " + e.getMessage());
        }
    }

    private Long getNextSequenceValue(String sequenceName) {
        Query query = entityManager.createNativeQuery("SELECT nextval('" + sequenceName + "')");
        Object result = query.getSingleResult();

        if (result instanceof BigInteger) {
            return ((BigInteger) result).longValue();
        } else if (result instanceof Long) {
            return (Long) result;
        } else if (result instanceof Integer) {
            return ((Integer) result).longValue();
        } else {
            throw new IllegalStateException("Unexpected sequence result type: " + result);
        }
    }

    //example
    // String planCode = autoCodeGenerator.generateCode("plan_code_seq", "PLN", "ddMMyyyy");

}
