package com.qlatform.quant.repository.userdb;

import com.qlatform.quant.model.AuthProvider;
import com.qlatform.quant.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("{'email': ?0, 'provider': ?1}")
    Optional<User> findByEmailAndProvider(String email, AuthProvider provider);

    List<User> findByEnabled(boolean enabled);

    List<User> findByBlocked(boolean blocked);

    @Query("{'provider': ?0}")
    List<User> findByProvider(AuthProvider provider);

    // For admin purposes
    @Query(value = "{}", sort = "{ 'createdAt': -1 }")
    List<User> findAllOrderByCreatedAtDesc();

    // Search users by name or email
    @Query("{ $or: [ {'name': { $regex: ?0, $options: 'i' }}, {'email': { $regex: ?0, $options: 'i' }} ]}")
    List<User> searchUsers(String searchTerm);

    // Find users created between dates
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Update user enabled status
    @Query("{ 'id': ?0 }")
    @Update("{ '$set': { 'enabled': ?1 }}")
    void updateEnabledStatus(String userId, boolean enabled);

    // Update user blocked status
    @Query("{ 'id': ?0 }")
    @Update("{ '$set': { 'blocked': ?1 }}")
    void updateBlockedStatus(String userId, boolean blocked);

    // Count users by provider
    long countByProvider(AuthProvider provider);

    // Find users who haven't verified their email
    List<User> findByEmailVerifiedFalse();
}
