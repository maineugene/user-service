package com.innowise.userservice.repository;

import com.innowise.userservice.model.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCardRepository
        extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    List<PaymentCard> findPaymentCardByUserId(Long userId);

    @Query(value = "SELECT COUNT(*) FROM payment_cards WHERE user_id = :userId", nativeQuery = true)
    int countCardsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE PaymentCard c SET c.active = :active WHERE c.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);
}
