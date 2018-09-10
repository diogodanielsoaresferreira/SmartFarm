package pt.ua.es.smartfarm.ServiceLayer;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

/**
 * Password converter to store passwords.
 */
@Converter
@Slf4j
public class PasswordConverter implements AttributeConverter<String, String> {

    /**
     * Convert password to save in the database.
     * @param plainPassword plain text password
     * @return ciphered password
     */
    @Override
    public String convertToDatabaseColumn(String plainPassword) {
        String encoded = null;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            encoded = Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            log.error("Could not encode password to string. Algorithm SHA-256 was not found.");
        }

        return encoded;
    }

    /**
     * Convert to attribute.
     * @param hashedPassword password hashed
     * @return password hashed
     */
    @Override
    public String convertToEntityAttribute(String hashedPassword) {
        return hashedPassword;
    }

}